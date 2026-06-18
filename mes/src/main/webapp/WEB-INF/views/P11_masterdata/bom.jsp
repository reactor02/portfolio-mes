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
<title>BOM 관리</title>

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
	href="${pageContext.request.contextPath}/static/css/P11_masterdata/bom.css">
</head>
<body>

	<%@ include file="/WEB-INF/views/P00_layout/header.jsp"%>

	<div class="layout_snb">
		<div class="snbContent">
			<%@ include file="/WEB-INF/views/P00_layout/snb.jsp"%>
		</div>

		<div class="content">
			<div class="bom-page">
				<div class="bom-page-top">
					<div class="bom-page-header">
						<h1>BOM 관리</h1>
						<p>제품별 자재명세서(BOM) 관리</p>
					</div>
				</div>

				<section class="bom-card">
					<div class="bom-card-top">
						<h2>BOM 코드 목록</h2>
						<c:if test="${isSuperAdmin}">
							<button type="button" class="bom-add-btn">+ BOM 등록</button>
						</c:if>
					</div>

					<form class="bom-filter-row" method="get"
						action="${pageContext.request.contextPath}/bom">
						<div class="bom-filter-left">
							<select name="itemGroup" class="bom-select">
								<option value="">선택</option>
								<option value="30"
									<c:if test="${itemGroup eq '30'}">selected</c:if>>완제품</option>
								<option value="20"
									<c:if test="${itemGroup eq '20'}">selected</c:if>>반제품</option>
							</select>
						</div>

						<div class="bom-filter-right">
							<input type="text" name="keyword" value="${keyword}"
								placeholder="제품 검색..." class="bom-search-input" />
							<button type="submit" class="bom-search-btn">검색</button>
						</div>
					</form>

					<div class="bom-table-wrap">
						<table class="bom-table">
							<thead>
								<tr>
									<th>BOM코드</th>
									<th>제품명</th>
									<th>분류</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${empty bomList}">
										<tr>
											<td colspan="3" class="bom-empty">조회된 BOM이 없습니다.</td>
										</tr>
									</c:when>
									<c:otherwise>
										<c:forEach var="bom" items="${bomList}">
											<tr>
												<td>${bom.bom_id}</td>
												<td>
													<a class="bom-name-link"
														href="${pageContext.request.contextPath}/bomDetail?bomId=${bom.bom_id}">
														${bom.item_name}
													</a>
												</td>
												<td>
													<c:if test="${bom.g_id == 30}">완제품</c:if>
													<c:if test="${bom.g_id == 20}">반제품</c:if>
													<c:if test="${bom.g_id == 10}">원자재</c:if>
												</td>
											</tr>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>

					<div class="pagination">
						<c:if test="${not empty totalPage}">
							<c:forEach var="i" begin="1" end="${totalPage}">
								<a
									href="${pageContext.request.contextPath}/bom?page=${i}&itemGroup=${itemGroup}&keyword=${keyword}"
									class="${page == i ? 'active' : ''}">${i}</a>
							</c:forEach>
						</c:if>

						<c:if test="${empty totalPage}">
							<a href="#" class="active">1</a>
						</c:if>
					</div>
				</section>
			</div>
		</div>
	</div>

	<c:if test="${isSuperAdmin}">
		<div class="add_item_modal" id="addItemModal" style="display: none;">
			<div class="add_item_modal_popup">
				<form action="${pageContext.request.contextPath}/BomAddController"
					method="post">
				<input type="hidden" id="next_bom_id" value="${nextBomId}">

				<h3 class="add_item_modal_title">BOM 등록</h3>

				<div class="add_item_form_row">
					<div class="add_item_form_group code">
						<label>BOM 코드</label>
						<input type="text" id="add_bom_id"
							class="add_item_info" name="bom_id" value="" readonly>
					</div>

					<div class="add_item_form_group group">
						<label>분류</label>
						<select id="add_g_id" class="add_item_info" name="g_id">
							<option value="30">완제품</option>
							<option value="20">반제품</option>
						</select>
						<input type="hidden" id="add_itemgroup_name"
							name="itemgroup_name" value="">
					</div>
				</div>

				<div class="add_item_form_row second">
					<div class="add_item_form_group name-group">
						<label>품목명</label>
						<input type="text" id="add_item_name"
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

	<div class="edit_item_modal" style="display: none;">
		<div class="edit_item_modal_popup">
			<form action="${pageContext.request.contextPath}/BomUpdateController"
				method="post">

				<h3 class="edit_item_modal_title">BOM 수정</h3>

				<div class="edit_item_form_row">
					<div class="edit_item_form_group code">
						<label>BOM 코드</label>
						<input id="edit_item_id" name="bom_id"
							class="edit_item_info readonly" type="text" value="" readonly>
					</div>

					<div class="edit_item_form_group group">
						<label>품목 그룹</label>
						<select id="edit_g_id" name="g_id" class="edit_item_info">
							<option value="30">완제품</option>
							<option value="20">반제품</option>
							<option value="10">원자재</option>
						</select>
					</div>
				</div>

				<div class="edit_item_form_row second">
					<div class="edit_item_form_group name-group">
						<label>제품명</label>
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

	<script
		src="${pageContext.request.contextPath}/static/js/11_masterdata/bom.js"></script>
</body>
</html>
