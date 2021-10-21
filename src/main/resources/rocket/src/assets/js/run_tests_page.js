const defaultMessage = 'Sorry we only support the REST API’s which have Swagger / Open APi definitions.';
let runDataMap = {};

$(document).ready(function () {

    let cases = window.cases ? window.cases : [];
    $.each(cases, function( index, item ) {
        runDataMap[item.dataId] = item;
    });

    addRunButtonEvents();

});

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

function loadingStart() {
    $('.preloader').show();
}

function loadingStop(delay) {
    setTimeout( function(){
        $('.preloader').hide();
    }, delay ? delay : 150);
}
