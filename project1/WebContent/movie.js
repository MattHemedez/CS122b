/**
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
    let movieInfoElement = jQuery("#movie_info");
    //movieInfoElement.append("<p>Error Message: " + resultData["errorMessage"] + "</p>");
    // append two html <p> created to the h3 body, which will refresh the page
    
    var movieTitle =resultData["movie_title"];
    movieInfoElement.append("<p>Movie Title: " + movieTitle + "</p>" +
        "<p>Movie Year: " + resultData["movie_year"] + "</p>" + 
        "<p>Movie Director: " + resultData["movie_director"] + "</p>" + 
        "<p>Movie rating: " + resultData["movie_rating"] + "</p>" +
        "<p>Number of User Votes: " + resultData["movie_num_votes"] + "</p>" + 
        
        "<form id='changeQuantity' name='changeQuantity' method='GET' action='api/cart'>"+
			"<input type='hidden' name='movieName' value=\""+movieTitle+"\"/>"+
			"<input type='hidden' name='movieId' value="+resultData["movie_id"]+"/>"+
			"<input type='hidden' name='increment' value='1'/>"+
			"<input type='submit' class='btn btn-sm btn-outline-secondary' value='Add to Cart'/>" +
    	"</form>");
    	
    console.log("handleResult: populating star table from resultData");
    // Populate the genre table
    // Find the empty table body by id "genre_table_body"
    let movieTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData["movie_stars"].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th><a href=\"star.html?id=" + resultData["movie_stars"][i]["star_id"] + "\">" + resultData["movie_stars"][i]["star_name"] + "</a></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
    
    console.log("handleResult: populating genre table from resultData");

    // Populate the genre table
    // Find the empty table body by id "genre_table_body"
    let genreTableBodyElement = jQuery("#genre_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData["movie_genres"].length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th><a href=\"SearchServlet?genre=" + resultData["movie_genres"][i]["genre_name"] + "\">" + resultData["movie_genres"][i]["genre_name"] + "</a></th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        genreTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by MovieServlet in Movie.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovieServlet
});