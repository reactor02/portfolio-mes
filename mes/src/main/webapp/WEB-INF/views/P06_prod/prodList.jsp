<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>생산관리</title>

<link rel="stylesheet" href="/mes/static/css/P00_common/common.css">
<link rel="stylesheet" href="/mes/static/css/P00_layout/header.css">
<script src="/mes/static/js/00_layout/header.js"></script>
<link rel="stylesheet" href="/mes/static/css/P00_layout/snb.css">
<script src="/mes/static/js/00_layout/snb.js"></script>
<link rel="stylesheet" href="/mes/static/css/P06_prod/prod.css">

</head>
<body>

<%@ include file="/WEB-INF/views/P00_layout/header.jsp" %>

<div class="layout_snb">
    <div class="snbContent">
        <%@ include file="/WEB-INF/views/P00_layout/snb.jsp" %>
    </div>
    <div class="content">
        <main class="pp">

  <div id="page-list">
    <div class="page-header-row">
      <div>
        <h1>생산관리</h1>
        <p class="page-header-desc">주간 생산 계획을 조회하고 관리합니다</p>
      </div>
      <div class="header-actions">
       
        <%-- auth 2이상만 등록 버튼 표시 --%>
        <c:if test="${dto.auth >= 2}">
          <button class="btn btn-primary btn-sm" onclick="openRegisterModal()">
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none">
              <path d="M7 2v10M2 7h10" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
            생산계획 등록
          </button>
        </c:if>
      </div>
    </div>

    <div class="table-toolbar">
      <select id="sizeSelect" class="date-input size-select">
        <option value="5"  <c:if test="${map.size == 5}">selected</c:if>>5개씩</option>
        <option value="10" <c:if test="${map.size == 10}">selected</c:if>>10개씩</option>
        <option value="15" <c:if test="${map.size == 15}">selected</c:if>>15개씩</option>
        <option value="20" <c:if test="${map.size == 20}">selected</c:if>>20개씩</option>
      </select>

      <label class="toolbar-label" for="startDate">기간:</label>
      <div class="toolbar-date-range">
        <input type="date" class="date-input" id="startDate" name="startDate"
               title="시작일" value="${map.startDate}">
        <span class="toolbar-date-sep">~</span>
        <input type="date" class="date-input" id="endDate" name="endDate"
               title="종료일" value="${map.endDate}">
      </div>

      <%-- 검색 인풋 + 검색/초기화 버튼 한 줄 --%>
      <div class="toolbar-right">
        <div class="search-wrap">
          <svg class="search-icon" width="14" height="14" viewBox="0 0 14 14" fill="none">
            <circle cx="6" cy="6" r="4.5" stroke="#94A3B8" stroke-width="1.4"/>
            <path d="M9.5 9.5L12 12" stroke="#94A3B8" stroke-width="1.4" stroke-linecap="round"/>
          </svg>
          <input type="text" class="search-input" placeholder="제품명 또는 계획ID로 검색"
                 id="searchKeyword" name="searchKeyword" value="${map.keyword}">
        </div>
        <button type="button" class="btn btn-outline btn-sm toolbar-btn" id="searchBtn">검색</button>
        <button type="button" class="btn btn-outline btn-sm toolbar-btn" id="resetBtn">초기화</button>
      </div>
    </div>

    <div class="table-wrap">
      <table>
        <thead>
          <tr>
            
            <th>계획ID</th>
            <th>제품명</th>
            <th>담당자</th>
            <th>목표수량</th>
            <th>기간</th>
            <th>진행률</th>
            <th>상태</th>

          </tr>
        </thead>
        <tbody id="planListBody">
          <c:forEach var="row" items="${map.list}">
            <tr>
              
              <td><span class="plan-id-text">${row.planId}</span></td>
              <td class="item-name-col">
                <a href="/mes/prod/detail?planId=${row.planId}" class="item-name-link">
                  ${row.itemName}
                </a>
              </td>
              <td>${row.ename}</td>
              <td><fmt:formatNumber value="${row.planQty}" pattern="#,###"/> EA</td>
              <td>
                <span class="date-range-text">
                  <fmt:formatDate value="${row.planSdate}" pattern="MM-dd"/>
                  &nbsp;~&nbsp;
                  <fmt:formatDate value="${row.planEdate}" pattern="MM-dd"/>
                </span>
              </td>
              <td>
                <div class="list-progress-wrap">
                  <div class="list-progress-bg">
                    <div class="list-progress-fill" style="width:${row.progressPct}%"></div>
                  </div>
                  <span class="list-progress-pct">${row.progressPct}%</span>
                </div>
              </td>
              <td>
                <c:choose>
                  <c:when test="${row.status == 0}"><span class="badge badge-gray">대기</span></c:when>
                  <c:when test="${row.status == 1}"><span class="badge badge-blue">진행중</span></c:when>
                  <c:when test="${row.status == 2}"><span class="badge badge-green">완료</span></c:when>
                  <c:when test="${row.status == 3}"><span class="badge badge-yellow">보류</span></c:when>
                  <c:when test="${row.status == 4}"><span class="badge badge-red">취소</span></c:when>
                </c:choose>
              </td>

            </tr>
          </c:forEach>
          <c:if test="${empty map.list}">
            <tr>
              <td colspan="8" class="empty-row">등록된 생산계획이 없습니다.</td>
            </tr>
          </c:if>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 -->
    <div class="pagination">

      <c:set var="searchParam"
             value="&size=${map.size}&keyword=${map.keyword}&startDate=${map.startDate}&endDate=${map.endDate}"/>

      <%-- 이전 --%>
      <c:choose>
        <c:when test="${map.page <= 1}">
          <button class="page-btn" disabled>&lt;</button>
        </c:when>
        <c:otherwise>
          <a class="page-btn" href="/mes/prod/list?page=${map.page - 1}${searchParam}">&lt;</a>
        </c:otherwise>
      </c:choose>

      <%-- 페이지 번호 --%>
      <c:forEach var="i" begin="${map.startSection}" end="${map.endSection}">
        <c:choose>
          <c:when test="${map.page eq i}">
            <button class="page-btn page-btn-active">${i}</button>
          </c:when>
          <c:otherwise>
            <a class="page-btn" href="/mes/prod/list?page=${i}${searchParam}">${i}</a>
          </c:otherwise>
        </c:choose>
      </c:forEach>

      <%-- 다음 --%>
      <c:choose>
        <c:when test="${map.page >= map.totalPage}">
          <button class="page-btn" disabled>&gt;</button>
        </c:when>
        <c:otherwise>
          <a class="page-btn" href="/mes/prod/list?page=${map.page + 1}${searchParam}">&gt;</a>
        </c:otherwise>
      </c:choose>

    </div>

  </div>


  <!-- 등록 모달 — regStatus 제거 -->
  <div id="modalRegister" class="pp-modal-overlay" style="display:none;">
    <div class="pp-modal">
      <div class="pp-modal-header">
        <h2>생산계획 등록</h2>
        <button class="pp-modal-close" onclick="closeRegisterModal()">&#x2715;</button>
      </div>
      <div class="pp-modal-body">
        <form id="registerForm" action="/mes/prod/insert" method="post">
          <div class="form-grid">

            <div class="form-group">
              <label class="form-label" for="regGroup">대분류 <span class="req">*</span></label>
              <select class="form-control" id="regGroup" name="gId" required>
                <option value="">대분류 선택</option>
                <c:forEach var="g" items="${groupList}">
                  <c:choose>
                    <c:when test="${g.gId == '30'}">
                      <option value="${g.gId}">완제품</option>
                    </c:when>
                    <c:when test="${g.gId == '20'}">
                      <option value="${g.gId}">반제품</option>
                    </c:when>
                  </c:choose>
                </c:forEach>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label" for="regSubItem">소분류 <span class="req">*</span></label>
              <select class="form-control" id="regSubItem" name="itemId" required>
                <option value="">소분류 선택</option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label" for="regUnit">단위</label>
              <input type="text" class="form-control reg-readonly" id="regUnit"
                     name="unit" readonly tabindex="-1" placeholder="소분류 선택 시 자동입력">
            </div>

            <div class="form-group">
              <label class="form-label" for="regSpec">규격</label>
              <input type="text" class="form-control reg-readonly" id="regSpec"
                     name="spec" readonly tabindex="-1" placeholder="소분류 선택 시 자동입력">
            </div>

            <div class="form-group">
              <label class="form-label" for="regEmpName">담당자 <span class="req">*</span></label>
              <div class="emp-search-wrap">
                <input type="text" class="form-control" id="regEmpName"
                       placeholder="돋보기를 눌러 검색" readonly tabindex="-1">
                <input type="hidden" id="regEmpId" name="empId">
                <button type="button" class="emp-search-btn" onclick="openEmpPopup('register')" title="담당자 검색">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="7" cy="7" r="5" stroke="#64748b" stroke-width="1.6"/>
                    <path d="M11 11L14 14" stroke="#64748b" stroke-width="1.6" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label" for="regQty">목표수량 <span class="req">*</span></label>
              <input type="number" class="form-control" id="regQty" name="planQty"
                     placeholder="목표 수량 입력" min="1" required>
            </div>

            <div class="form-group form-group-wide">
              <label class="form-label">기간 <span class="req">*</span></label>
              <div class="date-range-wrap">
                <input type="date" class="form-control" id="regStartDate" name="planSdate" required>
                <span class="date-range-sep">~</span>
                <input type="date" class="form-control" id="regEndDate" name="planEdate" required>
              </div>
            </div>

          </div>
          <div class="form-actions">
            <button type="button" class="btn btn-outline" onclick="closeRegisterModal()">취소</button>
            <button type="submit" class="btn btn-primary btn-sm">등록</button>
          </div>
        </form>
      </div>
    </div>
  </div>


  <!-- 수정 모달 -->
  <div id="modalEdit" class="pp-modal-overlay" style="display:none;">
    <div class="pp-modal">
      <div class="pp-modal-header">
        <h2>생산계획 수정</h2>
        <button class="pp-modal-close" onclick="closeEditModal()">&#x2715;</button>
      </div>
      <div class="pp-modal-body">
        <form id="editForm" action="/mes/prod/update" method="post">
          <input type="hidden" id="editPlanId" name="planId">
          <input type="hidden" id="editItemId" name="itemId">
          <div class="form-grid">
            <div class="form-group">
              <label class="form-label" for="editGroupView">대분류</label>
              <input type="text" class="form-control reg-readonly" id="editGroupView"
                     readonly tabindex="-1">
            </div>
            <div class="form-group">
              <label class="form-label" for="editSubItemView">소분류</label>
              <input type="text" class="form-control reg-readonly" id="editSubItemView"
                     readonly tabindex="-1">
            </div>
            <div class="form-group">
              <label class="form-label" for="editEmpName">담당자 <span class="req">*</span></label>
              <div class="emp-search-wrap">
                <input type="text" class="form-control" id="editEmpName"
                       placeholder="돋보기를 눌러 검색" readonly tabindex="-1">
                <input type="hidden" id="editEmpId" name="empId">
                <button type="button" class="emp-search-btn" onclick="openEmpPopup('edit')" title="담당자 검색">
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                    <circle cx="7" cy="7" r="5" stroke="#64748b" stroke-width="1.6"/>
                    <path d="M11 11L14 14" stroke="#64748b" stroke-width="1.6" stroke-linecap="round"/>
                  </svg>
                </button>
              </div>
            </div>
            <div class="form-group">
              <label class="form-label" for="editQty">목표수량 <span class="req">*</span></label>
              <input type="number" class="form-control" id="editQty" name="planQty" min="1" required>
            </div>
            <div class="form-group">
              <label class="form-label" for="editStatus">상태 <span class="req">*</span></label>
              <select class="form-control" id="editStatus" name="status" required>
                <option value="0">대기</option>
                <option value="1">진행중</option>
                <option value="2">완료</option>
                <option value="3">보류</option>
                <option value="4">취소</option>
              </select>
            </div>
            <div class="form-group form-group-wide">
              <label class="form-label">기간 <span class="req">*</span></label>
              <div class="date-range-wrap">
                <input type="date" class="form-control" id="editStartDate" name="planSdate" required>
                <span class="date-range-sep">~</span>
                <input type="date" class="form-control" id="editEndDate" name="planEdate" required>
              </div>
            </div>
          </div>
          <div class="form-actions">
            <button type="button" class="btn btn-outline" onclick="closeEditModal()">취소</button>
            <button type="submit" class="btn btn-primary btn-sm">수정</button>
          </div>
        </form>
      </div>
    </div>
  </div>


  <!-- 담당자 검색 팝업 -->
  <div id="empPopup" class="emp-popup-overlay" style="display:none;">
    <div class="emp-popup">
      <div class="emp-popup-header">
        <h3>담당자 검색</h3>
        <button class="emp-popup-close" onclick="closeEmpPopup()">&#x2715;</button>
      </div>
      <div class="emp-popup-search">
        <input type="text" class="search-input" id="empSearchKeyword" placeholder="이름 또는 사번 검색">
        <button class="btn btn-primary btn-sm" id="empSearchBtn">검색</button>
      </div>
      <div class="emp-popup-body">
        <table>
          <thead>
            <tr>
              <th>사번</th>
              <th>이름</th>
              <th>부서</th>
            </tr>
          </thead>
          <tbody id="empListBody"></tbody>
        </table>
      </div>
      <div class="emp-popup-footer">
        <div class="emp-paging" id="empPaging"></div>
      </div>
    </div>
  </div>


  <script>
    const itemDataMap = {};
    <c:forEach var="item" items="${itemList}">
    (function() {
      var gId = '${item.gId}';
      if (!itemDataMap[gId]) itemDataMap[gId] = [];
      itemDataMap[gId].push({
        itemId   : '${item.itemId}',
        itemName : '${item.itemName}',
        unit     : '${item.unit}',
        spec     : '${item.spec}'
      });
    })();
    </c:forEach>
  </script>
  <script src="/mes/static/js/06_prod/prod.js"></script>

        </main>
    </div>
</div>

</body>
</html>