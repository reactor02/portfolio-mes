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
@RequestMapping("/join")
public class JoinController {

	@Autowired
	private LoginService service;

	@GetMapping
	public String doGet(Model model) {
		System.out.println("/join doget 실행");

		List<LoginDTO> selectd = service.selectd();
		List<LoginDTO> selectm = service.selectm();

		model.addAttribute("selectd", selectd);
		model.addAttribute("selectm", selectm);

		// 회원가입으로
		return "P01_auth/join";
	}

	@PostMapping
	public String doPost(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println("/join dopost 실행");

		// 이 값들이 있다면 회원가입
		String join_name = request.getParameter("join_name");
		String join_phone = request.getParameter("join_phone");
		String join_dept = request.getParameter("join_dept");
		String join_license = request.getParameter("join_license");
		String join_mgr = request.getParameter("join_mgr");
		String join_pw = request.getParameter("join_pw");
		String join_pw2 = request.getParameter("join_pw2");

		// 데이터 확인용 로그 추가
		System.out.println("name: " + join_name);
		System.out.println("pw: " + join_pw);
		System.out.println("pw2: " + join_pw2);
		System.out.println("mgr: " + join_mgr);

		long phone = 0;
		if (join_phone != null) {
			phone = Long.parseLong(join_phone);
		}

		// 회원가입 로직
		if (join_name != null && join_pw != null && join_pw2 != null && join_mgr != null) {
			System.out.println("/login doget.join 실행");

			// 비밀번호 SHA-256 암호화
			System.out.println("암호화 비밀번호 확인 : " + service.encrypt(join_pw));

			// 데이터 바구니 소환
			LoginDTO d = new LoginDTO();
			d.setEname(join_name);
			d.setPhone(phone);
			d.setDeptno(join_dept);
			d.setMgr(join_mgr);
			d.setLicense(join_license);
			d.setPassword(service.encrypt(join_pw));
			d.setPassword2(join_pw2);

			List list = service.join(d);

			if (!list.isEmpty() && (int) list.get(0) > 0) {
				System.out.println("회원가입이 완료되었습니다!");

				// 세션 소환
				HttpSession session = request.getSession();

				session.setAttribute("joinResult", (LoginDTO) list.get(1));

				return "redirect:/joinResult";

			} else {
				System.out.println("회원가입에 실패했습니다. 값을 모두 입력해주세요.");

				// 에러값과 함께 회원가입 페이지로 추방
				model.addAttribute("error", "회원가입에 실패했습니다. 다시.");
				return "P01_auth/join";
			}
		}

		return "P01_auth/join";
	}

}
