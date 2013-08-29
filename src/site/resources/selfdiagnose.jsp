<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.philemonworks.selfdiagnose.SelfDiagnoseServlet"%>
<%
		// remove anything already written on out
		out.clearBuffer();		
		new SelfDiagnoseServlet().run(request,response);
%>
		