<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import = "java.util.*"%>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@page import="java.util.ArrayList" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Hello World - JSP tutorial</title>
</head>
<body>
    <%

    	ArrayList<String> test = (ArrayList<String>) request.getAttribute("movies");
    	HashMap<String, HashSet<String>> actors = (HashMap<String, HashSet<String>>) request.getAttribute("actors");
    	HashMap<String, HashSet<String>> genres = (HashMap<String, HashSet<String>>) request.getAttribute("genres");

    	
    	for(int i= 0; i<test.size(); ++i){
        	out.print("<h2> " + test.get(i) + " </h2>");
        	out.print("<p>" + actors.get(test.get(i)) + "</p>");
        	out.print("<p>" + genres.get(test.get(i)) + "</p>");

    	}
    	
    	
    	%>
</body>
</html>