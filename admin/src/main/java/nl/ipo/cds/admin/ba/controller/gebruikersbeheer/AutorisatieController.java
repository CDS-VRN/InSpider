package nl.ipo.cds.admin.ba.controller.gebruikersbeheer;

import javax.inject.Inject;

import nl.ipo.cds.dao.ManagerDao;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping ("/ba/gebruikersbeheer/autorisatie")
public class AutorisatieController {

	@Inject
	private ManagerDao managerDao;
	
	@RequestMapping (method = RequestMethod.GET)
	public String showAutorisatie () {
		return "/ba/gebruikersbeheer/autorisatie";
	}
}