<html>

<head>
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" type="text/css" href="/css/main.css">
    <script src="/js/jquery-2.1.4.min.js"></script>
    <script src="/js/main.js"></script>
</head>

<body>

<div id="noEmailFound">
    <div class="flash-error">
        <div class="container" id="notifText">
            Can't find that email, sorry.
            <button class="flash-close" onclick="emailNotFoundClose()"></button>
        </div>
    </div>
</div>

<div role="main" class="main-content">

    <div class="auth-form"id="enterEmail">
        <form id="forgotPasswordForm" method="post">
            <div class="auth-form-header">
                <h1>Forgot password</h1>
            </div>
            <div class="auth-form-body">
                <label for="email_field">Email</label>
                <input autofocus="autofocus" class="input-block" id="email_field" name="email"
                       placeholder="Enter your email address" type="email">
                <input name="commit" type="submit" value="Submit" class="btn" onclick="checkEmail($('#email_field').val())">
            </div>
        </form>
    </div>

    <div id="emailSent" class="auth-form">
        <div class="auth-form-header">
            <h1>Email sent</h1>
        </div>
        <div  class="auth-form-body">
            <p>
                We’ve sent you an email containing a link that will allow you to reset your password for the next 1 hour.

                Please check your spam folder if the email doesn’t appear within a few minutes.
            </p>
        </div>
    </div>

</div>

<script>

</script>

</body>
</html>
