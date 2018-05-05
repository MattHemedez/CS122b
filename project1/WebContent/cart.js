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
    	if(resultData[i]["moviePoster"] == "N/A")
    		resultData[i]["moviePoster"] = "imgs/error/not-found.png";
    	cartInfoElement.append("<tr class='cartItem'><td><img style='width:100px;height:140px;' src=" + resultData[i]["moviePoster"]+ ">" +
    			"<form id='changeQuantity' name='changeQuantity' method='GET' action='api/cart'>"+
					"<input type='submit' class='btn btn-sm btn-outline-secondary' name='deleteButton' style='width:40%;text-align:center;align:center;' value='delete' />"+
					"<input type='hidden' name='movieName' value="+resultData[i]["title"]+"/>"+
					"<input type='hidden' name='movieId' value="+resultData[i]["movieId"]+"/>"+
					"<input type='hidden' name='moviePoster' value="+resultData[i]["moviePoster"]+"/>"+
					"<input type='hidden' name='validInput' value='delete' />"+
				"</form></td>"+
    			
    			"<td>"+resultData[i]["title"]+"</td>" +
    			"<td>"+
    			"<form id='changeQuantity' name='changeQuantity' method='GET' action='api/cart'>"+
					"<input title='Quantity has to be >=0' id='amount' name='inputChange' style='width:7%;text-align:center;align:center;' " +
					"pattern='[0-9][0-9]{0,4}' value=" +resultData[i]["quantity"]+"><br/>"+
					
					"<input type='hidden' name='movieName' value="+resultData[i]["title"]+"/>"+
					"<input type='hidden' name='movieId' value="+resultData[i]["movieId"]+"/>"+
					"<input type='hidden' name='moviePoster' value="+resultData[i]["moviePoster"]+"/>"+
					"<input type='hidden' name='validInput' value='delete' />"+
    			"</form>"+
    			
    			"<form id='changeQuantity' name='changeQuantity' method='GET' action='api/cart'>"+
					"<input type='hidden' name='movieName' value="+resultData[i]["title"]+"/>"+
					"<input type='hidden' name='movieId' value="+resultData[i]["movieId"]+"/>"+
					"<input type='hidden' name='moviePoster' value="+resultData[i]["moviePoster"]+"/>"+
					"<input type='submit' name='decrement' class='btn btn-sm btn-outline-secondary' value='-' /input>"+ 
					"<input type='submit' name='increment' class='btn btn-sm btn-outline-secondary' value='+' /input>"+     
      
                "</form>"+
	                
	                
			
    			"</td>" +
    			"<td><p>Price: $17.99</p><p>2-day shipping</p><p>Pickup</p></td></tr>");
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

