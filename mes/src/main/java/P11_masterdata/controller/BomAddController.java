package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.BomDAO;
import P11_masterdata.DTO.BomDTO;

@Controller
@RequestMapping("/BomAddController")
public class BomAddController {

	@GetMapping
	public String doGet() {
		return "redirect:/bom";
	}

	@PostMapping
	public String doPost(
			@RequestParam String bom_id,
			@RequestParam String item_name,
			@RequestParam(required = false, defaultValue = "0") String g_id) {

		int gId = 0;
		try { gId = Integer.parseInt(g_id); } catch (Exception e) {}

		BomDAO bomDAO = new BomDAO();
		String parent_item_id = bomDAO.selectItemIdByNameAndGroup(item_name, gId);

		if (parent_item_id != null && bom_id != null && !bom_id.trim().isEmpty()) {
			BomDTO bomDTO = new BomDTO();
			bomDTO.setBom_id(bom_id);
			bomDTO.setParent_item_id(parent_item_id);
			bomDAO.insertBom(bomDTO);
		}
		return "redirect:/bom";
	}
}
