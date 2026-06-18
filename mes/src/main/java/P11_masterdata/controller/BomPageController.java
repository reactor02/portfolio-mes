package P11_masterdata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.BomDAO;
import P11_masterdata.DTO.BomDTO;

@Controller
@RequestMapping("/bom")
public class BomPageController {

	@GetMapping
	public String doGet(
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(required = false, defaultValue = "") String itemGroup,
			Model model) {

		BomDTO bomDTO = new BomDTO();
		bomDTO.setSize(size);
		bomDTO.setPage(page);
		bomDTO.setKeyword(keyword);
		bomDTO.setItemGroup(itemGroup);

		Map<String, Object> map = bomSelect(bomDTO);
		BomDAO bomDAO = new BomDAO();
		String nextBomId = bomDAO.selectNextBomId();

		model.addAttribute("bomList", map.get("list"));
		model.addAttribute("page", map.get("page"));
		model.addAttribute("size", map.get("size"));
		model.addAttribute("totalPage", map.get("totalPage"));
		model.addAttribute("totalCount", map.get("totalCount"));
		model.addAttribute("nextBomId", nextBomId);
		model.addAttribute("keyword", keyword);
		model.addAttribute("itemGroup", itemGroup);

		return "P11_masterdata/bom";
	}

	private Map<String, Object> bomSelect(BomDTO bomDTO) {
		int size = bomDTO.getSize() == 0 ? 5 : bomDTO.getSize();
		int page = bomDTO.getPage() == 0 ? 1 : bomDTO.getPage();

		int end = page * size;
		int start = end - (size - 1);

		bomDTO.setStart(start);
		bomDTO.setEnd(end);
		bomDTO.setSize(size);
		bomDTO.setPage(page);

		BomDAO bomDAO = new BomDAO();
		List<BomDTO> list = bomDAO.selectBomPage(bomDTO);
		int totalCount = bomDAO.selectBomTotalCount(bomDTO);
		int totalPage = totalCount / size;
		if (totalCount % size != 0) totalPage++;

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", totalCount);
		map.put("totalPage", totalPage);
		map.put("page", page);
		map.put("size", size);
		return map;
	}
}
