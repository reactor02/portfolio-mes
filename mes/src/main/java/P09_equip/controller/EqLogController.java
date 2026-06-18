package P09_equip.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P09_equip.EqService;
import P09_equip.DTO.EqLogDTO;

@Controller
@RequestMapping("/eqlogadd")
public class EqLogController {

	@Autowired
	private EqService service;

	@GetMapping
	public String doGet(
			@RequestParam String eqId,
			@RequestParam(required = false) String eqName,
			Model model) {

		System.out.println("/eqlogadd doGet 실행");
		model.addAttribute("eqId", eqId);
		model.addAttribute("eqName", eqName);
		return "P09_equip/addInsp";
	}

	@PostMapping
	public String addLog(
			@RequestParam String eqId,
			@RequestParam String wId,
			@RequestParam String sDate,
			@RequestParam String sTime,
			@RequestParam(required = false) String eDate,
			@RequestParam(required = false) String eTime,
			@RequestParam String inspType,
			@RequestParam String content) {

		System.out.println("/eqlogadd addLog 실행");

		EqLogDTO dto = new EqLogDTO();
		dto.setEqId(eqId);
		dto.setwId(wId);

		Timestamp sTimestamp = Timestamp.valueOf(sDate + " " + sTime + ":00");
		dto.setsTime(sTimestamp);

		if (eDate != null && !eDate.isEmpty()) {
			Timestamp eTimestamp = Timestamp.valueOf(eDate + " " + eTime + ":00");
			dto.seteTime(eTimestamp);
		}

		dto.setInspType(inspType);
		dto.setInspContent(content);

		service.addLog(dto);
		return "redirect:/eqdetail?eqId=" + eqId;
	}
}
