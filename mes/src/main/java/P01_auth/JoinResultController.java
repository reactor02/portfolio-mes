package P01_auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/joinResult")
public class JoinResultController {

	@GetMapping
	public String doGet() {
		System.out.println("/join doget 실행");

		// 회원가입결과로
		return "P01_auth/joinResult";
	}

}
