package nl.ipo.cds.admin.ba.controller.gebruikersbeheer;

import java.util.List;

import javax.inject.Inject;

import nl.ipo.cds.admin.ba.controller.gebruikersbeheer.beans.BronhouderForm;
import nl.ipo.cds.dao.ManagerDao;
import nl.ipo.cds.domain.Bronhouder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller to support CRUD operations on "Bronhouders" in the admin. 
 */
@Controller
@RequestMapping ("/ba/gebruikersbeheer/bronhouders")
public final class BronhouderController {

	@Inject
	private ManagerDao managerDao;
	
	/**
	 * Index page: lists bronhouders.
	 */
	@RequestMapping (method = RequestMethod.GET)
	public String listBronhouders (final Model model) {
		final List<Bronhouder> bronhouders = managerDao.getAllBronhouders ();
		
		model.addAttribute ("bronhouders", bronhouders);
	
		return "/ba/gebruikersbeheer/bronhouders";
	}

	/**
	 * Displays the form for creating a new bronhouder.
	 */
	@RequestMapping (value = "/-/create", method = RequestMethod.GET)
	public String createBronhouderForm (final Model model) {
		model.addAttribute ("bronhouderForm", new BronhouderForm ());
		return "/ba/gebruikersbeheer/edit-bronhouder";
	}
	
	/**
	 * Processes a POST of the create bronhouder form. 
	 */
	@RequestMapping (value = "/-/create", method = RequestMethod.POST)
	public String processCreateBronhouderForm () {
		return "redirect:/ba/gebruikersbeheer/bronhouders";
		
	}

	/**
	 * Displays the form for editing bronhouders.
	 * 
	 * @param bronhouderId	The ID of the bronhouder to edit.
	 */
	@RequestMapping (value = "/{bronhouderId}/edit", method = RequestMethod.GET)
	public String editBronhouderForm (final @PathVariable("bronhouderId") long bronhouderId, final Model model) {
		final Bronhouder bronhouder = managerDao.getBronhouder (bronhouderId);
		if (bronhouder == null) {
			return "redirect:/ba/gebruikersbeheer/bronhouders";
		}
		
		model.addAttribute ("bronhouderForm", new BronhouderForm (bronhouder));
		
		return "/ba/gebruikersbeheer/edit-bronhouder";
	}

	/**
	 * Processes a POST of the bronhouder edit form.
	 * 
	 * @param bronhouderId The ID of the bronhouder to save.
	 */
	@RequestMapping (value = "/{bronhouderId}/edit", method = RequestMethod.POST)
	public String processEditBronhouderForm (final @PathVariable ("bronhouderId") long bronhouderId) {
		return "redirect:/ba/gebruikersbeheer/bronhouders";
	}

	/**
	 * Displays the form for deleting a bronhouder.
	 * 
	 * @param bronhouderId	The ID of the bronhouder to delete.
	 */
	@RequestMapping (value = "/{bronhouderId}/delete", method = RequestMethod.GET)
	public String deleteBronhouderForm (final @PathVariable("bronhouderId") long bronhouderId, final Model model) {
		final Bronhouder bronhouder = managerDao.getBronhouder (bronhouderId);
		if (bronhouder == null) {
			return "redirect:/ba/gebruikersbeheer/bronhouders";
		}

		model.addAttribute ("bronhouder", bronhouder);
		
		return "/ba/gebruikersbeheer/delete-bronhouder";
	}

	/**
	 * Processes a POST of the bronhouder delete form.
	 * 
	 * @param bronhouderId
	 */
	@RequestMapping (value = "/{bronhouderId}/delete", method = RequestMethod.POST)
	public String processDeleteBronhouderForm (final @PathVariable("bronhouderId") long bronhouderId) {
		return "redirect:/ba/gebruikersbeheer/bronhouders";
	}
}