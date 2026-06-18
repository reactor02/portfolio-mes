package P07_work.cotroller;

import java.sql.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P07_work.SearchDTO;
import P07_work.WoDTO;
import P07_work.WoService;

@Controller
@RequestMapping("/worklist")
public class WoMainController {

    @Autowired
    private WoService woService;

    @GetMapping
    public String doGet(@RequestParam(value = "cmd", required = false) String cmd,
                        @RequestParam(value = "page", required = false) String pageStr,
                        @RequestParam(value = "status", required = false) String statusStr,
                        @RequestParam(value = "startDate", required = false) String sDateStr,
                        @RequestParam(value = "endDate", required = false) String eDateStr,
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "woId", required = false) String woId,
                        Model model) {
        System.out.println("/worklist doGet 실행");

        if ("search".equals(cmd)) {
            search(pageStr, statusStr, sDateStr, eDateStr, keyword, model);
        } else if ("detail".equals(cmd)) {
            return detail(woId);
        } else {
            getList(pageStr, model);
        }

        return "P07_work/main";
    }

    @PostMapping
    public String doPost() {
        System.out.println("/worklist doPost 실행");
        return "P07_work/main";
    }

    private void getList(String pageStr, Model model) {
        System.out.println("/worklist getList 실행");

        int size = 10;
        int page = 1;

        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
        }

        WoDTO dto = new WoDTO();
        dto.setSize(size);
        dto.setPage(page);

        Map woMap = woService.getList(dto);
        model.addAttribute("woMap", woMap);
    }

    private void search(String pageStr, String statusStr, String sDateStr, String eDateStr,
                        String keyword, Model model) {
        System.out.println("/worklist search 실행");

        int size = 10;
        int page = 1;

        try {
            page = Integer.parseInt(pageStr);
        } catch (Exception e) {
        }

        WoDTO dto = new WoDTO();
        dto.setSize(size);
        dto.setPage(page);

        int status = 0;
        if (statusStr != null && !statusStr.isEmpty()) {
            status = Integer.parseInt(statusStr);
        }

        SearchDTO search = new SearchDTO();
        search.setStatus(status);
        search.setsDate(sDateStr);
        search.seteDate(eDateStr);
        search.setKeyword(keyword != null ? keyword.trim() : "");

        Map map = woService.search(dto, search);
        model.addAttribute("woMap", map);
    }

    private String detail(String woId) {
        System.out.println("/worklist detail 실행");
        return "redirect:/workorder?woId=" + woId;
    }
}
