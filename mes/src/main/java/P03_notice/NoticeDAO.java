package P03_notice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeDAO {

    @Autowired
    private DataSource dataSource;

    private Connection getConn() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 목록 조회 (페이지네이션 + 키워드 검색)
    public List<NoticeDTO> selectAllNotice(NoticeDTO noticeDTO) {
        List<NoticeDTO> list = new ArrayList<>();

        String keyword = noticeDTO.getKeyword();
        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());

        String sql = "SELECT * FROM ("
                   + "  SELECT rownum AS rnum, n.* FROM ("
                   + "    SELECT a.boardno, a.title, a.content, a.ctime, a.mtime, a.views,"
                   + "           a.emp_id, a.deleted, a.origin_name, a.save_name, u.ename"
                   + "    FROM announcement a"
                   + "    LEFT JOIN user_info u ON a.emp_id = u.emp_id"
                   + "    WHERE a.deleted = 'N'"
                   + (hasKeyword ? " AND LOWER(a.title) LIKE LOWER(?)" : "")
                   + "    ORDER BY a.ctime DESC"
                   + "  ) n"
                   + ") WHERE rnum >= ? AND rnum <= ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int idx = 1;
            if (hasKeyword) {
                ps.setString(idx++, "%" + keyword.trim() + "%");
            }
            ps.setInt(idx++, noticeDTO.getStart());
            ps.setInt(idx,   noticeDTO.getEnd());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NoticeDTO dto = new NoticeDTO();
                    dto.setBoardno(  rs.getString("boardno") );
                    dto.setTitle(    rs.getString("title") );
                    dto.setContent(  rs.getString("content") );
                    dto.setCtime(    rs.getDate("ctime") );
                    dto.setMtime(    rs.getDate("mtime") );
                    dto.setViews(    rs.getInt("views") );
                    dto.setEmpId(    rs.getString("emp_id") );
                    dto.setEname(    rs.getString("ename") );
                    dto.setDeleted(  rs.getString("deleted") );
                    dto.setOriginName(rs.getString("origin_name") );
                    dto.setSaveName( rs.getString("save_name") );
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("NoticeDAO list.size: " + list.size());
        return list;
    }

    // 전체 건수 (검색 포함)
    public int selectNoticeTotal(String keyword) {
        int totalCount = 0;

        boolean hasKeyword = (keyword != null && !keyword.trim().isEmpty());

        String sql = "SELECT count(*) cnt FROM announcement"
                   + " WHERE deleted = 'N'"
                   + (hasKeyword ? " AND LOWER(title) LIKE LOWER(?)" : "");

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (hasKeyword) {
                ps.setString(1, "%" + keyword.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt("cnt");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalCount;
    }

    // 단건 조회
    public NoticeDTO selectOneNotice(String boardno) {
        NoticeDTO dto = null;

        String sql = "SELECT a.boardno, a.title, a.content, a.ctime, a.mtime, a.views,"
                   + "       a.emp_id, a.deleted, a.origin_name, a.save_name, u.ename"
                   + " FROM announcement a"
                   + " LEFT JOIN user_info u ON a.emp_id = u.emp_id"
                   + " WHERE a.boardno = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, boardno);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dto = new NoticeDTO();
                    dto.setBoardno(  rs.getString("boardno") );
                    dto.setTitle(    rs.getString("title") );
                    dto.setContent(  rs.getString("content") );
                    dto.setCtime(    rs.getDate("ctime") );
                    dto.setMtime(    rs.getDate("mtime") );
                    dto.setViews(    rs.getInt("views") );
                    dto.setEmpId(    rs.getString("emp_id") );
                    dto.setEname(    rs.getString("ename") );
                    dto.setDeleted(  rs.getString("deleted") );
                    dto.setOriginName(rs.getString("origin_name") );
                    dto.setSaveName( rs.getString("save_name") );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    // 등록
    public int insertNotice(NoticeDTO noticeDTO) {
        int result = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConn();
            conn.setAutoCommit(false);

            // boardno 채번
            ps = conn.prepareStatement(
                "SELECT 'ann_' || ann_seq.nextval AS boardno FROM dual"
            );
            rs = ps.executeQuery();
            String boardno = null;
            if (rs.next()) boardno = rs.getString("boardno");
            rs.close();
            ps.close();

            // INSERT
            ps = conn.prepareStatement(
                "INSERT INTO announcement"
              + " (boardno, title, content, ctime, mtime, views, emp_id, deleted, origin_name, save_name)"
              + " VALUES (?, ?, ?, sysdate, sysdate, 0, ?, 'N', ?, ?)"
            );
            ps.setString(1, boardno);
            ps.setString(2, noticeDTO.getTitle());
            ps.setString(3, noticeDTO.getContent());
            ps.setString(4, noticeDTO.getEmpId());
            ps.setString(5, noticeDTO.getOriginName());  // 파일 없으면 null
            ps.setString(6, noticeDTO.getSaveName());    // 파일 없으면 null

            result = ps.executeUpdate();
            conn.commit();

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (Exception e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (Exception e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }

        return result;
    }

    // 수정
    public int updateNotice(NoticeDTO noticeDTO) {
        int result = 0;

        String sql = "UPDATE announcement"
                   + " SET title = ?, content = ?, mtime = sysdate"
                   + " WHERE boardno = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, noticeDTO.getTitle());
            ps.setString(2, noticeDTO.getContent());
            ps.setString(3, noticeDTO.getBoardno());

            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 삭제
    public int deleteNotice(String boardno) {
        int result = 0;

        String sql = "DELETE FROM announcement WHERE boardno = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, boardno);
            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 조회수 +1
    public void updateViews(String boardno) {
        String sql = "UPDATE announcement SET views = views + 1 WHERE boardno = ?";

        try (Connection conn = getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, boardno);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
