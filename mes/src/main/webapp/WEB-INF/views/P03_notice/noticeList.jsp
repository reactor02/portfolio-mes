<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공지사항</title>
<link rel="stylesheet" href="/mes/static/css/P00_common/common.css">
<link rel="stylesheet" href="/mes/static/css/P00_layout/header.css">
<script src="/mes/static/js/00_layout/header.js"></script>
<link rel="stylesheet" href="/mes/static/css/P00_layout/snb.css">
<script src="/mes/static/js/00_layout/snb.js"></script>
<link rel="stylesheet" href="/mes/static/css/P07_work/main.css">
<link rel="stylesheet" href="/mes/static/css/P03_notice/notice.css">
</head>
<body>
<%@ include file="/WEB-INF/views/P00_layout/header.jsp" %>
<div class="layout_snb">
  <div class="snbContent"><%@ include file="/WEB-INF/views/P00_layout/snb.jsp" %></div>
  <div class="content">
  <main class="nc">

  <c:set var="ctx"            value="${pageContext.request.contextPath}" />
  <c:set var="list"           value="${map.list}" />
  <c:set var="size"           value="${map.size}" />
  <c:set var="page"           value="${map.page}" />
  <c:set var="keyword"        value="${map.keyword}" />
  <c:set var="totalPages"     value="${map.totalPages}" />
  <c:set var="groupStartPage" value="${map.groupStartPage}" />
  <c:set var="groupEndPage"   value="${map.groupEndPage}" />

  <div id="page-notice-list">
    <div class="page-header-row">
      <div>
        <h1>공지사항</h1>
        <p>공장 운영 관련 공지사항을 확인하세요</p>
      </div>
      <%-- auth 2 이상만 등록 버튼 표시 --%>
      <c:if test="${dto.auth >= 2}">
        <button class="btn btn-primary btn-sm"
          onclick="location.href='${ctx}/notice/register'">+ 공지사항 등록</button>
      </c:if>
    </div>

    <div class="table-toolbar">
      <form id="searchForm" action="${ctx}/notice/list" method="get">
        <input type="hidden" name="page"  value="1">
        <input type="hidden" name="size"  value="${size}">
        <div class="search-wrap">
          <svg class="search-icon" width="14" height="14" viewBox="0 0 14 14" fill="none">
            <circle cx="6" cy="6" r="4.5" stroke="#94A3B8" stroke-width="1.4"/>
            <path d="M9.5 9.5L12 12" stroke="#94A3B8" stroke-width="1.4" stroke-linecap="round"/>
          </svg>
          <input type="text" class="search-input" placeholder="제목 검색"
                 id="noticeKeyword" name="keyword" value="${keyword}">
          <button type="submit" class="btn btn-outline btn-sm nc-search-btn">검색</button>
        </div>
      </form>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            <th class="num-col">번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>등록일</th>
            <th>조회수</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="noticeDTO" items="${list}">
          <tr>
            <td class="center">${fn:substringAfter(noticeDTO.boardno, 'ann_')}</td>
            <td class="nc-title-col"
              onclick="location.href='${ctx}/notice/detail?boardno=${noticeDTO.boardno}&page=${page}&size=${size}'">
              ${noticeDTO.title}
            </td>
            <td>${noticeDTO.ename}</td>
            <td><fmt:formatDate value="${noticeDTO.ctime}" pattern="yyyy-MM-dd"/></td>
            <td class="center">${noticeDTO.views}</td>
          </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>

    <%-- 페이지네이션 --%>
    <div class="pagination">
      <c:choose>
        <c:when test="${page <= 1}">
          <button class="page-btn disabled">&lt;</button>
        </c:when>
        <c:otherwise>
          <button class="page-btn"
            onclick="location.href='${ctx}/notice/list?page=${page-1}&size=${size}&keyword=${keyword}'">&lt;</button>
        </c:otherwise>
      </c:choose>

      <c:forEach var="p" begin="${groupStartPage}" end="${groupEndPage}">
        <c:choose>
          <c:when test="${p == page}">
            <button class="page-btn active">${p}</button>
          </c:when>
          <c:otherwise>
            <button class="page-btn"
              onclick="location.href='${ctx}/notice/list?page=${p}&size=${size}&keyword=${keyword}'">${p}</button>
          </c:otherwise>
        </c:choose>
      </c:forEach>

      <c:choose>
        <c:when test="${page >= totalPages}">
          <button class="page-btn disabled">&gt;</button>
        </c:when>
        <c:otherwise>
          <button class="page-btn"
            onclick="location.href='${ctx}/notice/list?page=${page+1}&size=${size}&keyword=${keyword}'">&gt;</button>
        </c:otherwise>
      </c:choose>
    </div>

  </div>

  </main>
  </div>
</div>
</body>
<script src="${ctx}/static/js/03_notice/notice.js"></script>
</html>