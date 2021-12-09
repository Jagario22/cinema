<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="view/include/head/libs.jspf" %>
<%@ include file="view/include/head/session_locale.jspf" %>

<!DOCTYPE html>
<html lang="${currentLocale}">
<c:set var="movies" value="${sessionScope.movies}"/>
<c:set var="value" value="Cinema"/>
<%@ include file="view/include/head/head.jspf" %>
<body class="h-100" >
<c:choose>
    <c:when test="${sessionScope.user != null}">
        <c:set var="userRole" value="${sessionScope.user.role.name()}"/>
        <c:choose>
            <c:when test="${userRole.equals('USER')}">
                <%@include file="view/include/main/user/main_page.jspf" %>
            </c:when>
            <c:when test="${userRole.equals('ADMIN')}">
                <%@include file="view/include/main/admin/main_page.jspf" %>
            </c:when>
        </c:choose>
    </c:when>
    <c:otherwise>
        <div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
            <%@ include file="/view/include/head/nav.jspf" %>
            <a class="btn btn-outline-light mr-md-3 ml-2"
               href="${pageContext.request.contextPath}/view/login/user_login.jsp">
                <fmt:message key="link.login"/>
            </a>
        </div>
        <%@ include file="view/include/main/main_page.jspf" %>
    </c:otherwise>
</c:choose>
</body>
</html>
