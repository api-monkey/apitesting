const defaultMessage = 'Sorry we only support the REST API’s which have Swagger / Open APi definitions.';

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
                        // inputUrlForm.html('<p class="font-weight-bold font-size-20">'+ data.passedUrl +'</p>');
                        inputUrlForm.html('<h1><span class="badge badge-default">' + data.passedUrl + '</span></h1>');

                        console.log(data.cases);
                        // do something with data
                        showDataBlockList(data);

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

function showDataBlockList(data) {

    let cases = data.cases,
        wrapperCases = $('#wrapper-cases');

    $.each(cases, function( index, item ) {

        let dataItem = '<div id=' + item.dataId + '>\n' +
            '    <div class="opblock-summary">\n' +
            '        <div class="row row-grid align-items-center">\n' +
            '            <div class="col-12 col-lg-3">\n' +
            '                <h3><span class="badge ' + item.requestType.toLowerCase() + '-color">' + item.requestType + '</span></h3>\n' +
            '            </div>\n' +
            '            <div class="col-12 col-lg-4">\n' +
            '                <p>' + item.methodName + '</p>\n' +
            '            </div>\n' +
            '            <div class="col-12 col-lg-5">\n' +
            '                <p>' + item.summary + '</p>\n' +
            '            </div>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>';

        wrapperCases.append(dataItem);
    });

}

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
