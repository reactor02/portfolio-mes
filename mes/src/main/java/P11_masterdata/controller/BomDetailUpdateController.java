package P11_masterdata.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.BomDAO;
import P11_masterdata.DTO.BomDTO;

@Controller
@RequestMapping("/BomDetailUpdateController")
public class BomDetailUpdateController {

	@GetMapping
	public String doGet() {
		return "redirect:/bom";
	}

	@PostMapping
	public String doPost(
			@RequestParam String bom_id,
			@RequestParam String child_item_id,
			@RequestParam(required = false, defaultValue = "0") String bom_detail_id,
			@RequestParam(required = false, defaultValue = "0") String ea) {

		int bomDetailId = 0;
		BigDecimal eaVal = BigDecimal.ZERO;
		try { bomDetailId = Integer.parseInt(bom_detail_id); } catch (Exception e) {}
		try { eaVal = new BigDecimal(ea); } catch (Exception e) {}

		BomDAO bomDAO = new BomDAO();

		if (bom_id == null || bom_id.trim().isEmpty()
				|| child_item_id == null || child_item_id.trim().isEmpty()
				|| bomDetailId <= 0
				|| eaVal.compareTo(BigDecimal.ZERO) <= 0
				|| !bomDAO.isValidChildItem(null, child_item_id)) {
			return "redirect:/bomDetail?bomId=" + bom_id;
		}

		BomDTO bomDTO = new BomDTO();
		bomDTO.setBom_id(bom_id);
		bomDTO.setBom_detail_id(bomDetailId);
		bomDTO.setChild_item_id(child_item_id);
		bomDTO.setEa(eaVal);

		bomDAO.updateBomDetail(bomDTO);
		return "redirect:/bomDetail?bomId=" + bom_id;
	}
}
