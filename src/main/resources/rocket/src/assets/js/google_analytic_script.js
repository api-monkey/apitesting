window.dataLayer = window.dataLayer || [];

function gtag() {
    dataLayer.push(arguments);
}

gtag('js', new Date());
gtag('config', 'UA-101965889-1');

function enterSwaggerUrlEvent() {
    gtag('event', 'enter_swagger_url', {'event_category': 'link_click', 'event_label': 'enter_swagger_url_button_click', 'page_name': '/get-started'});
}

function generateTestsEvent() {
    gtag('event', 'generate_tests', {'event_category': 'link_click', 'event_label': 'generate_tests_button_click', 'page_name': '/run-tests'});
}

function runTestsEvent() {
    gtag('event', 'run_tests', {'event_category': 'link_click', 'event_label': 'run_tests_button_click', 'page_name': '/run-tests'});
}