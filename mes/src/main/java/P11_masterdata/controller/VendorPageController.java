package P11_masterdata.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import P01_auth.LoginDTO;
import P11_masterdata.DAO.VendorDAO;
import P11_masterdata.DTO.VendorDTO;

@Controller
@RequestMapping("/vendor")
public class VendorPageController {

	@GetMapping
	public String doGet(
			@RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String vendorType,
			@RequestParam(required = false) String keyword,
			HttpSession session,
			Model model) {

		if (size < 1) size = 5;
		if (page < 1) page = 1;

		VendorDTO vendorDTO = new VendorDTO();
		vendorDTO.setSize(size);
		vendorDTO.setPage(page);
		vendorDTO.setVendorType(vendorType);
		vendorDTO.setKeyword(keyword);

		Map<String, Object> map = vendorlistSelect(vendorDTO);

		model.addAttribute("vendor", map.get("list"));
		model.addAttribute("list", map.get("list"));
		model.addAttribute("page", map.get("page"));
		model.addAttribute("size", map.get("size"));
		model.addAttribute("totalPage", map.get("totalPage"));
		model.addAttribute("totalCount", map.get("totalCount"));
		model.addAttribute("vendorType", vendorType);
		model.addAttribute("keyword", keyword);
		model.addAttribute("nextVendorId", map.get("nextVendorId"));
		model.addAttribute("totalVendor", map.get("totalVendor"));
		model.addAttribute("vendorTypeA", map.get("vendorTypeA"));
		model.addAttribute("vendorTypeB", map.get("vendorTypeB"));
		model.addAttribute("currentEmpId", findSessionEmpId(session));

		return "P11_masterdata/vendor";
	}

	private Map<String, Object> vendorlistSelect(VendorDTO vendorDTO) {
		int size = vendorDTO.getSize() == 0 ? 5 : vendorDTO.getSize();
		int page = vendorDTO.getPage() == 0 ? 1 : vendorDTO.getPage();

		int end = page * size;
		int start = end - (size - 1);
		vendorDTO.setStart(start);
		vendorDTO.setEnd(end);
		vendorDTO.setSize(size);
		vendorDTO.setPage(page);

		VendorDAO vendorDAO = new VendorDAO();

		int totalCount = vendorDAO.selectVendorTotalCount(vendorDTO);
		int totalVendor = vendorDAO.selectVendorTotalCountAll();
		int vendorTypeA = vendorDAO.selectVendorTypeCount("공급업체");
		int vendorTypeB = vendorDAO.selectVendorTypeCount("고객사");

		int totalPage = totalCount / size;
		if (totalCount % size != 0) totalPage++;
		if (totalPage == 0) totalPage = 1;

		if (page > totalPage) {
			page = totalPage;
			end = page * size;
			start = end - (size - 1);
			vendorDTO.setPage(page);
			vendorDTO.setStart(start);
			vendorDTO.setEnd(end);
		}

		List<VendorDTO> list = vendorDAO.selectVendorPageList(vendorDTO);

		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		map.put("totalCount", totalCount);
		map.put("totalPage", totalPage);
		map.put("page", page);
		map.put("size", size);
		map.put("nextVendorId", vendorDAO.selectNextVendorId());
		map.put("totalVendor", totalVendor);
		map.put("vendorTypeA", vendorTypeA);
		map.put("vendorTypeB", vendorTypeB);
		return map;
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
