package P09_equip.controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
@RequestMapping("/eqlogmodify")
public class EqLogModifyController {

	@Autowired
	private EqService service;

	@GetMapping
	public String doGet(@RequestParam String logId, Model model) {
		System.out.println("/eqlogmodify doGet 실행");

		EqLogDTO dto = new EqLogDTO();
		dto.setLogId(logId);
		dto = service.getLogDetail(dto);

		DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

		String sDate = null, sTime = null;
		if (dto.getsTime() != null) {
			LocalDateTime ldt = dto.getsTime().toLocalDateTime();
			sDate = ldt.format(dateFmt);
			sTime = ldt.format(timeFmt);
		}
		String eDate = null, eTime = null;
		if (dto.geteTime() != null) {
			LocalDateTime ldt = dto.geteTime().toLocalDateTime();
			eDate = ldt.format(dateFmt);
			eTime = ldt.format(timeFmt);
		}

		model.addAttribute("logInfo", dto);
		model.addAttribute("sDate", sDate);
		model.addAttribute("sTime", sTime);
		model.addAttribute("eDate", eDate);
		model.addAttribute("eTime", eTime);

		return "P09_equip/modifyInsp";
	}

	@PostMapping
	public String modifyLog(
			@RequestParam String logId,
			@RequestParam String eqId,
			@RequestParam String wId,
			@RequestParam String sDate,
			@RequestParam String sTime,
			@RequestParam(required = false) String eDate,
			@RequestParam(required = false) String eTime,
			@RequestParam String inspType,
			@RequestParam String content) {

		System.out.println("/eqlogmodify modifyLog 실행");

		EqLogDTO dto = new EqLogDTO();
		dto.setLogId(logId);
		dto.setEqId(eqId);
		dto.setwId(wId);
		dto.setsTime(Timestamp.valueOf(sDate + " " + sTime + ":00"));
		dto.setInspType(inspType);
		dto.setInspContent(content);

		if (eDate != null && !eDate.isEmpty()) {
			dto.seteTime(Timestamp.valueOf(eDate + " " + eTime + ":00"));
		}

		service.modifyLog(dto);
		return "redirect:/eqdetail?eqId=" + eqId;
	}
}
