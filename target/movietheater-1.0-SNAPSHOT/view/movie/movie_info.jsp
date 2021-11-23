<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ taglib prefix="localeDate" tagdir="/WEB-INF/tags" %>
<%@ include file="/view/include/head/session_locale.jspf" %>
<c:set var="filmDTO" value="${requestScope.filmInfo}"/>

<!DOCTYPE html>
<html lang="${currentLocale}">
<%@ include file="/view/include/head/head.jspf" %>
<body style="background-color: #221f1f">
<div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
    <a href="${pageContext.request.contextPath}" class="my-0 mr-md-auto font-weight-normal text-white h5">Cinema</a>
    <nav class="my-md-0 mr-md-3">
    </nav>
    <c:choose>
        <c:when test="${filmDTO != null}">
            <a class="locale-btn btn btn-outline-light ml-2"
               href="?command=${param.command}&id=${param.id}&lang=en">En</a>
            <a class="locale-btn btn btn-outline-light ml-2"
               href="?command=${param.command}&id=${param.id}&lang=ru">Ru</a>
        </c:when>
        <c:otherwise>
            <a class="locale-btn btn btn-outline-light ml-2"
               href="movie_info.jsp?lang=en">En</a>
            <a class="locale-btn btn btn-outline-light ml-2"
               href="movie_info.jsp?lang=ru?">Ru</a>
        </c:otherwise>
    </c:choose>
    <a class="btn btn-outline-light mr-md-3 mr-md-2 ml-2"
       href="${pageContext.request.contextPath}/view/login/user_login.jsp">
        <fmt:message key="link.login"/>
    </a>
</div>
<c:choose>
    <c:when test="${filmDTO != null}">
        <div class="ml-3 pl-3 mt-3">
            <div class="row mr-0">
                <div class="col-mb-4">
                    <div class="card mb-4 box-shadow mt-2">
                        <img class="card-img-top movie-card movie-img" src="${filmDTO.film.img}" alt="default">
                    </div>
                </div>
                <div class="movie-info col-md-5 pl-3 pr-3 pt-2 ml-3 mr-3">
                    <h2>${filmDTO.film.title}</h2>
                    <div>
                        <ul class="movie-credentials">
                            <c:if test="${filmDTO.film.category != null}">
                                <li><b>Возраст:</b> ${filmDTO.film.category}+</li>
                            </c:if>
                            <li><b>Год:</b> ${filmDTO.film.year}</li>
                            <c:if test="${filmDTO.film.rating != 0}">
                                <li><b>Рейтинг:</b> ${filmDTO.film.rating}</li>
                            </c:if>
                            <c:if test="${filmDTO.film.len != 0}">
                                <li><b>Продолжительность:</b> ${filmDTO.film.len}мин.</li>
                            </c:if>
                            <c:if test="${filmDTO.genres.size() != 0}">
                                <li><b>Жанры:</b>
                                    <c:forEach var="genre" items="${filmDTO.genres}">
                                        "${genre.name}"
                                    </c:forEach>
                                </li>
                            </c:if>
                            <li><b>Последняя дата проката:</b> ${filmDTO.film.lastShowingDate}</li>
                        </ul>
                    </div>
                    <div class="movie-descr">
                        <p>${filmDTO.film.descr}</p>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="schedule-info pl-4">
                        <p class="schedule-title">Расписание</p>
                        <div class="dropdown">
                            <div class="title pointerCursor">Select an option</div>
                            <div id="dropdown_menu" class="menu pointerCursor hide">

                            </div>
                        </div>
                        <div id="result">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <section class="jumbotron text-center">
            <div class="container">
                <h1 class="jumbotron-heading">404</h1>
                <p class="lead text-muted">The page you requested was not found.</p>
            </div>
        </section>
    </c:otherwise>
</c:choose>
</body>
</html>