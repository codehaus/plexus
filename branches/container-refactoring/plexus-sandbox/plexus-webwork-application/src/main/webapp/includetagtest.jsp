<%@ taglib prefix="ww" uri="webwork" %>
<html>
<head><title>Include Tag Test</title></head>
<body>

<jsp:include page="included.jsp" />
<br>
<ww:include page="included.action"/>
<br>
<jsp:include page="included.jsp" />
<br>
<jsp:include page="included.action"/>
<br>
<jsp:include page="included.jsp" />

</body>
</html>