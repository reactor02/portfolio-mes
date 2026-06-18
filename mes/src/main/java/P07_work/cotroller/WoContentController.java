package P07_work.cotroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P07_work.WoDTO;
import P07_work.WoService;

@Controller
@RequestMapping("/contentmodify")
public class WoContentController {

    @Autowired
    private WoService woService;

    @GetMapping
    public String doGet(@RequestParam("woId") String woId, Model model) {
        System.out.println("/contentmodify doGet 실행");

        setContent(woId, model);
        return "P07_work/contentModify";
    }

    @PostMapping
    public String doPost(@RequestParam("woId") String woId,
                         @RequestParam(value = "status", required = false) String statusStr,
                         @RequestParam(value = "prevQty", required = false) String prevQtyStr,
                         @RequestParam(value = "worker", required = false) String worker,
                         Model model) {
        System.out.println("/contentmodify doPost 실행");

        try {
            if (woId == null || woId.trim().isEmpty()) {
                throw new RuntimeException("작업지시 코드가 없습니다.");
            }

            if (statusStr == null || statusStr.trim().isEmpty()) {
                throw new RuntimeException("작업 상태가 선택되지 않았습니다.");
            }

            if (worker == null || worker.trim().isEmpty()) {
                throw new RuntimeException("작업자 정보가 없습니다.");
            }

            int status = Integer.parseInt(statusStr);

            int prevQty = 0;
            if (status == 30) {
                if (prevQtyStr == null || prevQtyStr.trim().isEmpty()) {
                    throw new RuntimeException("완료 수량이 입력되지 않았습니다.");
                }
                prevQty = Integer.parseInt(prevQtyStr);
            } else {
                if (prevQtyStr != null && !prevQtyStr.trim().isEmpty()) {
                    prevQty = Integer.parseInt(prevQtyStr);
                }
            }

            woService.processContentModify(woId, status, prevQty, worker);

            return "redirect:/workorder?woId=" + woId;

        } catch (NumberFormatException e) {
            model.addAttribute("errorMsg", "숫자 형식이 올바르지 않습니다.");
            setContent(woId, model);
            return "P07_work/contentModify";

        } catch (RuntimeException e) {
            model.addAttribute("errorMsg", e.getMessage());
            setContent(woId, model);
            return "P07_work/contentModify";
        }
    }

    private void setContent(String woId, Model model) {
        System.out.println("/contentmodify setContent 실행");

        WoDTO dto = new WoDTO();
        dto.setWoId(woId);

        WoDTO woDTO = woService.detail(dto);
        System.out.println(dto);

        model.addAttribute("woInfo", woDTO);
    }
}
