package P09_equip.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P09_equip.EqService;
import P09_equip.DTO.EqDTO;
import P09_equip.DTO.EqLogDTO;

@Controller
@RequestMapping("/eqdetail")
public class EqDetailController {

	@Autowired
	private EqService service;

	@GetMapping
	public String doGet(
			@RequestParam String eqId,
			@RequestParam(required = false) String cmd,
			@RequestParam(defaultValue = "1") String page,
			Model model) {

		System.out.println("/eqdetail doGet 실행");

		if ("eqStop".equals(cmd)) {
			service.eqStop(eqId);
			service.stopLog(eqId);
			return "redirect:/eqdetail?eqId=" + eqId;
		}
		if ("eqRun".equals(cmd)) {
			service.eqRun(eqId);
			service.startLog(eqId);
			return "redirect:/eqdetail?eqId=" + eqId;
		}

		// 기본: 상세 조회
		EqDTO dto = new EqDTO();
		dto.setEqId(eqId);
		dto = service.detail(dto);
		model.addAttribute("eqInfo", dto);

		int pageNum = 1;
		try { pageNum = Integer.parseInt(page); } catch (Exception e) {}

		EqLogDTO logDTO = new EqLogDTO();
		logDTO.setSize(10);
		logDTO.setPage(pageNum);
		logDTO.setEqId(eqId);

		Map eqMap = service.getLog(logDTO);
		model.addAttribute("eqMap", eqMap);
		model.addAttribute("log", eqMap.get("list"));

		return "P09_equip/detail";
	}

	@PostMapping(params = "cmd=statusChange")
	public String statusChange(
			@RequestParam String eqId,
			@RequestParam String status) {

		System.out.println("/eqdetail statusChange 실행");
		service.statusChange(status, eqId);
		return "redirect:/eqdetail?eqId=" + eqId;
	}
}
