package P11_masterdata.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P01_auth.LoginDTO;
import P11_masterdata.DAO.VendorDAO;
import P11_masterdata.DTO.VendorDTO;

@Controller
@RequestMapping("/vendorUpdate")
public class VendorUpdateController {

	@GetMapping
	public String doGet() {
		return "redirect:/vendor";
	}

	@PostMapping
	public String doPost(
			@RequestParam(required = false) String vendor_id,
			@RequestParam(required = false) String vendor_type,
			@RequestParam(required = false) String vendor_name,
			@RequestParam(required = false) String phone_no,
			@RequestParam(required = false) String addr,
			@RequestParam(required = false, defaultValue = "") String emp_id,
			HttpSession session) {

		emp_id = emp_id.trim();
		if (emp_id.isEmpty()) {
			emp_id = findSessionEmpId(session);
		}
		if (emp_id.isEmpty()) {
			emp_id = null;
		}

		VendorDTO vendorDTO = new VendorDTO();
		vendorDTO.setVendor_id(vendor_id);
		vendorDTO.setVendor_type(vendor_type);
		vendorDTO.setVendor_name(vendor_name);
		vendorDTO.setPhone_no(phone_no);
		vendorDTO.setAddr(addr);
		vendorDTO.setEmp_id(emp_id);

		VendorDAO vendorDAO = new VendorDAO();
		vendorDAO.updateVendor(vendorDTO);

		return "redirect:/vendor";
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
