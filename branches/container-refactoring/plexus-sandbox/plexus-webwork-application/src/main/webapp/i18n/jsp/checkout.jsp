<%@ taglib uri="webwork" prefix="webwork" %>

<html>
<head>
<title>
   <webwork:text name="'checkout.title'"/>
</title>
</head>

<body bgcolor='<webwork:text name="'checkout.bgcolor'"/>'>
<font face="Times New Roman,Times" size="+3">
<webwork:text name="'checkout.title'"/>
</font>
<p> <hr>
<em><webwork:text name="'checkout.subhead'"/></em>
<p>
<center>
 	 <table border="0" cellpadding="0" width="100%" bgcolor='<webwork:text name="'cart.bgcolor'"/>'>
	 <tr>
		<td><b><webwork:text name="'cd.albumLabel'"/></b></td>
		<td><b><webwork:text name="'cd.artistLabel'"/></b></td>
		<td><b><webwork:text name="'cd.countryLabel'"/></b></td>
		<td><b><webwork:text name="'cd.priceLabel'"/></b></td>
      <td><b><webwork:text name="'cd.quantityLabel'"/></b></td>
	</tr>

<webwork:action name="'i18n.ComputePrice'" id="pricer"/>
<webwork:iterator value="cart.items">

		<tr>
         <webwork:push value="cd">
			   <td><b><webwork:property value="album"/></b></td>
			   <td><b><webwork:property value="artist"/></b></td>
			   <td><b><webwork:property value="country"/></b></td>
			   <td><b><webwork:text name="'price'"
               value0="#pricer.computePrice(price)"/></b></td>
         </webwork:push>

			<td><b><webwork:property value="quantity"/></b></td>
		</tr>
</webwork:iterator>

	<tr>
		<td>     </td>
		<td>     </td>
		<td><b><webwork:text name="'checkout.totalLabel'"/></b></td>
		<td><b><webwork:text name="'price'" value0="totalPrice"/></b></td>
		<td>     </td>
	</tr>

      </table>
<p>
<a href="i18n.Restart.action"><webwork:text name="'checkout.returnLabel'"/></a>
</center>

</body>
</html>
