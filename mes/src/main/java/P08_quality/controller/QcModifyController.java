package P08_quality.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P08_quality.QcAddDTO;
import P08_quality.QcDTO;
import P08_quality.QcService;

@Controller
@RequestMapping("/qcmodify")
public class QcModifyController {

	@Autowired
	private QcService service;

	@GetMapping
	public String doGet(@RequestParam String qcId, Model model) {
		System.out.println("/qcmodify doGet 실행");

		QcDTO qcDTO = service.getQc(qcId);
		model.addAttribute("qcInfo", qcDTO);
		return "P08_quality/qcModify";
	}

	@PostMapping
	public String doPost(
			@RequestParam String cmd,
			@RequestParam(required = false) String qcId,
			@RequestParam(required = false) String woId,
			@RequestParam(required = false) String sDate,
			@RequestParam(required = false) String workerId,
			@RequestParam(required = false) String content) {

		System.out.println("/qcmodify doPost 실행");

		if ("update".equals(cmd)) {
			QcAddDTO addDTO = new QcAddDTO();
			addDTO.setsDate(Date.valueOf(sDate));
			addDTO.setWorker(workerId);
			addDTO.setContent(content);
			service.modifyOrder(qcId, addDTO);
			return "redirect:/qcdetail?qcId=" + qcId;
		} else if ("delete".equals(cmd)) {
			service.deleteQc(qcId);
			service.delWoStatus(woId);
			return "redirect:/qclist";
		}
		return "redirect:/qclist";
	}
}
