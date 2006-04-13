<%@ taglib prefix="ww" uri="webwork" %>

<ww:action name="'VelocityCounter'" id="vc">
 <ww:param name="'foo'" value="'BAR'"/>
</ww:action>
<ww:push value="#vc">
 counter.count == <ww:property value="counter.count" />
 foo is also: <ww:property value="foo"/>
</ww:push>

