/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        alert("Successfully added item to shopping cart.");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert("Failed to add item to shopping cart.");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit addItem request");

    // Important: disable the default action of submitting the form
    formSubmitEvent.preventDefault();
    jQuery.post(
        "api/cart",
        // Serialize the login form to the data sent by GET request
        jQuery("#addItem").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));
}

// Bind the submit action of the form to a handler function
jQuery("#addItem").submit((event) => submitLoginForm(event));