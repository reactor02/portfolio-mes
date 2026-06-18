<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ page import="java.util.*"%>

<c:set var="nextSeq" value="${nextStepSeq}" />
<c:set var="isSuperAdmin"
	value="${not empty sessionScope.dto and sessionScope.dto.empid eq 'user_1001'}" />

<c:set var="defaultProcessType" value="wo" />
<c:if test="${not empty selectedProcess and not empty selectedProcess.process_type}">
	<c:set var="defaultProcessType" value="${selectedProcess.process_type}" />
</c:if>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공정 관리</title>

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
	href="${pageContext.request.contextPath}/static/css/P11_masterdata/process.css">
</head>
<body>

	<%@ include file="/WEB-INF/views/P00_layout/header.jsp"%>

	<div class="layout_snb">
		<div class="snbContent">
			<%@ include file="/WEB-INF/views/P00_layout/snb.jsp"%>
		</div>

		<div class="content">
			<div class="process-page">
				<div class="process-top">
					<div class="process-header">
						<h1>공정 관리</h1>
						<p>생산 공정 정보를 관리</p>
					</div>

					<c:if test="${isSuperAdmin}">
						<button type="button" class="process-primary-btn"
							id="openProcessStepModal">+ 공정 단계 등록</button>
					</c:if>
				</div>

				<div class="process-filter-row">
					<form method="get"
						action="${pageContext.request.contextPath}/process">
						<input type="hidden" name="keyword" value="${keyword}">
						<input type="hidden" name="size" value="${size}">
						<select name="processId" class="process-select"
							onchange="this.form.submit()">
							<c:forEach var="p" items="${allProcessList}">
								<option value="${p.process_id}"
									<c:if test="${selectedProcessId eq p.process_id}">selected</c:if>>
									${p.process_name}
								</option>
							</c:forEach>
						</select>
					</form>
				</div>

				<section class="process-card flow-card">
					<div class="process-card-header">
						<h2>공정 단계</h2>
					</div>

					<div class="flow-board">
						<c:choose>
							<c:when test="${empty stepList}">
								<div class="flow-empty">등록된 공정 단계가 없습니다.</div>
							</c:when>

							<c:otherwise>
								<div class="flow-top-line">
									<c:forEach var="step" items="${stepList}" varStatus="status">
										<div class="flow-step">
											<span class="flow-badge">${step.seq}단계</span>
											<strong>${step.step_name}</strong>
										</div>

										<c:if test="${not status.last}">
											<div class="flow-arrow">→</div>
										</c:if>
									</c:forEach>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</section>

				<section class="process-card list-card">
					<div class="process-list-top">
						<h2>공정 목록</h2>

						<div class="process-toolbar-wrap">
							<form class="process-toolbar" method="get"
								action="${pageContext.request.contextPath}/process">
								<input type="hidden" name="processId" value="${selectedProcessId}">
								<input type="hidden" name="size" value="${size}">

								<div class="process-search">
									<input class="process-search-text" type="text" name="keyword"
										value="${keyword}" placeholder="공정명 검색..." />
								</div>

								<button type="submit" class="process-primary-btn small">검색</button>
							</form>

							<c:if test="${isSuperAdmin}">
								<button type="button" class="process-primary-btn small"
									id="openProcessAddModal">+ 공정 등록</button>
							</c:if>
						</div>
					</div>

					<div class="process-table-wrap">
						<table class="process-table">
							<thead>
								<tr>
									<th>공정코드</th>
									<th>공정유형</th>
									<th>공정명</th>
									<th>설명</th>
									<th>품목코드</th>
									<th>관리</th>
								</tr>
							</thead>
							<tbody>
								<c:choose>
									<c:when test="${empty processList}">
										<tr class="process-empty-row">
											<td colspan="6">조회된 공정이 없습니다.</td>
										</tr>
									</c:when>

									<c:otherwise>
										<c:forEach var="p" items="${processList}">
											<tr data-process-row="true">
												<td>
													<span class="process-code-badge">${p.process_id}</span>
												</td>
												<td>
													<c:choose>
														<c:when test="${p.process_type eq 'wo'}">작업</c:when>
														<c:when test="${p.process_type eq 'qc'}">품질</c:when>
														<c:otherwise>-</c:otherwise>
													</c:choose>
												</td>
												<td>
													<a class="process-name-link"
														href="${pageContext.request.contextPath}/processDetail?processId=${p.process_id}">
														${p.process_name}
													</a>
												</td>
												<td>${p.process_info}</td>
												<td>${p.item_id}</td>
												<td>
													<div class="process-action-group">
														<button type="button" class="process-icon-btn edit"
															data-process-id="${p.process_id}"
															data-process-type="${p.process_type}"
															data-process-name="${fn:escapeXml(p.process_name)}"
															data-process-info="${fn:escapeXml(p.process_info)}">수정</button>
													</div>
												</td>
											</tr>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</tbody>
						</table>
					</div>

					<div class="process-pagination">
						<c:if test="${page > 1}">
							<c:url var="prevUrl" value="/process">
								<c:param name="page" value="${page - 1}" />
								<c:param name="size" value="${size}" />
								<c:param name="processId" value="${selectedProcessId}" />
								<c:param name="keyword" value="${keyword}" />
							</c:url>
							<a href="${prevUrl}">&lt;</a>
						</c:if>

						<c:forEach var="i" begin="1" end="${totalPage}">
							<c:url var="pageUrl" value="/process">
								<c:param name="page" value="${i}" />
								<c:param name="size" value="${size}" />
								<c:param name="processId" value="${selectedProcessId}" />
								<c:param name="keyword" value="${keyword}" />
							</c:url>
							<a href="${pageUrl}" class="${page == i ? 'active' : ''}">${i}</a>
						</c:forEach>

						<c:if test="${page < totalPage}">
							<c:url var="nextUrl" value="/process">
								<c:param name="page" value="${page + 1}" />
								<c:param name="size" value="${size}" />
								<c:param name="processId" value="${selectedProcessId}" />
								<c:param name="keyword" value="${keyword}" />
							</c:url>
							<a href="${nextUrl}">&gt;</a>
						</c:if>
					</div>
				</section>
			</div>
		</div>
	</div>

	<c:if test="${isSuperAdmin}">
		<!-- 공정 단계 등록 모달 : process_step INSERT -->
		<div class="process-step-modal" id="processStepModal">
			<div class="process-step-modal-popup">
				<form action="${pageContext.request.contextPath}/ProcessStepAddController"
					method="post">

				<div class="process-step-modal-header">
					<h3 class="process-step-modal-title">공정 단계 등록</h3>
				</div>

				<div class="process-step-form-row">
					<div class="process-step-form-group code">
						<label for="processStepProcessId">공정코드</label>
						<input type="text" id="processStepProcessId" name="process_id"
							class="process-step-input" value="${selectedProcessId}" readonly>
					</div>

					<div class="process-step-form-group code">
						<label for="processStepSeqView">단계순서</label>
						<input type="text" id="processStepSeqView"
							class="process-step-input" value="${nextSeq}단계" readonly>
						<input type="hidden" name="seq" value="${nextSeq}">
					</div>

					<div class="process-step-form-group name">
						<label for="processStepName">공정 단계명</label>
						<input type="text" id="processStepName" name="step_name"
							class="process-step-input" placeholder="공정 단계명을 입력하세요" required>
					</div>
				</div>

				<div class="process-step-preview-box">
					<h4 class="process-step-preview-title">공정 단계(예상)</h4>

					<c:choose>
						<c:when test="${empty stepList}">
							<div class="process-step-preview-line">
								<div class="process-step-preview-card">
									<span class="process-step-preview-badge">${nextSeq}단계</span>
									<strong>새 공정 단계</strong>
								</div>
							</div>
						</c:when>

						<c:otherwise>
							<div class="process-step-preview-line">
								<c:forEach var="step" items="${stepList}" varStatus="status">
									<div class="process-step-preview-card">
										<span class="process-step-preview-badge">${step.seq}단계</span>
										<strong>${step.step_name}</strong>
									</div>

									<c:if test="${not status.last}">
										<div class="process-step-preview-arrow">→</div>
									</c:if>
								</c:forEach>

								<div class="process-step-preview-arrow">→</div>

								<div class="process-step-preview-card">
									<span class="process-step-preview-badge">${nextSeq}단계</span>
									<strong>새 공정 단계</strong>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="process-step-modal-actions">
					<button type="button" class="process-step-cancel-btn"
						id="closeProcessStepModal">취소</button>
					<button type="submit" class="process-step-save-btn">등록</button>
				</div>
				</form>
			</div>
		</div>
	</c:if>

	<c:if test="${isSuperAdmin}">
		<!-- 공정 등록 모달 : process INSERT -->
		<div class="process-add-modal" id="processAddModal">
			<div class="process-add-modal-popup">
				<form action="${pageContext.request.contextPath}/ProcessAddController"
					method="post">

				<div class="process-add-modal-header">
					<h3 class="process-add-modal-title">공정 등록</h3>
				</div>

				<div class="process-add-form-row">
					<div class="process-add-form-group code">
						<label for="addProcessId">공정코드</label>
						<input type="text" id="addProcessId" name="process_id"
							class="process-add-input readonly" value="${nextProcessId}" readonly>
					</div>

					<div class="process-add-form-group type">
						<label for="addProcessType">공정유형</label>
						<select id="addProcessType" name="process_type"
							class="process-add-input">
							<option value="wo"
								<c:if test="${defaultProcessType eq 'wo'}">selected</c:if>>작업</option>
							<option value="qc"
								<c:if test="${defaultProcessType eq 'qc'}">selected</c:if>>품질</option>
						</select>
					</div>
				</div>

				<div class="process-add-form-row">
					<div class="process-add-form-group name">
						<label for="addProcessName">공정명</label>
						<input type="text" id="addProcessName" name="process_name"
							class="process-add-input" placeholder="공정명을 입력하세요" required>
					</div>
				</div>

				<div class="process-add-form-row">
					<div class="process-add-form-group item">
						<label for="addProcessItemId">품목코드</label>
						<input type="text" id="addProcessItemId" name="item_id"
							class="process-add-input" value="${selectedProcess.item_id}"
							placeholder="예: semi_1003" required>
					</div>

