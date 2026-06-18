package P06_prod;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/prod")
public class ProdController {

	@Autowired
	private ProdService prodService;

	/* ── 목록 ── */
	@GetMapping("/list")
	public String list(
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			Model model) {

		ProdDTO planDTO = new ProdDTO();
		planDTO.setSize(size);
		planDTO.setPage(page);
		planDTO.setKeyword(keyword);
		planDTO.setStartDate(startDate);
		planDTO.setEndDate(endDate);

		Map map = prodService.getPlanList(planDTO);
		map.put("size", size);
		map.put("page", page);
		map.put("keyword", keyword != null ? keyword : "");
		map.put("startDate", startDate != null ? startDate : "");
		map.put("endDate", endDate != null ? endDate : "");
		model.addAttribute("map", map);
		model.addAttribute("groupList", prodService.getGroupList());
		model.addAttribute("itemList", prodService.getItemList());

		return "P06_prod/prodList";
	}

	/* ── 상세 ── */
	@GetMapping("/detail")
	public String detail(@RequestParam(required = false) String planId, Model model) {
		if (planId == null || planId.isEmpty()) return "redirect:/prod/list";
		ProdDTO dto = prodService.getPlanDetail(planId);
		if (dto == null) return "redirect:/prod/list";
		model.addAttribute("planDto", dto);
		model.addAttribute("groupList", prodService.getGroupList());
		model.addAttribute("itemList", prodService.getItemList());
		List<Map<String, Object>> stepList = prodService.getProcessStepList(dto.getItemId());
		model.addAttribute("stepList", stepList);
		return "P06_prod/prodDetail";
	}

	/* ── 담당자 검색 AJAX ── */
	@GetMapping("/api/empSearch")
	@ResponseBody
	public String empSearch(
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int size) {

		Map empMap = prodService.getEmpList(keyword, page, size);
		List<Map> list = (List<Map>) empMap.get("list");
		int totalCount = (int) empMap.get("totalCount");

		StringBuilder sb = new StringBuilder();
		sb.append("{\"totalCount\":").append(totalCount).append(",\"list\":[");
		for (int i = 0; i < list.size(); i++) {
			Map emp = list.get(i);
			if (i > 0) sb.append(",");
			sb.append("{")
			  .append("\"empId\":\"").append(emp.get("empId")).append("\",")
			  .append("\"ename\":\"").append(emp.get("ename")).append("\",")
			  .append("\"deptName\":\"").append(emp.get("deptName")).append("\"")
			  .append("}");
		}
		sb.append("]}");
		return sb.toString();
	}

	/* ── 등록 ── */
	@PostMapping("/insert")
	public String insert(
			@RequestParam String itemId,
			@RequestParam String empId,
			@RequestParam(required = false) String planQty,
			@RequestParam(required = false) String planSdate,
			@RequestParam(required = false) String planEdate) {

		ProdDTO dto = new ProdDTO();
		dto.setItemId(itemId);
		dto.setEmpId(empId);
		try { dto.setPlanQty(Integer.parseInt(planQty)); } catch (Exception e) { e.printStackTrace(); }
		try { dto.setPlanSdate(Date.valueOf(planSdate)); } catch (Exception e) { e.printStackTrace(); }
		try { dto.setPlanEdate(Date.valueOf(planEdate)); } catch (Exception e) { e.printStackTrace(); }

		String newPlanId = prodService.insertPlan(dto);
		return newPlanId != null ? "redirect:/prod/detail?planId=" + newPlanId : "redirect:/prod/list";
	}

	/* ── 수정 ── */
	@PostMapping("/update")
	public String update(
			@RequestParam String planId,
			@RequestParam String itemId,
			@RequestParam String empId,
			@RequestParam(required = false) String planQty,
			@RequestParam(required = false) String planSdate,
			@RequestParam(required = false) String planEdate,
			@RequestParam(required = false) String status) {

		ProdDTO dto = new ProdDTO();
		dto.setPlanId(planId);
		dto.setItemId(itemId);
		dto.setEmpId(empId);
		try { dto.setPlanQty(Integer.parseInt(planQty)); } catch (Exception e) { e.printStackTrace(); }
		try { dto.setPlanSdate(Date.valueOf(planSdate)); } catch (Exception e) { e.printStackTrace(); }
		try { dto.setPlanEdate(Date.valueOf(planEdate)); } catch (Exception e) { e.printStackTrace(); }

		if (status != null && !status.isEmpty()) {
			try { dto.setStatus(Integer.parseInt(status)); } catch (Exception e) { e.printStackTrace(); }
		} else {
			ProdDTO current = prodService.getPlanDetail(planId);
			if (current != null) dto.setStatus(current.getStatus());
		}
		prodService.updatePlan(dto);
		return "redirect:/prod/list";
	}
}
