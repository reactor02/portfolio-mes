package P05_stock;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.stereotype.Controller
@RequestMapping("/io")
public class Controller {

    @Autowired
    StockService service;

    @GetMapping
    public Object doGet(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String filterIoType,
            @RequestParam(required = false) String filterVendorId,
            @RequestParam(required = false) String filterGId,
            @RequestParam(required = false) String filterItemId,
            @RequestParam(required = false) String filterDateFrom,
            @RequestParam(required = false) String filterDateTo,
            @RequestParam(required = false) String filterKeyword,
            @RequestParam(required = false) String filterExpiry,
            Model model) {

        // 출고 등록 시 LOT 목록 AJAX 요청
        if ("getInList".equals(action)) {
            List<StockDTO> inList = service.getInList();

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < inList.size(); i++) {
                StockDTO d = inList.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"io_id\":"     ).append("\"").append(d.getIo_id()).append("\",")
                  .append("\"item_id\":"   ).append("\"").append(d.getItem_id()).append("\",")
                  .append("\"item_name\":").append("\"").append(d.getItem_name() != null ? d.getItem_name() : "").append("\",")
                  .append("\"lot_id\":"    ).append("\"").append(d.getLot_id()).append("\",")
                  .append("\"lot_qty\":"   ).append(d.getLot_qty()).append(",")
                  .append("\"spec\":"      ).append("\"").append(d.getSpec() != null ? d.getSpec() : "").append("\",")
                  .append("\"unit\":"      ).append("\"").append(d.getUnit() != null ? d.getUnit() : "").append("\"")
                  .append("}");
            }
            sb.append("]");
            return ResponseEntity.ok(sb.toString());
        }

        if ("getLotList".equals(action)) {
            String kw = keyword;
            if (kw != null && kw.trim().isEmpty()) kw = null;
            List<StockDTO> lotList = service.getAvailableLotList(kw);

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < lotList.size(); i++) {
                StockDTO d = lotList.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"lot_id\":"     ).append("\"").append(d.getLot_id()).append("\",")
                  .append("\"remaining_qty\":").append(d.getLot_qty()).append(",")
                  .append("\"expiry_date\":").append("\"").append(d.getExpiry_date() != null ? d.getExpiry_date().toString() : "").append("\",")
                  .append("\"item_id\":"    ).append("\"").append(d.getItem_id()).append("\",")
                  .append("\"item_name\":"  ).append("\"").append(d.getItem_name() != null ? d.getItem_name() : "").append("\",")
                  .append("\"spec\":"       ).append("\"").append(d.getSpec() != null ? d.getSpec() : "").append("\",")
                  .append("\"unit\":"       ).append("\"").append(d.getUnit() != null ? d.getUnit() : "").append("\",")
                  .append("\"emp_id\":"     ).append("\"").append(d.getEmp_id()).append("\",")
                  .append("\"ename\":"      ).append("\"").append(d.getEname() != null ? d.getEname() : "").append("\"")
                  .append("}");
            }
            sb.append("]");
            return ResponseEntity.ok(sb.toString());
        }

        if ("getUserList".equals(action)) {
            String kw = keyword;
            if (kw != null && kw.trim().isEmpty()) kw = null;
            List<StockDTO> userList = service.getUserList(kw);

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < userList.size(); i++) {
                StockDTO d = userList.get(i);
                if (i > 0) sb.append(",");
                sb.append("{")
                  .append("\"emp_id\":\"").append(d.getEmp_id()).append("\",")
                  .append("\"ename\":\"").append(d.getEname() != null ? d.getEname() : "").append("\",")
                  .append("\"dept_no\":\"").append(d.getDept_no() != null ? d.getDept_no() : "").append("\"")
                  .append("}");
            }
            sb.append("]");
            return ResponseEntity.ok(sb.toString());
        }

        // 목록 페이지 조회
        int iSize = 10;
        int iPage = 1;
        try { if (size != null) iSize = Integer.parseInt(size); } catch (Exception e) { e.printStackTrace(); }
        try { if (page != null) iPage = Integer.parseInt(page); } catch (Exception e) { e.printStackTrace(); }
        if (filterIoType   != null && filterIoType.isEmpty())   filterIoType   = null;
        if (filterVendorId != null && filterVendorId.isEmpty()) filterVendorId = null;
        if (filterGId      != null && filterGId.isEmpty())      filterGId      = null;
        if (filterItemId   != null && filterItemId.isEmpty())   filterItemId   = null;
        if (filterDateFrom != null && filterDateFrom.isEmpty()) filterDateFrom = null;
        if (filterDateTo   != null && filterDateTo.isEmpty())   filterDateTo   = null;
        if (filterKeyword  != null && filterKeyword.isEmpty())  filterKeyword  = null;
        if (filterExpiry   != null && filterExpiry.isEmpty())   filterExpiry   = null;

        StockDTO stockDTO = new StockDTO();
        stockDTO.setSize(iSize);
        stockDTO.setPage(iPage);
        stockDTO.setFilterIoType(filterIoType);
        stockDTO.setFilterVendorId(filterVendorId);
        stockDTO.setFilterGId(filterGId);
        stockDTO.setFilterItemId(filterItemId);
        stockDTO.setFilterDateFrom(filterDateFrom);
        stockDTO.setFilterDateTo(filterDateTo);
        stockDTO.setFilterKeyword(filterKeyword);
        stockDTO.setFilterExpiry(filterExpiry);

        Map map = service.getListStock(stockDTO);
        map.put("size",       iSize);
        map.put("page",       iPage);
        map.put("vendorList", service.getVendorList());
        map.put("itemList",   service.getItemList());
        map.put("groupList",  service.getGroupList());
        map.put("filterIoType",   filterIoType);
        map.put("filterVendorId", filterVendorId);
        map.put("filterGId",      filterGId);
        map.put("filterItemId",   filterItemId);
        map.put("filterDateFrom", filterDateFrom);
        map.put("filterDateTo",   filterDateTo);
        map.put("filterKeyword",  filterKeyword);
        map.put("filterExpiry",   filterExpiry);
        map.put("expiryWarnCount", service.getExpiryWarnCount());
        map.put("expiryOverCount", service.getExpiryOverCount());

        model.addAttribute("map", map);
        return "P05_stock/io";
    }

    @PostMapping
    public Object doPost(HttpServletRequest request, Model model) {
        StockDTO dto = new StockDTO();
        try {
            dto.setIo_type(Integer.parseInt(request.getParameter("io_type")));
            dto.setIo_reason(request.getParameter("io_reason"));
            dto.setVender_id(request.getParameter("vender_id"));
            dto.setItem_id(request.getParameter("item_id"));
            dto.setLot_qty(Integer.parseInt(request.getParameter("lot_qty")));
            dto.setIo_qty(Integer.parseInt(request.getParameter("lot_qty")));
            String ioTimeStr = request.getParameter("io_time");
            ZonedDateTime kst = LocalDateTime.parse(ioTimeStr + "T00:00:00")
                                             .atZone(ZoneId.of("Asia/Seoul"));
            dto.setIo_time(Timestamp.from(kst.toInstant()));

            // 출고 시 기존 lot_id 사용
            String lotId = request.getParameter("lot_id");
            if (lotId != null && !lotId.isEmpty()) {
                dto.setLot_id(lotId);
            }

            String expiryStr = request.getParameter("expiry_date");
            if (expiryStr != null && !expiryStr.isEmpty()) {
                dto.setExpiry_date(Date.valueOf(expiryStr));
            }

            dto.setEmp_id(request.getParameter("emp_id"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dto.getIo_type() == 1) {  // 출고 시 재고 검사
            int currentStock = service.getStockNo(dto.getItem_id());
            if (currentStock - dto.getLot_qty() < 0) {
                model.addAttribute("errorMsg",
                    "재고 부족입니다. (현재 재고: " + currentStock + ")");
                return "P05_stock/io";
            }
        }

        service.insert(dto);
        return "redirect:/io";
    }
}
