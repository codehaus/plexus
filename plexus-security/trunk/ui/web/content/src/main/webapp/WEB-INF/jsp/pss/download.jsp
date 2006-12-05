<%@page import="java.io.OutputStream"%>
<%@page import="org.codehaus.plexus.security.ui.web.reports.Report"%>
<%
   /* Hokey usage done in order to prevent introducing a Servlet that every webapp needs to maintain */
   
            Report report = (Report) request.getAttribute( "report" );
            if ( report == null )
            {
                response.sendRedirect( request.getContextPath() + "/security/userList!show.action" );
            }
            else
            {
                response.setContentType( report.getMimeType() );
                OutputStream outStream = response.getOutputStream();
                report.writeReport( outStream );
                outStream.flush();
                outStream.close();
            }
%>
