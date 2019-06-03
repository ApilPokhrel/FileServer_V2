

$(document).ready(function () {

});

$(document).on("submit", "#login_form", function(event) {
    event.preventDefault();
    login("login_form");
});

$(document).on("submit", "#register_form", function(event) {
    event.preventDefault();
    register("register_form");
});

$(document).on("submit", "#verify_form", function(event) {
    event.preventDefault();
    verification("verify_form");
});


$(document).on("submit", "#delete_account_form", function(event) {
    event.preventDefault();
    verification("delete_account_form");
});

$(document).on("submit", "#input_email", function(event) {
    event.preventDefault();
    inputEmail("input_email");
});

$(document).on("submit", "#forget_pass_form", function(event) {
    event.preventDefault();
    forgetPass("forget_pass_form");
});

$(document).on("click","#send_code", function(event) {
    event.preventDefault();
    sendCodeAgain();
});

console.log("cookie is ", $.cookie("access_token"));

function login(form) {

    $.ajax({
        url: `/api/v1/auth/login`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data: core.form.get(`#${form}`)
    })
        .done(d => {
                console.log(d);
            if(d.tokens) {
                $.cookie("access_token", d.tokens[d.tokens.length - 1].token, {expires: 1});
            }

            if(d.verified){
                window.location.href = "/";
            } else{
                window.location.href = "/verification";
            }
        })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON

            console.log(error);
            swal({
                title: "Login failure!",
                text: error,
                icon: "error",

            });
        });
}



function register(form) {

    $.ajax({
        url: `/api/v1/auth/register`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data: core.form.get(`#${form}`)
    })
        .done(d => {
            if(d){
                // document.cookie = `access_token=${d.tokens[d.tokens.length - 1].token}; expires=Thu, 18 Dec 2020 12:00:00 UTC; path=/`;
                if(d[0].tokens) {
                    $.cookie("access_token", d[0].tokens[d[0].tokens.length - 1].token, {expires: 1});
                }

                if(d[1]) {
                    $.cookie("verify_code", d[1].toString(), {expires: 1});
                }


                window.location.href = "/verification";
            }
        })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON

            swal({
                title: "Register failure!",
                text: error,
                icon: "error",

            });
        });
}





function verification(form) {


    $.ajax({
        url: `/api/v1/auth/verification`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data: {
            code: $("#code").val(),
            ecode: $.cookie("verify_code")
        }
    })
        .done(d => {

            $.cookie("verify_code", null, {path: '/'});
            window.location.href = "/";
        })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON

            console.log(error);
            swal({
                title: "Verification failure!",
                text: error,
                icon: "error",

            });
        });
}






    function logout(form) {

        $.ajax({
            url: `/api/v1/auth/logout`,
            headers: { access_token: $.cookie("access_token") },
            method: "delete",
            data: {

            }
        })
            .done(d => {
                // $.cookie("access_token",'', {expires: 1});
                window.location.href = "/login";
            })
            .fail(e => {
                let error;
                if(e.responseText) error = e.responseText
                if(e.responseJSON) error = e.responseJSON
                swal({
                    title: "Logout Failed!",
                    text: error,
                    icon: "error",

                });
            });
}



function inputEmail(form) {

    $.ajax({
        url: `/api/v1/auth/email`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data: {
            email: $('#input_email input[name=email]').val()
        }
    })
        .done(d => {
            $.cookie("forget_pass",true, {expires: 1});
            if(d[1]) {
                console.log("inside log");
                             console.log("token is ", d[1].token)
                $.cookie("access_token", d[1].token, {expires: 1});
            }

            if(d[2]) {
                $.cookie("verify_code", d[2].toString(), {expires: 1});
            }
            window.location.href = "/forgetPass";
        })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON
            swal({
                title: "Email Input Failed!",
                text: error,
                icon: "error",

            });
        });
}




function setNewPass(form) {
    $.ajax({
        url: `/api/v1/auth/setNewPass`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data: core.form.get(`#${form}`)
    })
        .done(d => {
                console.log(d);
                })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON
            swal({
                title: "Set New Password Failed!",
                text: error,
                icon: "error",
            });
        });
}


function forgetPass(form) {
    $.ajax({
        url: `/api/v1/auth/forgetPass`,
        headers: { access_token: $.cookie("access_token") },
        method: "post",
        data: {
            code: $("#code").val(),
            ecode: $.cookie("verify_code"),
            email: $("#email").val(),
            newPassword: $("#newPassword").val()
        }
    })
        .done(d => {
            window.location.href = "/";
        })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON
            swal({
                title: "Forget Password Failed!",
                text: error,
                icon: "error",
            });
        });
}



function deleteAccount() {

    if( $.cookie("forget_pass")){
        swal({
            title: "Account Delete failure!",
            text: "Sorry Cannot Delete User",
            icon: "error",
        });
    }

    $.ajax({
        url: `/api/v1/auth/deleteAccount`,
        headers: {access_token: $.cookie("access_token")},
        method: "post",
        data: {
            id: $('#user_id').val()
        }
    })
        .done(d => {
            console.log(d);
            // window.location.href = "/";
        })
        .fail(e => {
            let error;
            if (e.responseText) error = e.responseText
            if (e.responseJSON) error = e.responseJSON

            console.log(error);
            swal({
                title: "Account Delete failure!",
                text: error,
                icon: "error",

            });
        });
}

function sendCodeAgain() {
    console.log('inside send code')
    $.ajax({
        url: `/api/v1/auth/sendCodeAgain/${$('#user_email').val()}`,
        headers: { access_token: $.cookie("access_token") },
        method: "get",
    })
        .done(d => {
            console.log(d);
            if(d[1]){
                $.cookie("verify_code", d[1].toString(), {expires: 1});
            }
            window.location.href = "?";
        })
        .fail(e => {
            let error;
            if(e.responseText) error = e.responseText
            if(e.responseJSON) error = e.responseJSON
            swal({
                title: "Set Code Failed!",
                text: error,
                icon: "error",
            });
        });
}


$(document).on("click", "#send_code", function () {
    sendCodeAgain();
});

$(document).on("click", "#delete_account", function () {
    deleteAccount();
});




