<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ taglib prefix="localeDate" tagdir="/WEB-INF/tags" %>
<%@ include file="/view/include/head/session_locale.jspf" %>
<c:set var="user" value="${sessionScope.user}"/>


<!DOCTYPE html>
<html lang="${currentLocale}">
<%@ include file="/view/include/head/head.jspf" %>
<body>
<div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
    <a href="${pageContext.request.contextPath}" class="my-0 mr-md-auto font-weight-normal text-white h5">Cinema</a>
    <nav class="my-md-0 mr-md-3">
    </nav>
    <a class="locale-btn btn btn-outline-light ml-2" href="?command=${param
    .command}&lang=en">En</a>
    <a class="locale-btn btn btn-outline-light ml-2" href="?command=${param
    .command}&lang=ru">Ru</a>
    <%@ include file="../include/form/logout.jspf" %>
</div>
<div class="container mt-5">
    <div class="main-body">
        <div class="row">
            <c:if test="${sessionScope.ticketPurchaseError != null}">
                <div class="alert alert-danger vw-100 text-center justify-content-center" role="alert">
                    <p>${sessionScope.ticketPurchaseError}</p>
                </div>
            </c:if>
            <c:if test="${sessionScope.successTicketPurchase != null}">
                <div class="alert alert-success vw-100 text-center justify-content-center" role="alert">
                    <p>Your payment was successful and your order is complete</p>
                </div>
            </c:if>
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-body">
                        <div class="d-flex flex-column align-items-center text-center">
                            <i class="far fa-user cinema-color"></i>
                            <div class="mt-3">
                                <h4>${user.login}</h4>
                                <p class="text-secondary mb-1">${user.email}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-8">
                <div class="row">
                    <c:choose>
                        <c:when test="${sessionScope.userTickets != null && sessionScope.userTickets.size() != 0}">
                            <c:forEach var="ticket" items="${sessionScope.userTickets}">
                                <div class="col-sm-6 mb-2">
                                    <div class="card">
                                        <div class="card-body">
                                            <h5 class="d-flex align-items-center mb-3">Ticket
                                                <span class="text-secondary float-right"></span>
                                            </h5>
                                            <p>${ticket.sessionInfo.film.title}</p>
                                            <p class="text-secondary">
                                                <localeDate:format
                                                        date="${ticket.sessionInfo.date}"
                                                        pattern="dd-MM-yyyy EEEE"/>
                                                <span class="text-secondary">
                                                        ${ticket.sessionInfo.time}
                                                </span>
                                            </p>
                                            <p>
                                                Row: ${ticket.row}
                                            </p>
                                            <p>
                                                Place: ${ticket.placeNumber}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="col vw-100  mb-2 text-center">
                                <h2>No tickets</h2>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>