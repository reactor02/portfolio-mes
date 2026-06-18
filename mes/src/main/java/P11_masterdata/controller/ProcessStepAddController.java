package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.ProcessDAO;
import P11_masterdata.DTO.ProcessDTO;

@Controller
@RequestMapping("/ProcessStepAddController")
public class ProcessStepAddController {

	@GetMapping
	public String doGet() {
		return "redirect:/process";
	}

	@PostMapping
	public String doPost(
			@RequestParam(required = false, defaultValue = "") String process_id,
			@RequestParam(required = false, defaultValue = "") String step_name,
			@RequestParam(required = false, defaultValue = "1") String seq) {

		process_id = process_id.trim();
		step_name = step_name.trim();

		if (process_id.isEmpty() || step_name.isEmpty()) {
			return "redirect:/process";
		}

		int seqInt = parseIntOrDefault(seq, 1);
		if (seqInt < 1) seqInt = 1;

		ProcessDTO processDTO = new ProcessDTO();
		processDTO.setProcess_id(process_id);
		processDTO.setStep_name(step_name);
		processDTO.setSeq(seqInt);

		ProcessDAO processDAO = new ProcessDAO();
		processDAO.insertProcessStep(processDTO);

		return "redirect:/process?processId=" + process_id;
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
