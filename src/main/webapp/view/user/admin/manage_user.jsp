<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ include file="/view/include/head/session_locale.jspf" %>
<!DOCTYPE html>
<html lang="${currentLocale}">
<c:set var="movies" value="${sessionScope.movies}"/>
<c:set var="value" value="Cinema"/>
<%@ include file="/view/include/head/head.jspf" %>
<body class="h-100">
<%@include file="../../include/head/admin_nav.jspf" %>
<div class="row h-100 vh-100 mr-0">
    <%@include file="/view/include/head/admin_menu.jspf" %>
    <div class="admin-body col-8 bg-white ml-3 pl-0 mt-5">
        <h2 class="text-center mb-3">Manage movies</h2>
        <table class="table">
            <thead>
            <tr>
                <th scope="col">id</th>
                <th scope="col">email</th>
                <th scope="col">login</th>
                <th scope="col">role</th>
                <th scope="col">action</th>
            </tr>
            </thead>
            <tbody class="overflow-auto">
            <c:forEach var="user" items="${sessionScope.users}" varStatus="rowCount">
                <tr>
                    <td>${user.id}</td>
                    <td>${user.email}</td>
                    <td>${user.login}</td>
                    <td>${user.role}</td>
                    <td>
                        <div class="dropdown">
                            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton"
                                    data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                Dropdown button
                            </button>
                            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                                <form action="${pageContext.request.contextPath}/" method="post">
                                    <input type="hidden" name="command" value="changeRole"/>
                                    <input type="hidden" name="id" value="${user.id}"/>
                                    <input type="hidden" name="role" value="admin">
                                    <button type="submit" class="dropdown-item">admin</button>
                                </form>
                                <form action="${pageContext.request.contextPath}/" method="post">
                                    <input type="hidden" name="command" value="changeRole"/>
                                    <input type="hidden" name="id" value="${user.id}"/>
                                    <input type="hidden" name="role" value="user">
                                    <button type="submit" class="dropdown-item">user</button>
                                </form>
                            </div>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
