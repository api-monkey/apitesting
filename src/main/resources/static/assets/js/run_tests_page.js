let runDataMap = {};

$(document).ready(function () {

    let cases = window.cases ? window.cases : [];
    $.each(cases, function( index, item ) {
        runDataMap[item.dataId] = item;
    });

    addGenerateButtonEvents();

    //adding retry logic
    let retryButtons = $('.retry-button');
    retryButtons.on('click', function() {

        let retryButton = $(this);
        let parentDiv = retryButton.parent('div'),
            runButton = parentDiv.find('.run-button'),
            headerText = parentDiv.find('.header-text'),
            headerParams = parentDiv.find('.header-params'),
            runCases = parentDiv.find('.run-case'),
            respText = parentDiv.find('.response-text');

        respText.fadeOut('fast');
        retryButton.hide();
        runButton.show();
        headerText.fadeIn('fast');
        headerParams.fadeIn('fast');

        runCases.each(function() {

            let runItem = $(this),
                keyParts = runItem.data("test-case").split(':'),
                caseDataId = keyParts[0],
                caseNumber = parseInt(keyParts[1]),
                savedData = runDataMap[caseDataId];

            runItem.removeClass('bg-200');
            runItem.removeClass('bg-error');
            runItem.attr("disabled", false);
            runItem.val('');

            if((savedData.requestType == 'POST' || savedData.requestType == 'PUT') && savedData.requestBodyVariants && savedData.requestBodyVariants[caseNumber]) {
                let jsonObj = savedData.requestBodyVariants[caseNumber],
                    jsonStr = JSON.stringify(jsonObj, null, 4);
                runItem.val(jsonStr);
            }

            if(savedData.requestType == 'GET' && savedData.requestParamsVariants && savedData.requestParamsVariants[caseNumber]) {
                let jsonObj = savedData.requestParamsVariants[caseNumber].parameterItems,
                    jsonStr = JSON.stringify(jsonObj, null, 4);
                runItem.val(jsonStr);
            }
        });
    });
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

            // headers params
            let headersParams = runItem.parents('.task-block').find('.header-params').find('.header-params-' + caseNumber).find('input');
            if(headersParams && headersParams.length > 0) {

                let headerParamsFromServer = dataToSend.inHeaderParameters[caseNumber].parameterItems;
                headersParams.each(function () {
                    let header = $(this),
                        name = header.attr("name"),
                        value = header.val();

                    if (name && name.length > 0) {
                        $.each(headerParamsFromServer, function (i, headerFromServer) {
                            if(headerFromServer.name === name) {
                                headerFromServer.value = value;
                            }
                        });
                    }
                });
            }

            if((dataToSend.requestType == 'POST' || dataToSend.requestType == 'PUT') && dataToSend.requestBodyVariants && dataToSend.requestBodyVariants[caseNumber]) {
                dataToSend.requestBodyVariants[caseNumber] = JSON.parse(runItem.val());
            }

            if(dataToSend.requestType == 'GET' && dataToSend.requestParamsVariants && dataToSend.requestParamsVariants[caseNumber]) {
                dataToSend.requestParamsVariants[caseNumber].parameterItems = JSON.parse(runItem.val());
            }

            let authHeaders = [];
            $('.auth-headers').each(function () {
                let authKey = $(this).find('input[name="auth-key"]').val(),
                    authVal = $(this).find('input[name="auth-value"]').val();
                if(authKey && authVal) {
                    authHeaders.push({
                        key: authKey,
                        value: authVal
                    });
                }
            });
            dataToSend.authHeaders = authHeaders;
            dataToSend.executeNumber = caseNumber;

            let sendBody  = {
                dataId: caseDataId,
                dataCase: dataToSend,
                swaggerDataHashId: window.swaggerDataHashId
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

                        //adding retry
                        let parentDiv = runButton.parent('div'),
                            retryButton = parentDiv.find('.retry-button');
                        retryButton.show();
                    }
                },
                timeout: 30000
            });

        });
    });
}

function addGenerateButtonEvents() {
    $('.generate-button').on('click', function() {

        loadingStart();
        let button = $(this);
        let parentDiv = button.parent('div'),
            runCases = parentDiv.find('.test-cases'),
            headerText = parentDiv.find('.header-text'),
            headerParams = parentDiv.find('.header-params');

        setTimeout(function(button) {

            button.remove();
            parentDiv.append('<button type="button" class="btn btn-tertiary width-220 m-2 run-button">Run tests</button>');
            runCases.fadeIn('fast');
            headerText.fadeIn('fast');
            headerParams.fadeIn('fast');

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
