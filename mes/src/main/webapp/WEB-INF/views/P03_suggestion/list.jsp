<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>건의사항</title>
<link rel="stylesheet" href="/mes/static/css/P00_common/common.css">
<link rel="stylesheet" href="/mes/static/css/P00_layout/header.css">
<script src="/mes/static/js/00_layout/header.js"></script>
<link rel="stylesheet" href="/mes/static/css/P00_layout/snb.css">
<script src="/mes/static/js/00_layout/snb.js"></script>
<link rel="stylesheet" href="/mes/static/css/P03_suggestion/suggestion.css">
</head>
<body>

<%@ include file="/WEB-INF/views/P00_layout/header.jsp" %>

<div class="layout_snb">
    <div class="snbContent">
        <%@ include file="/WEB-INF/views/P00_layout/snb.jsp" %>
    </div>
    <div class="content">
        <main class="sg sg-wide">

  <div id="page-suggest-list">
    <div class="page-header-row">
      <div>
        <h1>건의사항</h1>
        <p class="page-header-desc">건의사항 목록을 조회하고 관리합니다</p>
      </div>
      <button class="btn btn-primary"
              onclick="location.href='${pageContext.request.contextPath}/suggestion/register'">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
          <path d="M7 2v10M2 7h10" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
        </svg>
        건의사항 등록
      </button>
    </div>

    <%-- 검색 form: GET 방식으로 list로 submit --%>
    <form id="searchForm" method="get"
          action="${pageContext.request.contextPath}/suggestion/list">
      <input type="hidden" name="page" value="1">
      <input type="hidden" name="size" value="${map.size}">
      <div class="table-toolbar">
        <div class="search-wrap">
          <svg class="search-icon" width="14" height="14" viewBox="0 0 14 14" fill="none">
            <circle cx="6" cy="6" r="4.5" stroke="#94A3B8" stroke-width="1.4"/>
            <path d="M9.5 9.5L12 12" stroke="#94A3B8" stroke-width="1.4" stroke-linecap="round"/>
          </svg>
          <input type="text" class="search-input" placeholder="제목으로 검색"
                 id="searchKeyword" name="searchKeyword"
                 value="${map.searchKeyword}">
        </div>
        <button type="submit" class="btn btn-outline btn-sm toolbar-btn">검색</button>

        <select id="sizeSelect" class="date-input size-select">
          <option value="5"  ${map.size == 5  ? 'selected' : ''}>5건</option>
          <option value="10" ${map.size == 10 ? 'selected' : ''}>10건</option>
          <option value="15" ${map.size == 15 ? 'selected' : ''}>15건</option>
          <option value="20" ${map.size == 20 ? 'selected' : ''}>20건</option>
        </select>
      </div>
    </form>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>상태</th>
          </tr>
        </thead>
        <tbody id="suggestListBody">
          <c:forEach var="sg" items="${map.list}">
            <tr>
              <td>${fn:substring(sg.boardno, 5, fn:length(sg.boardno))}</td>
              <td>
                <a href="${pageContext.request.contextPath}/suggestion/detail?boardno=${sg.boardno}&page=${map.page}&size=${map.size}"
                   class="item-name-link">
                  ${sg.title}
                </a>
              </td>
              <td>${sg.ename}</td>
              <td>${sg.ctimeStr}</td>
              <td>
                <c:choose>
                  <c:when test="${sg.complete == 0}">
                    <span class="badge badge-blue">검토중</span>
                  </c:when>
                  <c:when test="${sg.complete == 2}">
                    <span class="badge badge-amber">답변달림</span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge badge-gray">검토완료</span>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty map.list}">
            <tr>
              <td colspan="5" class="empty-row">등록된 건의사항이 없습니다.</td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 (검색어 유지) -->
    <c:set var="sk" value="${map.searchKeyword}"/>
    <div class="pagination">
      <c:choose>
        <c:when test="${map.page == 1}">
          <button class="page-btn" disabled>&lt;</button>
        </c:when>
        <c:otherwise>
          <a class="page-btn"
             href="${pageContext.request.contextPath}/suggestion/list?page=${map.page - 1}&size=${map.size}&searchKeyword=${sk}">&lt;</a>
        </c:otherwise>
      </c:choose>

      <c:forEach var="i" begin="${map.groupStartPage}" end="${map.groupEndPage}">
        <c:choose>
          <c:when test="${map.page == i}">
            <button class="page-btn page-btn-active">${i}</button>
          </c:when>
          <c:otherwise>
            <a class="page-btn"
               href="${pageContext.request.contextPath}/suggestion/list?page=${i}&size=${map.size}&searchKeyword=${sk}">${i}</a>
          </c:otherwise>
        </c:choose>
      </c:forEach>

      <c:choose>
        <c:when test="${map.page >= map.totalPages}">
          <button class="page-btn" disabled>&gt;</button>
        </c:when>
        <c:otherwise>
          <a class="page-btn"
             href="${pageContext.request.contextPath}/suggestion/list?page=${map.page + 1}&size=${map.size}&searchKeyword=${sk}">&gt;</a>
        </c:otherwise>
      </c:choose>
    </div>

  </div>

  <script>
    // 건수 변경 시: 현재 검색어를 유지한 채로 이동
    document.getElementById("sizeSelect").addEventListener("change", function () {
      var sk = document.getElementById("searchKeyword").value || "";
      location.href = "${pageContext.request.contextPath}/suggestion/list?page=1&size=" + this.value
                    + "&searchKeyword=" + encodeURIComponent(sk);
    });
  </script>

        </main>
    </div>
</div>

</body>
</html>