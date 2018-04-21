/**
 * Handle the data returned by LogoutServlet
 * @param resultDataString jsonObject
 */
function handleLogoutResult(resultDataString) {
	try{
		resultDataJson = JSON.parse(resultDataString);
    }
    catch(err){
    	window.location.replace("login.html");
    }
    

    console.log("handle logout response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If logout success, redirect to index.html page
    if (resultDataJson["status"] === "success") {
        window.location.replace("login.html");
    }
    // If logout fail, display error message on <div> with id "logout_error_message"
    else {

        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert(resultDataJson["message"]);
        //jQuery("#logout_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLogoutForm(formSubmitEvent) {
    console.log("submit logout user request");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();
    try{
    	jQuery.post(
    	        "api/logout",
    	        // Serialize the logout form to the data sent by POST request
    	        jQuery("#logout_form").serialize(),
    	        (resultDataString) => handleLogoutResult(resultDataString));
    }
    catch(err){
    	window.location.replace("login.html");
    }
    

}

// Bind the submit action of the form to a handler function
try{
	jQuery("#logout_form").submit((event) => submitLogoutForm(event));
}
catch(err){
	window.location.replace("login.html");
}


