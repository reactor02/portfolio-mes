package P07_work.cotroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P07_work.WoAddDTO;
import P07_work.WoDTO;
import P07_work.WoService;

@Controller
@RequestMapping("/womodify")
public class WoModifyController {

    @Autowired
    private WoService woService;

    @GetMapping
    public String doGet(@RequestParam("woId") String woId, Model model) {
        System.out.println("/womodify doGet 실행");

        setContent(woId, model);
        return "P07_work/woModify";
    }

    @PostMapping
    public String doPost(@RequestParam(value = "cmd", required = false) String cmd,
                         @RequestParam(value = "woId", required = false) String woId,
                         @RequestParam(value = "targetQty", required = false) String targetQtyStr,
                         @RequestParam(value = "workDate", required = false) String workDate,
                         @RequestParam(value = "workerId", required = false) String worker,
                         @RequestParam(value = "content", required = false) String content) {
        System.out.println("/womodify doPost 실행");

        if ("update".equals(cmd)) {
            return modifyWo(woId, targetQtyStr, workDate, worker, content);
        } else if ("delete".equals(cmd)) {
            return deleteWo(woId);
        }

        return "redirect:/worklist";
    }

    private void setContent(String woId, Model model) {
        System.out.println("/womodify setContent 실행");

        WoDTO dto = new WoDTO();
        dto.setWoId(woId);

        WoDTO woDTO = woService.detail(dto);
        System.out.println(dto);

        model.addAttribute("woInfo", dto);
    }

    private String modifyWo(String woId, String targetQtyStr, String workDate,
                             String worker, String content) {
        System.out.println("/workadd modifyWo 실행");

        int woQty = Integer.parseInt(targetQtyStr);

        WoAddDTO addDTO = new WoAddDTO();
        addDTO.setWoId(woId);
        addDTO.setWoQty(woQty);
        addDTO.setWorkDate(workDate);
        addDTO.setWorker(worker);
        addDTO.setContent(content);

        int result = woService.modifyOrder(addDTO);
        System.out.println(result);

        return "redirect:/workorder?woId=" + addDTO.getWoId();
    }

    private String deleteWo(String woId) {
        System.out.println("/workadd deleteWo 실행");

        int result = woService.deleteOrder(woId);
        System.out.println(result);

        return "redirect:/worklist";
    }
}
