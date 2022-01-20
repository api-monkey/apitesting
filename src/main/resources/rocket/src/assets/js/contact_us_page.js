const offsetTopDef = 10000;

$(document).ready(function () {

    $('#send-message-submit').on('click', function (e) {

        //validate
        let offsetTop = offsetTopDef;
        $('div.required').each(function (index) {

            let textBlock = $(this).find("input[type='text'], textarea");
            if (textBlock && textBlock.length > 0) {
                let value = textBlock.val();
                offsetTop = Math.min(validateInput(this, value), offsetTop);
            }

            let emailBlock = $(this).find("input[type='email']");
            if (emailBlock && emailBlock.length > 0) {
                let value = emailBlock.val();
                offsetTop = Math.min(validateEmailInput(this, value), offsetTop);
            }
        });

        if (offsetTop !== 10000) {
            let scrollSize = offsetTop > 100 ? offsetTop - 100 : offsetTop;
            $('html, body').animate({
                scrollTop: scrollSize
            }, 400);
        } else {

            loadingStart();
            $('#email-warn-validate-message').hide();
            $.ajax({
                url: '/rest/contact-us-message',
                type: 'GET',
                data: {
                    firstName: $('#firstName').val(),
                    lastName: $('#lastName').val(),
                    email: $('#email').val(),
                    message: $('#message').val(),
                    phone: $('#phone').val()
                },
                success: function (data) {
                },
                error: function (jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                },
                complete: function() {
                    $('#send-message-submit').hide();
                    $('#message-sent-text').removeClass('d-none');
                    loadingStop();
                }
            });
        }
    });
});

function validateInput(element, value) {
    let invalidFeedback = $(element).find('.invalid-feedback');
    if (!value || value === 'None selected') {
        invalidFeedback.show();
        return invalidFeedback.offset().top
    } else {
        invalidFeedback.fadeOut('fast');
        return offsetTopDef;
    }
}

function validateEmailInput(element, value) {
    let invalidFeedback = $(element).find('.invalid-feedback');
    if (!value || value === 'None selected') {
        invalidFeedback.show();
        return invalidFeedback.offset().top
    } else {

        if (!validateEmail(value)) {
            invalidFeedback.show();
            return invalidFeedback.offset().top
        } else {
            invalidFeedback.fadeOut('fast');
            return offsetTopDef;
        }
    }
}

function validateEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
}

function loadingStart() {
    $('.preloader').show();
}

function loadingStop(delay) {
    setTimeout( function(){
        $('.preloader').hide();
    }, delay ? delay : 150);
}
