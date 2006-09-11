<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Resource</title>
  </head>

  <body>
    <p>
      Resource Modification Page

    </p>
    <ww:actionerror/>
    <ww:form action="saveResource" method="post">
      <ww:hidden name="resourceIdentifier"/>

      <ww:textfield label="identifier" name="identifier"/> <br/>

      <center><ww:submit/></center>
    </ww:form>

  </body>
</html>