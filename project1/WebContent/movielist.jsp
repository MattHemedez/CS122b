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
    	//ArrayList<String> f = new ArrayList<String>(request.getAttribute("title"));
    	//for(int i =0; i<4; ++i)
    		
    		
    	ArrayList<String> test = (ArrayList<String>) request.getAttribute("movies");
    	
    	for(int i= 0; i<test.size(); ++i){
        	out.print("<h2> " + test.get(i) + " </h2>");
    	}
    	
    	out.print("<h2> " + "TEST" + " </h2>");
    	%>
</body>
</html>