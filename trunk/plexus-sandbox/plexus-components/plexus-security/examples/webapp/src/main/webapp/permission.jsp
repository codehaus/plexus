<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Permission</title>
  </head>

  <body>
    <p>
      Permission Modification Page
    </p>
    <ww:actionerror/>
    <ww:form action="savePermission" method="post">


     <ww:textfield label="Name" name="name"/> <br/>
     <ww:textfield label="Description" name="description"/> <br/>


      <center><ww:submit/></center>
    </ww:form>

  </body>
</html>