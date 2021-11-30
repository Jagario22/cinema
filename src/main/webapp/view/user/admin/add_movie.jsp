<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ include file="/view/include/head/session_locale.jspf" %>
<!DOCTYPE html>
<html lang="${currentLocale}">
<c:set var="movies" value="${sessionScope.movies}"/>
<c:set var="value" value="Cinema"/>
<%@ include file="/view/include/head/head.jspf" %>
<body class="h-100 vw-100">
<%@include file="../../include/head/admin_nav.jspf" %>
<div class="container m-0">
    <div class="row h-100 vh-100 vw-100">
        <%@include file="../../include/head/admin_menu.jspf" %>
        <div class="ml-3 col-9 mt-2">
            <form action="${pageContext.request.contextPath}/" method="post" class="form">
                <input type="hidden" name="command" value="addMovie">
                <div class="row">
                    <div class="col-12 col-md-3 form__cover">
                        <div class="row">
                            <div class="col-12 col-sm-6 col-md-12">
                                <div class="form__img">
                                    <label for="form__img-upload">Upload cover</label>
                                    <input id="form__img-upload" name="img" type="file"
                                           accept=".png, .jpg, .jpeg">
                                    <img id="form__img" src="#" alt=" ">
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-md-9 form__content">
                        <div class="row">
                            <div class="col-12 mt-3">
                                <div class="form__group">
                                    <label>
                                        Title
                                        <input name="title" type="text" class="form__input" placeholder="Title">
                                    </label>
                                </div>
                            </div>
                            <div class="col-12 mt-5">
                                <div class="form__group">
                                    <label for="text">
                                        Description
                                    </label>
                                    <textarea id="text" name="description" class="form__textarea"
                                                      placeholder="Description"></textarea>
                                </div>
                            </div>
                            <div class="col-12 col-sm-6 col-lg-3 mt-4">
                                <div class="form__group">
                                    <label>
                                        Production year
                                        <input name="year_prod" type="text" class="form__input"
                                               placeholder="Production year">
                                    </label>
                                </div>
                            </div>
                            <div class="col-12 col-sm-6 col-lg-3 mt-4">
                                <div class="form__group">
                                    <label>
                                        Running time in min
                                        <input type="text" name="len"
                                               class="form__input" placeholder="Running timed in minutes">
                                    </label>
                                </div>
                            </div>
                            <div class="col-12 col-sm-6 col-lg-3 mt-4">
                                <div class="form__group">
                                    <label>
                                        Age category
                                        <input name="category"
                                               type="number" min="0" max="18" class="form__input" placeholder="Age">
                                    </label>
                                </div>
                            </div>
                            <div class="col-12 col-sm-6 col-lg-3 mt-4">
                                <div class="form__group">
                                    <label>
                                        Rating
                                        <input type="number" name="rating" min="1" max="10" class="form__input"
                                               placeholder="Rating">
                                    </label>
                                </div>
                            </div>
                            <div class="col-12 col-lg-6 mt-5">
                                <ul class="multi-select">
                                    <li class="dropdown pt-2">
                                        <a href="#" data-toggle="dropdown" class="dropdown-toggle text-secondary">
                                            Genres
                                            <b class="caret"></b></a>
                                        <div class="scrollable-menu w-100 bg-dark text-white dropdown-menu">
                                            <div class="form-check">
                                                <c:forEach var="genre" items="${requestScope.genres}" varStatus="loop">
                                                    <input style="transform: scale(1.5);"
                                                           class="form-check-input" type="checkbox" value="${genre.id}"
                                                           id="flexCheckDefault" name="genre">
                                                    <label class="form-check-label" for="flexCheckDefault">
                                                            ${genre.name}
                                                    </label><br/>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>

                            <div class="col-12 mt-4">
                                <button type="submit" style="width: 163px;height: 50px;"
                                        class="btn btn-outline-success">
                                    Add
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    let uploader = $('#form__img-upload').get(0)
    uploader.onchange = evt => {
        const [file] = uploader.files
        if (file) {
            let img = $('#form__img').get(0)
            img.src = URL.createObjectURL(file)
        }
    }
</script>

</body>
</html>