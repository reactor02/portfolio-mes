<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import="java.util.*"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>BOM 관리 상세페이지</title>

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
	href="${pageContext.request.contextPath}/static/css/P11_masterdata/bomDetail.css">
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
						<h1>BOM 관리 상세페이지</h1>
						<p>제품별 자재명세서(BOM) 관리</p>
					</div>
				</div>

				<section class="bom-card bom-detail-card">
					<div class="bom-detail-top">
						<div class="bom-detail-title-wrap">
							<c:choose>
								<c:when test="${not empty bomInfo}">
									<h2>${bomInfo.item_name} (${bomInfo.parent_item_id})</h2>
								</c:when>
								<c:otherwise>
									<h2>BOM 상세 정보</h2>
								</c:otherwise>
							</c:choose>
						</div>

						<c:if test="${not empty bomInfo}">
							<button type="button" class="bom-add-btn"
								id="openBomDetailAddModal">+ BOM 품목 추가</button>
						</c:if>
					</div>

					<div class="bom-detail-inner">
						<div class="bom-table-wrap bom-detail-table-wrap">
							<table class="bom-table bom-detail-table">
								<thead>
									<tr>
										<th>품목 코드</th>
										<th>품목명</th>
										<th>개수</th>
										<th>단위</th>
										<th>관리</th>
									</tr>
								</thead>
								<tbody>
									<c:choose>
										<c:when test="${empty detailList}">
											<tr>
												<td colspan="5" class="bom-empty">조회된 BOM 항목이 없습니다.</td>
											</tr>
										</c:when>
										<c:otherwise>
											<c:forEach var="detail" items="${detailList}">
												<tr>
													<td>${detail.child_item_id}</td>
													<td>${detail.child_item_name}</td>
													<td>${detail.ea}</td>
													<td>${detail.unit}</td>
													<td>
														<div class="bom-detail-action-group">
															<button type="button"
																class="bom-detail-edit-btn"
																data-bom-detail-id="${detail.bom_detail_id}"
																data-child-item-id="${detail.child_item_id}"
																data-ea="${detail.ea}"
																data-unit="${detail.unit}">
																수정
															</button>

															<form method="post"
																action="${pageContext.request.contextPath}/BomDetailDeleteController"
																class="bom-delete-form">
																<input type="hidden" name="bom_detail_id"
																	value="${detail.bom_detail_id}">
																<input type="hidden" name="bom_id"
																	value="${bomInfo.bom_id}">
																<button type="submit" class="bom-delete-btn"
																	onclick="return confirm('정말 삭제하시겠습니까?');">
																	삭제
																</button>
															</form>
														</div>
													</td>
												</tr>
											</c:forEach>
										</c:otherwise>
									</c:choose>
								</tbody>
							</table>
						</div>
					</div>

					<div class="bom-pagination">
						<c:choose>
							<c:when test="${not empty totalPage and totalPage > 0 and not empty bomInfo}">
								<c:forEach var="i" begin="1" end="${totalPage}">
									<a
										href="${pageContext.request.contextPath}/bomDetail?bomId=${bomInfo.bom_id}&page=${i}&size=${size}"
										class="${page == i ? 'active' : ''}">
										${i}
									</a>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<a href="#" class="active">1</a>
							</c:otherwise>
						</c:choose>
					</div>
				</section>
			</div>
		</div>
	</div>

	<c:if test="${not empty bomInfo}">
		<div class="bom-detail-modal" id="addBomDetailModal">
			<div class="bom-detail-modal-popup">
				<form action="${pageContext.request.contextPath}/BomDetailAddController"
					method="post">
					<input type="hidden" name="bom_id" value="${bomInfo.bom_id}">

					<h3 class="bom-detail-modal-title">BOM 품목 추가</h3>

					<div class="bom-detail-form-row">
						<div class="bom-detail-form-group code">
							<label>BOM 코드</label>
							<input type="text" class="bom-detail-input readonly"
								value="${bomInfo.bom_id}" readonly>
						</div>

						<div class="bom-detail-form-group name">
							<label>품목 선택</label>
							<select id="add_child_item_id" name="child_item_id"
								class="bom-detail-input" required>
								<option value="">선택</option>
								<c:forEach var="item" items="${itemList}">
									<option value="${item.child_item_id}" data-unit="${item.unit}">
										${item.child_item_name} (${item.child_item_id})
									</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="bom-detail-form-row second-row">
						<div class="bom-detail-form-group small">
							<label>개수</label>
							<input type="text" id="add_ea" name="ea"
								class="bom-detail-input" placeholder="개수를 입력하세요" required>
						</div>

						<div class="bom-detail-form-group small">
							<label>단위</label>
							<input type="text" id="add_unit"
								class="bom-detail-input readonly" readonly>
						</div>
					</div>

					<div class="bom-detail-modal-actions">
						<button type="button" class="bom-detail-cancel-btn"
							id="closeBomDetailAddModal">취소</button>
						<button type="submit" class="bom-detail-save-btn">등록</button>
					</div>
				</form>
			</div>
		</div>

		<div class="bom-detail-modal" id="editBomDetailModal">
			<div class="bom-detail-modal-popup">
				<form action="${pageContext.request.contextPath}/BomDetailUpdateController"
					method="post">
					<input type="hidden" name="bom_id" value="${bomInfo.bom_id}">
					<input type="hidden" name="bom_detail_id" id="edit_bom_detail_id">

					<h3 class="bom-detail-modal-title">BOM 품목 수정</h3>

					<div class="bom-detail-form-row">
						<div class="bom-detail-form-group code">
							<label>BOM 코드</label>
							<input type="text" class="bom-detail-input readonly"
								value="${bomInfo.bom_id}" readonly>
						</div>

						<div class="bom-detail-form-group name">
							<label>품목 선택</label>
							<select id="edit_child_item_id" name="child_item_id"
								class="bom-detail-input" required>
								<option value="">선택</option>
								<c:forEach var="item" items="${itemList}">
									<option value="${item.child_item_id}" data-unit="${item.unit}">
										${item.child_item_name} (${item.child_item_id})
									</option>
								</c:forEach>
							</select>
						</div>
					</div>

					<div class="bom-detail-form-row second-row">
						<div class="bom-detail-form-group small">
							<label>개수</label>
							<input type="text" id="edit_ea" name="ea"
								class="bom-detail-input" placeholder="개수를 입력하세요" required>
						</div>

						<div class="bom-detail-form-group small">
							<label>단위</label>
							<input type="text" id="edit_unit"
								class="bom-detail-input readonly" readonly>
						</div>
					</div>

					<div class="bom-detail-modal-actions">
						<button type="button" class="bom-detail-cancel-btn"
							id="closeBomDetailEditModal">취소</button>
						<button type="submit" class="bom-detail-save-btn">수정</button>
					</div>
				</form>
			</div>
		</div>
	</c:if>

	<script
		src="${pageContext.request.contextPath}/static/js/11_masterdata/bomDetail.js"></script>
</body>
</html>
