/**
 * Handle the data returned by CheckoutServlet
 * @param resultDataString jsonObject
 */
function handleCheckoutResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle checkout response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If checkout success, redirect to confirmation.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirmation.html");
    }
    // If checkout fail, display error message on <div> with id "checkout_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        let checkoutElement = jQuery("#checkout_error_message");
        checkoutElement.append('<div class="alert alert-danger" role="alert"><b>Error: </b>' + resultDataJson["message"] + '</div>');
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitCheckoutForm(formSubmitEvent) {
    console.log("submit customer information form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/checkout",
        // Serialize the checkout form to the data sent by POST request
        jQuery("#customerinfo").serialize(),
        (resultDataString) => handleCheckoutResult(resultDataString));

}

// Bind the submit action of the form to a handler function
jQuery("#customerinfo").submit((event) => submitCheckoutForm(event));

