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

import P08_quality.QcDTO;
import P08_quality.QcDefDTO;
import P08_quality.QcDisposeDTO;
import P08_quality.QcService;

@Controller
@RequestMapping("/qcresultmodify")
public class QcResultController {

	@Autowired
	private QcService service;

	@GetMapping
	public String doGet(@RequestParam String qcId, Model model) {
		System.out.println("/qcresultmodify doGet 실행");

		QcDTO dto = new QcDTO();
		dto.setQcId(qcId);
		dto = service.detail(dto);
		model.addAttribute("qcInfo", dto);

		List defList = service.defContent(qcId);
		model.addAttribute("defList", defList);

		QcDisposeDTO disDTO = new QcDisposeDTO();
		disDTO.setQcId(qcId);
		disDTO = service.disposeSum(disDTO);
		model.addAttribute("dispose", disDTO);

		return "P08_quality/resultModify";
	}

	/* defectAdd → JSON 응답 */
	@PostMapping(params = "cmd=defectAdd")
	@ResponseBody
	public String defectAdd(
			@RequestParam String qcId,
			@RequestParam int defQty,
			@RequestParam int defType,
			@RequestParam(required = false) String solution,
			@RequestParam(required = false) String dispose) {

		String disposeVal = "Y".equals(dispose) ? "Y" : null;

		QcDefDTO dto = new QcDefDTO();
		dto.setDefCnt(defQty);
		dto.setdType(defType);
		dto.setSolution(solution);
		dto.setDispose(disposeVal);

		service.addDef(qcId, dto);
		return "{\"success\":true}";
	}

	/* defectUpdate → JSON 응답 */
	@PostMapping(params = "cmd=defectUpdate")
	@ResponseBody
	public String defectUpdate(
			@RequestParam String defectId,
			@RequestParam int defQty,
			@RequestParam int defType,
			@RequestParam(required = false) String solution,
			@RequestParam(required = false) String dispose) {

		String disposeVal = "Y".equals(dispose) ? "Y" : null;

		QcDefDTO dto = new QcDefDTO();
		dto.setDefId(defectId);
		dto.setDefCnt(defQty);
		dto.setdType(defType);
		dto.setSolution(solution);
		dto.setDispose(disposeVal);

		int result = service.updateDef(dto);
		return "{\"success\":" + (result > 0) + "}";
	}

	/* defectDelete → JSON 응답 */
	@PostMapping(params = "cmd=defectDelete")
	@ResponseBody
	public String defectDelete(@RequestParam String defectId) {
		int result = service.deleteDef(defectId);
		return "{\"success\":" + (result > 0) + "}";
	}

	/* modify → redirect */
	@PostMapping(params = "cmd=modify")
	public String modifyResult(
			@RequestParam String qcId,
			@RequestParam(required = false) String eDate,
			@RequestParam int status) {

		Date eDateVal = null;
		if (status == 30) {
			if (eDate == null || eDate.trim().isEmpty())
				throw new RuntimeException("검사 완료 상태에서는 검사 완료일을 입력해야 합니다.");
			eDateVal = Date.valueOf(eDate);
		} else {
			if (eDate != null && !eDate.trim().isEmpty()) eDateVal = Date.valueOf(eDate);
		}

		QcDTO dto = new QcDTO();
		dto.setQcId(qcId);
		dto.seteDate(eDateVal);
		dto.setQcStatus(status);
		service.modifyResult(dto);

		return "redirect:/qcdetail?qcId=" + qcId;
	}
}
