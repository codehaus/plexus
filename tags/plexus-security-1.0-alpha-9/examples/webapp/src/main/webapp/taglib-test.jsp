<%@ taglib uri="/webwork" prefix="ww" %>
<%@ taglib uri="/plexusSecuritySystem" prefix="pss" %>
<html>
    <head>
        <title>Plexus Security Example Webapp</title>
    </head>
    <body>
      <p>
        jsp tag test page
      </p>
      <hr/>
      <p>
        test of the jsp tag pss:ifAuthorized 1:<br/>
        <br/>
        you should see an X right here -&gt;
        <pss:ifAuthorized permission="foo">
          X
        </pss:ifAuthorized>
      </p>
      <hr/>
      <p>
        test of the jsp tag pss:ifAuthorized 2:<br/>
        <br/>
        you should NOT see an X right here -&gt;
        <pss:ifAuthorized permission="bar">
          X
        </pss:ifAuthorized>
      </p>
      <hr/>
      <p>
        test of the jsp tag pss:ifAnyAuthorized 3:<br/>
        <br/>
        you should see an X right here -&gt;
        <pss:ifAnyAuthorized permissions="foo,bar">
          X
        </pss:ifAnyAuthorized>
      </p>
      <hr/>
      <p>
        test of the jsp tag pss:ifAnyAuthorized 4:<br/>
        <br/>
        you should NOT see an X right here -&gt;
        <pss:ifAnyAuthorized permissions="bar,dor">
          X
        </pss:ifAnyAuthorized>
      </p>
      <hr/>
    </body>
</html>