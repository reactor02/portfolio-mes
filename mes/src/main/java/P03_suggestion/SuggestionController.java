package P03_suggestion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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

import P01_auth.LoginDTO;

@Controller
@RequestMapping("/suggestion")
public class SuggestionController {

    private static final String UPLOAD_PATH =
        "C:\\workspace_proj2\\mes\\src\\main\\webapp\\static\\upload\\suggestion";

    @Autowired
    SuggestionService suggestionService;

    // ── GET /suggestion/list ─────────────────────────────────────────────────
    @GetMapping("/list")
    public String list(HttpServletRequest request, Model model) throws IOException {

        LoginDTO loginDto = (LoginDTO) request.getSession().getAttribute("dto");

        int size = 10;
        int page = 1;
        try { size = Integer.parseInt(request.getParameter("size")); } catch (Exception e) {}
        try { page = Integer.parseInt(request.getParameter("page")); } catch (Exception e) {}

        String searchKeyword = request.getParameter("searchKeyword");
        if (searchKeyword != null) searchKeyword = searchKeyword.trim();

        SuggestionDTO dto = new SuggestionDTO();
        dto.setSize(size);
        dto.setPage(page);
        dto.setSearchKeyword(searchKeyword);

        Map<String, Object> map = suggestionService.getList(dto);
        map.put("size",          size);
        map.put("page",          page);
        map.put("searchKeyword", searchKeyword == null ? "" : searchKeyword);

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
        return "P03_suggestion/list";
    }

    // ── GET /suggestion/detail ───────────────────────────────────────────────
    @GetMapping("/detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model)
            throws IOException {

        LoginDTO loginDto = (LoginDTO) request.getSession().getAttribute("dto");
        String loginId = loginDto.getEmpid();
        int    auth    = loginDto.getAuth();

        String boardno = request.getParameter("boardno");

        SuggestionDTO detail = suggestionService.getDetail(boardno);

        if (detail == null) {
            return "redirect:/suggestion/list";
        }

        // 상세 접근 제한: 작성자 본인 또는 auth >= 2만 접근 가능
        boolean isOwner = loginId.equals(detail.getEmpId());
        if (!isOwner && auth < 2) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('조회 권한이 없습니다.'); location.href='" + request.getContextPath() + "/suggestion/list';</script>");
            return null;
        }
        // 댓글 입력 가능 여부: 작성자 본인 OR auth >= 2
        boolean canComment = isOwner || auth >= 2;

        // JS 10분 체크용 ctime epoch ms
        long ctimeMs = detail.getCtimeKstMs();

        List<CommentDTO> commentList = suggestionService.getCommentList(boardno);

        model.addAttribute("detail",      detail);
        model.addAttribute("commentList", commentList);
        model.addAttribute("isOwner",     isOwner);
        model.addAttribute("ctimeMs",     ctimeMs);
        model.addAttribute("canComment",  canComment);
        model.addAttribute("page",        request.getParameter("page"));
        model.addAttribute("size",        request.getParameter("size"));
        return "P03_suggestion/detail";
    }

    // ── GET /suggestion/register ─────────────────────────────────────────────
    @GetMapping("/register")
    public String registerForm() {
        return "P03_suggestion/register";
    }

    // ── POST /suggestion/register ────────────────────────────────────────────
    @PostMapping("/register")
    public String registerSubmit(HttpServletRequest request) throws IOException {

        LoginDTO loginDto = (LoginDTO) request.getSession().getAttribute("dto");
        String loginId = loginDto.getEmpid();

        SuggestionDTO insertDto = new SuggestionDTO();
        insertDto.setEmpId(loginId);

        try {
            File uploadDir = new File(UPLOAD_PATH);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(uploadDir);
            factory.setSizeThreshold(1024 * 1024);

            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(1024 * 1024 * 10);

            List<FileItem> items = upload.parseRequest(request);

            for (FileItem fileItem : items) {
                if (fileItem.isFormField()) {
                    String fieldName = fileItem.getFieldName();
                    String value     = fileItem.getString("UTF-8");
                    if ("title".equals(fieldName))   insertDto.setTitle(value);
                    if ("content".equals(fieldName)) insertDto.setContent(value);
                } else {
                    if (fileItem.getSize() > 0) {
                        String originName = fileItem.getName();
                        String saveName   = System.currentTimeMillis() + "_" + originName;
                        fileItem.write(new File(uploadDir + "\\" + saveName));
                        insertDto.setOriginName(originName);
                        insertDto.setSaveName(saveName);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        suggestionService.insert(insertDto);
        return "redirect:/suggestion/list?page=1";
    }

    // ── POST /suggestion/detail (complete 처리) ───────────────────────────────
    @PostMapping("/detail")
    public String detailPost(HttpServletRequest request) {

        String action        = request.getParameter("action");
        String detailBoardno = request.getParameter("boardno");

        if ("complete".equals(action)) {
            suggestionService.updateComplete(detailBoardno);
        }
        return "redirect:/suggestion/detail?boardno=" + detailBoardno;
    }

    // ── POST /suggestion/comment ─────────────────────────────────────────────
    @PostMapping("/comment")
    public String comment(HttpServletRequest request) {

        LoginDTO loginDto = (LoginDTO) request.getSession().getAttribute("dto");
        String loginId = loginDto.getEmpid();

        String commentBoardno = request.getParameter("boardno");
        String commentContent = request.getParameter("commentContent");
        String parentComno    = request.getParameter("parentComno");

        if (parentComno != null && parentComno.trim().isEmpty()) {
            parentComno = null;
        }

        // 세션에서 로그인한 사람 이름 꺼내서 작성자로 저장
        String writer = loginDto.getEname();

        suggestionService.insertComment(commentBoardno, commentContent, parentComno, writer);

        // 작성자 본인 외의 사람이 댓글 달면 complete 0 → 2(답변달림) 자동 변경
        SuggestionDTO commentTarget = suggestionService.getDetail(commentBoardno);
        if (commentTarget != null && !loginId.equals(commentTarget.getEmpId())) {
            suggestionService.updateCompleteToAnswered(commentBoardno);
        }

        return "redirect:/suggestion/detail?boardno=" + commentBoardno;
    }

    // ── POST /suggestion/delete ──────────────────────────────────────────────
    @PostMapping("/delete")
    public String delete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        LoginDTO loginDto = (LoginDTO) request.getSession().getAttribute("dto");
        String loginId = loginDto.getEmpid();

        String deleteBoardno = request.getParameter("boardno");

        // 삭제 권한 체크: 작성자 본인인지만 확인 (10분 체크는 JS에서 처리)
        SuggestionDTO deleteTarget = suggestionService.selectOne(deleteBoardno);
        boolean isOwner = deleteTarget != null && loginId.equals(deleteTarget.getEmpId());

        if (!isOwner) {
            return "redirect:/suggestion/detail?boardno=" + deleteBoardno;
        }

        suggestionService.delete(deleteBoardno);
        return "redirect:/suggestion/list?page=1";
    }

    // ── GET /suggestion/download ─────────────────────────────────────────────
    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String saveName   = request.getParameter("save");
        String originName = request.getParameter("origin");

        if (saveName == null || saveName.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/suggestion/list");
            return;
        }

        File file = new File(UPLOAD_PATH + "\\" + saveName);

        if (!file.exists()) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<script>alert('파일을 찾을 수 없습니다.'); history.back();</script>");
            return;
        }

        response.setHeader("Cache-Control", "no-cache");
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
