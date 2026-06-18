package P08_quality.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P08_quality.QcDTO;
import P08_quality.QcDefDTO;
import P08_quality.QcService;

@Controller
@RequestMapping("/qcdetail")
public class QcDetailController {

	@Autowired
	private QcService service;

	@GetMapping
	public String doGet(@RequestParam String qcId, Model model) {
		System.out.println("/qcdetail doGet 실행");

		QcDTO dto = new QcDTO();
		dto.setQcId(qcId);
		dto = service.detail(dto);
		model.addAttribute("qcInfo", dto);

		List<QcDefDTO> defList = service.defContent(qcId);
		model.addAttribute("defList", defList);

		return "P08_quality/detail";
	}
}
