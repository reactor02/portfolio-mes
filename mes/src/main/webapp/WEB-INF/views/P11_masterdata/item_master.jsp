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
<title>품목 마스터</title>

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
	href="${pageContext.request.contextPath}/static/css/P11_masterdata/item_master.css">
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
						<h1>품목 마스터</h1>
						<p>제품 및 원자재 품목 정보를 관리</p>
					</div>
					<c:if test="${isSuperAdmin}">
						<button type="button" class="btn-add">+ 품목 등록</button>
					</c:if>
				</div>

				<div class="summary-cards">
					<div class="summary-box">
						<div class="summary-title">전체 품목</div>
						<div class="summary-value">${totalCount}</div>
					</div>
					<div class="summary-box">
						<div class="summary-title">완제품</div>
						<div class="summary-value">${finCount}</div>
					</div>
					<div class="summary-box">
						<div class="summary-title">반제품</div>
						<div class="summary-value">${semiCount}</div>
					</div>
					<div class="summary-box">
						<div class="summary-title">원자재</div>
						<div class="summary-value">${rawCount}</div>
					</div>
				</div>

				<div class="list-section">
					<h2>품목 목록</h2>

					<form class="filter-row" method="get"
						action="${pageContext.request.contextPath}/itemmaster">
						<input type="hidden" name="page" value="1"> <input
							type="hidden" name="size" value="${size}"> <select
							id="itemGroup" name="itemGroup" onchange="this.form.submit()">
							<option value="" <c:if test="${empty itemGroup}">selected</c:if>>선택</option>
							<option value="30"
								<c:if test="${itemGroup eq '30'}">selected</c:if>>완제품</option>
							<option value="20"
								<c:if test="${itemGroup eq '20'}">selected</c:if>>반제품</option>
							<option value="10"
								<c:if test="${itemGroup eq '10'}">selected</c:if>>원자재</option>
						</select>

						<div class="search-wrap">
							<input type="text" id="searchKeyword" name="keyword"
								value="${keyword}" placeholder="품목명 검색..." />
							<button type="submit" id="searchBtn" class="btn-search">검색</button>
						</div>
					</form>

					<div class="table-wrap">
						<table>
							<thead>
								<tr>
									<th>품목코드</th>
									<th>품목그룹</th>
									<th>품목명</th>
									<th>안전재고</th>
									<th>단가</th>
									<th>규격</th>
									<th>단위</th>
									<th>관리</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${empty list}">
										<tr>
											<td colspan="8">조회된 품목이 없습니다.</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach var="item" items="${list}">
											<tr data-g-id="${item.g_id}">
												<td>${item.item_id}</td>
												<td><c:choose>
														<c:when test="${not empty item.itemgroup_name}">
															<c:choose>
																<c:when test="${fn:toLowerCase(item.itemgroup_name) eq 'fin'}">완제품</c:when>
																<c:when test="${fn:toLowerCase(item.itemgroup_name) eq 'semi'}">반제품</c:when>
																<c:when test="${fn:toLowerCase(item.itemgroup_name) eq 'raw'}">원자재</c:when>
																<c:otherwise>${item.itemgroup_name}</c:otherwise>
															</c:choose>
														</c:when>
														<c:when test="${item.g_id == 30}">완제품</c:when>
														<c:when test="${item.g_id == 20}">반제품</c:when>
														<c:when test="${item.g_id == 10}">원자재</c:when>
														<c:otherwise>-</c:otherwise>
													</c:choose></td>
												<td><span class="item-name-text">${item.item_name}</span></td>
												<td><fmt:formatNumber value="${item.safe_qty}"
														pattern="#,##0" /></td>
												<td><fmt:formatNumber value="${item.pay}"
														pattern="#,##0" /></td>
												<td>${item.spec}</td>
												<td>${item.unit}</td>
												<td>
													<div class="action-btns">
														<button type="button" class="icon-btn edit"
															data-item-id="${item.item_id}"
															data-item-name="${fn:escapeXml(item.item_name)}"
															data-g-id="${item.g_id}" data-safe-qty="${item.safe_qty}"
															data-pay="${item.pay}"
															data-spec="${fn:escapeXml(item.spec)}"
															data-unit="${fn:escapeXml(item.unit)}">수정</button>
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
									href="${pageContext.request.contextPath}/itemmaster?page=${i}&size=${size}&itemGroup=${itemGroup}&keyword=${keyword}"
									class="${page == i ? 'active' : ''}"> ${i} </a>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>

			<div class="edit_item_modal">
				<div class="edit_item_modal_popup">
					<form action="${pageContext.request.contextPath}/itemUpdate"
						method="post">

						<h3 class="edit_item_modal_title">품목마스터 수정</h3>

						<div class="edit_item_form_row">
							<div class="edit_item_form_group code">
								<label>품목코드</label> <input id="edit_item_id" name="item_id"
									class="edit_item_info readonly" type="text" readonly>
							</div>

							<div class="edit_item_form_group group">
								<label>품목 그룹</label>
								<input id="edit_g_id_display" class="edit_item_info readonly"
									type="text" readonly>
								<input id="edit_g_id" name="g_id" type="hidden">
							</div>

							<div class="edit_item_form_group small">
								<label>안전재고</label> <input type="number" id="edit_safe_qty"
									name="safe_qty" class="edit_item_info" min="0">
							</div>

							<div class="edit_item_form_group small">
								<label>단가</label> <input type="text" id="edit_pay" name="pay"
									class="edit_item_info" value="0">
							</div>

							<div class="edit_item_form_group small">
								<label>규격</label> <input type="text" id="edit_spec" name="spec"
									class="edit_item_info">
							</div>

							<div class="edit_item_form_group small">
								<label>단위</label> <select id="edit_unit" name="unit"
									class="edit_item_info">
									<option value="L">L</option>
									<option value="m">m</option>
									<option value="cm">cm</option>
									<option value="장">장</option>
									<option value="EA">EA</option>
									<option value="mL">mL</option>
								</select>
							</div>
						</div>

						<div class="edit_item_form_row second">
							<div class="edit_item_form_group name-group">
								<label>품목명</label>
								<div class="edit_item_name_wrap">
									<input id="edit_item_name" name="item_name" type="text"
										class="edit_item_info">
								</div>
							</div>
						</div>

						<div class="edit_item_btn_area">
							<button type="button" class="edit_item_close_btn">닫기</button>
							<button type="submit" class="edit_item_save_btn">수정</button>
						</div>

					</form>
				</div>
			</div>
		</div>
	</div>

	<c:if test="${isSuperAdmin}">
		<div class="add_item_modal" id="addItemModal" style="display: none;">
			<div class="add_item_modal_popup">
				<form action="${pageContext.request.contextPath}/itemMasterAdd"
					method="post">

				<h3 class="add_item_modal_title">품목 등록</h3>

				<div class="add_item_form_row">
					<div class="add_item_form_group code">
						<label>품목코드</label> <input type="text" id="add_item_id"
							class="add_item_info readonly" name="item_id" readonly>
					</div>

					<div class="add_item_form_group group">
						<label>품목 그룹</label> <select id="add_g_id" class="add_item_info"
							name="g_id">
							<option value="30">완제품</option>
							<option value="20">반제품</option>
							<option value="10">원자재</option>
						</select> <input type="hidden" id="add_itemgroup_name"
							name="itemgroup_name" value="">
					</div>

					<div class="add_item_form_group small">
						<label>안전재고</label> <input type="number" id="add_safe_qty"
							class="add_item_info" name="safe_qty" min="0" value="0">
					</div>

					<div class="add_item_form_group small">
						<label>단가</label> <input type="text" id="add_pay"
							class="add_item_info" name="pay" value="0">
					</div>

					<div class="add_item_form_group small">
						<label>규격</label> <input type="text" id="add_spec"
							class="add_item_info" name="spec" placeholder="규격을 입력하세요">
					</div>

					<div class="add_item_form_group small">
						<label>단위</label> <input type="text" id="add_unit"
							class="add_item_info" name="unit" placeholder="단위를 입력하세요">
					</div>
				</div>

				<div class="add_item_form_row second">
					<div class="add_item_form_group name-group">
						<label>품목명</label> <input type="text" id="add_item_name"
							name="item_name" class="add_item_info" placeholder="품목명을 입력하세요">
					</div>
				</div>

				<div class="add_item_btn_area">
					<button type="button" class="add_item_close_btn"
						id="cancelAddItemModal">닫기</button>
					<button type="submit" class="add_item_save_btn"
						id="saveAddItemModal">등록</button>
				</div>

				</form>
			</div>
		</div>
	</c:if>

	<script>
		window.itemListForCode = [
			<c:forEach var="item" items="${allItemList}" varStatus="status">
				{
					itemId: "${fn:escapeXml(item.item_id)}",
					gId: ${item.g_id}
				}<c:if test="${not status.last}">,</c:if>
			</c:forEach>
		];
	</script>

	<script
		src="${pageContext.request.contextPath}/static/js/11_masterdata/item_master.js"></script>
</body>
</html>
