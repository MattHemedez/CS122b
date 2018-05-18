// Handle getMetaData request result
function handleGenresResult(resultData) {
    console.log("handleGenresResult: populating genres table");

    // If checkout success, redirect to confirmation.html page
    if (resultData["status"] === "success") {
    	console.log("successfully got genres")
    	console.log(resultData["message"]);
        
        let genreTable = jQuery("#genre");
        var genreTableContent = "";
        genreTable.empty();
        genreTableContent += '<option value="">None</option>';
        
        for(var i=0; i<resultData.genres.length; ++i)
        {
        	genreTableContent += '<option value="' + resultData.genres[i]["name"] + '">' + resultData.genres[i]["name"] + '</option>';
        }    
        
        genreTable.append(genreTableContent);
    }
    // If adding a movie failed display error message
    else {
        console.log("failed retrieving genres");
        console.log(resultData["message"]);
    }
}


/**
 * Execute when the javascript file is done loading
 */
// Requests the Metadata from the Server
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "POST",	// Setting request method
    url: "api/genres", // Setting request url, which is mapped by GenresServlet
    success: (resultData) => handleGenresResult(resultData) // Setting callback function to handle data returned successfully by the DashboardServlet
});

