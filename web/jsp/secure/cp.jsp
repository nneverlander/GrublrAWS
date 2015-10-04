<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <script src="/js/jquery-2.1.4.min.js"></script>
    <script src="/js/main.js"></script>
</head>

<body>

<div id="flashError">
    <div class="flash-error">
        <div class="container" id="notifText">
            An error occured. Please try again.
            <button class="flash-close" onclick="$('#flashError').hide()"></button>
        </div>
    </div>
</div>

<div role="main" class="main-content">

    <div class="auth-form">
        <form id="enterPasswordForm">
            <div class="auth-form-header">
                <h1>Enter new password for ${requestScope.user}</h1>
            </div>
            <div class="auth-form-body">
                <p class="note">Password must at least 8 characters long.</p>
                <label>Password</label>
                <input autofocus="autofocus" class="input-block change-password" id="password" name="password" tabindex="1"
                       pattern=".{8,}" type="password" required>

                <label>Confirm password</label>
                <input class="input-block change-password" id="password_confirmation" name="password_confirmation" tabindex="2"
                        type="password" required>

                <input class="btn" name="commit" tabindex="3" type="submit" value="Change password"
                       onclick="validatePassword(document.getElementById('password_confirmation'), ${requestScope.user})">
            </div>
        </form>
    </div>

    <div id="passwordChangeSuccess" class="auth-form">
        <div class="auth-form-header">
            <h1>Success</h1>
        </div>
        <div  class="auth-form-body">
            <p>
                Password changed successfully.
            </p>
        </div>
    </div>

</div>

</body>

</html>
