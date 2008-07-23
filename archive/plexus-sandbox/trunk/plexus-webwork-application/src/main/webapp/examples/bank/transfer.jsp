<%@ taglib uri="webwork" prefix="webwork" %>
<%@ taglib uri="webwork" prefix="ui" %>
<%@ taglib uri="webwork" prefix="iterator" %>
<link rel="stylesheet" type="text/css" href="../../template/standard/styles.css" title="Style">

<html>
<head>
<title>MyBank - Transfer</title>
</head>
<body>
   <table>
   <H2> Money Transfer </H2>
   <form action="<webwork:url value="bank.transfer.action"/>" method="post">

   <ui:textfield label="'Receiver'" name="'receiver'" maxlength="30"/>

   <ui:textfield label="'Account'" name="'account'"/>

   <ui:textfield label="'Amount'" name="'amount'"/>

   <iterator:generator val="'1,2,3,4,31'" separator="','" id="days"/>

   <ui:select label="'Date'" name="'date'" list="@days"/>

   <tr><td><input type=submit value="Transfer"/></td></tr>

    </form>
    </table>

    <P>
    This is a WebWork adaptation of the <a href="http://www.javaworld.com/javaworld/jw-12-2000/jw-1201-struts_p.html">Struts example</a> by Thor Kristmundsson.
</body>
</html>
