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
@RequestMapping("/mypage")
public class MypageController {

	@Autowired
	private LoginService service;

	@GetMapping
	public String doGet(HttpServletRequest request, Model model) {
		System.out.println("/mypage doget 실행");

		// 세션 소환
		HttpSession session = request.getSession();

		// 세션에서 값 꺼내기 (로그인 시 저장했던 이름으로)
		LoginDTO l = (LoginDTO) session.getAttribute("dto");
		String empid = l.getEmpid();

		if (empid != null) {
			System.out.println("현재 로그인된 아이디: " + empid);
		}

		//Paging 페이징
		String page = request.getParameter("mywork_btn");
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
		int count = service.wread(empid);

		int page_no = (int) Math.ceil((double) count / countPage);
		System.out.println("page_no : " + page_no);

		// 함수 소환 후 결과를 리스트에 저장.
		List<LoginDTO> mywork = service.mywork(empid, start_no, countPageNo);

		// 세션으로 보내기
		session.setAttribute("page_no", page_no);
		session.setAttribute("mywork", mywork);

		// 마이페이지로
		return "P01_auth/mypage";
	}

	@PostMapping
	public String doPost(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println("/mypage dopost 실행");

		// 이 값들이 있다면 정보 수정으로
		String mp_empid = request.getParameter("mp_empid");
		String mp_name = request.getParameter("mp_name");
		String mp_phone = request.getParameter("mp_phone");
		String mp_pw = request.getParameter("mp_pw");
		String mp_pw2 = request.getParameter("mp_pw2");

		// 정보 수정 로직
		if (!(mp_name == null && "".equals(mp_name)) || !(mp_phone == null && "".equals(mp_phone))
				|| !(mp_pw == null && "".equals(mp_pw))) {
			System.out.println("/login doget.edit 실행");

			// 만약 null이 아니라면 공백 제거 후 덮어씌우기
			if (mp_empid != null)
				mp_empid = mp_empid.trim();
			if (mp_name != null)
				mp_name = mp_name.trim();
			if (mp_phone != null)
				mp_phone = mp_phone.trim();
			if (mp_pw != null)
				mp_pw = mp_pw.trim();
			if (mp_pw2 != null)
				mp_pw2 = mp_pw2.trim();

			// 데이터 바구니 소환
			LoginDTO d = new LoginDTO();
			long phone = 0;

			// empid 바구니에 넣기.
			d.setEmpid(mp_empid);

			// 값이 있고, 길이가 0이 아니라면 바구니에 넣기.
			// 이름
			if (mp_name != null && mp_name.length() > 0) {
				d.setEname(mp_name);
			}
			// 연락처
			if (mp_phone != null && mp_phone.length() > 0) {
				phone = Integer.parseInt(mp_phone);
				d.setPhone(phone);
			}
			// 비밀번호
			if ((mp_pw != null && mp_pw2 != null) && mp_pw.equals(mp_pw2) && mp_pw.length() > 0) {

				//비밀번호 SHA-256 암호화
				System.out.println("암호화 비밀번호 확인 : " + service.encrypt(mp_pw));

				d.setPassword(service.encrypt(mp_pw));

			} else if ((mp_pw != null && mp_pw2 != null) && !mp_pw.equals(mp_pw2) && mp_pw.length() > 0) {
				// 비밀번호가 일치하지 않으면 null 실행
				d.setPassword(null);

				// 세션 소환
				HttpSession session = request.getSession();

				System.out.println("비밀번호가 서로 일치하지 않습니다.");
				model.addAttribute("error", "비밀번호가 서로 일치하지 않습니다. 비밀번호 변경에 실패했습니다.");
			}

			if (service.edit(d) > 0) {
				System.out.println("정보수정이 완료 되었습니다.");
			} else {
				System.out.println("정보수정에 실패 했습니다.");
			}
			return "redirect:/mypage";
		}

		return "redirect:/mypage";
	}

}
