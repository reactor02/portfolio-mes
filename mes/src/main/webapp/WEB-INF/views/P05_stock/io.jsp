<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>입출고 관리</title>
<link rel="stylesheet" href="/mes/static/css/P00_common/common.css">
<link rel="stylesheet" href="/mes/static/css/P00_layout/header.css">
<script src="/mes/static/js/00_layout/header.js"></script>
<link rel="stylesheet" href="/mes/static/css/P00_layout/snb.css">
<script src="/mes/static/js/00_layout/snb.js"></script>
<link rel="stylesheet" href="/mes/static/css/P05_stock/stock.css">
<script src="/mes/static/js/05_stock/stock.js"></script>
</head>
<body>
	<script>
		const SESSION_ENAME = '${dto.ename}';
		const SESSION_EMP_ID = '${dto.empid}';
	</script>
	<c:if test="${not empty errorMsg}">
		<script>
			alert('${errorMsg}');
		</script>
	</c:if>
	<%@ include file="/WEB-INF/views/P00_layout/header.jsp"%>
	<div class="layout_snb">
		<div class="snbContent">
			<%@ include file="/WEB-INF/views/P00_layout/snb.jsp"%>
		</div>
		<div class="content">
			<div class="page-wrapper">

				<%-- 페이지 헤더 --%>
				<div class="page-header">
					<div>
						<h1>입출고 관리</h1>
						<p>입출고 현황을 조회합니다</p>
					</div>
					<div style="display: flex; gap: 8px;">
						<button class="btn-register-in">+ 입고 등록</button>
						<button class="btn-register-out">+ 출고 등록</button>
					</div>
				</div>

				<%-- 유통기한 요약 카드 --%>
				<div class="inv-summary-cards">
					<div
						class="inv-card inv-card-warn ${map.filterExpiry == 'warn' ? 'inv-card-active' : ''}">
						<div class="inv-card-label">LOT 수</div>
						<div class="inv-card-value">${map.expiryWarnCount != null ? map.expiryWarnCount : 0}</div>
						<div class="inv-card-title">유통기한 임박</div>
					</div>
					<div
						class="inv-card inv-card-over ${map.filterExpiry == 'over' ? 'inv-card-active' : ''}">
						<div class="inv-card-label">LOT 수</div>
						<div class="inv-card-value">${map.expiryOverCount != null ? map.expiryOverCount : 0}</div>
						<div class="inv-card-title">유통기한 초과</div>
					</div>
				</div>

				<%-- 필터 / 검색 영역 --%>
				<div class="filter-bar">

					<%-- 페이지당 건수 (맨 왼쪽) --%>
					<label>열 개수:</label> <select id="size">
						<option value="5" ${map.size == 5  ? 'selected' : ''}>5개씩</option>
						<option value="10" ${map.size == 10 ? 'selected' : ''}>10개씩</option>
						<option value="15" ${map.size == 15 ? 'selected' : ''}>15개씩</option>
						<option value="20" ${map.size == 20 ? 'selected' : ''}>20개씩</option>
					</select>

					<%-- 입출고 분류 --%>
					<select id="filterIoType">
						<option value="">입출고 분류</option>
						<option value="0" ${map.filterIoType == '0' ? 'selected' : ''}>입고</option>
						<option value="1" ${map.filterIoType == '1' ? 'selected' : ''}>출고</option>
					</select>

					<%-- 거래처 --%>
					<select id="filterVendorId">
						<option value="">거래처명</option>
						<c:forEach var="v" items="${map.vendorList}">
							<option value="${v.vender_id}"
								${map.filterVendorId == v.vender_id ? 'selected' : ''}>
								${v.vender_name}</option>
						</c:forEach>
					</select>

					<%-- 자재 대분류 --%>
					<select id="filterGId">
						<option value="">자재 대분류</option>
						<c:forEach var="g" items="${map.groupList}">
							<option value="${g.g_id}"
								${map.filterGId == g.g_id ? 'selected' : ''}>
								<c:choose>
									<c:when test="${g.g_id == '10'}">원자재</c:when>
									<c:when test="${g.g_id == '20'}">반제품</c:when>
									<c:when test="${g.g_id == '30'}">완제품</c:when>
									<c:otherwise>${g.g_id}</c:otherwise>
								</c:choose>
							</option>
						</c:forEach>
					</select>

					<%-- 자재 소분류 --%>
					<select id="filterItemId">
						<option value="">자재 소분류</option>
						<c:forEach var="item" items="${map.itemList}">
							<option value="${item.item_id}" data-gid="${item.g_id}"
								${map.filterItemId == item.item_id ? 'selected' : ''}>
								${item.item_name}</option>
						</c:forEach>
					</select>

					<%-- 작업자 검색 --%>
					<div
						style="display: flex; gap: 6px; align-items: center; flex-shrink: 0;">
						<input type="text" id="filterEmp" placeholder="작업자 검색" readonly
							value="${map.filterEmp != null ? map.filterEmp : ''}">
						<button type="button" id="btnFilterEmpSearch">🔍</button>
					</div>
					<input type="hidden" id="filterEmpId"
						value="${map.filterEmpId != null ? map.filterEmpId : ''}">

					<%-- 기간 (한 묶음) --%>
					<div class="date-range-wrap">
						기간: <input type="date" id="filterDateFrom"
							value="${map.filterDateFrom != null ? map.filterDateFrom : ''}">
						~ <input type="date" id="filterDateTo"
							value="${map.filterDateTo != null ? map.filterDateTo : ''}">
					</div>

					<%-- 자재명/코드 검색  --%>
					<div class="search-wrap">
						<input type="text" id="filterKeyword" placeholder="자재명/코드"
							value="${map.filterKeyword != null ? map.filterKeyword : ''}" />
						<button class="btn-search" id="btnSearch">검색</button>
						
					</div>




				</div>

				<%-- 테이블 --%>
				<div class="table-wrap">
					<table>
						<thead>
							<tr>
								<th>입출고코드</th>
								<th>자재코드</th>
								<th>자재명</th>
								<th>분류</th>
								<th>LOT</th>
								<th>규격</th>
								<th>단위</th>
								<th>수량</th>
								<th>입출고일</th>
								<th>유통기한</th>
								<th>입출고사유</th>
								<th>거래처</th>
								<th>작업자</th>
								<th>입/출고</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="dto" items="${map.list}">
								<tr>
									<td>${dto.io_id}</td>
									<td>${dto.item_id}</td>
									<td>${dto.item_name}</td>
									<td><c:choose>
											<c:when test="${dto.g_id == '10'}">원자재</c:when>
											<c:when test="${dto.g_id == '20'}">반제품</c:when>
											<c:when test="${dto.g_id == '30'}">완제품</c:when>
											<c:otherwise>${dto.g_id}</c:otherwise>
										</c:choose></td>
									<td>${dto.lot_id}</td>
									<td>${dto.spec}</td>
									<td>${dto.unit}</td>
									<td>${dto.io_qty}</td>
									<td><fmt:formatDate value="${dto.io_time}"
											pattern="yyyy-MM-dd HH:mm" timeZone="Asia/Seoul" /></td>
									<td><c:choose>
											<c:when test="${empty dto.expiry_date}">-</c:when>
											<c:otherwise>${dto.expiry_date}</c:otherwise>
										</c:choose></td>
									<td>${dto.io_reason}</td>
									<td>${empty dto.vender_name ? '-' : dto.vender_name}</td>
									<td>${dto.ename}</td>
									<td><c:choose>
											<c:when test="${dto.io_type == 0}">
												<span class="tag-in">입고</span>
											</c:when>
											<c:otherwise>
												<span class="tag-out">출고</span>
											</c:otherwise>
										</c:choose></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>

					<%-- 페이지네이션 --%>
					<%
					Map map = (Map) request.getAttribute("map");
					int total = (int) map.get("totalCount");
					int size = (int) map.get("size");
					int totalPage = (int) Math.ceil((double) total / size);
					if (totalPage == 0)
						totalPage = 1;
					int section = 5;
					int pageNum = (int) map.get("page");
					int end_section = (int) Math.ceil((double) pageNum / section) * section;
					int start_section = end_section - section + 1;
					if (end_section > totalPage)
						end_section = totalPage;

					String filterParams = "";
					if (map.get("filterIoType") != null)
						filterParams += "&filterIoType=" + map.get("filterIoType");
					if (map.get("filterVendorId") != null)
						filterParams += "&filterVendorId=" + map.get("filterVendorId");
					if (map.get("filterGId") != null)
						filterParams += "&filterGId=" + map.get("filterGId");
					if (map.get("filterItemId") != null)
						filterParams += "&filterItemId=" + map.get("filterItemId");
					if (map.get("filterDateFrom") != null)
						filterParams += "&filterDateFrom=" + map.get("filterDateFrom");
					if (map.get("filterDateTo") != null)
						filterParams += "&filterDateTo=" + map.get("filterDateTo");
					if (map.get("filterKeyword") != null)
						filterParams += "&filterKeyword=" + map.get("filterKeyword");
					if (map.get("filterExpiry") != null)
						filterParams += "&filterExpiry=" + map.get("filterExpiry");
					%>

					<div class="pagination">
						<%-- 이전 버튼 --%>
						<c:if test="<%=pageNum == 1%>">
							<a>&lt;</a>
						</c:if>
						<c:if test="<%=pageNum != 1%>">
							<a
								href="/mes/io?page=<%=pageNum-1%>&size=${map.size}<%=filterParams%>">&lt;</a>
						</c:if>

						<%-- 페이지 번호 --%>
						<c:forEach var="i" begin="<%=start_section%>"
							end="<%=end_section%>">
							<c:if test="${map.page eq i}">
								<a href="/mes/io?page=${i}&size=${map.size}<%=filterParams%>"
									class="active"><strong>${i}</strong></a>
							</c:if>
							<c:if test="${map.page ne i}">
								<a href="/mes/io?page=${i}&size=${map.size}<%=filterParams%>">${i}</a>
							</c:if>
						</c:forEach>

						<%-- 다음 버튼 --%>
						<c:if test="<%=pageNum == totalPage%>">
							<a>&gt;</a>
						</c:if>
						<c:if test="<%=pageNum != totalPage%>">
							<a
								href="/mes/io?page=<%=pageNum+1%>&size=${map.size}<%=filterParams%>">&gt;</a>
						</c:if>
					</div>

					<%-- 입출고 등록 모달 --%>
					<dialog id="myModal" class="modal-box">
					<h2 class="modal-title" id="modalTitle">입출고 등록</h2>
					<form method="post" action="/mes/io">

						<%-- io_type hidden --%>
						<select name="io_type" id="io_type" style="display: none;">
							<option value="0">입고</option>
							<option value="1">출고</option>
						</select>

						<%-- 출고 전용: LOT 검색 --%>
						<div id="in_select_wrap"
							style="display: none; margin-bottom: 14px;">
							<div class="modal-field">
								<label>LOT 번호</label>
								<div style="display: flex; gap: 8px;">
									<input type="text" id="lot_id_display" placeholder="LOT 선택"
										readonly>
									<button type="button" id="btnLotSearch">🔍 검색</button>
								</div>
								<div class="modal-field">
									<label>자재명</label> <input type="text" id="item_name_display"
										placeholder="자동입력" readonly>
								</div>
								<input type="hidden" name="item_id" id="item_id_hidden">
								<input type="hidden" name="lot_id" id="lot_id_hidden">
							</div>
						</div>

						<div class="modal-grid">

							<%-- 거래처 --%>
							<div class="modal-field">
								<label>거래처</label> <select name="vender_id">
									<option value="">거래처 선택</option>
									<c:forEach var="v" items="${map.vendorList}">
										<option value="${v.vender_id}">${v.vender_name}</option>
									</c:forEach>
								</select>
							</div>

							<%-- 입고 전용: 자재 대분류/소분류 --%>
							<div id="item_wrap">
								<div class="modal-field">
									<label>자재 대분류</label> <select name="g_id" id="g_id">
										<option value="">대분류 선택</option>
										<c:forEach var="g" items="${map.groupList}">
											<option value="${g.g_id}">
												<c:choose>
													<c:when test="${g.g_id == '10'}">원자재</c:when>
													<c:when test="${g.g_id == '20'}">반제품</c:when>
													<c:when test="${g.g_id == '30'}">완제품</c:when>
													<c:otherwise>${g.g_id}</c:otherwise>
												</c:choose>
											</option>
										</c:forEach>
									</select>
								</div>
								<div class="modal-field">
									<label>자재 소분류</label> <select name="item_id" id="item_id">
										<option value="">소분류 선택</option>
										<c:forEach var="item" items="${map.itemList}">
											<option value="${item.item_id}" data-gid="${item.g_id}"
												data-spec="${item.spec}" data-unit="${item.unit}">
												${item.item_name}</option>
										</c:forEach>
									</select>
								</div>
							</div>

							<%-- 규격 --%>
							<div class="modal-field">
								<label>규격</label> <input type="text" name="spec" id="spec"
									placeholder="자동입력" readonly>
							</div>

							<%-- 단위 --%>
							<div class="modal-field">
								<label>단위</label> <input type="text" name="unit" id="unit"
									placeholder="자동입력" readonly>
							</div>

							<%-- 수량 --%>
							<div class="modal-field">
								<label>수량</label> <input type="number" name="lot_qty"
									id="lot_qty" min="1" placeholder="수량 입력">
							</div>

							<%-- 입출고일 --%>
							<div class="modal-field">
								<label id="io_time_label">입출고일</label> <input type="date"
									name="io_time" id="io_time">
							</div>

							<%-- 유통기한 --%>
							<div class="modal-field">
								<label>유통기한</label> <input type="date" name="expiry_date"
									id="expiry_date">
							</div>

							<%-- 입출고사유 --%>
							<div class="modal-field">
								<label id="io_reason_label">입출고사유</label> <select
									name="io_reason" id="io_reason">
									<option value="">사유 선택</option>
								</select>
							</div>

							<%-- 작업자 (세션에서 자동 세팅) --%>
							<div class="modal-field">
								<label>작업자</label> <input type="text" id="empName"
									value="${dto.ename}" readonly> <input type="hidden"
									name="emp_id" id="emp_id_hidden" value="${dto.empid}">
							</div>

							<div class="modal-footer">
								<button type="button" class="btn-cancel" id="btnCancel">←
									취소</button>
								<button type="submit" class="btn-submit">등록</button>
							</div>

						</div>
					</form>
					</dialog>

				</div>
			</div>
		</div>
	</div>

	<!-- LOT 검색 모달 -->
	<dialog id="lotSearchModal" class="modal-box">
	<h2 class="modal-title">LOT 검색</h2>
	<div style="display: flex; gap: 8px; margin-bottom: 12px;">
		<input type="text" id="lotKeyword" placeholder="자재명 또는 LOT번호 검색">
		<button type="button" id="btnLotKeywordSearch">검색</button>
	</div>
	<table id="lotSearchTable">
		<thead>
			<tr>
				<th>LOT번호</th>
				<th>자재코드</th>
				<th>자재명</th>
				<th>규격</th>
				<th>단위</th>
				<th>수량</th>
				<th>유통기한</th>
				<th>선택</th>
			</tr>
		</thead>
		<tbody id="lotSearchBody">
			<tr>
				<td colspan="8">검색어를 입력하세요</td>
			</tr>
		</tbody>
	</table>
	<div id="lotPagination"
		style="display: flex; justify-content: center; gap: 6px; margin-top: 10px;"></div>
	<div class="modal-footer">
		<button type="button" id="btnLotSearchCancel">닫기</button>
	</div>
	</dialog>

	<dialog id="empSearchModal" class="modal-box">
	<h2 class="modal-title">작업자 검색</h2>
	<div style="display: flex; gap: 8px; margin-bottom: 12px;">
		<input type="text" id="empKeyword" placeholder="이름 또는 사번 검색">
		<button type="button" id="btnEmpKeywordSearch">검색</button>
	</div>
	<table>
		<thead>
			<tr>
				<th>사번</th>
				<th>이름</th>
				<th>부서</th>
				<th>선택</th>
			</tr>
		</thead>
		<tbody id="empSearchBody">
			<tr>
				<td colspan="4">검색어를 입력하세요</td>
			</tr>
		</tbody>
	</table>
	<div class="modal-footer">
		<button type="button" id="btnEmpSearchCancel">닫기</button>
	</div>
	</dialog>
</body>
</html>