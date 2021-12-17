$(document).ready(function () {
    $("#show_hide_password span").on('click', function (event) {
        event.preventDefault();
        let showPassInput = $('#show_hide_password input'),
            showHidePassI = $('#show_hide_password i');

        if (showPassInput && showPassInput.attr('type') == 'text') {
            showPassInput.attr('type', 'password');
            showHidePassI.addClass("fa-eye-slash");
            showHidePassI.removeClass("fa-eye");

        } else if (showPassInput && showPassInput.attr('type') == 'password') {
            showPassInput.attr('type', 'text');
            showHidePassI.removeClass("fa-eye-slash");
            showHidePassI.addClass("fa-eye");
        }
    });
});