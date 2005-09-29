<%@ taglib uri="webwork" prefix="ww" %>
<%@ taglib uri="benchmark" prefix="benchmark" %>

<%-- this is to make our special controlfooter not close the row yet --%>
<ww:set name="extraCol" value="'true'" scope="webwork" />

<ww:form action="">

<tr>
    <th colspan="2" align="center">Tag</th>
    <th>Time</th>
</tr>

<benchmark:duration output="true">
<ww:label label="'label test'" name="'scalar'" value="scalar"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:label label="'label test'" name="'some random label (name) (compatibility)'"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:label label="'label test'" value="'some random label (value)'"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:textfield label="'textfield test'" name="'textfieldName'" value="scalar" size="50"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:textfield label="'required textfield test'" name="'textfieldName'" value="scalar" size="50" required="true"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:file label="'file test'" name="'uploadedFile'" size="'30'"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:password label="'password test'" name="'passwordField'" value="scalar" size="50"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:checkbox label="'checkbox test'" value="blah" name="'checkboxField1'" fieldValue="'foo'"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:checkboxlist label="'checkboxlist test'" name="'RadioField'" value="list[1].key" list="list" listKey="key" listValue="value"/>
</benchmark:duration> ms</td></tr>

<tr>
    <td align="right">Empty Component:</td><td>&nbsp;</td><td>
<benchmark:duration output="true">
<ww:component label="'component test'" name="'componentField'" template="'empty.vm'" />
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:radio label="'radio test'" name="'RadioField'" value="list[0].key" list="list" listKey="key" listValue="value"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:select label="'multiple select test'" name="'select1'" value="multiValues" list="multiList" multiple="'true'" size="'5'"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:select label="'select test'" name="'select2'" value="list[0].key" list="list" listKey="key" listValue="value" size="'5'"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:select label="'pulldown test'" name="'select3'" value="list[1].key" list="list" listKey="key" listValue="value"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:select label="'pulldown test (empty option)'" name="'select4'" value="list[2].key" list="list" listKey="key" listValue="value" />
</benchmark:duration> ms</td></tr>

<%--<benchmark:duration output="true">--%>
<%--<ww:doubleselect label="'double select test'" name="'dselect1'" doubleName="'dselect2'" value="list[1].key"--%>
<%--                 list="list" listKey="key" listValue="value" doubleList="children"/>--%>
<%--</benchmark:duration> ms</td></tr>--%>

<benchmark:duration output="true">
<ww:textarea rows="'10'" cols="'30'" label="'textarea test'" name="'textareaField'" value="scalar" />
</benchmark:duration> ms</td></tr>

<tr>
    <td align="right">Hidden:</td><td>&nbsp;</td><td>
<benchmark:duration output="true">
<ww:hidden name="'hiddenField'" value="scalar"/>
</benchmark:duration> ms</td></tr>

<benchmark:duration output="true">
<ww:submit value="'submit this thang!'" align="'right'"/>
</benchmark:duration> ms</td></tr>

</ww:form>