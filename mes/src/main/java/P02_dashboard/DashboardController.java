package P02_dashboard;

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
@RequestMapping("/dashboard")
public class DashboardController {

	@Autowired
	private LoginService s;

	@GetMapping
	public String doGet(
			@RequestParam(value = "n_btn", defaultValue = "1") String npage,
			@RequestParam(value = "s_btn", defaultValue = "1") String spage,
			@RequestParam(value = "a_btn", defaultValue = "1") String apage,
			HttpSession session) {

		System.out.println("dashboard의 doget 실행");

		// Paging (notice)
		int ncountPage = 5;
		int nstart_no = (Integer.parseInt(npage) - 1) * ncountPage;
		int ncountPageNo = nstart_no + ncountPage;
		int ncount = s.nread();
		int npage_no = (int) Math.ceil((double) ncount / ncountPage);

		// Paging (suggestion)
		int scountPage = 5;
		int sstart_no = (Integer.parseInt(spage) - 1) * scountPage;
		int scountPageNo = sstart_no + scountPage;
		int scount = s.sread();
		int spage_no = (int) Math.ceil((double) scount / scountPage);

		// 세션에서 로그인 사용자 정보
		LoginDTO l = (LoginDTO) session.getAttribute("dto");
		String empid = l.getEmpid();

		// Paging (alarms)
		int acountPage = 5;
		int astart_no = (Integer.parseInt(apage) - 1) * acountPage;
		int acountPageNo = astart_no + acountPage;
		int acount = s.aread(empid);
		int apage_no = (int) Math.ceil((double) acount / scountPage);

		// 데이터 조회
		List<DashDTO> list = s.defect();
		List<DashDTO> work_order = s.work_order();
		List<DashDTO> alarms = s.a(empid, astart_no, acountPageNo);
		List<DashDTO> notice = s.notice(nstart_no, ncountPageNo);
		List<DashDTO> suggestion = s.suggestion(empid, sstart_no, scountPageNo);

		// 세션에 담기
		session.setAttribute("list", list);
		session.setAttribute("npage_no", npage_no);
		session.setAttribute("notice", notice);
		session.setAttribute("work", work_order);
		session.setAttribute("apage_no", apage_no);
		session.setAttribute("alarms", alarms);
		session.setAttribute("spage_no", spage_no);
		session.setAttribute("suggestion", suggestion);

		return "P02_dashboard/dashboard";
	}
}
