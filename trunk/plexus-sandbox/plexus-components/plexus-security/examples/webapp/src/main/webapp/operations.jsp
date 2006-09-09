<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Operation</title>
  </head>

  <body>
    <p>
      Operations list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="opertaions" value="operation">
      <ww:url id="operationUrl" action="operation">
        <ww:param name="operationId" value="operation.id"/>
      </ww:url>

      <ww:a href="%{operationUrl}">${operation.name}</ww:a><br/>
      *
    </ww:iterator>

    <p>
      <ww:url id="newOperationUrl" action="operation"/>

      <ww:a href="%{newOperationUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>