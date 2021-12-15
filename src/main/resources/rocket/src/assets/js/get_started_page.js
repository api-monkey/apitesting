const defaultMessage = 'Sorry we only support the REST API’s which have Swagger / Open APi definitions.';

$(document).ready(function () {

    $('#swagger-url-submit-button').on('click', function() {

        let urlValue = $('#swagger-url-id').val();
        let variantNumber = 4;

        // send event to google analytic
        if (typeof enterSwaggerUrlEvent === 'function') {
            enterSwaggerUrlEvent();
        }

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
                    variantNumber: variantNumber
                },
                success: function (data) {

                    if (data && data.success) {

                        window.location.href = '/run-tests?api=' + data.hashId;

                    } else {
                        loadingStop();
                        showInvalidFeedback(data.errorMessage);
                    }
                },
                error: function (jqXHR, exception) {
                    showInvalidFeedback();
                    console.log(jqXHR.status);
                    console.log(exception);
                    loadingStop();
                },
                complete: function() {
                },
                timeout: 60000
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
