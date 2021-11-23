<%@ page contentType="text/html" pageEncoding="utf-8" %>
<%@ include file="/view/include/head/libs.jspf" %>
<%@ include file="/view/include/head/session_locale.jspf" %>

<!DOCTYPE html>
<html lang="${currentLocale}">
<fmt:message key="login.signin.title" var="value"/>
<%@ include file="/view/include/head/head.jspf" %>
<body>
<div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
    <h5 class="my-0 mr-md-auto font-weight-normal text-white">Cinema</h5>
    <a class="btn btn-outline-light mr-md-3" href="${pageContext.request.contextPath}">
        <fmt:message key="link.homepage"/>
    </a>
    <a class="btn btn-outline-light mr-md-3" href="${pageContext.request.contextPath}/view/login/user_login.jsp">
        <fmt:message key="link.login"/>
    </a>
    <a class="locale-btn btn btn-outline-light mr-md-2" href="?lang=en">En</a>
    <a class="locale-btn btn btn-outline-light" href="?lang=ru">Ru</a>
</div>
<section class="vh-100">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6 text-black">
                <div class="d-flex align-items-center h-custom-2 px-5 ms-xl-4 mt-5 pt-5 pt-xl-0 mt-xl-n5">
                    <form style="width: 23rem;" method="post" action="${pageContext.request.contextPath}">
                        <div class="form-outline mb-4">
                            <input type="hidden" name="command" value="register">
                            <h3 class="fw-normal mb-3 pb-3" style="letter-spacing: 1px;"><fmt:message
                                    key="login.signup.title"/></h3>
                            <label class="form-label" for="inputLoginSignUpForm"><fmt:message
                                    key="login.login.lbl"/></label>
                            <input type="text" id="inputLoginSignUpForm"
                                   class="form-control form-control-lg ${sessionScope.uniqueLoginValidationClass}"
                                   placeholder="login"
                                   pattern="^[a-zA-Z0-9_-]{3,16}$"
                                   name="login" required/>
                            <c:if test="${sessionScope.uniqueLoginValidationClass.equals('is-invalid')}">
                                <label class="form-not-unique-login-lbl text-danger"
                                       for="inputLoginSignUpForm"><fmt:message
                                        key="login.signin.notuniquelogin.lbl"/></label>
                            </c:if>
                        </div>
                        <div class="form-outline mb-4">
                            <label class="form-label" for="inputPasswordSignUpForm"><fmt:message
                                    key="login.password.lbl"/></label>
                            <input type="password" id="inputPasswordSignUpForm"
                                   class="form-control form-control-lg"
                                   placeholder="password"
                                   pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[*.!@\$%^&(){}:;<>,.?\/~_+-=|]).{8,32}$"
                                   name="password" required/>
                        </div>
                        <div class="form-outline mb-4">
                            <label class="form-label" for="inputEmailSignUpForm"><fmt:message
                                    key="login.email.lbl"/></label>
                            <input type="email" id="inputEmailSignUpForm"
                                   class="form-control form-control-lg ${sessionScope.uniqueEmailValidationClass}"
                                   placeholder="email@gmail.com"
                                   pattern="^\w+([\.-]?\w+)*@+(\w[\.-]?\w+)*(\.\w{2,3})+$"
                                   name="email" required/>
                            <c:if test="${sessionScope.uniqueEmailValidationClass.equals('is-invalid')}">
                                <label class="form-not-unique-email-label text-danger"
                                       for="inputLoginSignUpForm"><fmt:message
                                        key="login.signin.notunique.email.lbl"/></label>
                            </c:if>
                        </div>

                        <c:if test="${sessionScope.successRegistration == false}">
                            <div class="form-outline mb-4">
                            <span class="error text-danger">
                                <fmt:message key="login.singUp.errormassage"/>
                            </span>
                            </div>
                        </c:if>
                        <div class="pt-1 mb-4">
                            <button class="btn cinema-button btn-lg btn-block btn-dark" type="submit"><fmt:message
                                    key="signup.btn.value"/></button>
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
</body>
</html>
