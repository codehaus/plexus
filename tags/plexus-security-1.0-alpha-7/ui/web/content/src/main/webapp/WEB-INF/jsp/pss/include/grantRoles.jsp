<%--
  Created by IntelliJ IDEA.
  User: jesse
  Date: Sep 18, 2006
  Time: 1:48:21 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

    <ww:set name="availableRoles" value="${availableRoles}"/>
    <ww:form action="user" name="grantRole" method="post" namespace="/security/admin">
      <ww:hidden name="principal" value="${username}" />
      <ww:select name="roleName" list="availableRoles" labelposition="top" />
      <ww:submit value="Grant" />
    </ww:form>