package P01_auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pdetail")
public class PermissionDetailController {

	@Autowired
	private LoginService service;

	@GetMapping
	public String doGet(HttpServletRequest request) {
		System.out.println("permission의 doget 실행");

		// Paging 페이징
		String empid = request.getParameter("empid");

		LoginDTO d = service.detail(empid);
		List<LoginDTO> selecta = service.selecta();

		List<LoginDTO> selectd = service.selectd();

		// 세션 소환
		HttpSession session = request.getSession();

		// 세션으로 보내기
		session.setAttribute("d", d);
		session.setAttribute("selecta", selecta);
		session.setAttribute("selectd", selectd);

		return "P01_auth/perdetail";
	}

	@PostMapping
	public String doPost(HttpServletRequest request, HttpServletResponse response, Model model) {
		// 이 값들이 있다면 비밀번호 변경으로
		String permission = request.getParameter("permission");
		String changeAuth = request.getParameter("change-auth");
		String changeDept = request.getParameter("dept");

		int realPermission = -1;

		System.out.println("/pdtail doget.pdtail 실행");

		System.out.println(" 권한 : " + permission);
		System.out.println(" 아이디 : " + changeAuth);
		System.out.println(" 부서번호 : " + changeDept);

		LoginDTO d = service.detail(changeAuth);

		// 세션 소환
		HttpSession session = request.getSession();

		// 권한
		if (realPermission != -1) {

			if ("권한없음".equals(permission))
				realPermission = 0;
			if ("작업자".equals(permission))
				realPermission = 1;
			if ("관리자".equals(permission))
				realPermission = 2;
			if ("슈퍼바이저".equals(permission))
				realPermission = 3;

			System.out.println("권한 레벨 확인 : " + realPermission);

			// empid 바구니에 넣기.
			d.setAuth(realPermission);
			d.setEmpid(changeAuth);

			if (realPermission == 0) {
				int retire = 0;

				d.setRetire(retire);

				service.retire(d);

				model.addAttribute("error", "퇴사처리가 완료되었습니다!");

			} else {

				if (service.changeAuth(d) > 0) {

					// 세션으로 보내기
					System.out.println("권한 변경이 완료 되었습니다.");
					model.addAttribute("error", "권한 변경이 완료되었습니다!");
				} else {

					// 세션으로 보내기
					System.out.println("권한 변경에 실패 했습니다.");
					model.addAttribute("error", "권한 변경에 실패했습니다. 다시 시도해주세요.");
				}
			}
		}
		// 부서
		if (changeDept != null && !"".equals(changeDept)) {

			int deptno = 0;

			if ("Production".equals(changeDept))
				deptno = 10;
			if ("QC".equals(changeDept))
				deptno = 20;
			if ("admin".equals(changeDept))
				deptno = 30;

			System.out.println("변경 부서 확인 : " + deptno);

			// empid 바구니에 넣기.
			d.setDept_no(deptno);
			d.setEmpid(changeAuth);
		}

		if (service.changeDept(d) > 0) {

			// 세션으로 보내기
			System.out.println("부서 변경이 완료 되었습니다.");
			session.setAttribute("error1", "부서 변경이 완료되었습니다!");
		} else {

			// 세션으로 보내기
			System.out.println("부서 변경에 실패 했습니다.");
			session.setAttribute("error1", "부서 변경에 실패했습니다. 다시 시도해주세요.");
		}

		return "redirect:/pdetail";
	}

}
