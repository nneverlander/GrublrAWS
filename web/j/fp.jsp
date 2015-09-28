<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title></title>
    <style>
        .auth-form-header {
            position: relative;
            padding: 10px 20px;
            margin: 0;
            font-size: 17px;
            color: #fff;
            text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.3);
            background-color: #829aa8;
            border: 1px solid #768995;
            border-radius: 3px 3px 0 0;
        }

        .auth-form-header h1 {
            margin-top: 0;
            margin-bottom: 0;
            font-size: 20px;
        }

        .auth-form-body {
            padding: 20px;
            font-size: 17px;
            background-color: #fff;
            border: 1px solid #d8dee2;
            border-top: 0;
            border-radius: 0 0 3px 3px;
        }

        button, html input[type="button"], input[type="reset"], input[type="submit"] {
            -webkit-appearance: button;
            cursor: pointer;
        }

        .btn {
            position: relative;
            display: inline-block;
            padding: 6px 12px;
            font-size: 17px;
            font-weight: bold;
            line-height: 20px;
            color: #333;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            background-color: #eee;
            background-image: -webkit-linear-gradient(#fcfcfc, #eee);
            background-image: linear-gradient(#fcfcfc, #eee);
            border: 1px solid #d5d5d5;
            border-radius: 3px;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
            -webkit-appearance: none;
        }

        .auth-form-body .input-block {
            margin-top: 5px;
            margin-bottom: 15px;
        }

        .form-control, input[type="text"], input[type="password"], input[type="email"], input[type="number"], input[type="tel"], input[type="url"], select, textarea {
            min-height: 40px;
            padding: 7px 8px;
            font-size: 17px;
            color: #333;
            vertical-align: middle;
            background-color: #fff;
            background-repeat: no-repeat;
            background-position: right 8px center;
            border: 1px solid #ccc;
            border-radius: 3px;
            outline: none;
            box-shadow: inset 0 1px 2px rgba(0,0,0,0.075);
        }

        .input-block {
            display: block;
            width: 100%;
        }

        input, select, textarea, button {
            font: 13px/1.4 Helvetica, arial, nimbussansl, liberationsans, freesans, clean, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol";
        }

        input {
            line-height: normal;
        }

        button, input, optgroup, select, textarea {
            color: inherit;
            font: inherit;
            margin: 0;
        }

        * {
            box-sizing: border-box;
        }

        .auth-form {
            width: 400px;
            margin: 60px auto;
        }

        .flash-full {
            margin-top: -1px;
            border-width: 1px 0;
            border-radius: 0;
        }
        .flash-error {
            color: #911;
            background-color: #fcdede;
            border-color: #d2b2b2;
        }
        .flash {
            position: relative;
            padding: 15px;
            font-size: 14px;
            line-height: 1.5;
            color: #246;
            background-color: #e2eef9;
            border: 1px solid #bac6d3;
            border-radius: 3px;
        }

        .container {
            width: 980px;
            margin-right: auto;
            margin-left: auto;
        }

        .container:before {
            display: table;
            content: "";
        }
        .container:after {
            display: table;
            clear: both;
            content: "";
        }

        .flash-close {
            float: right;
            width: 34px;
            height: 44px;
            margin: -11px;
            color: inherit;
            line-height: 40px;
            text-align: center;
            cursor: pointer;
            opacity: 0.6;
            background: none;
            border: 0;
            -webkit-appearance: none;
        }
        input, select, textarea, button {
            font: 13px/1.4 Helvetica, arial, nimbussansl, liberationsans, freesans, clean, sans-serif, "Segoe UI Emoji", "Segoe UI Symbol";
        }
        button, html input[type="button"], input[type="reset"], input[type="submit"] {
            -webkit-appearance: button;
            cursor: pointer;
        }
        button, select {
            text-transform: none;
        }
        button {
            overflow: visible;
        }
        button, input, optgroup, select, textarea {
            color: inherit;
            font: inherit;
            margin: 0;
        }
        * {
            box-sizing: border-box;
        }
        input, textarea, keygen, select, button {
            margin: 0em;
            font: normal normal normal normal 13.3333330154419px/normal Arial;
            text-rendering: auto;
            color: initial;
            letter-spacing: normal;
            word-spacing: normal;
            text-transform: none;
            text-indent: 0px;
            text-shadow: none;
            display: inline-block;
            text-align: start;
        }
        input, textarea, keygen, select, button, meter, progress {
            -webkit-writing-mode: horizontal-tb;
        }
        button {
            -webkit-appearance: button;
        }

        .octicon-remove-close:before, .octicon-x:before {
            content: '\f081';
        }

        .octicon, .mega-octicon {
            font: normal normal normal 16px/1 octicons;
            display: inline-block;
            text-decoration: none;
            text-rendering: auto;
            -webkit-font-smoothing: antialiased;
            -moz-osx-font-smoothing: grayscale;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }

    </style>
</head>
<body>

<div id="js-flash-container">
    <div class="flash flash-full flash-error">
        <div class="container">
            <button class="flash-close js-flash-close">
                <span class="octicon octicon-x"></span>
            </button>
            Can't find that email, sorry.
        </div>
    </div>
</div>

<div role="main" class="main-content">

    <div class="auth-form">
        <form id="forgotPasswordForm" novalidate="novalidate" method="post" action="/r/accounts/forgotPassword">
            <div class="auth-form-header">
                <h1>Forgot password</h1>
            </div>
            <div class="auth-form-body">
                <label for="email_field">Email</label>
                <input autofocus="autofocus" class="input-block" id="email_field" name="email"
                       placeholder="Enter your email address" type="text">
                <input name="commit" type="submit" value="Submit" class="btn">
            </div>
        </form>
    </div>

</div>

</body>
</html>