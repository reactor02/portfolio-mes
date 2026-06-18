<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>재고 관리</title>
<link rel="stylesheet" href="/mes/static/css/P00_common/common.css">
<link rel="stylesheet" href="/mes/static/css/P00_layout/header.css">
<script src="/mes/static/js/00_layout/header.js"></script>
<link rel="stylesheet" href="/mes/static/css/P00_layout/snb.css">
<script src="/mes/static/js/00_layout/snb.js"></script>
<link rel="stylesheet" href="/mes/static/css/P07_work/main.css">
<link rel="stylesheet" href="/mes/static/css/P05_stock/stock2.css">
<script src="/mes/static/js/05_stock/stock2.js"></script>
</head>
<body>
    <%@ include file="/WEB-INF/views/P00_layout/header.jsp"%>

    <div class="layout_snb">
        <div class="snbContent">
            <%@ include file="/WEB-INF/views/P00_layout/snb.jsp"%>
        </div>
        <div class="content">
            <div class="page-wrapper">

                <div class="page-header">
                    <div>
                        <h1>재고 관리</h1>
                        <p>재고 현황을 조회합니다</p>
                    </div>
                </div>

                <div class="inv-summary-cards">
                    <div class="inv-card ${map.filterStock == null ? 'inv-card-active' : ''}">
                        <div class="inv-card-label">수량</div>
                        <div class="inv-card-value">${map.totalStock != null ? map.totalStock : 0}</div>
                        <div class="inv-card-title">품목</div>
                    </div>
                    <div class="inv-card inv-card-normal ${map.filterStock == 'normal' ? 'inv-card-active' : ''}">
                        <div class="inv-card-label">수량</div>
                        <div class="inv-card-value">${map.normalStock != null ? map.normalStock : 0}</div>
                        <div class="inv-card-title">정상재고</div>
                    </div>
                    <div class="inv-card inv-card-lack ${map.filterStock == 'lack' ? 'inv-card-active' : ''}">
                        <div class="inv-card-label">수량</div>
                        <div class="inv-card-value">${map.lackStock != null ? map.lackStock : 0}</div>
                        <div class="inv-card-title">부족 재고</div>
                    </div>
                </div>

                <div class="filter-bar">
                    <select id="filterGId">
                        <option value="">자재분류</option>
                        <c:forEach var="g" items="${map.groupList}">
                            <option value="${g.g_id}" ${map.filterGId == g.g_id ? 'selected' : ''}>
                                <c:choose>
                                    <c:when test="${g.g_id == '10'}">원자재</c:when>
                                    <c:when test="${g.g_id == '20'}">반제품</c:when>
                                    <c:when test="${g.g_id == '30'}">완제품</c:when>
                                    <c:otherwise>${g.g_id}</c:otherwise>
                                </c:choose>
                            </option>
                        </c:forEach>
                    </select>

                    <div class="search-wrap">
                        <input type="text" id="filterKeyword"
                            placeholder="자재명 또는 자재코드로 검색"
                            value="${map.filterKeyword != null ? map.filterKeyword : ''}" />
                        <button class="btn-search" id="btnSearch">검색</button>
                        <button class="btn-reset" id="btnReset">초기화</button>
                    </div>
                </div>

                <select id="size">
                    <option value="5"  ${map.size == 5  ? 'selected' : ''}>5</option>
                    <option value="10" ${map.size == 10 ? 'selected' : ''}>10</option>
                    <option value="15" ${map.size == 15 ? 'selected' : ''}>15</option>
                    <option value="20" ${map.size == 20 ? 'selected' : ''}>20</option>
                </select>

                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                <th>자재코드</th>
                                <th>자재명</th>
                                <th>분류</th>
                                <th>규격</th>
                                <th>단위</th>
                                <th>재고</th>
                                <th>안전 재고</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="dto" items="${map.list}">
                                <tr>
                                    <td>${dto.item_id}</td>
                                    <td>${dto.item_name}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${dto.g_id == '10'}">원자재</c:when>
                                            <c:when test="${dto.g_id == '20'}">반제품</c:when>
                                            <c:when test="${dto.g_id == '30'}">완제품</c:when>
                                            <c:otherwise>${dto.g_id}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${not empty dto.spec ? dto.spec : '-'}</td>
                                    <td>${not empty dto.unit ? dto.unit : '-'}</td>
                                    <td>${dto.stock_no != null ? dto.stock_no : 0}</td>
                                    <td>${dto.safe_qty != null ? dto.safe_qty : 0}</td><%-- ★ safe_no → safe_qty --%>

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
                    if (totalPage == 0) totalPage = 1;
                    int section = 5;
                    int pageNum = (int) map.get("page");
                    int end_section = (int) Math.ceil((double) pageNum / section) * section;
                    int start_section = end_section - section + 1;
                    if (end_section > totalPage) end_section = totalPage;

                    String filterParams = "";
                    if (map.get("filterGId")     != null) filterParams += "&filterGId="     + map.get("filterGId");
                    if (map.get("filterKeyword") != null) filterParams += "&filterKeyword=" + map.get("filterKeyword");
                    if (map.get("filterStock")   != null) filterParams += "&filterStock="   + map.get("filterStock");
                    %>

                    <div class="pagination">
                        <c:if test="<%=pageNum == 1%>">
                            <a>&lt;</a>
                        </c:if>
                        <c:if test="<%=pageNum != 1%>">
                            <a href="/mes/stock?page=<%=pageNum-1%>&size=<%=size%><%=filterParams%>">&lt;</a>
                        </c:if>

                        <c:forEach var="i" begin="<%=start_section%>" end="<%=end_section%>">
                            <c:if test="${map.page eq i}">
                                <a href="/mes/stock?page=${i}&size=<%=size%><%=filterParams%>" class="active"><strong>${i}</strong></a>
                            </c:if>
                            <c:if test="${map.page ne i}">
                                <a href="/mes/stock?page=${i}&size=<%=size%><%=filterParams%>">${i}</a>
                            </c:if>
                        </c:forEach>

                        <c:if test="<%=pageNum == totalPage%>">
                            <a>&gt;</a>
                        </c:if>
                        <c:if test="<%=pageNum != totalPage%>">
                            <a href="/mes/stock?page=<%=pageNum+1%>&size=<%=size%><%=filterParams%>">&gt;</a>
                        </c:if>
                    </div>
                </div>

            </div>
        </div>
    </div>



</body>
</html>