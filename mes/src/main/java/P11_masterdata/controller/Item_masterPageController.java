package P11_masterdata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.Item_masterDAO;
import P11_masterdata.DTO.Item_masterDTO;

@Controller
@RequestMapping("/itemmaster")
public class Item_masterPageController {

	@GetMapping
	public String doGet(
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String itemGroup,
			@RequestParam(required = false) String keyword,
			Model model) {

		if (size < 1) size = 5;
		if (page < 1) page = 1;

		Item_masterDTO dto = new Item_masterDTO();
		dto.setSize(size);
		dto.setPage(page);
		dto.setGroupKeyword(itemGroup);
		dto.setKeyword(keyword);

		Map<String, Object> map = itemSelect(dto);

		model.addAttribute("list", map.get("list"));
		model.addAttribute("allItemList", map.get("allItemList"));
		model.addAttribute("page", map.get("page"));
		model.addAttribute("size", map.get("size"));
		model.addAttribute("totalPage", map.get("totalPage"));
		model.addAttribute("totalCount", map.get("totalCount"));
		model.addAttribute("filteredCount", map.get("filteredCount"));
		model.addAttribute("finCount", map.get("finCount"));
		model.addAttribute("semiCount", map.get("semiCount"));
		model.addAttribute("rawCount", map.get("rawCount"));
		model.addAttribute("itemGroup", itemGroup);
		model.addAttribute("keyword", keyword);

		return "P11_masterdata/item_master";
	}

	private Map<String, Object> itemSelect(Item_masterDTO dto) {
		int size = dto.getSize() == 0 ? 5 : dto.getSize();
		int page = dto.getPage() == 0 ? 1 : dto.getPage();

		int end = page * size;
		int start = end - (size - 1);
		dto.setStart(start);
		dto.setEnd(end);
		dto.setSize(size);
		dto.setPage(page);

		Item_masterDAO dao = new Item_masterDAO();

		int totalCount = dao.selectItemTotalCountAll();
		int filteredCount = dao.selectItemTotalCount(dto);
		int finCount = dao.selectItemGroupCount(30);
		int semiCount = dao.selectItemGroupCount(20);
		int rawCount = dao.selectItemGroupCount(10);

		int totalPage = filteredCount / size;
		if (filteredCount % size != 0) totalPage++;
		if (totalPage == 0) totalPage = 1;

		if (page > totalPage) {
			page = totalPage;
			end = page * size;
			start = end - (size - 1);
			dto.setPage(page);
			dto.setStart(start);
			dto.setEnd(end);
		}

		List<Item_masterDTO> list = dao.selectItemPageList(dto);
		List<Item_masterDTO> allItemList = dao.selectItemList();

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("allItemList", allItemList);
		map.put("totalCount", totalCount);
		map.put("filteredCount", filteredCount);
		map.put("totalPage", totalPage);
		map.put("page", page);
		map.put("size", size);
		map.put("finCount", finCount);
		map.put("semiCount", semiCount);
		map.put("rawCount", rawCount);
		return map;
	}
}
