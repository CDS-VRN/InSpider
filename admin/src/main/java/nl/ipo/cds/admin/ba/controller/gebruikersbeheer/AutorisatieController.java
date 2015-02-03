package nl.ipo.cds.admin.ba.controller.gebruikersbeheer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.Valid;

import nl.ipo.cds.admin.ba.controller.gebruikersbeheer.beans.BronhouderThemas;
import nl.ipo.cds.admin.ba.controller.gebruikersbeheer.beans.GebruikerThemas;
import nl.ipo.cds.dao.ManagerDao;
import nl.ipo.cds.domain.Bronhouder;
import nl.ipo.cds.domain.BronhouderThema;
import nl.ipo.cds.domain.Gebruiker;
import nl.ipo.cds.domain.GebruikerThemaAutorisatie;
import nl.ipo.cds.domain.Thema;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for displaying and managing the authorization relationships between 
 * bronhouders and themes and between users and themes.
 */
@Controller
@RequestMapping ("/ba/gebruikersbeheer/autorisatie")
public class AutorisatieController {

	@Inject
	private ManagerDao managerDao;
	
	/**
	 * Displays authorization between bronhouders and themes.
	 */
	@RequestMapping (value = "bronhouders", method = RequestMethod.GET)
	public String showBronhouderThemaAutorisatie (final Model model) {
		model.addAttribute ("bronhouderThemas", getBronhouderThemas ());
		return "/ba/gebruikersbeheer/bronhouder-autorisatie";
	}

	/**
	 * Displays a form to edit the authorization between a single bronhouder and the available themes.
	 * Redirects back to {@link AutorisatieController#showBronhouderThemaAutorisatie(Model)} when the
	 * bronhouder can't be found. 
	 */
	@RequestMapping (value = "bronhouders/{bronhouderId}/edit", method = RequestMethod.GET)
	public String showBronhouderThemaAutorisatieForm (final @PathVariable("bronhouderId") long bronhouderId, final Model model) {
		final Bronhouder bronhouder = managerDao.getBronhouder (bronhouderId);
		if (bronhouder == null) {
			return "redirect:/ba/gebruikersbeheer/autorisatie/bronhouders";
		}
		
		model.addAttribute ("bronhouder", bronhouder);
		model.addAttribute ("themas", managerDao.getAllThemas ());
		model.addAttribute ("selectedThemas", new HashSet<Thema> (managerDao.getAllThemas (bronhouder)));
		
		return "/ba/gebruikersbeheer/bronhouder-autorisatie-edit";
	}

	/**
	 * Processes the authorization form for a bronhouder. Redirects back to {@link AutorisatieController#showBronhouderThemaAutorisatie(Model)}
	 * after completing or when the bronhouder can't be found.
	 */
	@RequestMapping (value = "bronhouders/{bronhouderId}/edit", method = RequestMethod.POST)
	@Transactional
	public String processBronhouderThemaAutorisatieForm (
			final @PathVariable("bronhouderId") long bronhouderId, 
			final Model model,
			final @Valid IdSet idSet,
			final BindingResult bindingResult) {
		
		final Bronhouder bronhouder = managerDao.getBronhouder (bronhouderId);
		
		// Redirect immediately in case of an error:
		if (bronhouder == null || bindingResult.hasErrors ()) {
			return "redirect:/ba/gebruikersbeheer/autorisatie/bronhouders";
		}
		
		final Set<Long> ids = idSet.getIds ().keySet ();
		final Map<Long, BronhouderThema> bronhouderThemas = new HashMap<Long, BronhouderThema> ();
		
		// Create a map of BronhouderThemas for convenient lookups:
		for (final BronhouderThema bronhouderThema: managerDao.getBronhouderThemas (bronhouder)) {
			bronhouderThemas.put (bronhouderThema.getThema ().getId (), bronhouderThema);
		}
		
		// Insert new BronhouderThema instances:
		for (final Long id: ids) {
			if (bronhouderThemas.containsKey (id)) {
				// An entry already exists: remove from the map and skip.
				bronhouderThemas.remove (id);
				continue;
			}
			
			final Thema thema = managerDao.getThema (id);
			if (thema == null) {
				continue;
			}
			
			managerDao.create (new BronhouderThema (thema, bronhouder));
		}

		// Remove bronhouder themas that are no longer relevant:
		for (final BronhouderThema bronhouderThema: bronhouderThemas.values ()) {
			managerDao.delete (bronhouderThema);
		}
		
		return "redirect:/ba/gebruikersbeheer/autorisatie/bronhouders";
	}

