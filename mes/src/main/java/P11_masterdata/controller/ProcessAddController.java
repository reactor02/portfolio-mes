package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.ProcessDAO;
import P11_masterdata.DTO.ProcessDTO;

@Controller
@RequestMapping("/ProcessAddController")
public class ProcessAddController {

	@GetMapping
	public String doGet() {
		return "redirect:/process";
	}

	@PostMapping
	public String doPost(
			@RequestParam(required = false, defaultValue = "") String process_id,
			@RequestParam(required = false, defaultValue = "1") String seq,
			@RequestParam(required = false, defaultValue = "") String process_name,
			@RequestParam(required = false, defaultValue = "wo") String process_type,
			@RequestParam(required = false, defaultValue = "") String item_id,
			@RequestParam(required = false, defaultValue = "") String process_info) {

		ProcessDAO processDAO = new ProcessDAO();

		process_id = process_id.trim();
		process_name = process_name.trim();
		process_type = process_type.trim();
		item_id = item_id.trim();
		process_info = process_info.trim();

		if (process_id.isEmpty()) {
			process_id = processDAO.selectNextProcessId();
		}

		int seqInt = parseIntOrDefault(seq, 1);
		if (seqInt < 1) seqInt = 1;

		if (process_type.isEmpty()) process_type = "wo";
		if (process_info.isEmpty()) process_info = process_name;

		if (process_name.isEmpty() || item_id.isEmpty()) {
			return "redirect:/process";
		}

		ProcessDTO processDTO = new ProcessDTO();
		processDTO.setProcess_id(process_id);
		processDTO.setSeq(seqInt);
		processDTO.setProcess_name(process_name);
		processDTO.setProcess_type(process_type);
		processDTO.setItem_id(item_id);
		processDTO.setProcess_info(process_info);

		int result = processDAO.insertProcess(processDTO);

		if (result > 0) {
			return "redirect:/process?page=1&processId=" + process_id;
		} else {
			return "redirect:/process";
		}
	}

	private int parseIntOrDefault(String value, int defaultValue) {
		try {
			if (value == null || value.trim().isEmpty()) return defaultValue;
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return defaultValue;
		}
	}
}
