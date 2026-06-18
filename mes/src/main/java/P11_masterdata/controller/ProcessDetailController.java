package P11_masterdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P11_masterdata.DAO.ProcessDAO;
import P11_masterdata.DTO.ProcessDTO;

@Controller
@RequestMapping("/processDetail")
public class ProcessDetailController {

	@GetMapping
	public String doGet(
			@RequestParam(required = false) String processId,
			Model model) {

		ProcessDAO processDAO = new ProcessDAO();

		if (processId == null || processId.trim().isEmpty()) {
			List<ProcessDTO> processList = processDAO.selectProcessList();
			if (processList != null && !processList.isEmpty()) {
				processId = processList.get(0).getProcess_id();
			}
		}

		ProcessDTO processDetail = null;
		List<ProcessDTO> materialList = null;
		List<ProcessDTO> equipmentList = null;

		if (processId != null && !processId.trim().isEmpty()) {
			processDetail = processDAO.selectProcessDetail(processId);
			materialList = processDAO.selectMaterialList(processId);
			equipmentList = processDAO.selectEquipmentList(processId);
		}

		model.addAttribute("processDetail", processDetail);
		model.addAttribute("materialList", materialList);
		model.addAttribute("equipmentList", equipmentList);

		return "P11_masterdata/processDetail";
	}
}
