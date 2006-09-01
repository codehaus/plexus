<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="plexus-security-system" prefix="pss" %>
<html>
    <head>
        <title>Plexus Security Example Webapp</title>
    </head>
    <body>
      <p>
        status page
      </p>

      <p>
        test of the jsp tag 1:<br/>
        <br/>
        you should see and X right here ->

        <pss:ifAuthorized permission="foo">
          X
        </pss:ifAuthorized>
                

      </p>

    </body>
</html>