const defaultMessage = 'Sorry we only support the REST API’s which have Swagger / Open APi definitions.';
let runDataMap = {};

$(document).ready(function () {

    $('#swagger-url-submit-button').on('click', function() {

        let urlValue = $('#swagger-url-id').val();
        let variantNumber = 4;

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

                        let inputUrlForm = $('#input-url-form');
                        $('#swagger-url-submit-button').hide();
                        inputUrlForm.empty();
                        // inputUrlForm.html('<p class="font-weight-bold font-size-20">'+ data.passedUrl +'</p>');
                        inputUrlForm.html('<h1><span class="badge badge-default">' + data.passedUrl + '</span></h1>');

                        // do something with data
                        showDataBlockList(data, variantNumber);
                        addRunButtonEvents();

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

function showDataBlockList(data, variantNumber) {

    let cases = data.cases,
        wrapperCases = $('#wrapper-cases'),
        varNumber = variantNumber ? variantNumber : 1;

    $.each(cases, function( index, item ) {

        let bodyItems= '',
            runButton= '<button type="button" class="run-button btn btn-default width-220 m-2">Run</button>',
            bodyItemsArray = [];

        if(item.requestType == 'POST' && item.requestBodyVariants && item.requestBodyVariants.length > 0) {
            $.each(item.requestBodyVariants, function( bodyIndex, bodyItem ) {
                bodyItemsArray.push(bodyItem);
            });
        }

        if(item.requestType == 'GET' && item.requestParamsVariants && item.requestParamsVariants.length > 0) {
            $.each(item.requestParamsVariants, function( bodyIndex, bodyItem ) {
                if(bodyItem.parameterItems && bodyItem.parameterItems.length > 0) {
                    bodyItemsArray.push(bodyItem.parameterItems);
                }
            });
        }

        if(bodyItemsArray && bodyItemsArray.length > 0) {

            runDataMap[item.dataId] = item;

            bodyItems = '<div class="row row-grid font-weight-bolder m-0 p-0 w-100">';
            $.each(bodyItemsArray, function( bodyIndex, bodyItem ) {

                bodyItems +=
                    '<div class="p-0 col-12 col-md-' + (12/varNumber) + '">' +
                    '   <textarea type="text" class="run-case form-control fill-available mx-2" rows="5" name="content" data-test-case="' + item.dataId + ':' + bodyIndex + '">' + JSON.stringify(bodyItem, null, 4) + '</textarea>' +
                    '</div>';
            });
            bodyItems += '</div>';
        }


        let dataItem = '<div id=' + item.dataId + '>\n' +
            '    <div class="opblock-summary border border-default rounded mt-2">\n' +
            '        <div class="row row-grid align-items-center font-weight-bolder m-2">\n' +
            '            <div class="col-12 col-md-3">\n' +
            '                <h3><span class="badge ' + item.requestType.toLowerCase() + '-color">' + item.requestType + '</span></h3>\n' +
            '            </div>\n' +
            '            <div class="col-12 col-md-4">\n' +
            '                <span>' + item.methodName + '</span>\n' +
            '            </div>\n' +
            '            <div class="col-12 col-md-5">\n' +
            '                <span>' + item.summary + '</span>\n' +
            '            </div>\n' +
            '        </div>\n' +

                    bodyItems +
                    runButton +

            '    </div>\n' +
            '</div>';

        wrapperCases.append(dataItem);
    });

}

function addRunButtonEvents() {
    $('.run-button').on('click', function() {

        let runCases = $(this).parent('div').find('.run-case');
        runCases.each(function() {

            let runItem = $(this),
                keyParts = runItem.data("test-case").split(':'),
                caseDataId = keyParts[0],
                caseNumber = parseInt(keyParts[1]),
                dataToSend = runDataMap[caseDataId];

            if(dataToSend.requestType == 'POST' && dataToSend.requestBodyVariants && dataToSend.requestBodyVariants[caseNumber]) {
                dataToSend.requestBodyVariants[caseNumber] = JSON.parse(runItem.val());
            }

            if(dataToSend.requestType == 'GET' && dataToSend.requestParamsVariants && dataToSend.requestParamsVariants[caseNumber]) {
                // not implemented update params
            }

            let sendBody  = {
                dataId: caseDataId,
                number: caseNumber,
                dataCase: dataToSend
            };

            loadingStart();

            $.ajax({
                url: '/rest/runCase',
                type: 'post',
                beforeSend: function(xhr) {
                    xhr.setRequestHeader("Accept", "application/json");
                    xhr.setRequestHeader("Content-Type", "application/json");
                },
                dataType: 'json',
                data: JSON.stringify(sendBody),
                success: function (data) {

                    if (data) {
                        showResult(runItem, data);
                    } else {
                        runItem.addClass('bg-error');
                        runItem.val('');
                        runItem.val('error');
                    }
                },
                error: function (jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                },
                complete: function() {
                    loadingStop();
                },
                // timeout: 20000
            });

        });
    });
}

function showResult(runItem, response) {
    if (response.responseCode == '200') {
        runItem.addClass('bg-200');
    } else {
        runItem.addClass('bg-error');
    }
    runItem.val('');
    runItem.val(response.responseBody);
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
