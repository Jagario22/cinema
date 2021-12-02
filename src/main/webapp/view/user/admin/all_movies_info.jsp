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
        <table id="all_movies" class="table table-striped hover responsive
        table-bordered table-sm" style="width:100%">
            <thead>
            <tr>
                <th>FilmID</th>
                <th>Film title</th>
                <td>Film rating</td>
                <td>Last showing date</td>
                <td>Age category</td>
                <td>Running time in mins</td>
                <td>Genres</td>
            </tr>
            </thead>
            <tbody class="overflow-auto">
            <c:forEach var="film" items="${sessionScope.films}" varStatus="rowCount">
                    <tr>
                        <td>${film.film.id}</td>
                        <td>${film.film.title}</td>
                        <td>${film.film.rating}</td>
                        <td>${film.film.lastShowingDate}</td>
                        <td>${film.film.category}</td>
                        <td>${film.film.len}</td>
                        <td>
                        <c:forEach var="genre" items="${film.genres}">
                            ${genre.name}
                        </c:forEach>
                        </td>
                    </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script>
    $(document).ready(function () {
        $('#all_movies').DataTable();
        $("#all_movies").DataTable({retrieve: true}).destroy();
        $("#all_movies").DataTable({
            scrollY: 400,
            scrollCollapse: true
        });
    });
</script>
</body>
</html>
