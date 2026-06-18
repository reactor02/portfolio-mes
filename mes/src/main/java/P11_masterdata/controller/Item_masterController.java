package P11_masterdata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/itemMaster")
public class Item_masterController {

	@GetMapping
	public String doGet() {
		return "redirect:/itemmaster";
	}
}
