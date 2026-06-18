package P05_stock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    @Autowired
    StockDAO stockDAO;

    public Map getListStock(StockDTO stockDTO) {
        int size  = stockDTO.getSize();
        int page  = stockDTO.getPage();
        int end   = size * page;
        int start = end - (size - 1);

        stockDTO.setEnd(end);
        stockDTO.setStart(start);

        List<StockDTO> list = stockDAO.select(stockDTO);
        int totalCount = stockDAO.selectTotal(stockDTO);

        Map map = new HashMap();
        map.put("list",       list);
        map.put("totalCount", totalCount);
        map.put("page",       page);
        map.put("size",       size);
        return map;
    }

    public void insert(StockDTO dto) {
        stockDAO.insert(dto);
    }

    public List<StockDTO> getVendorList() {
        return stockDAO.selectVendorList();
    }

    public List<StockDTO> getItemList() {
        return stockDAO.selectItemList();
    }

    public List<StockDTO> getGroupList() {
        return stockDAO.selectGroupList();
    }

    // 입고 목록 (출고 등록 시 AJAX 요청)
    public List<StockDTO> getInList() {
        return stockDAO.selectInList();
    }

    public List<StockDTO> getAvailableLotList(String keyword) {
        return stockDAO.selectAvailableLotList(keyword);
    }

    public List<StockDTO> getUserList(String keyword) {
        return stockDAO.selectUserList(keyword);
    }

    public int getStockNo(String itemId) {
        return stockDAO.selectStockNo(itemId);
    }

    // 유통기한 임박 LOT 수
    public int getExpiryWarnCount() {
        return stockDAO.selectExpiryWarnCount();
    }

    // 유통기한 초과 LOT 수
    public int getExpiryOverCount() {
        return stockDAO.selectExpiryOverCount();
    }
}
