<%@ taglib uri="/webwork" prefix="ww" %>

<html>
<head>
  <title>Plexus Example Webapp Role</title>
</head>

<body>
<p>
  Role Modification Page

</p>
<ww:actionerror/>
<ww:form action="saveRole" method="post">

  <ww:textfield label="name" name="name"/> <br/>
  <ww:checkbox label="description" name="description"/> <br/>

  <p>
    <ww:submit/>
  </p>
</ww:form>

</body>
</html>