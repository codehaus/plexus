<%@ taglib uri="webwork" prefix="webwork" %>

<webwork:action name="'i18n.ComputePrice'" id="pricer"/>

<select name="album">
   <webwork:iterator value="CDList">
   <option value="<webwork:property value="album"/>">
      <webwork:property value="album"/>,
      <webwork:property value="artist"/>, <webwork:property value="country"/>,
      <webwork:text name="'price'" value0="#pricer.computePrice(price)"/>
   </option>
   </webwork:iterator>
</select>