<!-- 					<div class="process-add-form-group seq"> -->
<!-- 						<label for="addProcessSeq">순서</label> -->
<!-- 						<input type="number" id="addProcessSeq" name="seq" -->
<!-- 							class="process-add-input" value="1" min="1"> -->
<!-- 					</div> -->
				</div>

				<div class="process-add-form-row">
					<div class="process-add-form-group info">
						<label for="addProcessInfo">공정 설명</label>
						<textarea id="addProcessInfo" name="process_info"
							class="process-add-textarea" placeholder="공정 설명을 입력하세요"></textarea>
					</div>
				</div>

				<div class="process-add-modal-actions">
					<button type="button" class="process-add-cancel-btn"
						id="closeProcessAddModal">취소</button>
					<button type="submit" class="process-add-save-btn">등록</button>
				</div>
				</form>
			</div>
		</div>
	</c:if>

	<!-- 공정 수정 모달 -->
	<div class="process-edit-modal" id="processEditModal">
		<div class="process-edit-modal-popup">
			<form action="${pageContext.request.contextPath}/processUpdate"
				method="post">

				<div class="process-edit-modal-header">
					<h3 class="process-edit-modal-title">
						<span id="editProcessModalTitleText">공정</span> 수정
					</h3>
				</div>

				<div class="process-edit-form-row">
					<div class="process-edit-form-group code">
						<label for="editProcessId">공정 코드</label>
						<input type="text" id="editProcessId" name="process_id"
							class="process-edit-input readonly" readonly>
					</div>
				</div>

				<div class="process-edit-form-row">
					<div class="process-edit-form-group type">
						<label for="editProcessType">공정유형</label>
						<select id="editProcessType" name="process_type"
							class="process-edit-input">
							<option value="wo">작업</option>
							<option value="qc">품질</option>
						</select>
					</div>
				</div>

				<div class="process-edit-form-row">
					<div class="process-edit-form-group name">
						<label for="editProcessName">공정명</label>
						<input type="text" id="editProcessName" name="process_name"
							class="process-edit-input">
					</div>
				</div>

				<div class="process-edit-form-row">
					<div class="process-edit-form-group info">
						<label for="editProcessInfo">공정 설명</label>
						<textarea id="editProcessInfo" name="process_info"
							class="process-edit-textarea"></textarea>
					</div>
				</div>

				<div class="process-edit-modal-actions">
					<button type="button" class="process-edit-cancel-btn"
						id="closeProcessEditModal">취소</button>
					<button type="submit" class="process-edit-save-btn">수정</button>
				</div>

			</form>
		</div>
	</div>

	<script
		src="${pageContext.request.contextPath}/static/js/11_masterdata/process.js"></script>

</body>
</html>
