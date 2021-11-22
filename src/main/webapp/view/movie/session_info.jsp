<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ taglib prefix="localeDate" tagdir="/WEB-INF/tags" %>
<%@ include file="/view/include/head/session_locale.jspf" %>
<c:set var="sessionInfo" value="${sessionScope.sessionInfo}"/>

<!DOCTYPE html>
<html lang="${currentLocale}">
<%@ include file="/view/include/head/head.jspf" %>
<body>
<div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
    <a href="${pageContext.request.contextPath}" class="my-0 mr-md-auto font-weight-normal text-white h5">Cinema</a>
    <nav class="my-md-0 mr-md-3">
    </nav>
</div>
<c:choose>
    <c:when test="${sessionInfo != null}">
        <section class="vh-100">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-sm-6 text-black">
                        <div class="d-flex align-items-center h-custom-2 px-5 ms-xl-4 mt-5 pt-5 pt-xl-0">
                            <form style="width: 23rem;" method="post" action="${pageContext.request.contextPath}/">
                                <h3 class="fw-normal mb-3 pb-3" style="letter-spacing: 1px;">
                                        ${sessionInfo.film.title}
                                </h3>
                                <input type="hidden" name="command" value="buyTicket">
                                <div class="row">
                                    <div class="col-sm-6">
                                        <div class="card mb-4 box-shadow mt-2">
                                            <img class="card-img-top session-info movie-img"
                                                 src="${sessionInfo.film.img}" alt="default">
                                        </div>
                                    </div>
                                    <div class="col-sm-5">
                                        ${sessionInfo.film.year}
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="col-sm-6 px-0 d-none d-sm-block">
                        <img src="https://pyxis.nymag.com/v1/imgs/978/4d0/4b4779e1dcb86984abe55c08366f9babe7-13-empty-theater.rsquare.w700.jpg"
                             alt="Login image" class="w-100 vh-100" style="object-fit: cover; object-position: left;">
                    </div>
                </div>
            </div>
        </section>
    </c:when>
</c:choose>
</body>
</html>