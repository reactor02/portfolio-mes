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
<title>공정 상세</title>

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
	href="${pageContext.request.contextPath}/static/css/P11_masterdata/processDetail.css">
</head>
<body>

	<%@ include file="/WEB-INF/views/P00_layout/header.jsp"%>

	<div class="layout_snb">
		<div class="snbContent">
			<%@ include file="/WEB-INF/views/P00_layout/snb.jsp"%>
		</div>

		<div class="content">
			<div class="process-detail-page">

				<div class="detail-top">
					<div class="detail-title-wrap">
						<c:choose>
							<c:when test="${not empty processDetail}">
								<h1>${processDetail.process_name} 상세</h1>
							</c:when>
							<c:otherwise>
								<h1>공정 상세</h1>
							</c:otherwise>
						</c:choose>
					</div>

<!-- 					<button type="button" class="detail-edit-btn">+ 공정 수정</button> -->
				</div>

				<c:choose>
					<c:when test="${empty processDetail}">
						<div class="detail-card">
							<div class="description-box">조회할 공정 정보가 없습니다.</div>
						</div>
					</c:when>

					<c:otherwise>
						<div class="detail-summary-grid">
							<div class="detail-card summary-card">
								<div class="process-code-badge">${processDetail.process_id}</div>

								<div class="summary-main">
									<div class="summary-label">공정명</div>
									<div class="summary-value">${processDetail.process_name}</div>
								</div>

<%-- 								<p class="summary-desc">${processDetail.process_info}</p> --%>
							</div>

							<div class="detail-card summary-card">
								<div class="card-title-row">
									<h2>설비 목록</h2>
									<span class="card-sub-text">현재 공정명과 맞는 설비 목록입니다.</span>
								</div>

								<ul class="equip-list">
									<c:choose>
										<c:when test="${empty equipmentList}">
											<li>등록된 설비가 없습니다.</li>
										</c:when>
										<c:otherwise>
											<c:forEach var="equipment" items="${equipmentList}">
												<li>${equipment.eq_name}(${equipment.eq_id})</li>
											</c:forEach>
										</c:otherwise>
									</c:choose>
								</ul>

							</div>
						</div>

						<div class="detail-card">
							<div class="card-header-block">
								<h2>자재 목록</h2>
								<p>이 공정에 필요한 자재 목록입니다.</p>
							</div>

							<div class="table-box">
								<table class="material-table">
									<thead>
										<tr>
											<th>자재 코드</th>
											<th>자재명</th>
											<th>단위</th>
										</tr>
									</thead>
									<tbody>
										<c:choose>
											<c:when test="${empty materialList}">
												<tr>
													<td colspan="3">등록된 자재가 없습니다.</td>
												</tr>
											</c:when>
											<c:otherwise>
												<c:forEach var="m" items="${materialList}">
													<tr>
														<td>${m.item_id}</td>
														<td>${m.item_name}</td>
														<td>${m.unit}</td>
													</tr>
												</c:forEach>
											</c:otherwise>
										</c:choose>
									</tbody>
								</table>
							</div>
						</div>

						<div class="detail-card">
							<div class="card-header-block">
								<h2>공정 설명</h2>
							</div>

							<div class="description-box">${processDetail.process_info}</div>
						</div>
					</c:otherwise>
				</c:choose>

				<div class="detail-bottom">
					<a href="${pageContext.request.contextPath}/process"
						class="back-btn">&lt; 목록으로</a>
				</div>

			</div>
		</div>
	</div>

</body>
</html>
