package P03_suggestion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SuggestionService {

    @Autowired
    SuggestionDAO suggestionDAO;

    // 목록 조회 (페이지네이션 + 검색)
    public Map<String, Object> getList(SuggestionDTO dto) {
        int size  = dto.getSize();
        int page  = dto.getPage();
        int end   = size * page;
        int start = end - (size - 1);
        dto.setEnd(end);
        dto.setStart(start);

        List<SuggestionDTO> list = suggestionDAO.selectList(dto);
        int totalCount = suggestionDAO.selectTotal(dto);

        Map<String, Object> map = new HashMap<>();
        map.put("list",       list);
        map.put("totalCount", totalCount);
        return map;
    }

    // 단건 조회 + 조회수 증가
    public SuggestionDTO getDetail(String boardno) {
        suggestionDAO.updateViews(boardno);
        return suggestionDAO.selectOne(boardno);
    }

    // 단건 조회 (조회수 증가 없음 - 삭제 권한 체크 용도)
    public SuggestionDTO selectOne(String boardno) {
        return suggestionDAO.selectOne(boardno);
    }

    // 검토완료 처리 (2 → 1)
    public int updateComplete(String boardno) {
        return suggestionDAO.updateComplete(boardno);
    }

    // 댓글 등록 시 complete 0 → 2 자동 변경
    public void updateCompleteToAnswered(String boardno) {
        suggestionDAO.updateCompleteToAnswered(boardno);
    }

    // 등록
    public int insert(SuggestionDTO dto) {
        return suggestionDAO.insert(dto);
    }

    // 삭제
    public int delete(String boardno) {
        return suggestionDAO.delete(boardno);
    }

    // 댓글 목록 조회 (CONNECT BY 계층)
    public List<CommentDTO> getCommentList(String boardno) {
        return suggestionDAO.selectCommentList(boardno);
    }

    // 댓글 등록 (writer: 로그인한 사람 이름)
    public int insertComment(String boardno, String content, String parentComno, String writer) {
        return suggestionDAO.insertComment(boardno, content, parentComno, writer);
    }
}
