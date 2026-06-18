<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="java.util.*"%>

<c:set var="isSuperAdmin"
	value="${not empty sessionScope.dto and sessionScope.dto.empid eq 'user_1001'}" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>거래처 관리</title>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/css/P00_common/common.css">

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/css/P00_layout/header.css">
<script
	src="${pageContext.request.contextPath}/static/js/00_layout/header.js"></script>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/css/P00_layout/snb.css">
<script
	src="${pageContext.request.contextPath}/static/js/00_layout/snb.js"></script>

<link rel="stylesheet"
	href="${pageContext.request.contextPath}/static/css/P11_masterdata/vendor.css">
</head>

<body>

	<%@ include file="/WEB-INF/views/P00_layout/header.jsp"%>

	<div class="layout_snb">
		<div class="snbContent">
			<%@ include file="/WEB-INF/views/P00_layout/snb.jsp"%>
		</div>

		<div class="content">
			<div class="page-wrapper">
				<div class="page-top">
					<div class="page-header">
						<h1>거래처 관리</h1>
						<p>공급업체 및 고객사 거래처 정보를 관리</p>
					</div>
					<c:if test="${isSuperAdmin}">
						<button type="button" class="btn-add">+ 거래처 등록</button>
					</c:if>
				</div>

				<div class="summary-cards">
					<div class="summary-box">
						<div class="summary-title">전체 거래처</div>
						<div class="summary-value">${totalVendor}</div>
					</div>
					<div class="summary-box">
						<div class="summary-title">공급업체</div>
						<div class="summary-value">${vendorTypeA}</div>
					</div>
					<div class="summary-box">
						<div class="summary-title">고객사</div>
						<div class="summary-value">${vendorTypeB}</div>
					</div>
				</div>

				<div class="list-section">
					<h2>거래처 목록</h2>

					<form class="filter-row" method="get"
						action="${pageContext.request.contextPath}/vendor">
						<input type="hidden" name="page" value="1"> <input
							type="hidden" name="size" value="${size}"> <select
							id="vendorType" name="vendorType" onchange="this.form.submit()">
							<option value="" ${empty vendorType ? 'selected' : ''}>선택</option>
							<option value="공급업체" ${vendorType eq '공급업체' ? 'selected' : ''}>공급업체</option>
							<option value="고객사" ${vendorType eq '고객사' ? 'selected' : ''}>고객사</option>
						</select>

						<div class="search-wrap">
							<input type="text" id="searchKeyword" name="keyword"
								value="${keyword}" placeholder="거래처명 검색..." />
							<button type="submit" id="searchBtn" class="btn-search">검색</button>
						</div>
					</form>

					<div class="table-wrap">
						<table>
							<thead>
								<tr>
									<th>거래처코드</th>
									<th>거래처명</th>
									<th>거래처분류</th>
									<th>연락처</th>
									<th>주소</th>
									<th>관리</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${empty list}">
										<tr>
											<td colspan="6" class="empty-row">조회된 거래처가 없습니다.</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach var="vendor" items="${list}">
											<tr>
												<td>${vendor.vendor_id}</td>
												<td>${vendor.vendor_name}</td>
												<td><span
													class="badge ${vendor.vendor_type eq '공급업체' ? 'supplier' : 'customer'}">
														${vendor.vendor_type} </span></td>
												<td>${vendor.phone_no}</td>
												<td>${vendor.addr}</td>
												<%-- 												<td>${vendor.emp_id}</td> --%>
												<td>
													<div class="action-btns">
														<button type="button" class="icon-btn edit"
															data-vendor-id="${vendor.vendor_id}"
															data-vendor-name="${fn:escapeXml(vendor.vendor_name)}"
															data-vendor-type="${fn:escapeXml(vendor.vendor_type)}"
															data-phone-no="${vendor.phone_no}"
															data-addr="${fn:escapeXml(vendor.addr)}"
															data-emp-id="${fn:escapeXml(vendor.emp_id)}">
															수정</button>
													</div>
												</td>
											</tr>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>

						<div class="pagination">
							<c:forEach var="i" begin="1" end="${totalPage}">
								<a
									href="${pageContext.request.contextPath}/vendor?page=${i}&size=${size}&vendorType=${vendorType}&keyword=${keyword}"
									class="${page == i ? 'active' : ''}">${i}</a>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<!-- 수정 모달 -->
			<div class="edit_vendor_modal">
				<div class="edit_vendor_modal_popup">
					<form action="${pageContext.request.contextPath}/vendorUpdate"
						method="post">
						<input type="hidden" id="edit_emp_id" name="emp_id"
							value="${not empty currentEmpId ? currentEmpId : (not empty sessionScope.dto.empid ? sessionScope.dto.empid : sessionScope.dto.empId)}">

						<h3 class="edit_vendor_modal_title">거래처 수정</h3>

						<div class="edit_vendor_form_row">
							<div class="edit_vendor_form_group code">
								<label>거래처코드</label> <input id="edit_vendor_id" name="vendor_id"
									class="edit_vendor_info readonly" type="text" readonly>
							</div>

							<div class="edit_vendor_form_group group">
								<label>거래처분류</label> <select id="edit_vendor_type"
									name="vendor_type" class="edit_vendor_info">
									<option value="공급업체">공급업체</option>
									<option value="고객사">고객사</option>
								</select>
							</div>

						</div>

						<div class="edit_vendor_form_row second">
							<div class="edit_vendor_form_group name-group">
								<label>거래처명</label> <input id="edit_vendor_name"
									name="vendor_name" type="text" class="edit_vendor_info">
							</div>
						</div>

						<div class="edit_vendor_form_row second">
							<div class="edit_vendor_form_group small">
								<label>연락처</label> <input type="text" id="edit_phone_no"
									name="phone_no" class="edit_vendor_info" min="0">
							</div>

							<div class="edit_vendor_form_group name-group">
								<label>주소</label> <input type="text" id="edit_addr" name="addr"
									class="edit_vendor_info">
							</div>
						</div>

						<div class="edit_vendor_btn_area">
							<button type="button" class="edit_vendor_close_btn">닫기</button>
							<button type="submit" class="edit_vendor_save_btn">수정</button>
						</div>

					</form>
				</div>
			</div>
		</div>
	</div>

	<c:if test="${isSuperAdmin}">
		<!-- 등록 모달 -->
		<div class="add_vendor_modal" id="addVendorModal"
			style="display: none;">
			<div class="add_vendor_modal_popup">
				<form action="${pageContext.request.contextPath}/vendorAdd"
					method="post">
				<input type="hidden" id="add_emp_id" name="emp_id"
					value="${not empty currentEmpId ? currentEmpId : (not empty sessionScope.dto.empid ? sessionScope.dto.empid : sessionScope.dto.empId)}">

				<h3 class="add_vendor_modal_title">거래처 등록</h3>

				<div class="add_vendor_form_row">
					<div class="add_vendor_form_group code">
						<label>거래처코드</label> <input type="text" id="add_vendor_id"
							class="add_vendor_info readonly" name="vendor_id"
							value="${nextVendorId}" readonly>
					</div>

					<div class="add_vendor_form_group group">
						<label>거래처분류</label> <select id="add_vendor_type"
							class="add_vendor_info" name="vendor_type">
							<option value="공급업체">공급업체</option>
							<option value="고객사">고객사</option>
						</select>
					</div>
				</div>

				<div class="add_vendor_form_row second">
					<div class="add_vendor_form_group name-group">
						<label>거래처명</label> <input type="text" id="add_vendor_name"
							name="vendor_name" class="add_vendor_info"
							placeholder="거래처명을 입력하세요">
					</div>
				</div>

				<div class="add_vendor_form_row second">
					<div class="add_vendor_form_group small">
						<label>연락처</label> <input type="text" id="add_phone_no"
							class="add_vendor_info" name="phone_no" min="0"
							placeholder="연락처를 입력하세요">
					</div>

					<div class="add_vendor_form_group name-group">
						<label>주소</label> <input type="text" id="add_addr"
							class="add_vendor_info" name="addr" placeholder="주소를 입력하세요">
					</div>
				</div>

				<div class="add_vendor_btn_area">
					<button type="button" class="add_vendor_close_btn"
						id="cancelAddVendorModal">닫기</button>
					<button type="submit" class="add_vendor_save_btn"
						id="saveAddVendorModal">등록</button>
				</div>

				</form>
			</div>
		</div>
	</c:if>

	<script
		src="${pageContext.request.contextPath}/static/js/11_masterdata/vendor.js"></script>
</body>
</html>
