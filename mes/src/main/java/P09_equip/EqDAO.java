package P09_equip;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import P00_layout.LoggableStatement;
import P09_equip.DTO.EqDTO;
import P09_equip.DTO.EqLogDTO;
import P09_equip.DTO.EqSearchDTO;

@Repository
public class EqDAO {

	@Autowired
	private DataSource dataSource;

	public List<EqDTO> getList(int start, int end) {

		List<EqDTO> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = "SELECT * "
					+ "FROM ( "
					+ "    SELECT ROWNUM rnum, inner_query.* "
					+ "    FROM ( "
					+ "        SELECT  "
					+ "            e.eq_id, "
					+ "            e.eq_name, "
					+ "            e.eq_status, "
					+ "            ROUND((SYSDATE - e.eq_startdate) * 24 * 60) AS total_min, "
					+ "            ROUND(NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0)) AS run_min, "
					+ "            ROUND( "
					+ "                (SYSDATE - e.eq_startdate) * 24 * 60 "
					+ "                - NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0) "
					+ "            , 2) AS stop_min, "
					+ "            ROUND( "
					+ "                CASE  "
					+ "                    WHEN (SYSDATE - e.eq_startdate) = 0 THEN 0 "
					+ "                    ELSE  "
					+ "                        NVL(SUM((NVL(r.etime, SYSDATE) - r.stime)), 0)  "
					+ "                        / (SYSDATE - e.eq_startdate) * 100 "
					+ "                END "
					+ "            , 2) AS run_rate "
					+ "        FROM equipment e "
					+ "        LEFT JOIN eqrun_log r "
					+ "            ON e.eq_id = r.eq_id "
					+ "        GROUP BY e.eq_id, e.eq_name, e.eq_startdate, e.eq_status "
					+ "        ORDER BY e.eq_id "
					+ "    ) inner_query "
					+ ") "
					+ "WHERE rnum BETWEEN ? AND ?";

			ps = conn.prepareStatement(query);

			ps.setInt(1, start);
			ps.setInt(2, end);

			rs = ps.executeQuery();

			while (rs.next()) {

				String eqId = rs.getString("eq_id");
				String eqName = rs.getString("eq_name");

				int totalMin = rs.getInt("total_min");
				int runMin = rs.getInt("run_min");
				int stopMin = rs.getInt("stop_min");
				double runRate = rs.getDouble("run_rate");
				String status = rs.getString("eq_status");

				EqDTO dto = new EqDTO();

				dto.setEqId(eqId);
				dto.setEqName(eqName);
				dto.setTotalMin(totalMin);
				dto.setRunMin(runMin);
				dto.setStopMin(stopMin);
				dto.setRunRate(runRate);
				dto.setStatus(status);

				list.add(dto);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;

	} // getList


	public int count() {

		int cnt = 0;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = " SELECT count(*) cnt from equipment ";

			ps = new LoggableStatement(conn, query);

			rs = ps.executeQuery();

			while (rs.next()) {

				cnt = rs.getInt("cnt");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return cnt;

	} // count


	public List<EqDTO> getAllList() {

		List<EqDTO> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = "SELECT  "
					+ "    e.eq_id, "
					+ "    e.eq_name, "
					+ "    e.eq_status, "
					+ "    ROUND((SYSDATE - e.eq_startdate) * 24 * 60) total_min, "
					+ "    ROUND(NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0)) run_min, "
					+ "    ROUND( "
					+ "        (SYSDATE - e.eq_startdate) * 24 * 60 "
					+ "        - NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0) "
					+ "    , 2) stop_min, "
					+ "    ROUND( "
					+ "        CASE  "
					+ "            WHEN (SYSDATE - e.eq_startdate) = 0 THEN 0 "
					+ "            ELSE  "
					+ "                NVL(SUM((NVL(r.etime, SYSDATE) - r.stime)), 0)  "
					+ "                / (SYSDATE - e.eq_startdate) * 100 "
					+ "        END "
					+ "    , 2) run_rate "
					+ "FROM equipment e "
					+ "LEFT JOIN eqrun_log r "
					+ "    ON e.eq_id = r.eq_id "
					+ "GROUP BY e.eq_id, e.eq_name, e.eq_startdate, e.eq_status "
					+ "ORDER BY e.eq_id";

			ps = conn.prepareStatement(query);

			rs = ps.executeQuery();

			while (rs.next()) {

				String eqId = rs.getString("eq_id");
				String eqName = rs.getString("eq_name");

				int totalMin = rs.getInt("total_min");
				int runMin = rs.getInt("run_min");
				int stopMin = rs.getInt("stop_min");
				double runRate = rs.getDouble("run_rate");
				String status = rs.getString("eq_status");

				EqDTO dto = new EqDTO();

				dto.setEqId(eqId);
				dto.setEqName(eqName);
				dto.setTotalMin(totalMin);
				dto.setRunMin(runMin);
				dto.setStopMin(stopMin);
				dto.setRunRate(runRate);
				dto.setStatus(status);

				list.add(dto);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;

	} // getAllList


	public List<EqDTO> search(int start, int end, EqSearchDTO search) {
		List<EqDTO> list = new ArrayList<>();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = "SELECT * "
				+ "FROM ( "
				+ "    SELECT ROWNUM rnum, inner_query.* "
				+ "    FROM ( "
				+ "        SELECT  "
				+ "            e.eq_id, "
				+ "            e.eq_name, "
				+ "            e.eq_status, "
				+ "            ROUND((SYSDATE - e.eq_startdate) * 24 * 60) total_min, "
				+ "            ROUND(NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0)) run_min, "
				+ "            ROUND( "
				+ "                (SYSDATE - e.eq_startdate) * 24 * 60 "
				+ "                - NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0) "
				+ "            , 2) stop_min, "
				+ "            ROUND( "
				+ "                CASE "
				+ "                    WHEN (SYSDATE - e.eq_startdate) = 0 THEN 0 "
				+ "                    ELSE NVL(SUM((NVL(r.etime, SYSDATE) - r.stime)), 0) "
				+ "                         / (SYSDATE - e.eq_startdate) * 100 "
				+ "                END "
				+ "            , 2) run_rate "
				+ "        FROM equipment e "
				+ "        LEFT JOIN eqrun_log r "
				+ "            ON e.eq_id = r.eq_id "
				+ "        WHERE e.eq_id IS NOT NULL ";

			if (!search.getStatus().isEmpty()) {
				query += " AND e.eq_status = ? ";
			}

			if (search.getKeyword() != null && !search.getKeyword().isEmpty()) {
				query += " AND (UPPER(e.eq_name) LIKE UPPER(?) OR UPPER(e.eq_id) LIKE UPPER(?)) ";
			}

			query += " GROUP BY e.eq_id, e.eq_name, e.eq_startdate, e.eq_status "
					+ " ORDER BY e.eq_id "
					+ "    ) inner_query "
					+ ") "
					+ "WHERE rnum BETWEEN ? AND ?";

			ps = conn.prepareStatement(query);

			int idx = 1;

			if (search.getStatus() != null && !search.getStatus().isEmpty()) {
				ps.setString(idx++, search.getStatus());
			}

			if (search.getKeyword() != null && !search.getKeyword().isEmpty()) {
				String keyword = "%" + search.getKeyword() + "%";
				ps.setString(idx++, keyword);
				ps.setString(idx++, keyword);
			}

			ps.setInt(idx++, start);
			ps.setInt(idx++, end);

			rs = ps.executeQuery();

			while (rs.next()) {

				String eqId = rs.getString("eq_id");
				String eqName = rs.getString("eq_name");

				int totalMin = rs.getInt("total_min");
				int runMin = rs.getInt("run_min");
				int stopMin = rs.getInt("stop_min");
				double runRate = rs.getDouble("run_rate");
				String status = rs.getString("eq_status");

				EqDTO dto = new EqDTO();

				dto.setEqId(eqId);
				dto.setEqName(eqName);

				dto.setTotalMin(totalMin);
				dto.setRunMin(runMin);
				dto.setStopMin(stopMin);
				dto.setRunRate(runRate);
				dto.setStatus(status);

				list.add(dto);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	} // search

	public int countSearch(EqSearchDTO search) {

		int cnt = 0;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = "SELECT COUNT(*) "
					+ "FROM ( "
					+ "    SELECT  "
					+ "        e.eq_id "
					+ "    FROM equipment e "
					+ "    LEFT JOIN eqrun_log r "
					+ "        ON e.eq_id = r.eq_id "
					+ "    WHERE e.eq_id is not null";

			if (!search.getStatus().isEmpty()) {
				query += " AND e.eq_status = ? ";
			}

			if (search.getKeyword() != null && !search.getKeyword().isEmpty()) {
				query += " AND (UPPER(e.eq_name) LIKE UPPER(?) OR UPPER(e.eq_id) LIKE UPPER(?)) ";
			}

			query += "    GROUP BY e.eq_id, e.eq_name, e.eq_startdate, e.eq_status "
					+ ")";

			ps = new LoggableStatement(conn, query);

			int idx = 1;

			if (search.getStatus() != null && !search.getStatus().isEmpty()) {
				ps.setString(idx++, search.getStatus());
			}

			if (search.getKeyword() != null && !search.getKeyword().isEmpty()) {
				String keyword = "%" + search.getKeyword() + "%";
				ps.setString(idx++, keyword);
				ps.setString(idx++, keyword);
			}

			rs = ps.executeQuery();

			if (rs.next()) {
				cnt = rs.getInt(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return cnt;

	} // countSearch


	public EqDTO setting(EqDTO dto) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = "SELECT  "
					+ "    e.eq_id, "
					+ "    e.eq_name, "
					+ "    e.eq_buydate, "
					+ "    e.eq_startdate, "
					+ "    e.process_id, "
					+ "    e.eq_status, "
					+ "    ROUND((SYSDATE - e.eq_startdate) * 24 * 60) total_min, "
					+ "    ROUND(NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0)) run_min, "
					+ "    ROUND( "
					+ "        (SYSDATE - e.eq_startdate) * 24 * 60 "
					+ "        - NVL(SUM((NVL(r.etime, SYSDATE) - r.stime) * 24 * 60), 0) "
					+ "    , 2) stop_min, "
					+ "    ROUND( "
					+ "        CASE  "
					+ "            WHEN (SYSDATE - e.eq_startdate) = 0 THEN 0 "
					+ "            ELSE  "
					+ "                NVL(SUM((NVL(r.etime, SYSDATE) - r.stime)), 0)  "
					+ "                / (SYSDATE - e.eq_startdate) * 100 "
					+ "        END "
					+ "    , 2) run_rate "
					+ "FROM equipment e "
					+ "LEFT JOIN eqrun_log r "
					+ "    ON e.eq_id = r.eq_id "
					+ "WHERE e.eq_id = ? "
					+ "GROUP BY e.eq_id, e.eq_name, e.eq_startdate, e.eq_buydate, e.process_id, e.eq_status "
					+ "ORDER BY e.eq_id";

			ps = conn.prepareStatement(query);
			ps.setString(1, dto.getEqId());

			rs = ps.executeQuery();

			while (rs.next()) {

				String eqId = rs.getString("eq_id");
				String eqName = rs.getString("eq_name");
				Date bDate = rs.getDate("eq_buydate");
				Date sDate = rs.getDate("eq_startDate");
				String procId = rs.getString("process_id");

				int totalMin = rs.getInt("total_min");
				int runMin = rs.getInt("run_min");
				int stopMin = rs.getInt("stop_min");
				double runRate = rs.getDouble("run_rate");
				String status = rs.getString("eq_status");

				dto.setEqId(eqId);
				dto.setEqName(eqName);
				dto.setBuyDate(bDate);
				dto.setStartDate(sDate);
				dto.setProcId(procId);

				dto.setTotalMin(totalMin);
				dto.setRunMin(runMin);
				dto.setStopMin(stopMin);
				dto.setRunRate(runRate);
				dto.setStatus(status);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return dto;

	} // setting


	public List<EqLogDTO> getLog(int start, int end, EqLogDTO eqDTO) {

		List<EqLogDTO> list = new ArrayList();

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query =
				    "SELECT * " +
				    "FROM ( " +
				    "    SELECT ROWNUM rnum, a.* " +
				    "    FROM ( " +
				    "        SELECT l.*, u.eName " +
				    "        FROM equipment_log l " +
				    "        LEFT OUTER JOIN user_info u " +
				    "            ON l.EMP_ID = u.EMP_ID " +
				    "        WHERE l.eq_id = ? " +
				    "        ORDER BY l.EQ_LOG_ID DESC " +
				    "    ) a " +
				    ") " +
				    "WHERE rnum BETWEEN ? AND ?";

			ps = conn.prepareStatement(query);
			ps.setString(1, eqDTO.getEqId());
			ps.setInt(2, start);
			ps.setInt(3, end);

			rs = ps.executeQuery();

			while (rs.next()) {

				String logId = rs.getString("eq_log_id");
				String eqId = rs.getString("eq_id");
				String wId = rs.getString("emp_id");
				String wName = rs.getString("ename");

				Timestamp sTime = rs.getTimestamp("start_time");
				Timestamp eTime = rs.getTimestamp("end_time");

				String inspType = rs.getString("insp_type");
				String inspContent = rs.getString("insp_content");

				EqLogDTO dto = new EqLogDTO();

				dto.setLogId(logId);
				dto.setEqId(eqId);
				dto.setwId(wId);
				dto.setwName(wName);
				dto.setsTime(sTime);
				dto.seteTime(eTime);
				dto.setInspType(inspType);
				dto.setInspContent(inspContent);

				list.add(dto);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;

	} // getLog

	public int countLog(String eqId) {

		int cnt = 0;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = " SELECT count(*) cnt from equipment_log where eq_id = ? ";

			ps = new LoggableStatement(conn, query);
			ps.setString(1, eqId);

			rs = ps.executeQuery();

			while (rs.next()) {

				cnt = rs.getInt("cnt");

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return cnt;

	} // countLog


	public int eqStop(String eqId) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "update equipment set eq_status = '점검 중' where eq_id = ?";

			ps = new LoggableStatement(conn, query);
			ps.setString(1, eqId);

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // eqStop



	public int stopLog(String eqId) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "UPDATE eqrun_log "
					+ "SET etime = sysdate   "
					+ "WHERE eq_id = ? AND etime IS NULL ";

			ps = new LoggableStatement(conn, query);
			ps.setString(1, eqId);

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // stopLog



	public int eqRun(String eqId) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "update equipment set eq_status = '가동' where eq_id = ?";

			ps = new LoggableStatement(conn, query);
			ps.setString(1, eqId);

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // eqRun



	public int startLog(String eqId) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "INSERT INTO EQRUN_LOG (eqrun_id, eq_id, stime) "
					+ "VALUES ('run_'||run_seq.nextval, ?, sysdate  )";

			ps = new LoggableStatement(conn, query);
			ps.setString(1, eqId);

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // startLog



	public int statusChange(String status, String eqId) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "UPDATE EQUIPMENT "
					+ "SET eq_status = ? "
					+ "WHERE eq_id = ? ";

			ps = new LoggableStatement(conn, query);
			ps.setString(1, status);
			ps.setString(2, eqId);

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // statusChange



	public int addLog(EqLogDTO dto) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "INSERT INTO EQUIPMENT_LOG (Eq_log_id, start_time, end_time, emp_id, eq_id, insp_type, insp_content) "
					+ "VALUES ('log_'||log_seq.nextval, ?, ?, ?, ?, ?, ?)";

			ps = new LoggableStatement(conn, query);

			ps.setTimestamp(1, dto.getsTime());
			ps.setTimestamp(2, dto.geteTime());
			ps.setString(3, dto.getwId());
			ps.setString(4, dto.getEqId());
			ps.setString(5, dto.getInspType());
			ps.setString(6, dto.getInspContent());

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // addLog


	public EqLogDTO getLogDetail(EqLogDTO dto) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = dataSource.getConnection();

			String query = "SELECT l.*, u.eName, e.eq_name "
					+ "FROM EQUIPMENT_LOG l	 "
					+ "LEFT OUTER JOIN user_info u "
					+ "	ON l.emp_id = u.EMP_ID "
					+ "LEFT OUTER JOIN equipment e "
					+ "	ON l.eq_id = e.eq_id "
					+ "WHERE l.EQ_LOG_ID = ?";

			ps = conn.prepareStatement(query);
			ps.setString(1, dto.getLogId());

			rs = ps.executeQuery();

			while (rs.next()) {

				String logId = rs.getString("eq_log_id");
				String eqId = rs.getString("eq_id");
				String eqName = rs.getString("eq_name");
				String wId = rs.getString("emp_id");
				String wName = rs.getString("ename");

				Timestamp sTime = rs.getTimestamp("start_time");
				Timestamp eTime = rs.getTimestamp("end_time");

				String inspType = rs.getString("insp_type");
				String inspContent = rs.getString("insp_content");

				dto.setLogId(logId);
				dto.setEqId(eqId);
				dto.setEqName(eqName);
				dto.setwId(wId);
				dto.setwName(wName);
				dto.setsTime(sTime);
				dto.seteTime(eTime);
				dto.setInspType(inspType);
				dto.setInspContent(inspContent);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return dto;

	} // getLogDetail



	public int modifyLog(EqLogDTO dto) {

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int result = -1;

		try {

			conn = dataSource.getConnection();

			String query = "UPDATE EQUIPMENT_LOG "
					+ "SET start_time=?, end_time=?, insp_type=?, insp_content=? "
					+ "WHERE eq_log_id=?";

			ps = new LoggableStatement(conn, query);

			ps.setTimestamp(1, dto.getsTime());
			ps.setTimestamp(2, dto.geteTime());
			ps.setString(3, dto.getInspType());
			ps.setString(4, dto.getInspContent());
			ps.setString(5, dto.getLogId());

			result = ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	} // modifyLog

}
