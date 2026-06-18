package P05_stock;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/stock")
public class Stock2Conttroller {

    @Autowired
    Stock2Service service;

    @GetMapping
    public String doGet(
            @RequestParam(required = false) String size,
            @RequestParam(required = false) String page,
            @RequestParam(required = false) String filterGId,
            @RequestParam(required = false) String filterKeyword,
            @RequestParam(required = false) String filterStock,
            Model model) {

        int iSize = 10;
        int iPage = 1;
        try { if (size != null) iSize = Integer.parseInt(size); } catch (Exception e) {}
        try { if (page != null) iPage = Integer.parseInt(page); } catch (Exception e) {}
        if (filterGId     != null && filterGId.isEmpty())     filterGId     = null;
        if (filterKeyword != null && filterKeyword.isEmpty()) filterKeyword = null;
        if (filterStock   != null && filterStock.isEmpty())   filterStock   = null;

        Stock2DTO dto = new Stock2DTO();
        dto.setSize(iSize);
        dto.setPage(iPage);
        dto.setFilterGId(filterGId);
        dto.setFilterKeyword(filterKeyword);
        dto.setFilterStock(filterStock);

        Map map = service.getStockList(dto);
        map.put("size",          iSize);
        map.put("page",          iPage);
        map.put("groupList",     service.getGroupList());
        map.put("filterGId",     filterGId);
        map.put("filterKeyword", filterKeyword);
        map.put("filterStock",   filterStock);
        map.put("totalStock",    service.getTotalCount());
        map.put("normalStock",   service.getNormalCount());
        map.put("lackStock",     service.getLackCount());

        model.addAttribute("map", map);
        return "P05_stock/stock";
    }

    @PostMapping
    public Object doPost(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String item_id,
            @RequestParam(required = false) String safe_no) {

        if ("updateSafeNo".equals(action)) {
            int safeNo = 0;
            try { safeNo = Integer.parseInt(safe_no); } catch (Exception e) {}
            service.updateSafeNo(item_id, safeNo);
            return ResponseEntity.ok("{\"result\":\"ok\"}");
        }

        return "redirect:/stock";
    }
}
