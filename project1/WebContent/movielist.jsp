<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "java.util.*"%>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@page import="java.util.ArrayList" %>

<html>
	<head>
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="main.css">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>FabFlix Movie Listings</title>
	</head>
	<body style="body {padding-top: 100px;}">
		<%
	    	ArrayList<String> movieList = (ArrayList<String>) request.getAttribute("movies");
	    	HashMap<String, HashSet<String>> actors = (HashMap<String, HashSet<String>>) request.getAttribute("actors");
	    	HashMap<String, HashSet<String>> genres = (HashMap<String, HashSet<String>>) request.getAttribute("genres");
	    	int pageNum = Integer.parseInt((String)request.getAttribute("pageNum"));
	    	int totalPages = (int)request.getAttribute("totalPages");
	    	String url = (String) request.getAttribute("url");
	    	if(!url.contains("&pagenum="))
	    		url += "&pagenum=";
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
							<a class="navbar-brand navbar-left" href="index.html">Fabflix   <span class="glyphicon glyphicon-film"></span></a>
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
										<li><a href="search.html">Browse by Genre</a></li>
									</ul>
								<li><a href="search.html">Search</a></li>
								<li><a href="search.html">Shopping Cart <span class="glyphicon glyphicon-shopping-cart"></span></a></li>
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
					<a href="<%=url.substring(0,url.indexOf("&pagenum=")) + "&pagenum=" +1%>" aria-label="Previous">
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
							link = url.substring(0,url.indexOf("&pagenum=")) + "&pagenum=" +pageIndex;
						else
							liClass = " class='disabled'";
				%>
				
				
				<li<%=liClass%>><a href="<%=link%>"><%=pageIndex%></a></li>
				<%pageIndex += 1;}%>
				<li>
					<a href="<%=url.substring(0,url.indexOf("&pagenum=")) + "&pagenum=" + totalPages%>" aria-label="Next">
						<span aria-hidden="true">&raquo;</span>
					</a>	
				</li>
			</ul>
		</nav>
		
		<div class="container-fluid">
		<%
	    	for(int i= 0; i<movieList.size(); ++i){
	    %>
	    		<div class = "row">
	    			<div class="col-sm-4" style="background-color:yellow;">
	    				<% out.print("<h4> " + movieList.get(i) + " </h4>"); %>  <!-- DISPLAYS THE MOVIE NAME -->
	    			</div>
	    			<div class="col-sm-8" style="background-colo:pink">
	        			<% out.println("<p>" + actors.get(movieList.get(i)) + "</p>");
	        			out.print("<p>" + genres.get(movieList.get(i)) + "</p>");%> <!-- DISPLAYS THE ACTORS AND GENRES NAME -->
	        			</div></div> <%}%>
	    </div>
	    
	    <nav aria-label="Bottom Page navigation">
			<ul class="pagination">
				<li>
					<a href="#" aria-label="Previous">
						<span aria-hidden="true">&laquo;</span>
					</a>
				</li>
				<li><a href="#">1</a></li>
				<li><a href="#">2</a></li>
				<li><a href="#">3</a></li>
				<li><a href="#">4</a></li>
				<li><a href="#">5</a></li>
				<li>
					<a href="#" aria-label="Next">
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