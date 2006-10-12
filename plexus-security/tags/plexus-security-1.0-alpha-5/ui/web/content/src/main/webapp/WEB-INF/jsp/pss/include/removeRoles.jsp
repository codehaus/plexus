<%--
  Created by IntelliJ IDEA.
  User: jesse
  Date: Sep 18, 2006
  Time: 1:50:21 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib uri="/webwork" prefix="ww" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <ww:form action="user" name="removeRole" method="post" namespace="/security/admin">
      <ww:hidden name="principal" value="${username}" />
      <ww:select name="roleName" list="assignedRoles" labelposition="top" />
      <ww:submit value="Remove" />
    </ww:form>