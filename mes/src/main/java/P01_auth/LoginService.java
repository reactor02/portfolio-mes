package P01_auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.List;

import org.springframework.stereotype.Service;

import P02_dashboard.DashDTO;
import P10_report.ReportDTO;

@Service
public class LoginService {
	
	public List<LoginDTO> login(LoginDTO d) {
		System.out.println("/login service.login() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.login(d);				
	}
	
	public int edit(LoginDTO d) {
		System.out.println("/login service.edit() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//일단 수정요청자의 각 정보들을 조회해서 호출
		LoginDTO dto = a.editCheck(d);
		
		System.out.println(dto);
		System.out.println(d);
		
		
		//controller에서 	받아온 값이 null이거나 ""이라면 기존 값 덮어 씌우기	
		if(   d.getEname() == null ||
				( d.getEname() == null && d.getEname().trim().length() == 0 ) ) {
			d.setEname(dto.getEname());			
		} 
		
		if(d.getPassword() == null 
				|| (d.getPassword() != null && d.getPassword().trim().length() == 0) ) {
			d.setPassword(dto.getPassword());
		} 
		
		if( d.getPhone() == 0 ) {
			d.setPhone(dto.getPhone());			
		} 
		
		//정보수정 실행 결과 리턴.
		return a.edit(d);
		
		
	}
	
	public int changepw(LoginDTO d) {
		System.out.println("/login service.changepw() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		
		
		//정보수정 실행 결과 리턴.
		return a.changepw(d);
		
		
	}
	
	public int changeAuth(LoginDTO d) {
		System.out.println("/login service.changeAuth() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		
		
		//정보수정 실행 결과 리턴.
		return a.changeAuth(d);
		
		
	}
	
	public int changeDept(LoginDTO d) {
		System.out.println("/login service.changeDept() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		
		
		//정보수정 실행 결과 리턴.
		return a.changeDept(d);
		
		
	}
	public int retire(LoginDTO d) {
		System.out.println("/login service.retire() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		
		
		//정보수정 실행 결과 리턴.
		return a.retire(d);
		
		
	}
	
	public LoginDTO detail(String empid) {
		System.out.println("/login service.detail() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();		
		
		//정보수정 실행 결과 리턴.
		return a.detail(empid);
		
		
	}
	
	
	public int permission(LoginDTO d) {
		System.out.println("/login service.permission() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();		
		
		//정보수정 실행 결과 리턴.
		return a.permission(d);
		
		
	}
	
	
	public List join(LoginDTO d) {
		System.out.println("/login service.join() 실행 ");
		
		//바구니 소환
		List list = new ArrayList();
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//회원가입
		list.add(a.join(d));
		
		//회원가입 후 생성되는 empno를 비롯한 다른 것들.
	    list.add(a.empno(d));
	     
	     return list;
	}
	
	
	public int readEmp() {
		System.out.println("/login service.readEmp() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.readEmp();				
	}
	
	public List<LoginDTO> paging(int start_no, int countPageNo ) {
		System.out.println("/login service.paging() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.paging(start_no, countPageNo);				
	}
	
	
	public List<DashDTO> defect() {
		System.out.println("/dashboard service.defect() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.defect();				
	}
	
	
	public int nread() {
		System.out.println("/login service.nread() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.nread();				
	}
	
	public List<DashDTO> notice(int nstart_no, int ncountPageNo) {
		System.out.println("/dashboard service.notice() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.notice(nstart_no, ncountPageNo);				
	}
	
	public int sread() {
		System.out.println("/login service.sread() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.sread();				
	} 
	
	
	public List<DashDTO> suggestion(String empid, int sstart_no, int scountPageNo) {
		System.out.println("/dashboard service.suggestion() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.suggestion(empid, sstart_no, scountPageNo);				
	}
	
	public List<DashDTO> work_order() {
		System.out.println("/dashboard service.work_order() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.work_order();				
	}
	
	public int aread(String empid) {
		System.out.println("/login service.aread() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.aread(empid);				
	}
	
	public List<DashDTO> a(String empid, int astart_no, int acountPageNo) {
		System.out.println("/dashboard service.a() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.a(empid, astart_no, acountPageNo);				
	}
	
	public int wread(String empid) {
		System.out.println("/login service.wread() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.wread(empid);				
	}
	
	public List<LoginDTO> mywork(String empid, int astart_no, int acountPageNo) {
		System.out.println("/dashboard service.mywork() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.mywork(empid, astart_no, acountPageNo);				
	}
	
	
	
	
	public int dread() {
		System.out.println("/login service.dread() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.dread();				
	}
	
	public List<LoginDTO> defect_report(int astart_no, int acountPageNo) {
		System.out.println("/dashboard service.defect_report() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.defect_report(astart_no, acountPageNo);				
	}
	
	
	
	public List<LoginDTO> dMonthChart() {
		System.out.println("/dashboard service.dMonthChart() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.dMonthChart();				
	}
	
	
	public List<LoginDTO> selectd() {
		System.out.println("/dashboard service.d() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.selectd();				
	}
	public List<LoginDTO> selectm() {
		System.out.println("/dashboard service.selectm() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.selectm();				
	}
	public List<LoginDTO> selecta() {
		System.out.println("/dashboard service.selecta() 실행 ");
		
		//실무 함수 소환
		LoginDAO a = new LoginDAO();
		
		//로그인 함수 실행결과 리턴
		return a.selecta();				
	}
	
	
	
	// 1. 목표 대비 달성률(sysdate)
	public List<ReportDTO> date() {
	    System.out.println("/dashboard service.date() 실행 ");
	    LoginDAO a = new LoginDAO();
	    return a.date();
	}

	// 2. 공정 양품률(sysdate)
	public List<ReportDTO> clear() {
	    System.out.println("/dashboard service.clear() 실행 ");
	    LoginDAO a = new LoginDAO();
	    return a.clear();
	}
	
	// 1. 목표 대비 달성률(sysdate)
		public ReportDTO sumdate() {
		    System.out.println("/dashboard service.sumdate() 실행 ");
		    LoginDAO a = new LoginDAO();
		    return a.sumdate();
		}
		// 1. 목표 대비 달성률(sysdate)
		public List<ReportDTO> sumclear() {
			System.out.println("/dashboard service.sumclear() 실행 ");
			LoginDAO a = new LoginDAO();
			return a.sumclear();
		}
		// 1. topfive
		public List<ReportDTO> topfive() {
			System.out.println("/dashboard service.topfive() 실행 ");
			LoginDAO a = new LoginDAO();
			return a.topfive();
		}
		// 1. errormch
		public List<ReportDTO> errormch(int start_no, int countPageNo) {
			System.out.println("/dashboard service.errormch() 실행 ");
			LoginDAO a = new LoginDAO();
			return a.errormch(start_no, countPageNo);
		}
		
		// 1. 목표 대비 달성률(sysdate)
		public ReportDTO press() {
		    System.out.println("/dashboard service. press() 실행 ");
		    LoginDAO a = new LoginDAO();
		    return a.press();
		}


//
//	// 5. 설비 실시간 가동 현황
//	public List<ReportDTO> mchdate() {
//	    System.out.println("/dashboard service.mchdate() 실행 ");
//	    LoginDAO a = new LoginDAO();
//	    return a.mchdate();
//	}
//
//	// 7. 실시간 공정이슈 및 시정조치
//	public List<ReportDTO> errormch() {
//	    System.out.println("/dashboard service.errormch() 실행 ");
//	    LoginDAO a = new LoginDAO();
//	    return a.errormch();
//	}
//	
	
	
	
	public static String encrypt(String password) {
	    try {
	        // 1. 암호화 도구 준비 (알고리즘 선택)
	        MessageDigest md = MessageDigest.getInstance("SHA-256");

	        // 2. 재료 손질 (문자열 -> 바이트 배열)
	        byte[] passBytes = password.getBytes();

	        // 3. 암호화 실행 (해싱)
	        md.update(passBytes);
	        byte[] hashBytes = md.digest();

	        // 4. 결과물 포장 (바이트 -> 16진수 문자열)
	        StringBuilder sb = new StringBuilder();
	        for (byte b : hashBytes) {
	            sb.append(String.format("%02x", b));
	        }

	        // 5. 완제품 반환
	        return sb.toString();

	    } catch (NoSuchAlgorithmException e) {
	        return null; // 알고리즘 이름이 틀렸을 때 예외 처리
	    }
	}

	
	
	
	

}
