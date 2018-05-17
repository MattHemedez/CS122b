function handleLoginResult(resultDataString) {
	console.log("Test Two");
    resultDataJson = JSON.parse(resultDataString);
    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login success, redirect to index.html page
    if (resultDataJson["status"] === "success" && resultDataJson["recaptcha"] === "success") {
        window.location.replace("_dashboard.html");
    }
    // If login fail, display error message on <div> with id "login_error_message"
    else {
    
        console.log("show error message");
        console.log(resultDataJson["message"]);
        jQuery("#dashboardlogin_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    console.log("Test 1");
    jQuery.post(
        "api/dashboard_login",
        // Serialize the login form to the data sent by POST request
        jQuery("#dashboardlogin_form").serialize(),
        (resultDataString) => handleLoginResult(resultDataString));
    console.log("Test Zilch");

}

// Bind the submit action of the form to a handler function
jQuery("#dashboardlogin_form").submit((event) => submitLoginForm(event));

