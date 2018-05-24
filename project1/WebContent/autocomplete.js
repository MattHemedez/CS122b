// Initial Variables
var localDict = {};

var result = {
        suggestions: [
            { "value": "United Arab Emirates", "data": "AE" },
            { "value": "United Kingdom",       "data": "UK" },
            { "value": "United States",        "data": "US" }
        ]
    };
/*
 * This function is called by the library when it needs to lookup a query.
 * 
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated");
	
	console.log("looking up in local cache");
	// checking local cache of suggestions
	if(localDict.hasOwnProperty(escape(query)))
	{
		console.log("found suggestions in local cache");
		var result = { suggestions: localDict[escape(query)] };
		console.log(result);
		doneCallback(result);
	}
	// sending the HTTP GET request to the Java Servlet with the query data
	else
	{
		console.log("not in local cache");
		console.log("sending AJAX request to backend Java Servlet");
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "movie-suggestion?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	
	// result into a global variable
	localDict[escape(query)] = data;
	
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"])
	var url = suggestion["data"]["category"] + "-hero" + "?id=" + suggestion["data"]["heroID"]
	console.log(url)
}


// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete(
{
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set the groupby name in the response json data field
    groupBy: "title",
    // set delay time
    deferRequestBy: 300,
    // prevents queries starting with problematic first words to stop generating queries
    preventBadQueries: true,
    // set minimum characters needed before an autocomplete query gets sent
    minChars: 3,
    // set maximum number of results listed from local lookup
    lookupLimit: 10,
    // TODO: add other parameters, such as minimum characters
});



