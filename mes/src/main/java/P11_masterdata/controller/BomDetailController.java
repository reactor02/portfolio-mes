package P11_masterdata.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.BomDAO;
import P11_masterdata.DTO.BomDTO;

@Controller
@RequestMapping("/bomDetail")
public class BomDetailController {

	@GetMapping
	public String doGet(
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String bomId,
			Model model) {

		BomDAO bomDAO = new BomDAO();

		if (bomId == null || bomId.trim().isEmpty()) {
			List<BomDTO> bomList = bomDAO.selectAll();
			if (bomList != null && !bomList.isEmpty()) {
				bomId = bomList.get(0).getBom_id();
			}
		}

		BomDTO bomInfo = null;
		List<BomDTO> detailList = new ArrayList<>();
		List<BomDTO> itemList = new ArrayList<>();
		int totalCount = 0, totalPage = 1;

		if (bomId != null && !bomId.trim().isEmpty()) {
			BomDTO pageDTO = new BomDTO();
			pageDTO.setBom_id(bomId);
			pageDTO.setSize(size);
			pageDTO.setPage(page);

			int end = page * size;
			int start = end - (size - 1);
			pageDTO.setStart(start);
			pageDTO.setEnd(end);

			bomInfo = bomDAO.selectBomInfo(bomId);
			detailList = bomDAO.selectBomDetailPage(pageDTO);
			itemList = bomDAO.selectBomDetailItemList(null);

			totalCount = bomDAO.selectBomDetailTotalCount(bomId);
			totalPage = totalCount / size;
			if (totalCount % size != 0) totalPage++;
			if (totalPage == 0) totalPage = 1;
		}

		model.addAttribute("bomInfo", bomInfo);
		model.addAttribute("detailList", detailList);
		model.addAttribute("itemList", itemList);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("nextBomDetailId", bomDAO.selectNextBomDetailId());

		return "P11_masterdata/bomDetail";
	}

	@PostMapping
	public String doPost(
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String bomId,
			Model model) {
		return doGet(size, page, bomId, model);
	}
}
