$( document ).ready(function() {
    $("#emailSent").hide();
    $("#noEmailFound").hide();
    $("#passwordChangeSuccess").hide();
});

function emailSent() {
    $("#enterEmail").hide();
    $("#emailSent").show();
}

function emailNotFoundClose() {
    $("#noEmailFound").hide();
}

function checkEmail(email) {
    $.ajax({
        method: "POST",
        url: "r/accounts/forgotPassword",
        data: { email : email },
        success: function(result) {
            if (result == 'ok') {
                emailSent();
            } else {
                $("#noEmailFound").show();
            }
        },
        error: function() {
            $('#notifText').text('An error occurred');
            $("#noEmailFound").show();
        }
    });
}

function validatePassword(confirmPassword){
    alert(confirmPassword + "::" + $('#password_confirmation').val());
    console.log(confirmPassword + "::" + $('#password_confirmation').val());
    if(confirmPassword != $('#password_confirmation').val()) {
        confirmPassword.setCustomValidity("Passwords don't match");
    } else {
        confirmPassword.setCustomValidity('');
        $.ajax({
            method: "POST",
            url: "r/accounts/changePassword",
            data: { passwd : confirmPassword },
            success: function(result) {
                $("#passwordChangeSuccess").show();
            },
            error: function() {
                $('#notifText').text('An error occurred');
                $("#noEmailFound").show();
            }
        });
    }
}