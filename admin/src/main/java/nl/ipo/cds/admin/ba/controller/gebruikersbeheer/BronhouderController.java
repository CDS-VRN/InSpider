package nl.ipo.cds.admin.ba.controller.gebruikersbeheer;

import java.util.List;

import javax.inject.Inject;

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
	
	@RequestMapping (method = RequestMethod.GET)
	public String listBronhouders (final Model model) {
		final List<Bronhouder> bronhouders = managerDao.getAllBronhouders ();
		
		model.addAttribute ("bronhouders", bronhouders);
	
		return "/ba/gebruikersbeheer/bronhouders";
	}
	
	@RequestMapping (value = "/-/create", method = RequestMethod.GET)
	public String createBronhouderForm () {
		return "/ba/gebruikersbeheer/edit-bronhouder";
	}
	
	@RequestMapping (value = "/{bronhouderId}/edit", method = RequestMethod.GET)
	public String editBronhouderForm (final @PathVariable("bronhouderId") long bronhouderId) {
		final Bronhouder bronhouder = managerDao.getBronhouder (bronhouderId);
		if (bronhouder == null) {
			return "redirect:/ba/gebruikersbeheer/bronhouders";
		}
		
		return "/ba/gebruikersbeheer/edit-bronhouder";
	}
	
	@RequestMapping (value = "/{bronhouderId}/delete", method = RequestMethod.GET)
	public String deleteBronhouderForm (final @PathVariable("bronhouderId") long bronhouderId) {
		final Bronhouder bronhouder = managerDao.getBronhouder (bronhouderId);
		if (bronhouder == null) {
			return "redirect:/ba/gebruikersbeheer/bronhouders";
		}
		
		return "/ba/gebruikersbeheer/delete-bronhouder";
	}
}