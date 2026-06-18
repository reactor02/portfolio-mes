package P09_equip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import P07_work.SearchDTO;
import P07_work.WoDAO;
import P07_work.WoDTO;
import P09_equip.DTO.EqDTO;

import P09_equip.DTO.EqLogDTO;
import P09_equip.DTO.EqSearchDTO;

@Service
public class EqService {
	@Autowired
	EqDAO dao;
	
	public Map getList(EqDTO dto) {
		Map map = new HashMap();
		
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
	
	public List getAllList() {
		return dao.getAllList();
	}

	public Map search(EqDTO dto, EqSearchDTO search) {
		Map map = new HashMap();
		
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
	
	public EqDTO detail(EqDTO dto) {
		return dao.setting(dto);
	}
	
	public Map getLog(EqLogDTO dto) {
		Map map = new HashMap();
		
		int size = dto.getSize();
		int page = dto.getPage();
		
		int start = 0, end = 0;
		
		end = size*page;
		start = end - (size-1);
		
		int cnt = dao.countLog(dto.getEqId());
		
		map.put("list", dao.getLog(start, end, dto));
		map.put("totalPage", (int)Math.ceil ((double)cnt/size));
		map.put("size", size);
		map.put("page", page);
		
		return map;
	}
	
	public int eqStop(String eqId) {
		return dao.eqStop(eqId);
	}
	
	public int stopLog(String eqId) {
		return dao.stopLog(eqId);
	}
	
	public int eqRun(String eqId) {
		return dao.eqRun(eqId);
	}
	
	public int startLog(String eqId) {
		return dao.startLog(eqId);
	}
	
	public int statusChange(String status, String eqId) {
		return dao.statusChange(status, eqId);
	}
	
	public int addLog(EqLogDTO dto) {
		return dao.addLog(dto);
	}
	
	public EqLogDTO getLogDetail(EqLogDTO dto) {
		return dao.getLogDetail(dto);
	}
	
	public int modifyLog(EqLogDTO dto) {
		return dao.modifyLog(dto);
	}
	
//	public int deleteInsp(String logId) {
//		return dao.deleteInsp(logId);
//	}

}
