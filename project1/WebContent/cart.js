/**
 * 
 *//**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // populate the movie info h3
    // find the empty h3 body by id "movie_info"

    let cartInfoElement = jQuery("#cart_info");
    cartInfoElement.append("<p> DOES THIS EVEN WORK? </p>");
    //movieInfoElement.append("<p>Error Message: " + resultData["errorMessage"] + "</p>");
    // append two html <p> created to the h3 body, which will refresh the page
    
//    for(let i=0; i<resultData.size(); ++i){
//    	cartInfoElement.append("<p> Movie Title: " + resultData[i]["title"] + "</p>");
//    }


  
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL

let customerId = getParameterByName('id');
// Makes the HTTP GET request and registers on success callback function handleResult
console.log("This is the customerId: " + customerId);

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method

    url: "api/cart", // Setting request url, which is mapped by MovieServlet in Movie.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});
