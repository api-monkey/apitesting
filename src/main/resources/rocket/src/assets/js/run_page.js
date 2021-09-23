$(document).ready(function () {

    $('#swagger-url-submit-button').on('click', function() {

        let urlValue = $('#swagger-url-id').val();
        let invalidFeedback = $(this).parents('div').find('.invalid-feedback');

        if(!urlValue) {
            invalidFeedback.show();

        } else {

            invalidFeedback.hide();
            $.ajax({
                url: '/rest/parseSwaggerUrl',
                type: 'get',
                data: {
                    url: urlValue,
                    variantNumber: 1
                },
                success: function (data) {
                    console.log(data);
                },
                error: function (jqXHR, exception) {
                    console.log(jqXHR.status);
                    console.log(exception);
                },
            });


        }



    });
});