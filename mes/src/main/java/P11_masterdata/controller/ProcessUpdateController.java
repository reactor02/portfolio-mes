package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.ProcessDAO;
import P11_masterdata.DTO.ProcessDTO;

@Controller
@RequestMapping("/processUpdate")
public class ProcessUpdateController {

	@GetMapping
	public String doGet() {
		return "redirect:/process";
	}

	@PostMapping
	public String doPost(
			@RequestParam(required = false) String process_id,
			@RequestParam(required = false) String process_name,
			@RequestParam(required = false) String process_info,
			@RequestParam(required = false) String process_type) {

		ProcessDTO processDTO = new ProcessDTO();
		processDTO.setProcess_id(process_id);
		processDTO.setProcess_name(process_name);
		processDTO.setProcess_info(process_info);
		processDTO.setProcess_type(process_type);

		ProcessDAO processDAO = new ProcessDAO();
		processDAO.updateProcess(processDTO);

		return "redirect:/process?processId=" + process_id;
	}
}
