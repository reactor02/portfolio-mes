package P00_layout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logout")
public class LogOutController {

	@GetMapping
	public String doGet(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("/logout doGet 실행");

		HttpSession session = request.getSession(false); // 기존 세션만 가져오기

		if (session != null) {
			session.invalidate(); // 세션 완전 삭제
		}

		return "redirect:/login";
	}

	@PostMapping
	public String doPost(HttpServletRequest request, HttpServletResponse response) {
		return doGet(request, response);
	}

}
