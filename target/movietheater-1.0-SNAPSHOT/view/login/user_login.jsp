<%@ page contentType="text/html" pageEncoding="utf-8" %>
<%@ include file="/view/include/libs.jspf" %>
<%@ include file="/view/include/session_locale.jspf" %>

<!DOCTYPE html>
<html lang="${currentLocale}">
<fmt:message key="login.signin.title" var="value"/>
<%@ include file="/view/include/head.jspf" %>
<body>
<div class="nav-color d-flex flex-column flex-md-row align-items-center p-3 px-md-4 box-shadow">
    <%@ include file="/view/include/nav.jspf" %>
</div>
<section class="vh-100">
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6 text-black">
                <div class="d-flex align-items-center h-custom-2 px-5 ms-xl-4 mt-5 pt-5 pt-xl-0">
                    <form style="width: 23rem;" method="post" action="${pageContext.request.contextPath}/">
                        <h3 class="fw-normal mb-3 pb-3" style="letter-spacing: 1px;"><fmt:message
                                key="login.signin.title"/></h3>
                        <div class="form-outline mb-4">
                            <input type="hidden" name="pageCommand" value="login">
                            <c:if test="${sessionScope.successRegistration == true}">
                                <p class="error text-success">
                                    <fmt:message key="login.message.success.signup"/></p>
                            </c:if>
                            <label class="form-label" for="inputLoginForm"><fmt:message key="login.login.lbl"/></label>
                            <input type="text" id="inputLoginForm" class="form-control form-control-lg"
                                   placeholder="login"
                                   name="login" required/>
                        </div>
                        <div class="form-outline mb-4">
                            <label class="form-label" for="inputPasswordForm"><fmt:message
                                    key="login.password.lbl"/></label>
                            <input type="password" id="inputPasswordForm" class="form-control form-control-lg"
                                   placeholder="password"
                                   name="password" required/>
                        </div>
                        <c:if test="${sessionScope.signUpError != null}">
                            <div class="form-outline mb-4">
                            <span class="error text-danger">
                                <fmt:message key="login.errormassage"/>
                            </span>
                            </div>
                        </c:if>
                        <div class="pt-1 mb-4">
                            <button class="btn btn-login btn-lg btn-block btn-dark" type="submit"><fmt:message
                                    key="login.btn.value"/></button>
                        </div>
                        <p class="small mb-5 pb-lg-2"><a class="text-muted" href="#!"><fmt:message
                                key="login.forgotpassword.value"/></a></p>
                        <p><fmt:message key="login.signup.noaccount"/><a
                                href="${pageContext.request.contextPath}/view/login/user_register.jsp"
                                class="title-color ml-1"><fmt:message
                                key="login.signup.value"/></a></p>
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
