<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ include file="/view/include/head/cinema_constants.jspf" %>
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
    <div class="admin-body col-8 bg-white ml-3 pl-0 mt-3">
        <section id="file-export">
            <div class="row">
                <div class="col-12">
                    <div class="card">
                        <div class="card-header">
                            <c:if test="${sessionScope.incorrectDataError != null}">
                                <div class="alert alert-danger" role="alert">
                                        ${sessionScope.incorrectDataError}
                                </div>

                            </c:if>
                            <c:if test="${sessionScope.successMsg != null}">
                                <div class="alert alert-success" role="alert">
                                        ${sessionScope.successMsg}
                                </div>

                            </c:if>
                            <h2 class="text-center mb-3">Manage schedule</h2>
                        </div>
                        <div class="card-content collapse show">
                            <div class="card-body card-dashboard">
                                <div id="DataTables_Table_10_wrapper" class="dataTables_wrapper dt-bootstrap4">
                                    <div class="dt-buttons btn-group mb-2">
                                        <button type="button" class="btn btn-dark"
                                                data-toggle="modal" data-target="#addSessionModal"
                                                tabindex="0"><span>Add Film to Schedule</span></button>
                                        <button type="button" class="btn btn-dark"
                                                data-toggle="modal" data-target="#cancelSessionModal"
                                                tabindex="0"><span>Cancel film session</span>
                                        </button>
                                    </div>
                                    <table id="all_sessions" class="table table-striped hover responsive
        table-bordered table-sm" style="width:100%">
                                        <thead>
                                        <tr>
                                            <th>Session ID</th>
                                            <th>Datetime</th>
                                            <th>End time</th>
                                            <th>End datetime</th>
                                            <td>Film title</td>
                                            <td>Film ID</td>

                                        </tr>
                                        </thead>
                                        <tbody class="overflow-auto">
                                        <c:forEach var="session" items="${sessionScope.sessions}" varStatus="rowCount">
                                            <tr>
                                                <td>${session.id}</td>
                                                <td>${session.date} ${session.time}</td>
                                                <td>${session.time.plusMinutes(session.film.len)
                                                        .plusMinutes(breakTime)}</td>
                                                <td>${session.film.title}</td>
                                                <td>${session.film.title}</td>
                                                <td>${session.film.id}</td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</div>


<div class="modal fade" id="addSessionModal" tabindex="-1" role="dialog" aria-labelledby="addSessionModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form class="form-group" action="${pageContext.request.contextPath}/" method="post">
                <div class="modal-header">
                    <h5 class="modal-title" id="addSessionModalLabel">Add film to schedule</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <input type="hidden" name="command" value="addSession">
                    <c:set var="now" value="<%=new java.util.Date()%>"/>
                    <label for="sessionIdInput" class="mb-2">
                        Session date
                        <input name="date" type="date" min="<fmt:formatDate
                                                           pattern="yyyy-MM-dd"  value="${now}"/>"
                               aria-describedby="datetimeHelp"
                               class="form-control" id="sessionDateInput" required>
                    </label>
                    <label for="sessionIdInput">
                        Session time
                        <input name="time" type="time"
                               min="${startTime}" max="${endTime}"
                               aria-describedby="datetimeHelp"
                               class="form-control" id="sessionTimeInput" required>
                    </label>
                    <small id="datetimeHelp" class="form-text text-muted">Session date time should after now
                        and unique in schedule</small>
                    <label for="filmIdInput">
                        Film ID
                        <input name="filmId" type="number" min="1" class="form-control" id="filmIdInput"
                               aria-describedby="filmIdHelp" placeholder="Film ID" required>
                    </label>
                    <small id="filmIdHelp" class="form-text text-muted">Film id should be of existed film and
                        bigger than 1</small>
                    <label>
                        Language of session
                        <select class="form-select" name="s_lang" required>
                            <option value="ukrainian">ukrainian</option>
                            <option value="s_lang">original</option>
                        </select>
                    </label>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Save changes</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="modal fade" id="cancelSessionModal" tabindex="-1" role="dialog" aria-labelledby="cancelSessionModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form action="${pageContext.request.contextPath}/" method="post">
                <div class="modal-header">
                    <h5 class="modal-title" id="cancelSessionModalLabel">Cancel session</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <div class="modal-body">

                    <input type="hidden" name="command" value="cancelSession"/>
                    <label for="sessionIdInput">
                        Session ID
                        <input name="sessionId" type="number" min="1" class="form-control" id="sessionIdInput"
                               aria-describedby="sessionIdHelp" placeholder="Enter session ID">
                    </label>
                    <small id="sessionIdHelp" class="form-text text-muted">Session id should be of existed session and
                        bigger than 1</small>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                    <button type="submit" class="btn btn-primary">Save changes</button>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
<script>
    $(document).ready(function () {
        $('#all_sessions').DataTable();
        $("#all_sessions").DataTable({retrieve: true}).destroy();
        $("#all_sessions").DataTable({
            scrollY: 400,
            scrollCollapse: true
        });
    });
</script>
</body>
</html>
