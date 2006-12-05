<%@page import="java.io.OutputStream"%>
<%@page import="org.codehaus.plexus.security.ui.web.reports.Report"%>
<% 
   Report report = (Report) pageContext.getAttribute("report");
   response.setContentType( report.getMimeType() ); 
   OutputStream outStream = response.getOutputStream();
   report.writeReport( outStream );
   outStream.flush();
   outStream.close();
%>