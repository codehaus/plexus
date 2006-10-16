<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>

<html>
<head>
   <title>
      <webwork:text name="'main.title'"/>
   </title>
</head>

<body bgcolor='<webwork:text name="'main.bgcolor'"/>'>
   <font face="Arial,Times New Roman,Times" size="+3">
      <webwork:text name="'main.title'"/>
   </font>
   <br>
   <em>
   <webwork:text name="'main.subhead'"/></em>
   <hr>
   <p>

   <center>
   <form name="shoppingForm" action="i18n.Add.action" method="POST">

   <webwork:text name="'cd.label'"/>:<webwork:include page="i18n.CDList.action"  />


   <ui:textfield label="getText('main.qtyLabel')" name="'quantity'" value="1" size="3"/>
   <input type=submit name=Submit value="<webwork:text name="'main.addLabel'"/>">
   </form>
   </center>
   <p>
   <webwork:include value="'cart.jsp'"  />
</body>

</html>

