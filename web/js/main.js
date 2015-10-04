$( document ).ready(function() {
    $("#emailSent").hide();
    $("#flashError").hide();
    $("#passwordChangeSuccess").hide();
});

function emailSent() {
    $("#enterEmail").hide();
    $("#emailSent").show();
}

function checkEmail(email) {
    $.ajax({
        method: "POST",
        url: "r/accounts/forgotPassword",
        data: { userName : email },
        success: function(result) {
            if (result == 'ok') {
                emailSent();
            } else {
                $("#flashError").show();
            }
        },
        error: function() {
            $('#notifText').text('An error occurred. Please try again.');
            $("#flashError").show();
        }
    });
}

function validatePassword(confirmPassword, user){
    if($('#password').val() != confirmPassword.value) {
        confirmPassword.setCustomValidity("Passwords don't match");
    } else {
        confirmPassword.setCustomValidity("");
        $.ajax({
            method: "POST",
            url: "r/accounts/changePassword",
            data: { userName : user, password : confirmPassword.value },
            success: function(result) {
                $("#passwordChangeSuccess").show();
            },
            error: function() {
                $("#flashError").show();
            }
        });
    }
}