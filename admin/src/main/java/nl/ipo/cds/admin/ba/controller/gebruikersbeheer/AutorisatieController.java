package nl.ipo.cds.admin.ba.controller.gebruikersbeheer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import nl.ipo.cds.admin.ba.controller.gebruikersbeheer.beans.BronhouderThemas;
import nl.ipo.cds.dao.ManagerDao;
import nl.ipo.cds.domain.Bronhouder;
import nl.ipo.cds.domain.BronhouderThema;
import nl.ipo.cds.domain.Thema;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping ("/ba/gebruikersbeheer/autorisatie")
public class AutorisatieController {

	@Inject
	private ManagerDao managerDao;
	
	@RequestMapping (value = "bronhouders", method = RequestMethod.GET)
	public String showBronhouderThemaAutorisatie (final Model model) {
		model.addAttribute ("bronhouderThemas", getBronhouderThemas ());
		return "/ba/gebruikersbeheer/bronhouder-autorisatie";
	}
	
	@RequestMapping (value = "bronhouders/{bronhouderId}/edit", method = RequestMethod.GET)
	public String showBronhouderThemaAutorisatieForm (final @PathVariable("bronhouderId") long bronhouderId, final Model model) {
		return "/ba/gebruikersbeheer/bronhouder-autorisatie-edit";
	}
	
	@RequestMapping (value = "bronhouders/{bronhoduerId}/edit", method = RequestMethod.POST)
	public String processBronhouderThemaAutorisatieForm (final @PathVariable("bronhouderId") long bronhouderId, final Model model) {
		return "redirect:/ba/gebruikersbeheer/autorisatie/bronhouders";
	}
	
	@RequestMapping (value = "gebruikers", method = RequestMethod.GET)
	public String showGebruikerThemaAutorisatie (final Model model) {
		return "/ba/gebruikersbeheer/gebruiker-autorisatie";
	}

	@RequestMapping (value = "gebruikers/{commonName}/edit", method = RequestMethod.GET)
	public String showGebruikerThemaAutorisatieForm (final @PathVariable("commonName") String commonName, final Model model) {
		return "/ba/gebruikersbeheer/gebruiker-autorisatie-edit";
	}
	
	@RequestMapping (value = "gebruikers/{commonName}/edit", method = RequestMethod.POST)
	public String processGebruikerThemaAutorisatieForm (final @PathVariable("commonName") String commonName, final Model model) {
		return "redirect:/ba/gebruikersbeheer/autorisatie/gebruikers";
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
}