	/**
	 * Displays the authorization between users and themes.
	 */
	@RequestMapping (value = "gebruikers", method = RequestMethod.GET)
	public String showGebruikerThemaAutorisatie (final Model model) {
		
		return "/ba/gebruikersbeheer/gebruiker-autorisatie";
	}

	/**
	 * Displays a form to edit the authorization between a single user and all available themes.
	 * Redirects to {@link AutorisatieController#showGebruikerThemaAutorisatie(Model)} if the user
	 * can't be found.
	 */
	@RequestMapping (value = "gebruikers/{commonName}/edit", method = RequestMethod.GET)
	public String showGebruikerThemaAutorisatieForm (final @PathVariable("commonName") String commonName, final Model model) {
		return "/ba/gebruikersbeheer/gebruiker-autorisatie-edit";
	}

	/**
	 * Processes the authorization between a single user and all available themes.
	 * Redirects to {@link AutorisatieController#showGebruikerThemaAutorisatie(Model)} upon completion or if the user
	 * can't be found.
	 */
	@RequestMapping (value = "gebruikers/{commonName}/edit", method = RequestMethod.POST)
	@Transactional
	public String processGebruikerThemaAutorisatieForm (final @PathVariable("commonName") String commonName, final Model model) {
		return "redirect:/ba/gebruikersbeheer/autorisatie/gebruikers";
	}
	
	/**
	 * Combines the list of all users with all {@link GebruikerThemaAutorisatie} associations.
	 * 
	 * @return A list of {@link GebruikerThemas} instances each containing a user and associated
	 * 			{@link GebruikerThemaAutorisatie}'s.
	 */
	private List<GebruikerThemas> getGebruikerThemas () {
		final List<Gebruiker> gebruikers = managerDao.getAllGebruikers ();
		//managerDao.getGe
		
		return null;
		
	}
	
	/**
	 * Combines the list of all bronhouders with all {@link BronhouderThema} assosications.
	 * 
	 * @return 	A list of {@link BronhouderThemas} instances each containing a bronhouder and
	 * 			associated {@link Thema}'s.
	 */
	private List<BronhouderThemas> getBronhouderThemas () {
		final List<BronhouderThema> bronhouderThemas = managerDao.getBronhouderThemas ();
		final List<Bronhouder> bronhouders = new ArrayList<Bronhouder> (managerDao.getAllBronhouders ());
		final List<BronhouderThemas> result = new ArrayList<BronhouderThemas> ();
		
		// Make sure the bronhouder list is ordered by name:
		Collections.sort (bronhouders, new Comparator<Bronhouder> () {
			@Override
			public int compare (Bronhouder o1, Bronhouder o2) {
				return o1.getNaam ().compareTo (o2.getNaam ());
			}
		});
		
		int i = 0;
		
		for (final Bronhouder bronhouder: bronhouders) {
			final List<Thema> themas = new ArrayList<Thema> ();
			
			while (i < bronhouderThemas.size () && bronhouderThemas.get (i).getBronhouder ().getId ().equals (bronhouder.getId ())) {
				themas.add (bronhouderThemas.get (i).getThema ());
				++ i;
			}
			
			result.add (new BronhouderThemas (bronhouder, themas));
		}
		
		return Collections.unmodifiableList (result);
	}
	
	private static class IdSet {
		@Valid
		private Map<Long, Boolean> ids = new HashMap<Long, Boolean> ();

		public Map<Long, Boolean> getIds () {
			return ids;
		}

		@SuppressWarnings("unused")
		public void setIds (final Map<Long, Boolean> ids) {
			this.ids = ids;
		}
	}
}