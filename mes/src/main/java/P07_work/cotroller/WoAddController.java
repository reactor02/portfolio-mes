package P07_work.cotroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import P01_auth.DTO.UserWoDTO;
import P06_prod.DTO.PlanWoDTO;
import P07_work.WoAddDTO;
import P07_work.WoService;

@Controller
@RequestMapping("/workadd")
public class WoAddController {

    @Autowired
    private WoService woService;

    @GetMapping
    public String doGet(@RequestParam(value = "cmd", required = false) String cmd,
                        @RequestParam(value = "planId", required = false) String planId,
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        Model model) {
        System.out.println("/workadd doGet 실행");

        if ("getPlan".equals(cmd)) {
            return null; // handled by separate @ResponseBody method
        }

        if ("searchWorker".equals(cmd)) {
            return null; // handled by separate @ResponseBody method
        }

        planList(model);
        return "P07_work/add";
    }

    @GetMapping(params = "cmd=getPlan")
    @ResponseBody
    public ResponseEntity<String> getPlan(@RequestParam("planId") String planId) {
        System.out.println("/workadd getPlan 실행");

        PlanWoDTO planDTO = new PlanWoDTO();
        planDTO.setPlanId(planId);

        planDTO = woService.getPlan(planDTO);

        String json = "{"
            + "\"planId\":\"" + planDTO.getPlanId() + "\","
            + "\"itemId\":\"" + planDTO.getItemId() + "\","
            + "\"itemName\":\"" + planDTO.getItemName() + "\","
            + "\"prevQty\":\"" + planDTO.getPrevQty() + "\","
            + "\"planQty\":\"" + planDTO.getPlanQty() + "\","
            + "\"dName\":\"" + planDTO.getdName() + "\","
            + "\"dId\":\"" + planDTO.getdId() + "\","
            + "\"sDate\":\"" + planDTO.getsDate() + "\","
            + "\"eDate\":\"" + planDTO.geteDate() + "\""
            + "}";

        return ResponseEntity.ok()
            .header("Content-Type", "application/json; charset=UTF-8")
            .body(json);
    }

    @GetMapping(params = "cmd=searchWorker")
    @ResponseBody
    public ResponseEntity<String> searchWorker(
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        System.out.println("/workadd searchWorker 실행");

        List<UserWoDTO> list = woService.searchWorker(keyword.trim());

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            UserWoDTO e = list.get(i);
            json.append("{")
                .append("\"empId\":\"").append(e.getEmpId()).append("\",")
                .append("\"empName\":\"").append(e.geteName()).append("\"")
                .append("}");
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");

        return ResponseEntity.ok()
            .header("Content-Type", "application/json; charset=UTF-8")
            .body(json.toString());
    }

    @PostMapping
    public String doPost(@RequestParam("planId") String planId,
                         @RequestParam("targetQty") int woQty,
                         @RequestParam("workDate") String workDate,
                         @RequestParam("workerId") String worker,
                         @RequestParam(value = "content", required = false) String content) {
        System.out.println("/workadd doPost 실행");

        WoAddDTO addDTO = new WoAddDTO();
        addDTO.setPlanId(planId);
        addDTO.setWoQty(woQty);
        addDTO.setWorkDate(workDate);
        addDTO.setWorker(worker);
        addDTO.setContent(content);

        int result = woService.addOrder(addDTO);
        System.out.println(result);

        return "redirect:/worklist";
    }

    private void planList(Model model) {
        System.out.println("/workadd planList 실행");
        List planList = woService.planList();
        model.addAttribute("planList", planList);
    }

    private String safe(String str) {
        return str == null ? "" : str.replace("\"", "\\\"");
    }
}
