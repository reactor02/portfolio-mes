package P11_masterdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import P11_masterdata.DAO.BomDAO;

/**
 * 구버전 BOM 컨트롤러 (레거시) - /bomOld 경로
 */
@Controller
@RequestMapping("/bomOld")
public class BomController {

	@GetMapping
	public String doGet(Model model) {
		BomDAO bomDAO = new BomDAO();
		List list = bomDAO.selectAll();
		model.addAttribute("bomList", list);
		return "P11_masterdata/bom";
	}
}
