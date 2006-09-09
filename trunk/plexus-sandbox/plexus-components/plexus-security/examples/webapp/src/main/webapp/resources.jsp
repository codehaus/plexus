<%@ taglib uri="/webwork" prefix="ww" %>

<html>
  <head>
    <title>Plexus Example Webapp Resource</title>
  </head>

  <body>
    <p>
      Resources list page
    </p>
    <ww:actionerror/>

    <ww:iterator id="resources" value="resource">
      <ww:url id="resourceUrl" action="resource">
        <ww:param name="resourceId" value="resource.id"/>
      </ww:url>

      <ww:a href="%{resourceUrl}">${resource.identifier}</ww:a><br/>
      *
    </ww:iterator>

    <p>
      <ww:url id="newResourceUrl" action="resource"/>

      <ww:a href="%{newResourceUrl}">new</ww:a><br/>
    </p>
    

  </body>
</html>