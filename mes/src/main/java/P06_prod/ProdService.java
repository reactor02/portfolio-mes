package P06_prod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service

public class ProdService {

    ProdDAO prodPlanDAO = new ProdDAO();

    /* ── 생산계획 목록 ────────────────────────────────────── */
    public Map getPlanList(ProdDTO planDTO) {
        int size = planDTO.getSize();
        int page = planDTO.getPage();

        int end   = size * page;
        int start = end - (size - 1);
        planDTO.setEnd(end);
        planDTO.setStart(start);

        List list       = prodPlanDAO.selectAll(planDTO);
        int  totalCount = prodPlanDAO.selectTotal(planDTO);

        int totalPage    = (int) Math.ceil((double) totalCount / size);
        if (totalPage < 1) totalPage = 1;

        int section      = 5;
        int endSection   = (int) Math.ceil((double) page / section) * section;
        int startSection = endSection - section + 1;
        if (endSection > totalPage) endSection = totalPage;

        Map map = new HashMap();
        map.put("list",         list);
        map.put("totalCount",   totalCount);
        map.put("totalPage",    totalPage);
        map.put("startSection", startSection);
        map.put("endSection",   endSection);

        return map;
    }

    /* ── 생산계획 등록 (신규 plan_id 반환) ────────────────── */
    public String insertPlan(ProdDTO dto) {
        return prodPlanDAO.insertPlan(dto);
    }

    /* ── 생산계획 상세 ────────────────────────────────────── */
    public ProdDTO getPlanDetail(String planId) {
        return prodPlanDAO.selectOne(planId);
    }

    /* ── 생산계획 수정 ────────────────────────────────────── */
    public int updatePlan(ProdDTO dto) {
        return prodPlanDAO.updatePlan(dto);
    }

    /* ── 대분류 목록 ──────────────────────────────────────── */
    public List<Map<String, String>> getGroupList() {
        return prodPlanDAO.selectGroupList();
    }

    /* ── 소분류(item) 전체 목록 ───────────────────────────── */
    public List<Map<String, String>> getItemList() {
        return prodPlanDAO.selectItemList();
    }

    /* ── 공정 단계 목록 (item_id 기준) ───────────────────── */
    public List<Map<String, Object>> getProcessStepList(String itemId) {
        String processId = prodPlanDAO.selectProcessIdByItemId(itemId);
        if (processId == null || processId.isEmpty()) return new java.util.ArrayList<>();
        return prodPlanDAO.selectProcessStepList(processId);
    }

    /* ── 담당자 검색 ──────────────────────────────────────── */
    public Map getEmpList(String keyword, int page, int size) {
        int end   = size * page;
        int start = end - (size - 1);

        List list       = prodPlanDAO.selectEmpList(keyword, start, end);
        int  totalCount = prodPlanDAO.selectEmpTotal(keyword);

        Map map = new HashMap();
        map.put("list",       list);
        map.put("totalCount", totalCount);
        return map;
    }
}