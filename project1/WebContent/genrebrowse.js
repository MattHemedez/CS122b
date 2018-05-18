// Handle getMetaData request result
function handleGenresResult(resultData) {
    console.log("handleGenresResult: populating genres table");

    // If checkout success, redirect to confirmation.html page
    if (resultData["status"] === "success") {
    	console.log("successfully got genres")
    	console.log(resultData["message"]);
        
        let genreTable = jQuery("#movieGenres");
        var genreTableContent = "";
        var genreImgUrlDict = {}
        
        genreImgUrlDict["Action"] = "imgs/genres/action.jpg";
        genreImgUrlDict["Adult"] = "imgs/genres/adult.png";
        genreImgUrlDict["Adventure"] = "imgs/genres/adventure.jpg";
        genreImgUrlDict["Animation"] = "imgs/genres/animation.jpg";
        genreImgUrlDict["Comedy"] = "imgs/genres/comedy.jpg";
        genreImgUrlDict["Crime"] = "imgs/genres/crime.jpg";
        genreImgUrlDict["Documentary"] = "imgs/genres/documentary.jpg";
        genreImgUrlDict["Drama"] = "imgs/genres/drama.jpg";
        genreImgUrlDict["Family"] = "imgs/genres/family.jpg";
        genreImgUrlDict["Fantasy"] = "imgs/genres/fantasy.jpg";
        genreImgUrlDict["Horror"] = "imgs/genres/horror.jpg";
        genreImgUrlDict["Music"] = "imgs/genres/music.jpg";
        genreImgUrlDict["Musical"] = "imgs/genres/musical.jpg";
        genreImgUrlDict["Mystery"] = "imgs/genres/mystery.jpg";
        genreImgUrlDict["Reality-TV"] = "imgs/genres/reality-tv.jpg";
        genreImgUrlDict["Romance"] = "imgs/genres/romance.jpg";
        genreImgUrlDict["Sci-Fi"] = "imgs/genres/sci-fi.jpg";
        genreImgUrlDict["Sport"] = "imgs/genres/sport.jpg";
        genreImgUrlDict["Thriller"] = "imgs/genres/thriller.jpg";
        genreImgUrlDict["War"] = "imgs/genres/war.jpg";
        genreImgUrlDict["Western"] = "imgs/genres/western.jpg";
        
        genreTable.empty();
        
        for(var i=0; i<resultData.genres.length; ++i)
        {
        	genreTableContent += '<option value="' + resultData.genres[i]["name"] + '">' + resultData.genres[i]["name"] + '</option>';
        	var genreImgUrl = '';
        	
        	if(!(resultData.genres[i]["name"] in genreImgUrlDict))
    		{
        		genreImgUrl = "imgs/error/not-found.png";
    		}
        	else
    		{
        		genreImgUrl = genreImgUrlDict[resultData.genres[i]["name"]];
    		}
        	genreTableContent += '<button type="submit" name="genre" value="' + resultData.genres[i]["name"] + '" style="background: url(' + genreImgUrl + ');background-size: cover;"><h2><span class="label label-default">' + resultData.genres[i]["name"] + '</span></h2></button>';
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

