package P01_auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/permission")
public class PermissionController {

	@Autowired
	private LoginService service;

	@GetMapping
	public String doGet(HttpServletRequest request) {
		System.out.println("permission의 doget 실행");

		//Paging 페이징
		String page = request.getParameter("per_btn");
		System.out.println("page : " + page);

		//처음은 1로 스타트되게
		if (page == null) {
			page = "1";
		}
		// 한 페이지당 보여줄 개수
		int countPage = 5;

		//시작 번호 1이면 0. 2면 5. 3이면 10.
		int start_no = (Integer.parseInt(page) - 1) * countPage;

		System.out.println("start_no : " + start_no);

		int countPageNo = start_no + countPage;
		System.out.println(" countPageNo : " + countPageNo);

		// 함수 소환 후 결과(전체 사원수)를 인트에 저장.
		int count = service.readEmp();

		int page_no = (int) Math.ceil((double) count / countPage);
		System.out.println("page_no : " + page_no);

		// 함수 소환 후 결과를 리스트에 저장.
		List<LoginDTO> list = service.paging(start_no, countPageNo);

		// 세션 소환
		HttpSession session = request.getSession();

		// 세션으로 보내기
		session.setAttribute("page_no", page_no);
		session.setAttribute("list", list);

		return "P01_auth/permission";
	}

}
