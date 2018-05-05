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
    	$("#checkout_panel").addClass('hidden');
    	$("#confirmation_panel").removeClass('hidden');
    	$("#cartitems_div").removeClass('hidden');
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

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"

    let cartInfoElement = jQuery("#cart_table");
//    <img class="card-img-top" src=<%=(!movieUrl.equals("N/A")?movieUrl:"imgs/error/not-found.png")%> >
    for(var i=0; i<resultData.length; ++i){
    	if(resultData[i]["moviePoster"] == "N/A")
    		resultData[i]["moviePoster"] = "imgs/error/not-found.png";
    	cartInfoElement.append("<tr class='cartItem'><td><img style='width:100px;height:140px;' src=" + resultData[i]["moviePoster"]+ ">" +
    			"<td>"+resultData[i]["title"]+"</td>" +
   				"<td><p>Bought: " + resultData[i]["quantity"] + "<p><p>Purchased and should ship in a few days.</p></td></tr>");
    }
    let checkout = jQuery("#checkout");

    checkout.append("<form action='customerinfo.html'>" +
    		"<input type='submit' class='btn btn-primary btn-lg' value='Checkout' />" +
    		"</form>");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */


jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/cart", // Setting request url, which is mapped by MovieServlet in Movie.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});
// Bind the submit action of the form to a handler function
jQuery("#customerinfo").submit((event) => submitCheckoutForm(event));

