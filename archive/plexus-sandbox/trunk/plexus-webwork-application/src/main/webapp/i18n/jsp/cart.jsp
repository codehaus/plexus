<%@ taglib uri="webwork" prefix="webwork" %>

<webwork:push value="cart.items">
<webwork:if test="top != null">

	 <center>
 	 <table border="0" cellpadding="0" width="100%" bgcolor='<webwork:text name="'cart.bgcolor'"/>'>
	 <tr>
		<td><b><webwork:text name="'cd.albumLabel'"/></b></td>
		<td><b><webwork:text name="'cd.artistLabel'"/></b></td>
		<td><b><webwork:text name="'cd.countryLabel'"/></b></td>
		<td><b><webwork:text name="'cd.priceLabel'"/></b></td>
      <td><b><webwork:text name="'cd.quantityLabel'"/></b></td>
		<td></td>
	</tr>

<webwork:action name="'i18n.ComputePrice'" id="pricer"/>
<webwork:iterator>
	<tr>
      <webwork:push value="cd">
		<td><b><webwork:property value="album"/></b></td>
		<td><b><webwork:property value="artist"/></b></td>
		<td><b><webwork:property value="country"/></b></td>
		<td><b><webwork:text name="'price'" value0="#pricer.computePrice(price)"/></b></td>
	  </webwork:push>

		<td><b><webwork:property value="quantity"/></b></td>
		<td>
			<form action="i18n.Delete.action" method="post">
			  <input type=submit value='<webwork:text name="'cart.delLabel'"/>'>
			  <input type=hidden name="album" value='<webwork:property value="cd.album"/>'>
			</form>
		</td>
	</tr>
</webwork:iterator>

   </table>
	<p>
	<p>
	<form action="i18n.Checkout.action"  method="post">
        <input type="submit" value='<webwork:text name="'cart.checkoutLabel'"/>'>
   </form>
  </center>

</webwork:if>
</webwork:push>
