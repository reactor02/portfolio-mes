package P07_work.cotroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P07_work.ProcessDTO;
import P07_work.WoBOMDTO;
import P07_work.WoDTO;
import P07_work.WoService;

@Controller
@RequestMapping("/workorder")
public class WoDetailController {

    @Autowired
    private WoService woService;

    @GetMapping
    public String doGet(@RequestParam("woId") String woId, Model model) {
        System.out.println("/workorder doGet 실행");

        setting(woId, model);
        setBOM(woId, model);
        setProcess(woId, model);

        return "P07_work/detail";
    }

    @PostMapping
    public String doPost() {
        System.out.println("/workorder doPost 실행");
        return "P07_work/detail";
    }

    private void setting(String woId, Model model) {
        System.out.println("/workorder setting 실행");

        WoDTO dto = new WoDTO();
        dto.setWoId(woId);

        dto = woService.detail(dto);
        System.out.println(dto);

        model.addAttribute("woInfo", dto);
    }

    private void setBOM(String woId, Model model) {
        System.out.println("/workorder setBOM 실행");

        WoBOMDTO dto = new WoBOMDTO();
        dto.setWoId(woId);

        List<WoBOMDTO> bom = woService.setBOM(dto);
        System.out.println(bom);

        model.addAttribute("bom", bom);
    }

    private void setProcess(String woId, Model model) {
        System.out.println("/workorder setProcess 실행");
        System.out.println(woId);

        ProcessDTO dto = new ProcessDTO();
        dto.setWoId(woId);

        List<ProcessDTO> process = woService.setProcess(dto);
        System.out.println(process);

        model.addAttribute("process", process);
    }
}
