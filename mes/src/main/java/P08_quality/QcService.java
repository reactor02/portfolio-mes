package P08_quality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import P01_auth.DTO.UserWoDTO;
import P07_work.SearchDTO;
import P07_work.WoDTO;
import P08_quality.QcDAO;
import P08_quality.QcDTO;

import java.sql.Connection;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@Service
public class QcService {
	
	public Map getList(QcDTO dto) {
		Map map = new HashMap();
		QcDAO dao = new QcDAO();
		
		int size = dto.getSize();
		int page = dto.getPage();
		
		int start = 0, end = 0;
		
		end = size*page;
		start = end - (size-1);
		
		int cnt = dao.count();
		
		map.put("list", dao.getList(start, end));
		map.put("totalPage", (int)Math.ceil ((double)cnt/size));
		map.put("size", size);
		map.put("page", page);
		
		return map;
	}
	
	public QcCardDTO getCard() {
		QcDAO dao = new QcDAO();
		return dao.getCard();
	}
	
	public Map search(QcDTO dto, SearchDTO search) {
		Map map = new HashMap();
		QcDAO dao = new QcDAO();
		
		int size = dto.getSize();
		int page = dto.getPage();
		
		int start = 0, end = 0;
		
		end = size*page;
		start = end - (size-1);
		
		int cnt = dao.countSearch(search);
		
		map.put("list", dao.search(start, end, search));
		map.put("totalPage", (int)Math.ceil ((double)cnt/size));
		map.put("size", size);
		map.put("page", page);
		
		return map;
	}
	
	public QcDTO detail(QcDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.detail(dto);
	}
	
	public List defContent(String qcId) {
		QcDAO dao = new QcDAO();
		return dao.defContent(qcId);
	}
	
	public List getWoList() {
		QcDAO dao = new QcDAO();
		return dao.getWoList();
	}
	
	public WoDTO setWo(WoDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.setWo(dto);
	}
	
	public List<UserWoDTO> searchWorker(String keyword) {
		QcDAO dao = new QcDAO();
		return dao.searchWorker(keyword);
	}
	
	public int addQc(QcAddDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.addQc(dto);
	}
	
	public int woStatus(QcAddDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.woStatus(dto);
	}
	
	public QcDTO getQc(String qcId) {
		QcDAO dao = new QcDAO();
		return dao.getQc(qcId);
	}
	
	public int modifyOrder(String qcId, QcAddDTO addDTO) {
		QcDAO dao = new QcDAO();
		return dao.modifyOrder(qcId, addDTO);
	}
	
	public int deleteQc(String qcId) {
		QcDAO dao = new QcDAO();
		return dao.deleteQc(qcId);
	}
	
	public int delWoStatus(String woId) {
		QcDAO dao = new QcDAO();
		return dao.delWoStatus(woId);
	}
	
	public int addDef(String qcId, QcDefDTO defDTO) {
		QcDAO dao = new QcDAO();
		return dao.addDef(qcId, defDTO);
	}
	
	public QcDisposeDTO disposeSum (QcDisposeDTO disDTO) {
		QcDAO dao = new QcDAO();
		return dao.disposeSum(disDTO);
	}
	
	public int updateDef(QcDefDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.updateDef(dto);
	}
	
	public int deleteDef(String defId) {
		QcDAO dao = new QcDAO();
		return dao.deleteDef(defId);
	}
	
	public int modifyResult(QcDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.modifyResult(dto);
	}
	
	public String lotId() {
		QcDAO dao = new QcDAO();
		return dao.lotId();
	}
	
	public int addLot(FinIoDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.addLot(dto);
	}
	
	public int addIn(FinIoDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.addIn(dto);
	}
	
	public int updateStock(FinIoDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.updateStock(dto);
	}
	
	public int updateWoStatus(String woId) {
		QcDAO dao = new QcDAO();
		return dao.updateWoStatus(woId);
	}
	
	public int updateQcStatus(String qcId) {
		QcDAO dao = new QcDAO();
		return dao.updateQcStatus(qcId);
	}
	
	public int updateWoLot(String woId, FinIoDTO dto) {
		QcDAO dao = new QcDAO();
		return dao.updateWoLot(woId, dto);
	}
	
	
	
	//////////////////////////
	
	
	
	public void processFinIn(String woId, String qcId, FinIoDTO ioDTO) {
	    Connection conn = null;

	    try {
	        Context ctx = new InitialContext();
	        DataSource dataFactory = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracle");

	        conn = dataFactory.getConnection();
	        conn.setAutoCommit(false);

	        QcDAO dao = new QcDAO();

	        // 1. lot_id 생성
	        String lotId = dao.lotId(conn);
	        if (lotId == null || lotId.trim().isEmpty()) {
	            throw new RuntimeException("LOT 번호 생성 실패");
	        }
	        ioDTO.setLotId(lotId);

	        // 2. LOT 생성
	        int result1 = dao.addLot(conn, ioDTO);
	        if (result1 <= 0) {
	            throw new RuntimeException("LOT 생성 실패");
	        }

	        // 3. IO 입고 생성
	        int result2 = dao.addIn(conn, ioDTO);
	        if (result2 <= 0) {
	            throw new RuntimeException("입고 이력 생성 실패");
	        }

	        // 4. 재고 증가
	        int result3 = dao.updateStock(conn, ioDTO);
	        if (result3 <= 0) {
	            throw new RuntimeException("재고 반영 실패");
	        }

	        // 5. 작업 상태 변경
	        int result4 = dao.updateWoStatus(conn, woId);
	        if (result4 <= 0) {
	            throw new RuntimeException("작업 상태 변경 실패");
	        }

	        // 6. 검사 상태 변경
	        int result5 = dao.updateQcStatus(conn, qcId);
	        if (result5 <= 0) {
	            throw new RuntimeException("검사 상태 변경 실패");
	        }

	        // 7. work_order에 lot 연결
	        int result6 = dao.updateWoLot(conn, woId, ioDTO);
	        if (result6 <= 0) {
	            throw new RuntimeException("LOT 연결 실패");
	        }

	        conn.commit();

	    } catch (Exception e) {
	        try {
	            if (conn != null) conn.rollback();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        throw new RuntimeException(e);

	    } finally {
	        try {
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

}
