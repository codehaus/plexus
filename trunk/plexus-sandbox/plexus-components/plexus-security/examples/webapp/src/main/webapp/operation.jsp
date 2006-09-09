<%@ taglib uri="/webwork" prefix="ww" %>

<html>
<head>
  <title>Plexus Example Webapp Operation</title>
</head>

<body>
<p>
  Operation Modification Page

</p>
<ww:actionerror/>
<ww:form action="saveOperation" method="post">

  <ww:textfield label="name" name="name"/> <br/>
  <ww:textfield label="description" name="description"/> <br/>

  <ww:submit/>
</ww:form>

</body>
</html>