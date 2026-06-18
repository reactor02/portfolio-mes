package P11_masterdata.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import P01_auth.LoginDTO;
import P11_masterdata.DAO.VendorDAO;
import P11_masterdata.DTO.VendorDTO;

@Controller
@RequestMapping("/vendorr")
public class VendorController {

	@GetMapping
	public String doGet(HttpSession session, Model model) {

		VendorDAO vendorDAO = new VendorDAO();
		List<VendorDTO> list = vendorDAO.selectAll();

		int totalVendor = 0;
		int vendorTypeA = 0;
		int vendorTypeB = 0;

		if (list != null) {
			totalVendor = list.size();
			for (VendorDTO dto : list) {
				if ("공급업체".equals(dto.getVendor_type())) {
					vendorTypeA++;
				} else if ("고객사".equals(dto.getVendor_type())) {
					vendorTypeB++;
				}
			}
		}

		model.addAttribute("vendor", list);
		model.addAttribute("list", list);
		model.addAttribute("nextVendorId", vendorDAO.selectNextVendorId());
		model.addAttribute("totalVendor", totalVendor);
		model.addAttribute("vendorTypeA", vendorTypeA);
		model.addAttribute("vendorTypeB", vendorTypeB);
		model.addAttribute("page", 1);
		model.addAttribute("size", list != null ? list.size() : 0);
		model.addAttribute("totalPage", 1);
		model.addAttribute("currentEmpId", findSessionEmpId(session));

		return "P11_masterdata/vendor";
	}

	private String findSessionEmpId(HttpSession session) {
		if (session == null) return "";
		try {
			LoginDTO dto = (LoginDTO) session.getAttribute("dto");
			if (dto != null && dto.getEmpid() != null && !dto.getEmpid().trim().isEmpty()) {
				return dto.getEmpid().trim();
			}
		} catch (Exception e) { }
		Object empId = session.getAttribute("emp_id");
		if (empId != null) return String.valueOf(empId).trim();
		return "";
	}
}
