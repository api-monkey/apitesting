let runDataMap = {};

$(document).ready(function () {

    let cases = window.cases ? window.cases : [];
    $.each(cases, function( index, item ) {
        runDataMap[item.dataId] = item;
    });

    addGenerateButtonEvents();

});

function addRunButtonEvents(runButton) {

    runButton.on('click', function() {

        let count = 0;
        loadingStart();
        let parentDiv = $(this).parent('div'),
            runCases = parentDiv.find('.run-case'),
            respText = parentDiv.find('.response-text');
        respText.fadeIn('fast');

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

            count++;

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
                    if(--count <= 0) {
                        loadingStop();
                        runButton.hide();
                    }
                },
                // timeout: 20000
            });

        });
    });
}

function addGenerateButtonEvents() {
    $('.generate-button').on('click', function() {

        loadingStart();
        let button = $(this);

        setTimeout(function(button) {
            let parentDiv = button.parent('div'),
                runCases = parentDiv.find('.test-cases');

            button.remove();
            parentDiv.append('<button type="button" class="btn btn-tertiary width-220 m-2 run-button">Run tests</button>');
            runCases.fadeIn('fast');

            let runButton = parentDiv.find('.run-button');

            addRunButtonEvents(runButton);

            loadingStop();

        }, 1000, button);
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
    runItem.attr("disabled", true);
}

function loadingStart() {
    $('.preloader').show();
}

function loadingStop(delay) {
    setTimeout( function(){
        $('.preloader').hide();
    }, delay ? delay : 150);
}
