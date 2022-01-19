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
                }
            });
        }
    });


    $('#match-me-with-online-detailed-back').on('click', function (e) {
        window.history.back();
    });

    let reportNeedRadio = $('#reportNeed');
    let whatExactlyLookingForText = $('#whatExactlyLookingForText');
    let appreciateResponse = $('#appreciateResponse');
    let noAnswerSubmitButton = $('#no-answer-submit-button');
    noAnswerSubmitButton.hide();
    whatExactlyLookingForText.hide();
    appreciateResponse.hide();

    reportNeedRadio.change(function(){
        let reportNeedVal = reportNeedRadio.find("input[type='radio']:checked").val();
        if (reportNeedVal && reportNeedVal === 'yes') {

            whatExactlyLookingForText.removeClass("required");
            appreciateResponse.show();
            whatExactlyLookingForText.hide();
            noAnswerSubmitButton.hide();

        } else {
            whatExactlyLookingForText.show();
            appreciateResponse.hide();
            whatExactlyLookingForText.addClass("required");
            noAnswerSubmitButton.show();
        }
    });

    noAnswerSubmitButton.on('click', function (e) {

        let offsetTop = offsetTopDef;
        $('div.required').each(function (index) {

            let textBlock = $(this).find("input[type='text'], textarea");
            if (textBlock && textBlock.length > 0) {
                let value = textBlock.val();
                offsetTop = Math.min(validateInput(this, value), offsetTop);
            }
        });

        if (offsetTop !== 10000) {
            let scrollSize = offsetTop > 100 ? offsetTop - 100 : offsetTop;
            $('html, body').animate({
                scrollTop: scrollSize
            }, 400);
        } else {

            let userDataId = $('#user-data-id').val(),
                whatExactlyLookingForData = whatExactlyLookingForText.find("input[type='text'], textarea").val();
            let orderDataStr = JSON.stringify({'userDataId': userDataId, 'whatExactlyLookingForData': whatExactlyLookingForData});

            $.ajax({
                url: '/content/scholarship/updateMatchMeOnlineData',
                type: 'post',
                data: orderDataStr,
                beforeSend: function(xhr) {
                    xhr.setRequestHeader("Accept", "application/json");
                    xhr.setRequestHeader("Content-Type", "application/json");
                },
                success: function (data) {
                    // console.log('Order sent');
                },
                error: function (jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                },
            });

            whatExactlyLookingForText.empty();
            $( this ).html('');
            $( this ).html('<p class="h3 font-weight-normal text-muted text-center mb-4">Thank you for answer!</p>');
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
