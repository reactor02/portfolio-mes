package P00_layout;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// @WebFilter("/*") — disabled: replaced by Spring LoginInterceptor in spring-mvc.xml
public class LoginAuthFilter implements Filter {

    public LoginAuthFilter() {
        System.out.println("LoginFilter 생성자 실행");
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        System.out.println("LoginFilter init 실행");
    }

    @Override
    public void destroy() {
        System.out.println("LoginFilter 소멸자 실행");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("doFilter 실행 전");

        request.setCharacterEncoding("utf-8");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());
        System.out.println("요청 경로 : " + path);

        // 로그인 없이 접근 가능한 경로
        if (isExclude(path)) {
            System.out.println("세션 없이 진행");
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);

        // 세션 없으면 로그인 페이지로
        if (session == null) {
            System.out.println("세션 없음 -> 로그인 페이지 이동");
//            resp.sendRedirect(req.getContextPath() + "/login");
            alertAndRedirect(resp, req.getContextPath() + "/login", "로그인 후 이용 가능합니다.");
            return;
        }

        String login = (String) session.getAttribute("login");

        // 로그인 안 되어 있으면 로그인 페이지로
        if (!"true".equals(login)) {
            System.out.println("로그인 [FAIL] : [" + path + "]");
//            resp.sendRedirect(req.getContextPath() + "/login");
            alertAndRedirect(resp, req.getContextPath() + "/login", "로그인 후 이용 가능합니다.");
            return;
        }

        // 권한 체크
        Integer auth = (Integer) session.getAttribute("auth");
        if (auth == null) {
            System.out.println("auth 없음 -> 접근 불가");
//            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "권한 정보가 없습니다.");
            alertAndBack(resp, "권한 정보가 없습니다. 다시 로그인해주세요.");
            return;
        }

        int requiredAuth = getRequiredAuth(path);

        if (requiredAuth > 0 && auth < requiredAuth) {
            System.out.println("권한 부족 : 현재 auth=" + auth + ", 필요 auth=" + requiredAuth + ", path=" + path);
//            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "접근 권한이 없습니다.");
            alertAndBack(resp, "권한이 없어 접근할 수 없습니다.");
            return;
        }

        System.out.println("로그인/권한 [OK] 계속 진행");
        chain.doFilter(request, response);

        System.out.println("doFilter 실행 후");
    }

    private boolean isExclude(String path) {
        return "/login".equals(path)
                || "/changepw".equals(path)
                || path.startsWith("/static")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith("/joinResult.jsp");
    }

    private int getRequiredAuth(String path) {

        // 관리자 이상(auth >= 2)
        if ("/workadd".equals(path)
                || "/womodify".equals(path)
                || "/qcadd".equals(path)
                || "/qcmodify".equals(path)
                || "/notice/register".equals(path)
                || "/notice/edit".equals(path)) {
            return 2;
        }

        // 슈퍼바이저 이상(auth >= 3)
        if ("/permission".equals(path)
                || "/pdetail".equals(path)
                || "/join".equals(path)) {
            return 3;
        }

        // 제한 없음
        return 0;
    }

    private void alertAndRedirect(HttpServletResponse resp, String moveUrl, String msg) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        out.println("<script>");
        out.println("alert('" + escapeJs(msg) + "');");
        out.println("location.href='" + moveUrl + "';");
        out.println("</script>");
        out.flush();
    }

    private void alertAndBack(HttpServletResponse resp, String msg) throws IOException {
        resp.setContentType("text/html; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();
        out.println("<script>");
        out.println("alert('" + escapeJs(msg) + "');");
        out.println("history.back();");
        out.println("</script>");
        out.flush();
    }

    private String escapeJs(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("'", "\\'")
                  .replace("\"", "\\\"")
                  .replace("\r", "")
                  .replace("\n", "\\n");
    }
}