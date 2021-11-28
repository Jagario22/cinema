<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
    <style>
        td {
            border-bottom: 1px solid #ddd;
            padding: 5px;
        }
    </style>
    <title>Cinema</title>
</head>

<body>

Page ${param.page} of ${param.pageCount}

|

<c:choose>
    <c:when test="${param.page - 1 > 0}">
        <a href="${pageContext.request.contextPath}?page=${param.page-1}&pageSize=${param.pageSize}">Previous</a>
    </c:when>
    <c:otherwise>
        Previous
    </c:otherwise>
</c:choose>


<c:forEach var="p" begin="${param.minPossiblePage}" end="${param.maxPossiblePage}">
    <c:choose>
        <c:when test="${param.page == p}">${p}</c:when>
        <c:otherwise>
            <a href="${pageContext.request.contextPath}?page=${p}&pageSize=${param.pageSize}">${p}</a>
        </c:otherwise>
    </c:choose>
</c:forEach>

<c:choose>
    <c:when test="${param.page + 1 <= param.pageCount}">
        <a href="${pageContext.request.contextPath}?page=${param.page+1}&pageSize=${param.pageSize}">Next</a>
    </c:when>
    <c:otherwise>
        Next
    </c:otherwise>
</c:choose>

|
</body>
</html>