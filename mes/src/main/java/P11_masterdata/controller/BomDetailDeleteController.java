package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.BomDAO;

@Controller
@RequestMapping("/BomDetailDeleteController")
public class BomDetailDeleteController {

	@GetMapping
	public String doGet() {
		return "redirect:/bom";
	}

	@PostMapping
	public String doPost(
			@RequestParam String bom_id,
			@RequestParam(required = false, defaultValue = "0") String bom_detail_id) {

		int bomDetailId = 0;
		try { bomDetailId = Integer.parseInt(bom_detail_id); } catch (Exception e) {}

		BomDAO bomDAO = new BomDAO();
		bomDAO.deleteBomDetail(bomDetailId);

		return "redirect:/bomDetail?bomId=" + bom_id;
	}
}
