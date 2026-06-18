package P10_report;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P01_auth.LoginDTO;
import P01_auth.LoginService;

@Controller
@RequestMapping("/reporting")
public class ReportingController {

	@Autowired
	private LoginService s;

	@GetMapping
	public String doGet(
			@RequestParam(value = "d_btn", defaultValue = "1") String page,
			HttpSession session) {

		System.out.println("/reporting doget 실행");

		List<ReportDTO> date = s.date();
		ReportDTO sumdate = s.sumdate();
		List<ReportDTO> sumclear = s.sumclear();
		List<ReportDTO> topfive = s.topfive();
		ReportDTO press = s.press();
		List<ReportDTO> clear = s.clear();
		List<LoginDTO> dMonthChart = s.dMonthChart();

		int countPage = 5;
		int start_no = (Integer.parseInt(page) - 1) * countPage;
		int countPageNo = start_no + countPage;
		int count = s.dread();
		int page_no = (int) Math.ceil((double) count / countPage);

		List<ReportDTO> errormch = s.errormch(start_no, countPageNo);
		List<LoginDTO> defect_report = s.defect_report(start_no, countPageNo);

		session.setAttribute("date", date);
		session.setAttribute("sumdate", sumdate);
		session.setAttribute("sumclear", sumclear);
		session.setAttribute("errormch", errormch);
		session.setAttribute("press", press);
		session.setAttribute("topfive", topfive);
		session.setAttribute("clear", clear);
		session.setAttribute("dMonthChart", dMonthChart);
		session.setAttribute("page_no", page_no);
		session.setAttribute("defect_report", defect_report);

		return "P10_report/reporting";
	}
}
