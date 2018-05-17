// Handle Functions
// Handle the data returned by DashboardServlet after an addMovie request
function handleAddMovieResult(resultDataString) 
{
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle addMovie response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);
    
    let addMovieCount = jQuery("#addMovieCount");
    var addMovieCountNum = +(addMovieCount.text());
	addMovieCountNum += 1;
	addMovieCount.empty();
	addMovieCount.append(addMovieCountNum);
    
    // If checkout success, redirect to confirmation.html page
    if (resultDataJson["status"] === "success") {
    	console.log("successfully added movie")
    	console.log(resultDataJson["message"]);
        let statusBody = jQuery("#addMovie_status_message");
        statusBody.empty();
        statusBody.append('<div class="alert alert-success" role="alert"><b>Status Message: </b>' + resultDataJson["message"] + '</div>');
    }
    // If adding a movie failed display error message
    else {
        console.log("movie wasn't added\ndisplaying error message");
        console.log(resultDataJson["message"]);
        let statusBody = jQuery("#addMovie_status_message");
        statusBody.empty();
        statusBody.append('<div class="alert alert-danger" role="alert"><b>Error: </b>' + resultDataJson["message"] + '</div>');
    }
}

//Handle the data returned by DashboardServlet after an addMovie request
function handleAddStarResult(resultDataString) 
{
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle addStar response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    let addMovieCount = jQuery("#addStarCount");
    var addMovieCountNum = +(addMovieCount.text());
	addMovieCountNum += 1;
	addMovieCount.empty();
	addMovieCount.append(addMovieCountNum);
    
    // If checkout success, redirect to confirmation.html page
    if (resultDataJson["status"] === "success") {
    	console.log("successfully added star")
    	console.log(resultDataJson["message"]);
        let statusBody = jQuery("#addStar_status_message");
        statusBody.empty();
        statusBody.append('<div class="alert alert-success addStarAlert" role="alert"><b>Status Message: </b>' + resultDataJson["message"] + '</div>');
    }
    // If adding a star failed display error message
    else {
        console.log("star wasn't added\ndisplaying error message");
        console.log(resultDataJson["message"]);
        let statusBody = jQuery("#addStar_status_message");
        statusBody.empty();
        statusBody.append('<div class="alert alert-danger" role="alert"><b>Error: </b>' + resultDataJson["message"] + '</div>');
    }
}

// Handle getMetaData request result
function handleGetMetadataResult(resultData) {

    console.log("handleGetMetadataResult: populating metadata table");

    // If checkout success, redirect to confirmation.html page
    if (resultData["status"] === "success") {
    	console.log("successfully got metadata")
    	console.log(resultData["message"]);
        let statusBody = jQuery("#getMetaData_status_message");
        statusBody.empty();
        statusBody.append('<div class="alert alert-success addStarAlert" role="alert"><b>Status Message: </b>' + resultData["message"] + '</div>');
        
        let metadataTable = jQuery("#metadataTable");
        var metadataTableContent = "";
        
        for(var i=0; i<resultData.tables.length; ++i)
        {
        	metadataTableContent +="<div class='panel panel-info'>" +
    									"<div class='panel-heading'>" +
    										"<h4><b>Table Name: " + resultData.tables[i]["tableName"] + "</b></h4>" +
    									"</div>" +
    									"<div class='panel-body'>" +
    										"<table class='table table-striped table-hover'>" +
    											"<thead>" +
    												"<tr>" +
    												"<th>Column Name</th>" +
    												"<th>Column Data Type</th>" +
    												"<th>Is Nullable</th>" +
    												"</tr>" +
    											"</thead>" + 
    											"<tbody>";
        	
        	for(var j=0;j<resultData.tables[i]["columns"].length; ++j)
    		{
        		metadataTableContent +=				"<tr>" +
    													"<td>" + resultData.tables[i]["columns"][j]["columnName"] + "</td>" +
    													"<td>" + resultData.tables[i]["columns"][j]["columnType"] + "</td>" +
    													"<td>" + resultData.tables[i]["columns"][j]["isNullable"] + "</td>" +
    												"</tr>";
    		}
        	
        	metadataTableContent +=				"</tbody>"+
        									"</table>" +
    									"</div>" +
    								"</div>";
        }    
        metadataTable.append(metadataTableContent);
    }
    // If adding a movie failed display error message
    else {
        console.log("failed retrieving metadata");
        console.log(resultData["message"]);
        let statusBody = jQuery("#getMetaData_status_message");
        statusBody.empty();
        statusBody.append('<div class="alert alert-danger" role="alert"><b>Error: </b>' + resultData["message"] + '</div>');
    }
}

// Submit the addMovie form using the POST method
function submitAddMovieForm(formSubmitEvent) 
{
    console.log("submit add movie request");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/_dashboard",
        // Serialize the checkout form to the data sent by POST request
        jQuery("#addMovie").serialize(),
        (resultDataString) => handleAddMovieResult(resultDataString));
}

//Submit the addStar form using the POST method
function submitAddStarForm(formSubmitEvent) 
{
    console.log("submit add star request");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/_dashboard",
        // Serialize the checkout form to the data sent by POST request
        jQuery("#addStar").serialize(),
        (resultDataString) => handleAddStarResult(resultDataString));
}


/**
 * Execute when the javascript file is done loading
 */
// Requests the Metadata from the Server
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "POST",	// Setting request method
    url: "api/_dashboard", // Setting request url, which is mapped by DashboardServlet
    data: "Request=getMetadata",
    success: (resultData) => handleGetMetadataResult(resultData) // Setting callback function to handle data returned successfully by the DashboardServlet
});

// Bind the submit actions of the forms to a handler functions
jQuery("#addMovie").submit((event) => submitAddMovieForm(event));
jQuery("#addStar").submit((event) => submitAddStarForm(event));

