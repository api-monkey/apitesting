const defaultMessage = 'The swagger page url is invalid. Pass the correct one please.';

$(document).ready(function () {

    $('#swagger-url-submit-button').on('click', function() {

        let urlValue = $('#swagger-url-id').val();

        if(!urlValue) {
            showInvalidFeedback();

        } else {

            hideInvalidFeedback();
            loadingStart();

            $.ajax({
                url: '/rest/parseSwaggerUrl',
                type: 'get',
                data: {
                    url: urlValue,
                    variantNumber: 1
                },
                success: function (data) {

                    if (data && data.success) {

                        let inputUrlForm = $('#input-url-form');
                        $('#swagger-url-submit-button').hide();
                        inputUrlForm.empty();
                        inputUrlForm.html('<p class="font-weight-bold font-size-20">'+ data.passedUrl +'</p>');

                        console.log(data.cases);
                        // do something with data

                    } else {
                        showInvalidFeedback(data.errorMessage);
                    }
                },
                error: function (jqXHR, exception) {
                    showInvalidFeedback();
                    console.log(jqXHR.status);
                    console.log(exception);
                },
                complete: function() {
                    loadingStop();
                },
                timeout: 20000
            });
        }
    });
});

function showInvalidFeedback(message) {
    let feedback = $('#swagger-invalid-feedback');
    feedback.html(message ? message : defaultMessage);
    feedback.show();
}

function hideInvalidFeedback() {
    let feedback = $('#swagger-invalid-feedback');
    feedback.html(defaultMessage);
    feedback.hide();
}

function loadingStart() {
    $('.preloader').show();
}

function loadingStop(delay) {
    setTimeout( function(){
        $('.preloader').hide();
    }, delay ? delay : 150);
}
