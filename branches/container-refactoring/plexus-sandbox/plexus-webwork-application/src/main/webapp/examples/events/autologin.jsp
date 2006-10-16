<%@ taglib uri="webwork" prefix="webwork" %>

<webwork:include page="events.Login.action">
   <webwork:param name="'name" value="'name'"/>
   <webwork:param name="'password" value="'password'"/>
   <webwork:param name="'autoLogin'" value="true"/>
</webwork:include>
