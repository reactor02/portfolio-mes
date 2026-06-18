package P09_equip.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P09_equip.EqService;
import P09_equip.DTO.EqDTO;
import P09_equip.DTO.EqSearchDTO;

@Controller
@RequestMapping("/equipment")
public class EqMainController {

	@Autowired
	private EqService service;

	@GetMapping
	public String doGet(
			@RequestParam(required = false) String cmd,
			@RequestParam(required = false) String eqId,
			@RequestParam(defaultValue = "1") String page,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String keyword,
			Model model) {

		System.out.println("/equipment doGet 실행");

		// 항상 전체 목록 세팅
		List<EqDTO> eqAllList = service.getAllList();
		model.addAttribute("eqAllList", eqAllList);

		if ("detail".equals(cmd) && eqId != null) {
			return "redirect:/eqdetail?eqId=" + eqId;
		} else if ("search".equals(cmd)) {
			int pageNum = 1;
			try { pageNum = Integer.parseInt(page); } catch (Exception e) {}

			EqDTO dto = new EqDTO();
			dto.setSize(10);
			dto.setPage(pageNum);

			String statusVal = ("전체".equals(status)) ? "" : status;

			EqSearchDTO search = new EqSearchDTO();
			search.setStatus(statusVal != null ? statusVal : "");
			search.setKeyword(keyword != null ? keyword.trim() : "");

			Map eqMap = service.search(dto, search);
			model.addAttribute("eqMap", eqMap);
		} else {
			int pageNum = 1;
			try { pageNum = Integer.parseInt(page); } catch (Exception e) {}

			EqDTO dto = new EqDTO();
			dto.setSize(10);
			dto.setPage(pageNum);

			Map eqMap = service.getList(dto);
			model.addAttribute("eqMap", eqMap);
		}

		return "P09_equip/main";
	}
}
