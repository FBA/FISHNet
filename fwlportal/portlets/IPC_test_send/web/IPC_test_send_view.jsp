<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ page import="javax.portlet.*"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:defineObjects />

<form action="<portlet:actionURL />" method="POST">
    <h2>Test string send</h2>
    String: <input type="text" name ="textValue" size="25"/><input type="submit" value="send"/>
    <h2>Test object send</h2>
    Firstname: <input type="text" name="firstname" size="25"/><br/>
    Surname: <input type="text" name="surname" size="25"/><br/>
    <input type="submit" value="send"/>
</form>