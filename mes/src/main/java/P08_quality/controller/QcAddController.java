package P08_quality.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import P01_auth.DTO.UserWoDTO;
import P07_work.WoDTO;
import P08_quality.QcAddDTO;
import P08_quality.QcService;

@Controller
@RequestMapping("/qcadd")
public class QcAddController {

	@Autowired
	private QcService service;

	@GetMapping
	public String doGet(
			@RequestParam(required = false) String cmd,
			@RequestParam(required = false) String woId,
			@RequestParam(required = false) String keyword,
			Model model) {

		System.out.println("/qcadd doGet 실행");

		if ("getWo".equals(cmd)) {
			return null; // handled by separate @ResponseBody endpoint
		}
		if ("searchWorker".equals(cmd)) {
			return null; // handled by separate @ResponseBody endpoint
		}

		List woList = service.getWoList();
		model.addAttribute("woList", woList);

		return "P08_quality/add";
	}

	@GetMapping(params = "cmd=getWo")
	@ResponseBody
	public String getWo(@RequestParam String woId) {
		System.out.println("/qcadd getWo 실행");

		WoDTO woDTO = new WoDTO();
		woDTO.setWoId(woId);
		woDTO = service.setWo(woDTO);

		return "{"
			+ "\"woId\":\"" + woDTO.getWoId() + "\","
			+ "\"itemId\":\"" + woDTO.getItemId() + "\","
			+ "\"itemName\":\"" + woDTO.getItemName() + "\","
			+ "\"qty\":\"" + woDTO.getWoQty() + "\","
			+ "\"woDate\":\"" + woDTO.getWorkDate() + "\""
			+ "}";
	}

	@GetMapping(params = "cmd=searchWorker")
	@ResponseBody
	public String searchWorker(@RequestParam String keyword) {
		System.out.println("/qcadd searchWorker 실행");

		List<UserWoDTO> list = service.searchWorker(keyword.trim());

		StringBuilder json = new StringBuilder("[");
		for (int i = 0; i < list.size(); i++) {
			UserWoDTO e = list.get(i);
			json.append("{")
				.append("\"empId\":\"").append(e.getEmpId()).append("\",")
				.append("\"empName\":\"").append(e.geteName()).append("\"")
				.append("}");
			if (i < list.size() - 1) json.append(",");
		}
		json.append("]");
		return json.toString();
	}

	@PostMapping
	public String doPost(
			@RequestParam String woId,
			@RequestParam String qcDate,
			@RequestParam(required = false, defaultValue = "") String workerId,
			@RequestParam(required = false, defaultValue = "") String content) {

		System.out.println("/qcadd doPost 실행");

		QcAddDTO addDTO = new QcAddDTO();
		addDTO.setWoId(woId);
		addDTO.setsDate(Date.valueOf(qcDate));
		addDTO.setDirector("");
		addDTO.setWorker(workerId);
		addDTO.setContent(content);
		addDTO.setStatus(10);

		int result = service.addQc(addDTO);
		int woStatus = service.woStatus(addDTO);
		System.out.println(result + ", " + woStatus);

		return "redirect:/qclist";
	}
}
