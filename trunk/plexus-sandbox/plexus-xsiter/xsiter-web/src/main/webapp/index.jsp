<%@ taglib prefix="ww" uri="/webwork" %>
<html>
    <head>
        <title>Hello</title>
    </head>
    <body>
        <ww:form name="myForm" action="DisplayWorkspaces.action" method="POST">
            <ww:textfield label="First Name" name="person.firstName"/><br>
            <ww:textfield label="Last Name" name="person.lastName"/><br>
            <ww:submit value="Submit" />
        </ww:form>
    </body>
</html>
