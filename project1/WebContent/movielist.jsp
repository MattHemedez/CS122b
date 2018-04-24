<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "java.util.*"%>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@page import="java.util.ArrayList" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FabFlix</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>
<body>
	
	
	<div class="container-fluid">
    <%
    	ArrayList<String> movieList = (ArrayList<String>) request.getAttribute("movies");
    	HashMap<String, HashSet<String>> actors = (HashMap<String, HashSet<String>>) request.getAttribute("actors");
    	HashMap<String, HashSet<String>> genres = (HashMap<String, HashSet<String>>) request.getAttribute("genres");

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
</body>
</html>