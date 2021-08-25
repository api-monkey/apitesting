window.dataLayer = window.dataLayer || [];

function gtag() {
    dataLayer.push(arguments);
}

gtag('js', new Date());
gtag('config', 'UA-101965889-1');

function sendSampleReportEvent() {
    gtag('event', 'show_sample_report', {'event_category': 'link_click', 'event_label': 'sample_report_link_click', 'page_name': 'match-with-online-masters-graduate-programs-detailed-results'});
}
function sendCustomReportEvent() {
    gtag('event', 'show_custom_report', {'event_category': 'link_click', 'event_label': ' custom_report_click_when_zero_results', 'page_name': 'match-with-online-masters-graduate-programs-results'});
}