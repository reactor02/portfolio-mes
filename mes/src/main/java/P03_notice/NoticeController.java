package P03_notice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    // 파일 저장 경로
    private static final String UPLOAD_PATH =
        "C:\\workspace_proj2\\mes\\src\\main\\webapp\\static\\upload\\notice";

    @Autowired
    NoticeService noticeService;

    // ── 공통: 세션에서 loginId 추출 ──────────────────────────────────────────
    private String getLoginId(HttpServletRequest request) {
        Object sessionDto = request.getSession().getAttribute("dto");
        String loginId = "";
        if (sessionDto != null) {
            try {
                loginId = (String) sessionDto.getClass().getMethod("getEmpid").invoke(sessionDto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return loginId;
    }

    // ── GET /notice/list ─────────────────────────────────────────────────────
    @GetMapping("/list")
    public String list(HttpServletRequest request, Model model) throws IOException {

        int size = 10;
        int page = 1;
        try { size = Integer.parseInt(request.getParameter("size")); } catch (Exception e) {}
        try { page = Integer.parseInt(request.getParameter("page")); } catch (Exception e) {}

        String keyword = request.getParameter("keyword");

        NoticeDTO noticeDTO = new NoticeDTO();
        noticeDTO.setSize(size);
        noticeDTO.setPage(page);
        noticeDTO.setKeyword(keyword);

        Map map = noticeService.getListNotice(noticeDTO);
        map.put("size",    size);
        map.put("page",    page);
        map.put("keyword", keyword);

        int totalCount    = (int) map.get("totalCount");
        int totalPages    = (int) Math.ceil((double) totalCount / size);
        if (totalPages < 1) totalPages = 1;

        int pageGroupSize  = 5;
        int currentGroup   = (int) Math.ceil((double) page / pageGroupSize);
        int groupStartPage = (currentGroup - 1) * pageGroupSize + 1;
        int groupEndPage   = Math.min(groupStartPage + pageGroupSize - 1, totalPages);

        map.put("totalPages",     totalPages);
        map.put("groupStartPage", groupStartPage);
        map.put("groupEndPage",   groupEndPage);

        model.addAttribute("map", map);
        return "P03_notice/noticeList";
    }

    // ── GET /notice/detail ───────────────────────────────────────────────────
    @GetMapping("/detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {

        String loginId = getLoginId(request);
        String boardno = request.getParameter("boardno");

        if (boardno == null || boardno.trim().isEmpty()) {
            return "redirect:/notice/list";
        }

        NoticeDTO dto = noticeService.selectOneNotice(boardno);

        if (dto == null) {
            return "redirect:/notice/list";
        }

        // 쿠키로 조회수 중복 방지
        String cookieName = "notice_" + boardno + "_" + loginId;

        boolean viewed = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals(cookieName)) {
                    viewed = true;
                    break;
                }
            }
        }

        if (!viewed) {
            noticeService.updateViews(boardno);
            Cookie cookie = new Cookie(cookieName, "1");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
            dto.setViews(dto.getViews() + 1);
        }

        model.addAttribute("noticeDTO", dto);
        model.addAttribute("page", request.getParameter("page"));
        model.addAttribute("size", request.getParameter("size"));
        return "P03_notice/noticeDetail";
    }

    // ── GET /notice/register ─────────────────────────────────────────────────
    @GetMapping("/register")
    public String registerForm() {
        return "P03_notice/noticeRegister";
    }

    // ── POST /notice/register ────────────────────────────────────────────────
    @PostMapping("/register")
    public String registerSubmit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String loginId = getLoginId(request);
        NoticeDTO dto = new NoticeDTO();
        dto.setEmpId(loginId);

        try {
            // 업로드 폴더 없으면 생성
            File uploadDir = new File(UPLOAD_PATH);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(uploadDir);
            factory.setSizeThreshold(1024 * 1024);

            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(1024 * 1024 * 10); // 10MB

            // ★ getParameter() 보다 먼저 parseRequest() 해야 함
            List<FileItem> items = upload.parseRequest(request);

            for (FileItem fileItem : items) {
                if (fileItem.isFormField()) {
                    // 일반 텍스트 필드
                    String fieldName = fileItem.getFieldName();
                    String value     = fileItem.getString("UTF-8");

                    if ("title".equals(fieldName))   dto.setTitle(value);
                    if ("content".equals(fieldName)) dto.setContent(value);

                } else {
                    // 첨부파일
                    if (fileItem.getSize() > 0) {
                        String originName = fileItem.getName(); // 원본 파일명
                        String saveName   = System.currentTimeMillis() + "_" + originName;

                        fileItem.write(new File(uploadDir + "\\" + saveName));

                        dto.setOriginName(originName);
                        dto.setSaveName(saveName);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        noticeService.insertNotice(dto);
        return "redirect:/notice/list?page=1";
    }

    // ── GET /notice/edit ─────────────────────────────────────────────────────
    @GetMapping("/edit")
    public String editForm(HttpServletRequest request, Model model) {

        String boardno = request.getParameter("boardno");
        NoticeDTO dto = noticeService.selectOneNotice(boardno);
        model.addAttribute("noticeDTO", dto);
        return "P03_notice/noticeEdit";
    }

    // ── POST /notice/edit ────────────────────────────────────────────────────
    @PostMapping("/edit")
    public String editSubmit(HttpServletRequest request) {

        NoticeDTO dto = new NoticeDTO();
        dto.setBoardno( request.getParameter("boardno") );
        dto.setTitle(   request.getParameter("title") );
        dto.setContent( request.getParameter("content") );

        noticeService.updateNotice(dto);
        return "redirect:/notice/detail?boardno=" + dto.getBoardno();
    }

    // ── POST /notice/delete ──────────────────────────────────────────────────
    @PostMapping("/delete")
    public String delete(HttpServletRequest request) {

        String boardno = request.getParameter("boardno");
        noticeService.deleteNotice(boardno);
        return "redirect:/notice/list?page=1";
    }

    // ── GET /notice/download ─────────────────────────────────────────────────
    // 요청 예: /notice/download?save=밀리초_파일명&origin=원본파일명
    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String saveName   = request.getParameter("save");
        String originName = request.getParameter("origin");

        if (saveName == null || saveName.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/notice/list");
            return;
        }

        File file = new File(UPLOAD_PATH + "\\" + saveName);

        if (!file.exists()) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('파일을 찾을 수 없습니다.'); history.back();</script>");
            return;
        }

        // 브라우저 캐시 미사용
        response.setHeader("Cache-Control", "no-cache");
        // 다운로드 헤더 - 원본 파일명으로 저장되도록
        String encodedName = new String(originName.getBytes("UTF-8"), "ISO-8859-1");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
        response.setContentLengthLong(file.length());

        byte[] buf = new byte[1024 * 8];
        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            int count;
            while ((count = is.read(buf)) != -1) {
                os.write(buf, 0, count);
            }
            os.flush();
        }
    }
}
