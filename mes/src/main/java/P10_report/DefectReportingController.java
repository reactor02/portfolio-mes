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
import P02_dashboard.DashDTO;

@Controller
@RequestMapping("/defectreporting")
public class DefectReportingController {

	@Autowired
	private LoginService s;

	@GetMapping
	public String doGet(
			@RequestParam(value = "d_btn", defaultValue = "1") String page,
			HttpSession session) {

		System.out.println("/defectreporting doget 실행");

		List<DashDTO> list = s.defect();
		List<LoginDTO> dMonthChart = s.dMonthChart();

		int countPage = 5;
		int start_no = (Integer.parseInt(page) - 1) * countPage;
		int countPageNo = start_no + countPage;
		int count = s.dread();
		int page_no = (int) Math.ceil((double) count / countPage);

		List<LoginDTO> defect_report = s.defect_report(start_no, countPageNo);

		if (!defect_report.isEmpty()) {
			LoginDTO d1 = defect_report.get(0);
			session.setAttribute("d1", d1);
		}

		session.setAttribute("list", list);
		session.setAttribute("dMonthChart", dMonthChart);
		session.setAttribute("page_no", page_no);
		session.setAttribute("defect_report", defect_report);

		return "P10_report/defectReporting";
	}
}
