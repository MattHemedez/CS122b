<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "java.util.*"%>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@page import="java.util.ArrayList" %>
<%@page import="java.net.URL" %>
<%@page import="com.google.gson.JsonObject" %>
<%@page import="com.google.gson.JsonParser" %>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="main.css">
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="movielist.css">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>FabFlix Movie Listings</title>
	</head>
	<body style="body {padding-top: 100px;} background-color:white" >
		<%
	    	ArrayList<String> movieList = (ArrayList<String>) request.getAttribute("movies");
	    	HashMap<String, HashSet<String>> actors = (HashMap<String, HashSet<String>>) request.getAttribute("actors");
	    	HashMap<String, HashSet<String>> genres = (HashMap<String, HashSet<String>>) request.getAttribute("genres");
	    	HashMap<String,String> movieID = (HashMap<String,String>) request.getAttribute("movieID");
	    	
	    	HashMap<String,String> movieRating = (HashMap<String,String>) request.getAttribute("movieRating");
	    	HashMap<String,String> movieDirector = (HashMap<String,String>) request.getAttribute("movieDirector");
	    	HashMap<String,String> movieYear = (HashMap<String,String>) request.getAttribute("movieYear");


	    	String query = (String) request.getAttribute("query");

	    	int pageNum = Integer.parseInt((String)request.getAttribute("pageNum"));
	    	int totalPages = (int)request.getAttribute("totalPages");
	    	String url = ((String) request.getAttribute("url")).replaceAll("&pagenum=[^&]*", "") + "&pagenum=";
	    	String baseUrl = (String) request.getAttribute("baseUrl");
	    	String firstLink = "";
	    	if(pageNum == 1)
	    		firstLink = "#";
	    	else 
	    		firstLink = url + "1";
	    	String lastLink = "";
	    	if(pageNum >= totalPages)
	    		lastLink = "#";
	    	else 
	    		lastLink = url + totalPages;
	    %>
		<nav class="navbar navbar-inverse navbar-fixed-top">
			<div class="container-fluid">
				<div class="row">
					<div class="col-md-3">
						<div class="navbar-header">
							<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-elements" aria-expanded="false">
								<span class="sr-only">Toggle navigation</span>
								<span class="icon-bar"></span>
							</button>
							<a class="navbar-brand navbar-left" href="index.html">Fabflix  <span class="glyphicon glyphicon-film"></span></a>
						</div>
					</div>
					<div class="col-md-8">
						<div class="collapse navbar-collapse" id=navbar-elements>
							<ul class="nav navbar-nav navbar-right">
								<li class="dropdown">
									<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Browse<span class="caret"></span></a>
									<ul class="dropdown-menu">
										<li class="dropdown-header">Browsing Options</li>
										<li role="separator" class="divider"></li>
										<li><a href="search.html">Browse by Movie Title</a></li>
										<li><a href="genrebrowse.html">Browse by Genre</a></li>
									</ul>
								<li><a href="search.html">Search</a></li>
								<li><a href="shoppingcart.html">Shopping Cart <span class="glyphicon glyphicon-shopping-cart"></span></a></li>
								<li>
									<form id="logout_form" method="post" action="#" >
										<button type="submit" type="submit" class="btn btn-primary navbar-btn">
											Sign Out <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
										</button>
									</form>
								</li>
							</ul> 
						</div>
					</div>
					<div class="col-md-1"></div>
				</div>
		  	</div>
		</nav>
		
		<nav aria-label="Upper Page navigation">
			<ul class="pagination">
				<li>
					<a href="<%=firstLink%>" aria-label="Previous">
						<span aria-hidden="true">&laquo;</span>
					</a>
				</li>
				<%
					int pageIndex = pageNum + -2;
					String link = "";
					String liClass = "";
					for(int i = -2; i < 3; ++i)
					{
						liClass = "";
						if(pageIndex <= 0)
							pageIndex = 1;
						if(pageIndex == pageNum)
						{
							liClass = " class='active'";
							link = "#";
						}
						else if(pageIndex <= totalPages)
							link = url + pageIndex;
						else
							liClass = " class='disabled'";
						
				%>
				
				
				<li<%=liClass%>><a href="<%=link%>"><%=pageIndex%></a></li>
				<%pageIndex += 1;}%>
				<li>
					<a href="<%=lastLink%>" aria-label="Next">
						<span aria-hidden="true">&raquo;</span>
					</a>	
				</li>
			</ul>
		</nav>
		
		
        <div class="album py-5 bg-light">
        	<div class="container">
     	    	<div class='row row-equal-height'>
     	    	
     	    	
     	    	
     	    	
        			
			<%
		    for(int i= 0; i<movieList.size(); ++i){
		    %>	
		    			
		    <% 
			    if(i%2 == 0 && i != 0){
		    		out.print("</div><div class='row row-equal-height'>");
		    	}
		    	String imdbID =movieID.get(movieList.get(i));
				String movieURL =  baseUrl + "movie.html?id=" + imdbID;
		    	out.print("<h4><a href='"+ movieURL+"'>" + movieList.get(i) + "</a> ("+movieYear.get(movieList.get(i))+ ")</h4>");  // <!-- DISPLAYS THE MOVIE NAME -->
				String movieGenres = "| ";
				for(String g: genres.get(movieList.get(i))){	
					movieGenres+=g+" | ";
				}
				out.print("<p style=\"font-size:10px\"><span>" + movieGenres+"</span><span id='ratings'>&#9733 "+movieRating.get(movieList.get(i)) + "</span></p>"); //<!-- DISPLAYS THE GENRES/RATINGS --> 
				
				String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam...";
				// Trying API Calls to OMDB CALL BY IMDB ID
	    		// http://www.omdbapi.com/?i=tt0094859&apikey=d4e302c3 EXAMPLE KEY
		    	String base = "http://www.omdbapi.com/?i=" + imdbID;
		    	String myKey = "&apikey=d4e302c3";
		    	String charset = "UTF-8";
		    	
		    	
		    	// Opening connection and getting response to parse. Grab the movieURL and plot only to use
		    	URLConnection connection = new URL(base+myKey).openConnection();
		    	InputStream apiResponse = connection.getInputStream();
		    	JsonParser jsonParser = new JsonParser();
		    	JsonObject jsonObject = (JsonObject)jsonParser.parse(new InputStreamReader(apiResponse, "UTF-8"));
		    	String movieUrl =  jsonObject.get("Poster").getAsString();
		    	String plot = jsonObject.get("Plot").getAsString();
		    	
		    	
		    	
			%>  
    	

	    	<div class="col-md-3">
	    				
             	<div class="card mb-4 box-shadow">
                	<img class="card-img-top" src=<%=(!movieUrl.equals("N/A")?movieUrl:"not-found.png")%> >   <!-- DISPLAYS THE MOVIE IMAGE HERE -->
                		<div class="card-body">
                  			<p class="card-text"><%="<br />" + (!plot.equals("N/A") ? plot:loremIpsum) %></p>  <!-- DISPLAYS THE MOVIE PLOT HERE -->
                  				<div class="d-flex justify-content-between align-items-center">
                   					<div class="btn-group">
                      					<button type="button" class="btn btn-sm btn-outline-secondary"><a href=<%=movieURL %>>View</a></button>  
                      					
										<form id="addItem" method="GET"  action="api/cart"> <!--  WHEN FINISHED ADD THIS TO MAKE SO IT DOESNT DISPLAY NEW PAGE onsubmit="return false" -->
											
											<input type="hidden" name="movieName" value="<%=movieList.get(i) %>"/>
											<input type="hidden" name="movieId" value="<%=imdbID%>"/>
											
	                      					<input type="submit" class="btn btn-sm btn-outline-secondary" value="Add to Cart" 
	                      					onclick="return confirm('Are you sure you would like to add <%=movieList.get(i)%> to cart?');"></input>  
										</form> 
										
                    				</div>
                    				<small class="text-muted pull-right"><%=imdbID %></small>
                    				
                    			</div>
                    	</div>
                 </div>
	    	</div>
	    	
	    		<div class="col-md-3" >
	    			<div class="card mb-4 box-shadow">
	    				<div class="card-body">
	        			<% 
	        			out.print("<h3 style='color:red'>FEATURING</h3>");
						for(String unparsed: actors.get(movieList.get(i))){
			    			StringTokenizer actorsST = new StringTokenizer(unparsed,":"); // starID:Name
		        			out.print("<p class='card-text'> <a href='"+ baseUrl+"star.html?id=" + actorsST.nextToken()+"'>" + actorsST.nextToken() + "</a></p>");   //<!-- DISPLAYS THE ACTORS HERE --> 
						}
	        			out.print("<h3 style='color:red'>DIRECTED BY</h3>");
	        			out.print("<p>"+ movieDirector.get(movieList.get(i))+"</p>");



	        			%> <!-- DISPLAYS THE ACTORS AND GENRES NAME -->
	        			</div>
	        		</div>	
                </div>
             
	 	<%
	 		}
	 	%>
	       		</div> 		
	        	
	       </div>	
	    </div>
	    
	    <nav aria-label="Upper Page navigation">
			<ul class="pagination">
				<li>
					<a href="<%=firstLink%>" aria-label="Previous">
						<span aria-hidden="true">&laquo;</span>
					</a>
				</li>
				<%
					pageIndex = pageNum + -2;
					link = "";
					liClass = "";
					for(int i = -2; i < 3; ++i)
					{
						liClass = "";
						if(pageIndex <= 0)
							pageIndex = 1;
						if(pageIndex == pageNum)
						{
							liClass = " class='active'";
							link = "#";
						}
						else if(pageIndex <= totalPages)
							link = url + pageIndex;
						else
							liClass = " class='disabled'";
				%>
				
				
				<li<%=liClass%>><a href="<%=link%>"><%=pageIndex%></a></li>
				<%pageIndex += 1;}%>
				<li>
					<a href="<%=lastLink%>" aria-label="Next">
						<span aria-hidden="true">&raquo;</span>
					</a>	
				</li>
			</ul>
		</nav>
		
	    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
		<script src="logout.js"></script>
	</body>
</html>