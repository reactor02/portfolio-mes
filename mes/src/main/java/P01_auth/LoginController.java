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
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private LoginService service;

	@GetMapping
	public String doGet(Model model) {
		System.out.println("/login doget 실행");

		//로그인으로
		return "P01_auth/login";
	}

	@PostMapping
	public String doPost(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println("/login dopost 실행");

		// 이 값이 있다면 로그인
		String id = request.getParameter("login_id");
		String pw = request.getParameter("login_pw");

		System.out.println("/login doget.login 실행");

		//비밀번호 SHA-256 암호화
		System.out.println("암호화 비밀번호 확인 : " + service.encrypt(pw));

		// 데이터 바구니 소환
		LoginDTO d = new LoginDTO();

		// 값 저장
		d.setEmpid(id);
		d.setPassword(service.encrypt(pw));

		// 로그인 함수 소환후 함수 작동결과를 변수에 담기
		List<LoginDTO> list = service.login(d);

		// 로그인 함수 작동 결과 값이 있다면
		if (list.size() == 1) {
			System.out.println("로그인 성공");

			// 세션 소환
			HttpSession session = request.getSession();

			session.setAttribute("dto", list.get(0));
			session.setAttribute("login", "true");
			session.setAttribute("auth", list.get(0).getAuth());

			return "redirect:/dashboard";

			// 아이디와 패스워드가 없다면. 로그인 페이지로.
		} else if ((id == null) && (pw == null)) {

			System.out.println("login 페지이에 오신것을 환영합니다!");

			model.addAttribute("error", "로그인 페이지에 오신것을 환영합니다!");

			return "P01_auth/login";

			// 없다면 로그인 실패 메세지와 함께 로그인 페이지로.
		} else {
			System.out.println("로그인 실패");
			model.addAttribute("error", "아이디 또는 비밀번호가 일치하지 않습니다.");
			model.addAttribute("empid", id);
			return "P01_auth/login";
		}
	}

}
