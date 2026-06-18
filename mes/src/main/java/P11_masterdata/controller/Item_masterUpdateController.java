package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.Item_masterDAO;
import P11_masterdata.DTO.Item_masterDTO;

@Controller
@RequestMapping("/itemUpdate")
public class Item_masterUpdateController {

	@GetMapping
	public String doGet() {
		return "redirect:/itemmaster";
	}

	@PostMapping
	public String doPost(
			@RequestParam(required = false) String item_id,
			@RequestParam(required = false, defaultValue = "0") String g_id,
			@RequestParam(required = false) String unit,
			@RequestParam(required = false) String spec,
			@RequestParam(required = false) String item_name,
			@RequestParam(required = false, defaultValue = "0") String safe_qty,
			@RequestParam(required = false, defaultValue = "0") String pay) {

		Item_masterDTO item_masterDTO = new Item_masterDTO();
		item_masterDTO.setItem_id(item_id);
		item_masterDTO.setG_id(parseIntOrZero(g_id));
		item_masterDTO.setUnit(unit);
		item_masterDTO.setSpec(spec);
		item_masterDTO.setItem_name(item_name);
		item_masterDTO.setSafe_qty(parseIntOrZero(safe_qty));
		item_masterDTO.setPay(parseIntOrZero(pay));

		Item_masterDAO item_masterDAO = new Item_masterDAO();
		item_masterDAO.updateItem(item_masterDTO);

		return "redirect:/itemmaster";
	}

	private int parseIntOrZero(String value) {
		try {
			if (value == null || value.trim().isEmpty()) return 0;
			return Integer.parseInt(value.trim().replace(",", ""));
		} catch (Exception e) {
			return 0;
		}
	}
}
