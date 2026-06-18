package P08_quality.controller;

import java.sql.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P07_work.SearchDTO;
import P08_quality.QcCardDTO;
import P08_quality.QcDTO;
import P08_quality.QcService;

@Controller
@RequestMapping("/qclist")
public class QcMainController {

	@Autowired
	private QcService service;

	@GetMapping
	public String doGet(
			@RequestParam(required = false) String cmd,
			@RequestParam(required = false) String qcId,
			@RequestParam(defaultValue = "1") String page,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String startDate,
			@RequestParam(required = false) String endDate,
			@RequestParam(required = false) String keyword,
			Model model) {

		System.out.println("/qclist doGet 실행");

		// 카드 조회 (항상)
		QcCardDTO cardDTO = service.getCard();
		model.addAttribute("cardDTO", cardDTO);

		if ("detail".equals(cmd) && qcId != null) {
			return "redirect:/qcdetail?qcId=" + qcId;
		} else if ("search".equals(cmd)) {
			int pageNum = 1;
			try { pageNum = Integer.parseInt(page); } catch (Exception e) {}

			QcDTO dto = new QcDTO();
			dto.setSize(10);
			dto.setPage(pageNum);

			int statusInt = 0;
			if (status != null && !status.isEmpty()) statusInt = Integer.parseInt(status);

			SearchDTO search = new SearchDTO();
			search.setStatus(statusInt);
			search.setsDate(startDate);
			search.seteDate(endDate);
			search.setKeyword(keyword != null ? keyword.trim() : "");

			Map map = service.search(dto, search);
			model.addAttribute("qcMap", map);
		} else {
			int pageNum = 1;
			try { pageNum = Integer.parseInt(page); } catch (Exception e) {}

			QcDTO dto = new QcDTO();
			dto.setSize(10);
			dto.setPage(pageNum);

			Map qcMap = service.getList(dto);
			model.addAttribute("qcMap", qcMap);
		}

		return "P08_quality/main";
	}
}
