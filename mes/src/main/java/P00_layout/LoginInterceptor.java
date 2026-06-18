package P00_layout;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String path = request.getRequestURI().substring(request.getContextPath().length());

        HttpSession session = request.getSession(false);

        // 세션 없으면 로그인 페이지로
        if (session == null) {
            alertAndRedirect(response, request.getContextPath() + "/login", "로그인 후 이용 가능합니다.");
            return false;
        }

        String login = (String) session.getAttribute("login");

        // 로그인 안 된 경우
        if (!"true".equals(login)) {
            alertAndRedirect(response, request.getContextPath() + "/login", "로그인 후 이용 가능합니다.");
            return false;
        }

        // 권한 체크
        Integer auth = (Integer) session.getAttribute("auth");
        if (auth == null) {
            alertAndBack(response, "권한 정보가 없습니다. 다시 로그인해주세요.");
            return false;
        }

        int requiredAuth = getRequiredAuth(path);
        if (requiredAuth > 0 && auth < requiredAuth) {
            alertAndBack(response, "권한이 없어 접근할 수 없습니다.");
            return false;
        }

        return true;
    }

    private int getRequiredAuth(String path) {
        if ("/workadd".equals(path) || "/womodify".equals(path)
                || "/qcadd".equals(path) || "/qcmodify".equals(path)
                || "/notice/register".equals(path) || "/notice/edit".equals(path)) {
            return 2;
        }
        if ("/permission".equals(path) || "/pdetail".equals(path) || "/join".equals(path)) {
            return 3;
        }
        return 0;
    }

    private void alertAndRedirect(HttpServletResponse resp, String moveUrl, String msg) throws Exception {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<script>alert('" + escapeJs(msg) + "'); location.href='" + moveUrl + "';</script>");
        out.flush();
    }

    private void alertAndBack(HttpServletResponse resp, String msg) throws Exception {
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<script>alert('" + escapeJs(msg) + "'); history.back();</script>");
        out.flush();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) throws Exception {
    }

    private String escapeJs(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("'", "\\'").replace("\"", "\\\"")
                  .replace("\r", "").replace("\n", "\\n");
    }
}
