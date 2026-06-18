package P11_masterdata.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.ProcessDAO;
import P11_masterdata.DTO.ProcessDTO;

@Controller
@RequestMapping("/process")
public class ProcessController {

	@GetMapping
	public String doGet(
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(required = false) String processId,
			Model model) {

		if (size < 1) size = 5;
		if (page < 1) page = 1;

		ProcessDAO processDAO = new ProcessDAO();
		keyword = keyword.trim();

		List<ProcessDTO> allProcessList = processDAO.selectProcessList();
		ProcessDTO selectedProcess = null;

		if ((processId == null || processId.trim().isEmpty()) && !allProcessList.isEmpty()) {
			processId = allProcessList.get(0).getProcess_id();
		}

		for (ProcessDTO dto : allProcessList) {
			if (dto.getProcess_id() != null && dto.getProcess_id().equals(processId)) {
				selectedProcess = dto;
				break;
			}
		}

		ProcessDTO processDTO = new ProcessDTO();
		processDTO.setKeyword(keyword);

		int totalCount = processDAO.selectProcessTotalCount(processDTO);
		int totalPage = totalCount / size;
		if (totalCount % size != 0) totalPage++;
		if (totalPage == 0) totalPage = 1;
		if (page > totalPage) page = totalPage;

		processDTO.setPage(page);
		processDTO.setSize(size);
		processDTO.setStart((page - 1) * size + 1);
		processDTO.setEnd(page * size);

		List<ProcessDTO> processList = processDAO.selectProcessPageList(processDTO);
		List<ProcessDTO> stepList = new ArrayList<>();
		int nextStepSeq = 1;

		if (processId != null && !processId.trim().isEmpty()) {
			stepList = processDAO.selectProcessStepList(processId);
			if (!stepList.isEmpty()) {
				nextStepSeq = stepList.get(stepList.size() - 1).getSeq() + 1;
			}
		}

		String nextProcessId = processDAO.selectNextProcessId();

		model.addAttribute("allProcessList", allProcessList);
		model.addAttribute("processList", processList);
		model.addAttribute("stepList", stepList);
		model.addAttribute("nextStepSeq", nextStepSeq);
		model.addAttribute("selectedProcessId", processId);
		model.addAttribute("selectedProcess", selectedProcess);
		model.addAttribute("nextProcessId", nextProcessId);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("keyword", keyword);

		return "P11_masterdata/process";
	}
}
