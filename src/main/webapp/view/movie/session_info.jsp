<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ include file="/view/include/head/cinema_constants.jspf" %>
<%@ taglib prefix="localeDate" tagdir="/WEB-INF/tags/format" %>
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
    <a class="locale-btn btn btn-outline-light ml-2" href="?command=${param
    .command}&filmId=${param.filmId}&sessionId=${param.sessionId}&lang=en">En</a>
    <a class="locale-btn btn btn-outline-light ml-2" href="?command=${param
    .command}&filmId=${param.filmId}&sessionId=${param.sessionId}&lang=ru">Ru</a>
</div>
<form class="h-100" id="ticket" action="${pageContext.request.contextPath}/" method="post">
    <input type="hidden" name="command" value="buyTicket" form="ticket">
    <div class="row h-100 mr-0">
        <div class="col-md-8">
            <div class="session-info">
                <div class="row">
                    <div class="col-md-3">
                        <img class="card-img-top session-info movie-img"
                             src="${sessionInfo.film.img}" alt="default">
                    </div>
                    <div class="col-md-8 mt-3">
                        <h2 class="session-info movie-title">
                            ${sessionInfo.film.title}
                        </h2>
                        <div class="row">
                            <div class="col-md-6 ml-4">
                                <div class="card time-card mb-3 mt-5" style="max-width: 250px">
                                    <div class="row no-gutters">
                                        <div class="col-md-4 text-center pb-2 pt-3 pr-3 pl-3 calendar-icon">
                                            <i class="fa fa-calendar" aria-hidden="true"></i>
                                        </div>
                                        <div class="col-md-8">
                                            <div class="p-2 card-body text-center text-dark">
                                                <p class="mb-0 card-text">${sessionInfo.date}</p>
                                                <p class="mb-0 cinema-color card-title font-weight-bold">
                                                    <localeDate:format
                                                            date="${sessionInfo.date}"
                                                            pattern="EEEE"/></p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-5 ml-4">
                                <div class="card time-card mb-3 mt-5" style="max-width: 250px">
                                    <div class="row no-gutters">
                                        <div class="col-md-4 text-center pb-2 pt-3 pr-3 pl-3 calendar-icon">
                                            <i class="fa fa-clock-o" aria-hidden="true"></i>
                                        </div>
                                        <div class="col-md-8">
                                            <div class="p-2 card-body text-center text-dark">
                                                <p class="mb-0 card-text">Time</p>
                                                <p class="mb-0 cinema-color card-title font-weight-bold">
                                                    <c:set var="min" value="${sessionInfo.film.len}"/>
                                                    ${sessionInfo.time} -
                                                    ${sessionInfo.time.plusMinutes(min)}
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="places-info">
                                <h2 class="places-title">Places</h2>
                                <label for="rowSelect" class="mr-2 mt-2">Row</label><br/>
                                <select name="rowSelect" id="rowSelect" class="ticket-select form-control"
                                        onfocus='this.size=5;' onblur='this.size=1;'
                                        onchange='this.size=1; this.blur();'>
                                    <c:forEach var="index" begin="1" end="${countOfRows}">
                                        <option value="${index}">${index}</option>
                                    </c:forEach>
                                </select>
                                <br/>
                                <label for="placeSelect" class="mt-2">Place</label><br/>
                                <select name="ticketId" id="placeSelect" form="ticket"
                                        class="ticket-select form-control"
                                        onfocus='this.size=5;' onblur='this.size=1;'
                                        onchange='this.size=1; this.blur();' required>
                                    <option value="">Select something</option>
                                    <c:forEach var="index" begin="1" end="${countOfRowSeats}">
                                        <option class="text-danger" value="${index}" disabled>${index}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="pb-3">
            </div>
        </div>
        <div class="col-md-4 ticket-info p-3">
            <div class="ticket-title">
                Tickets
                <p class="float-right text-secondary mr-3">Price: <span id="price"></span></p>
            </div>
            <p class="float-right text-secondary mr-3">Your balance: <span
                    id="balance">${sessionScope.user.wallet.balance}</span></p>
            <div class="ticket-icon">
                <i class="fa fa-ticket" aria-hidden="true"></i>
            </div>
            <p class="ticket-title">Ticket info</p>
            <input type="hidden" name="sessionId" value="${sessionInfo.id}" form="ticket">
            <input type="hidden" name="userId" value="${sessionScope.user.id}" form="ticket">
            <p>Row: <span id="selected_row_info"></span></p>
            <p>Place: <span id="selected_place_info"></span></p>
            <p>Ticket type: <span id="ticket_type_info"></span></p>
            <button id="ticket_btn" class="btn ticket-button cinema-button btn-lg btn-block btn-dark" type="submit"
                    form="ticket">Buy
            </button>
        </div>
    </div>
</form>
</body>
</html>