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

    let cartInfoElement = jQuery("#cart_table");
//    <img class="card-img-top" src=<%=(!movieUrl.equals("N/A")?movieUrl:"imgs/error/not-found.png")%> >
    for(var i=0; i<resultData.length; ++i){
    	cartInfoElement.append("<tr><td><img style='width:100px;height:140px;' src=" + resultData[i]["moviePoster"]+ "></td>" +
    			"<td>"+resultData[i]["title"]+"</td>" +
    			"<td>Qty<input id='amount' style='width:5%;text-align:center;' value=" +resultData[i]["quantity"]+">"+
    			
    			"<form id='changeQuantity' name='changeQuantity' method='GET' action='api/cart'>"+
					"<input type='hidden' name='movieName' value="+resultData[i]["title"]+"/>"+
					"<input type='hidden' name='movieId' value="+resultData[i]["movieId"]+"/>"+
					"<input type='hidden' name='moviePoster' value="+resultData[i]["moviePoster"]+"/>"+

					"<input type='submit' name='decrement' class='btn btn-sm btn-outline-secondary' value='-' /input>"+ 
					"<input type='submit' name='increment' class='btn btn-sm btn-outline-secondary' value='+' /input>"+
                
                
                "</form>"+
	                
	                
			
    			"</td>" +
    			"<td><p>Price: $17.99</p><p>2-day shipping</p><p>Pickup</p></td></tr>");
//    	cartInfoElement.append("<p>" + resultData[i]["title"]+  ":" + resultData[i]["quantity"] + "</p>");
    }
    //movieInfoElement.append("<p>Error Message: " + resultData["errorMessage"] + "</p>");
    // append two html <p> created to the h3 body, which will refresh the page
    
//    for(let i=0; i<resultData.size(); ++i){
//    	cartInfoElement.append("<p> Movie Title: " + resultData[i]["title"] + "</p>");
//    }


  
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


//function submitQuantity(formSubmitEvent) {
//    console.log("submit change quanity form");
////    window.location.reload(true);
//
//    // Important: disable the default action of submitting the form
//    //   which will cause the page to refresh
//    //   see jQuery reference for details: https://api.jquery.com/submit/
//    formSubmitEvent.preventDefault(formSubmitEvent);
//    var movieName= document.getElementById('movieName').value;
//    var movieId= document.getElementById('movieId').value;
//    var moviePoster= document.getElementById('moviePoster').value;
////    var form = $('changeQuantity');
////    console.log("Movie: " + movieName + " MovieId: " + movieId + "moviePoster: " + moviePoster);
//    jQuery.ajax({
//    	data: {"movieName":movieName, "movieId":movieId, "moviePoster":moviePoster},
//        dataType: "json",  // Setting return data type
//        method: "GET",// Setting request method
//        url: "api/cart", // Setting request url, which is mapped by MovieServlet in Movie.java
//        success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
//    });
//
//}
//
////// Bind the submit action of the form to a handler function
//jQuery("#changeQuantity").submit((event) => submitQuantity(event));