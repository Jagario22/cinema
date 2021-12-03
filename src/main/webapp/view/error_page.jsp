<%@ page isErrorPage="true" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ include file="/view/include/head/session_locale.jspf" %>

<c:set var="code" value="${requestScope['javax.servlet.error.status_code']}"/>
<c:set var="message" value="${requestScope['javax.servlet.error.message']}"/>
<c:set var="exception" value="${requestScope['javax.servlet.error.exception']}"/>


<!DOCTYPE html>
<html lang="${currentLocale}">
<c:set var="value" value="Error"/>
<%@ include file="/view/include/head/head.jspf" %>
<body>
<div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
    <%@ include file="/view/include/head/nav.jspf" %>
</div>
<section class="jumbotron text-center">
    <div class="container">

        <c:if test="${not empty code}">
            <h3 class="text-center text-danger">${code}</h3>
        </c:if>
        <h3>Oops. Something happened</h3>
        <c:if test="${not empty message}">
            <h3>${message}</h3>
        </c:if>
    </div>
</section>
</body>
</html>