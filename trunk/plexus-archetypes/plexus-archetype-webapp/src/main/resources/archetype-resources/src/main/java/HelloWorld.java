package $package;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.plexus.servlet.PlexusServlet;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class HelloWorld
    extends PlexusServlet
{
    public void doGet(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        if ( !hasComponent( Component.class.getName() ) )
             throw new ServletException("No component implementation available");

        Component component = (Component) lookup( Component.class.getName() );

        new OutputStreamWriter( res.getOutputStream() ).write( component.sayHello() );
    }
}
