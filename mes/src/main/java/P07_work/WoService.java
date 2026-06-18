package P07_work;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import P06_prod.DTO.PlanWoDTO;

import java.sql.Connection;
import javax.sql.DataSource;

@Service
public class WoService {

    @Autowired
    private WoDAO woDAO;

    public Map getList(WoDTO dto) {
        Map map = new HashMap();

        int size = dto.getSize();
        int page = dto.getPage();

        int start = 0, end = 0;

        end = size * page;
        start = end - (size - 1);

        int cnt = woDAO.count();

        map.put("list", woDAO.getList(start, end));
        map.put("totalPage", (int) Math.ceil((double) cnt / size));
        map.put("size", size);
        map.put("page", page);

        return map;
    }

    public Map search(WoDTO dto, SearchDTO search) {
        Map map = new HashMap();

        int size = dto.getSize();
        int page = dto.getPage();

        int start = 0, end = 0;

        end = size * page;
        start = end - (size - 1);

        int cnt = woDAO.countSearch(search);

        map.put("list", woDAO.search(start, end, search));
        map.put("totalPage", (int) Math.ceil((double) cnt / size));
        map.put("size", size);
        map.put("page", page);

        return map;
    }

    public WoDTO detail(WoDTO dto) {
        return woDAO.detail(dto);
    }

    public List planList() {
        return woDAO.planList();
    }

    public PlanWoDTO getPlan(PlanWoDTO dto) {
        return woDAO.getPlan(dto);
    }

    public List searchWorker(String keyword) {
        return woDAO.searchWorker(keyword);
    }

    public int addOrder(WoAddDTO dto) {
        return woDAO.addOrder(dto);
    }

    public int modifyOrder(WoAddDTO dto) {
        return woDAO.modifyOrder(dto);
    }

    public int deleteOrder(String woId) {
        return woDAO.deleteOrder(woId);
    }

    public int updateContent(String woId, int status, int prevQty) {
        return woDAO.updateContent(woId, status, prevQty);
    }

    public int updatePlan() {
        return woDAO.updatePlan();
    }

    public LotDTO getLot(String itemId) {
        return woDAO.getLot(itemId);
    }

    public List<WoBOMDTO> setBOM(WoBOMDTO dto) {
        return woDAO.setBOM(dto);
    }

    public List<ProcessDTO> setProcess(ProcessDTO dto) {
        return woDAO.setProcess(dto);
    }

    /////////////////////////////////////////

    public void processContentModify(String woId, int status, int prevQty, String worker) {

        Connection conn = null;

        try {
            conn = woDAO.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // 1. 기존 작업 조회
            WoDTO param = new WoDTO();
            param.setWoId(woId);

            WoDTO old = woDAO.detail(conn, param);

            if (worker == null || worker.trim().isEmpty()) {
                throw new RuntimeException("작업자 정보가 없습니다.");
            }

            if (old == null || old.getWoId() == null) {
                throw new RuntimeException("작업지시 없음");
            }

            if (status != 10 && status != 20 && status != 30 && status != 50) {
                throw new RuntimeException("상태값 오류");
            }

            if (prevQty < 0 || prevQty > old.getWoQty()) {
                throw new RuntimeException("완료 수량 오류");
            }

            int oldStatus = old.getWoStatus();

            // 2. 작업지시 업데이트
            int updateResult = woDAO.updateContent(conn, woId, status, prevQty);
            if (updateResult == 0) {
                throw new RuntimeException("작업기록 수정에 실패했습니다.");
            }

            // 3. 완료로 바뀐 경우만 BOM 처리
            if (oldStatus == 30 && status == 30) {
                throw new RuntimeException("이미 완료된 작업은 수정할 수 없습니다.");
            }

            if (oldStatus != 30 && status == 30) {
                completeWorkByBom(conn, woId, prevQty, worker);
            }

            // 4. 계획 업데이트
            woDAO.updatePlan(conn);

            conn.commit(); // 성공 → 전부 반영

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback(); // 실패 → 전부 취소
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

    public void completeWorkByBom(Connection conn, String woId, int completedQty, String worker) {

        WoBOMDTO param = new WoBOMDTO();
        param.setWoId(woId);

        List<WoBOMDTO> bomList = woDAO.setBOM(conn, param);

        if (bomList == null || bomList.isEmpty()) {
            throw new RuntimeException("BOM 정보가 없습니다.");
        }

        for (WoBOMDTO bom : bomList) {
            double requiredQty = bom.getEa() * completedQty;

            System.out.println("ea : " + bom.getEa());
            System.out.println("qty : " + completedQty);

            while (requiredQty > 0.000001) {
                LotDTO lot = woDAO.getLot(conn, bom.getcId());

                // lot 없으면 바로 예외
                if (lot == null) {
                    throw new RuntimeException("사용 가능한 LOT이 부족합니다. itemId=" + bom.getcId());
                }

                double lotQty = lot.getQty();

                if (lotQty <= 0) {
                    throw new RuntimeException("LOT 수량이 0 이하입니다. lotId=" + lot.getLotId());
                }

                // 실제 이번 loop에서 소비할 양
                double consumeQty = Math.min(requiredQty, lotQty);

                // 1. stock 차감 - 실제 사용량만 차감
                woDAO.minStock(conn, bom.getcId(), consumeQty);

                // 2. 출고 로그 1건
                woDAO.insertOut(conn, lot, worker);

                // 3. lot 수량 갱신
                double remainQty = lotQty - consumeQty;
                lot.setQty(remainQty);

                if (remainQty == 0) {
                    lot.setStatus("사용 완료");
                } else {
                    lot.setStatus("사용 중");
                }

                woDAO.updateLot(conn, lot);

                // 4. 일부만 쓰고 남았으면 잔여 입고 로그 1건
                if (remainQty > 0) {
                    woDAO.insertIn(conn, lot, worker);
                }

                // 5. 남은 필요량 차감
                requiredQty -= consumeQty;
            }
        }
    }
}
