package P05_stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StockDAO {

    @Autowired
    DataSource dataSource;

    private Connection getConn() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public int selectTotal(StockDTO stockDTO) {
        int totalCount = 0;
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select count(*) cnt from io i " +
                "left join vendor v on i.vendor_id = v.vendor_id " +
                "join item it       on i.item_id   = it.item_id " +
                "join lot l         on i.lot_id    = l.lot_id " +
                "join user_info u   on i.emp_id    = u.emp_id " +
                "where 1=1 " +
                "and (? is null or i.io_type = ?) " +
                "and (? is null or v.vendor_id = ?) " +
                "and (? is null or it.g_id = ?) " +
                "and (? is null or it.item_id = ?) " +
                "and (? is null or i.io_time >= to_date(?, 'yyyy-mm-dd')) " +
                "and (? is null or i.io_time <= to_date(?, 'yyyy-mm-dd') + 1) " +
                "and (? is null or it.item_name like ? or it.item_id like ? or l.lot_id like ?) " +
                // expiry 필터: 괄호 수정 + 출고 차단 + 잔여수량 > 0 조건
                "and (? is null or (" +
                "        (? = 'warn' and l.expiry_date >= trunc(sysdate) and l.expiry_date <= trunc(sysdate) + 10)" +
                "     or (? = 'over' and l.expiry_date < trunc(sysdate))" +
                ")) " +
                "and (? is null or i.io_type = 0) " +
                "and (? is null or nvl((" +
                "    select sum(case when io_type = 0 then io_qty else -io_qty end) " +
                "    from io where lot_id = l.lot_id" +
                "), 0) > 0) "
            );
        ) {
            String ioType   = stockDTO.getFilterIoType();
            String vendorId = stockDTO.getFilterVendorId();
            String gId      = stockDTO.getFilterGId();
            String itemId   = stockDTO.getFilterItemId();
            String dateFrom = stockDTO.getFilterDateFrom();
            String dateTo   = stockDTO.getFilterDateTo();
            String keyword  = stockDTO.getFilterKeyword();
            String kwLike   = (keyword != null && !keyword.isEmpty()) ? "%" + keyword + "%" : null;
            String expiry   = stockDTO.getFilterExpiry();

            int idx = 1;
            ps.setString(idx++, ioType);   ps.setString(idx++, ioType);
            ps.setString(idx++, vendorId); ps.setString(idx++, vendorId);
            ps.setString(idx++, gId);      ps.setString(idx++, gId);
            ps.setString(idx++, itemId);   ps.setString(idx++, itemId);
            ps.setString(idx++, dateFrom); ps.setString(idx++, dateFrom);
            ps.setString(idx++, dateTo);   ps.setString(idx++, dateTo);
            ps.setString(idx++, kwLike); ps.setString(idx++, kwLike); ps.setString(idx++, kwLike); ps.setString(idx++, kwLike);
            // expiry: IS NULL체크, warn비교, over비교, io_type차단, 잔여량>0
            ps.setString(idx++, expiry); ps.setString(idx++, expiry); ps.setString(idx++, expiry);
            ps.setString(idx++, expiry); ps.setString(idx++, expiry);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalCount = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    public List<StockDTO> select(StockDTO stockDTO) {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select * from ( " +
                "    select rownum as rnum, e.* from ( " +
                "        select " +
                "            i.io_id, " +
                "            i.io_time, " +
                "            i.io_type, " +
                "            i.io_reason, " +
                "            i.io_qty, " +
                "            v.vendor_id, " +
                "            v.vendor_name, " +
                "            it.item_id, " +
                "            it.g_id, " +
                "            it.item_name, " +
                "            it.unit, " +
                "            it.spec, " +
                "            l.lot_id, " +
                "            l.expiry_date, " +
                "            u.ename, " +
                "            u.dept_no, " +
                "            u.retire, " +
                "            u.emp_id " +
                "        from io i " +
                "        left join vendor v on i.vendor_id = v.vendor_id " +
                "        join item it       on i.item_id   = it.item_id " +
                "        join lot l         on i.lot_id    = l.lot_id " +
                "        join user_info u   on i.emp_id    = u.emp_id " +
                "        where 1=1 " +
                "        and (? is null or i.io_type = ?) " +
                "        and (? is null or v.vendor_id = ?) " +
                "        and (? is null or it.g_id = ?) " +
                "        and (? is null or it.item_id = ?) " +
                "        and (? is null or i.io_time >= to_date(?, 'yyyy-mm-dd')) " +
                "        and (? is null or i.io_time <= to_date(?, 'yyyy-mm-dd') + 1) " +
                "        and (? is null or it.item_name like ? or it.item_id like ? or l.lot_id like ?) " +
                "        and (? is null or (" +
                "                (? = 'warn' and l.expiry_date >= trunc(sysdate) and l.expiry_date <= trunc(sysdate) + 10)" +
                "             or (? = 'over' and l.expiry_date < trunc(sysdate))" +
                "        )) " +
                "        and (? is null or i.io_type = 0) " +
                "        and (? is null or nvl((" +
                "            select sum(case when io_type = 0 then io_qty else -io_qty end) " +
                "            from io where lot_id = l.lot_id" +
                "        ), 0) > 0) " +
                "        order by i.io_time desc " +
                "    ) e " +
                ") where rnum >= ? and rnum <= ?"
            );
        ) {
            String ioType   = stockDTO.getFilterIoType();
            String vendorId = stockDTO.getFilterVendorId();
            String gId      = stockDTO.getFilterGId();
            String itemId   = stockDTO.getFilterItemId();
            String dateFrom = stockDTO.getFilterDateFrom();
            String dateTo   = stockDTO.getFilterDateTo();
            String keyword  = stockDTO.getFilterKeyword();
            String kwLike   = (keyword != null && !keyword.isEmpty()) ? "%" + keyword + "%" : null;
            String expiry   = stockDTO.getFilterExpiry();

            int idx = 1;
            ps.setString(idx++, ioType);   ps.setString(idx++, ioType);
            ps.setString(idx++, vendorId); ps.setString(idx++, vendorId);
            ps.setString(idx++, gId);      ps.setString(idx++, gId);
            ps.setString(idx++, itemId);   ps.setString(idx++, itemId);
            ps.setString(idx++, dateFrom); ps.setString(idx++, dateFrom);
            ps.setString(idx++, dateTo);   ps.setString(idx++, dateTo);
            ps.setString(idx++, kwLike); ps.setString(idx++, kwLike); ps.setString(idx++, kwLike); ps.setString(idx++, kwLike);
            // expiry: IS NULL체크, warn비교, over비교, io_type차단, 잔여량>0
            ps.setString(idx++, expiry); ps.setString(idx++, expiry); ps.setString(idx++, expiry);
            ps.setString(idx++, expiry); ps.setString(idx++, expiry);

            // 페이징
            ps.setInt(idx++, stockDTO.getStart());
            ps.setInt(idx++, stockDTO.getEnd());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockDTO dto = new StockDTO();
                    dto.setIo_id(rs.getString("io_id"));
                    dto.setIo_time(rs.getTimestamp("io_time"));
                    dto.setIo_type(rs.getInt("io_type"));
                    dto.setIo_reason(rs.getString("io_reason"));
                    dto.setIo_qty(rs.getInt("io_qty"));
                    dto.setVender_id(rs.getString("vendor_id"));
                    dto.setVender_name(rs.getString("vendor_name"));
                    dto.setItem_id(rs.getString("item_id"));
                    dto.setG_id(rs.getString("g_id"));
                    dto.setItem_name(rs.getString("item_name"));
                    dto.setUnit(rs.getString("unit"));
                    dto.setSpec(rs.getString("spec"));
                    dto.setLot_id(rs.getString("lot_id"));
                    dto.setExpiry_date(rs.getDate("expiry_date"));
                    dto.setEname(rs.getString("ename"));
                    dto.setDept_no(rs.getString("dept_no"));
                    dto.setRetire(rs.getInt("retire"));
                    dto.setEmp_id(rs.getString("emp_id"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(StockDTO dto) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConn();
            conn.setAutoCommit(false);

            String lotId = null;
            String ioId  = null;

            if (dto.getIo_type() == 0) {
                // 입고: lot_seq로 새 LOT ID 생성
                ps = conn.prepareStatement(
                    "select 'lot_' || lot_seq.nextval as lot_id from dual"
                );
                rs = ps.executeQuery();
                if (rs.next()) lotId = rs.getString("lot_id");
                rs.close();
                ps.close();

                // lot INSERT
                ps = conn.prepareStatement(
                    "insert into lot (lot_id, item_id, lot_qty, expiry_date) " +
                    "values (?, ?, ?, ?)"
                );
                ps.setString(1, lotId);
                ps.setString(2, dto.getItem_id());
                ps.setInt(3, dto.getLot_qty());
                ps.setDate(4, dto.getExpiry_date());
                ps.executeUpdate();
                ps.close();

                // 입고 io_id 생성
                ps = conn.prepareStatement(
                    "select 'in_' || in_seq.nextval as io_id from dual"
                );
                rs = ps.executeQuery();
                if (rs.next()) ioId = rs.getString("io_id");
                rs.close();
                ps.close();

                ps = conn.prepareStatement(
                    "update stock set stock_no = stock_no + ? where item_id = ?"
                );
                ps.setInt(1, dto.getLot_qty());
                ps.setString(2, dto.getItem_id());
                ps.executeUpdate();
                ps.close();

            } else {
                lotId = dto.getLot_id();

                // 출고 io_id 생성
                ps = conn.prepareStatement(
                    "select 'out_' || out_seq.nextval as io_id from dual"
                );
                rs = ps.executeQuery();
                if (rs.next()) ioId = rs.getString("io_id");
                rs.close();
                ps.close();

                // 재고 차감
                ps = conn.prepareStatement(
                    "update stock set stock_no = stock_no - ? where item_id = ?"
                );
                ps.setInt(1, dto.getLot_qty());
                ps.setString(2, dto.getItem_id());
                ps.executeUpdate();
                ps.close();
            }

            // io INSERT (입고/출고 공통)
            ps = conn.prepareStatement(
                "insert into io (io_id, io_time, io_type, io_reason, vendor_id, item_id, lot_id, emp_id, io_qty) " +
                "values (?, sysdate + 9/24, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, ioId);
            ps.setInt(2, dto.getIo_type());
            ps.setString(3, dto.getIo_reason());
            ps.setString(4, dto.getVender_id());
            ps.setString(5, dto.getItem_id());
            ps.setString(6, lotId);
            ps.setString(7, dto.getEmp_id());
            ps.setInt(8, dto.getIo_qty());
            ps.executeUpdate();

            conn.commit();

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            try { if (rs   != null) rs.close();   } catch (Exception e) { e.printStackTrace(); }
            try { if (ps   != null) ps.close();   } catch (Exception e) { e.printStackTrace(); }
            try { if (conn != null) conn.close(); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public List<StockDTO> selectVendorList() {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select vendor_id, vendor_name from vendor"
            );
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                StockDTO dto = new StockDTO();
                dto.setVender_id(rs.getString("vendor_id"));
                dto.setVender_name(rs.getString("vendor_name"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<StockDTO> selectItemList() {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select item_id, item_name, g_id, spec, unit from item"
            );
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                StockDTO dto = new StockDTO();
                dto.setItem_id(rs.getString("item_id"));
                dto.setItem_name(rs.getString("item_name"));
                dto.setG_id(rs.getString("g_id"));
                dto.setSpec(rs.getString("spec"));
                dto.setUnit(rs.getString("unit"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<StockDTO> selectGroupList() {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select g_id from item group by g_id order by g_id"
            );
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                StockDTO dto = new StockDTO();
                dto.setG_id(rs.getString("g_id"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 입고 목록 조회 (출고 등록 시 AJAX 요청)
    public List<StockDTO> selectInList() {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select " +
                "    i.io_id, " +
                "    it.item_id, " +
                "    it.item_name, " +
                "    it.spec, " +
                "    it.unit, " +
                "    l.lot_id, " +
                "    l.lot_qty " +
                "from io i " +
                "join item it  on i.item_id   = it.item_id " +
                "join lot l    on i.lot_id     = l.lot_id " +
                "where i.io_type = 0 " +
                "order by i.io_time desc"
            );
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                StockDTO dto = new StockDTO();
                dto.setIo_id(rs.getString("io_id"));
                dto.setItem_id(rs.getString("item_id"));
                dto.setItem_name(rs.getString("item_name"));
                dto.setSpec(rs.getString("spec"));
                dto.setUnit(rs.getString("unit"));
                dto.setLot_id(rs.getString("lot_id"));
                dto.setLot_qty(rs.getInt("lot_qty"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<StockDTO> selectAvailableLotList(String keyword) {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select l.lot_id, l.expiry_date, " +
                "       it.item_id, it.item_name, it.spec, it.unit, " +
                "       u.emp_id, u.ename, " +
                "       nvl(( " +
                "           select sum(case when io_type = 0 then io_qty else -io_qty end) " +
                "           from io where lot_id = l.lot_id " +
                "       ), 0) as remaining_qty " +
                "from lot l " +
                "join item it on l.item_id = it.item_id " +
                "join user_info u on u.emp_id = ( " +
                "    select i2.emp_id from io i2 " +
                "    where i2.lot_id = l.lot_id and i2.io_type = 0 and rownum = 1 " +
                ") " +
                "where nvl(( " +
                "    select sum(case when io_type = 0 then io_qty else -io_qty end) " +
                "    from io where lot_id = l.lot_id " +
                "), 0) > 0 " +
                "and (it.item_name like ? or l.lot_id like ?) " +
                "order by l.lot_id desc"
            );
        ) {
            String kw = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockDTO dto = new StockDTO();
                    dto.setLot_id(rs.getString("lot_id"));
                    dto.setLot_qty(rs.getInt("remaining_qty"));
                    dto.setExpiry_date(rs.getDate("expiry_date"));
                    dto.setItem_id(rs.getString("item_id"));
                    dto.setItem_name(rs.getString("item_name"));
                    dto.setSpec(rs.getString("spec"));
                    dto.setUnit(rs.getString("unit"));
                    dto.setEmp_id(rs.getString("emp_id"));
                    dto.setEname(rs.getString("ename"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<StockDTO> selectUserList(String keyword) {
        List<StockDTO> list = new ArrayList<>();
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select emp_id, ename, dept_no " +
                "from user_info " +
                "where (retire = 0 or retire is null) " +
                "and (ename like ? or emp_id like ?) " +
                "order by ename"
            );
        ) {
            String kw = "%" + (keyword == null ? "" : keyword) + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockDTO dto = new StockDTO();
                    dto.setEmp_id(rs.getString("emp_id"));
                    dto.setEname(rs.getString("ename"));
                    dto.setDept_no(rs.getString("dept_no"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // 유통기한 임박 LOT 수 (오늘 ~ 10일 이내)
    public int selectExpiryWarnCount() {
        int count = 0;
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select count(*) cnt from lot l " +
                "where l.expiry_date is not null " +
                "and l.expiry_date >= trunc(sysdate) " +
                "and l.expiry_date <= trunc(sysdate) + 10 " +
                "and nvl(( " +
                "    select sum(case when io_type = 0 then io_qty else -io_qty end) " +
                "    from io where lot_id = l.lot_id " +
                "), 0) > 0"
            );
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // 유통기한 초과 LOT 수 (오늘 이전)
    public int selectExpiryOverCount() {
        int count = 0;
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select count(*) cnt from lot l " +
                "where l.expiry_date is not null " +
                "and l.expiry_date < trunc(sysdate) " +
                "and nvl(( " +
                "    select sum(case when io_type = 0 then io_qty else -io_qty end) " +
                "    from io where lot_id = l.lot_id " +
                "), 0) > 0"
            );
        ) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) count = rs.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int selectStockNo(String itemId) {
        int result = 0;
        try (
            Connection conn = getConn();
            PreparedStatement ps = conn.prepareStatement(
                "select stock_no from stock where item_id = ?"
            );
        ) {
            ps.setString(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getInt("stock_no");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
