package P08_quality.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P08_quality.FinIoDTO;
import P08_quality.QcService;

@Controller
@RequestMapping("/fin")
public class FinIoController {

	@Autowired
	private QcService service;

	@GetMapping
	public String doGet() {
		System.out.println("/fin doGet 실행");
		return "redirect:/qclist";
	}

	@PostMapping
	public String itemIn(
			@RequestParam String empId,
			@RequestParam String itemId,
			@RequestParam String woId,
			@RequestParam String qcId,
			@RequestParam int qty,
			@RequestParam String date) {

		System.out.println("/fin itemIn 실행");

		if (empId == null || empId.trim().isEmpty()) throw new RuntimeException("작업자 정보가 없습니다.");
		if (itemId == null || itemId.trim().isEmpty()) throw new RuntimeException("제품 정보가 없습니다.");
		if (woId == null || woId.trim().isEmpty()) throw new RuntimeException("작업지시 정보가 없습니다.");
		if (qcId == null || qcId.trim().isEmpty()) throw new RuntimeException("검사 정보가 없습니다.");
		if (qty <= 0) throw new RuntimeException("입고 수량이 0 이하입니다.");

		FinIoDTO ioDTO = new FinIoDTO();
		ioDTO.setEmpId(empId);
		ioDTO.setItemId(itemId);
		ioDTO.setQty(qty);
		ioDTO.setDate(Date.valueOf(date));

		service.processFinIn(woId, qcId, ioDTO);

		return "redirect:/qcdetail?qcId=" + qcId;
	}
}
