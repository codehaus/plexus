<%@ taglib prefix="ww" uri="webwork" %>
<%@ taglib uri="c" prefix="c" %>

<ww:bean name="'com.opensymphony.webwork.example.counter.SimpleCounter'" id="counter">
 <ww:param name="'foo'" value="'BAR'"/>

 Inside the Bean tag, the value of foo is : <ww:property value="foo"/><br />
</ww:bean>

Testing to see if the Bean's value is available outside of the ww:bean tag:<br />
foo is : <ww:property value="#counter.foo"/><br />

Testing to see if Bean's value is available outside of the ww:bean tag using JSTL:<br />
foo is : <c:out value="${counter.foo}"/><br />