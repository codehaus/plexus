package org.codehaus.plexus.httpserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * AbstractHttpServer is an abstract class that provides the
 * basic functionality of a mini-webserver. An
 * AbstractHttpServer must be extended
 * and the concrete subclass should define the <b>getResponse</b>
 * method which is responsible for handling the request<p>
 *
 * The HttpServer creates a thread that listens on a socket
 * and accepts  HTTP GET requests. The HTTP response contains the
 * byte for the resource that requested in the GET header. <p>
 */
public class DefaultHttpServer
    extends AbstractLogEnabled
    implements HttpServer, Runnable, Serviceable, Configurable
{
    private ServerSocket server;
    private String behaviourRole;
    private HttpBehaviour behaviour;
    private int port; //Lifecycle contract says that configure() will set this up correctly

    /** So it knows not to print exceptions about ServerSocket closing */
    private boolean stopping = false;
    private ServiceManager serviceManager;

    public DefaultHttpServer()
    {
    }

    /**
     * The "listen" thread that accepts a connection to the
     * server, parses the header to obtain the class file name
     * and sends back the bytecodes for the class (or error
     * if the class is not found or the response was malformed).
     */
    public void run()
    {
        final Socket socket;
        try
        {
            socket = server.accept();
        }
        catch (IOException e)
        {
            //if stopping == true, then the component is intentionally stopping
            //and an exception is expected
            if (stopping != true)
            {
                getLogger().warn("HttpServer died", e);
            }
            return;
        }

        // create a new thread to accept the next connection
        newListener();

        try
        {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                HttpRequest request = new HttpRequest(in);
                //request.dump(System.err);
                HttpResponse response = behaviour.getResponse(request);

                // send bytecodes in response (assumes HTTP/1.0 or later)
                try
                {
                    out.writeBytes("HTTP/1.0 " + response.getStatusCode() + " " + response.getStatusMessage() + "\r\n");
                    out.writeBytes("Content-Type: " + response.getContentType() + "\r\n");
                    if (response.getContentLength() != -1)
                    {
                        out.writeBytes("Content-Length: " + response.getContentLength() + "\r\n");
                    }
                    out.writeBytes("\r\n");

                    if (response.getInputStream() != null)
                    {
                        transferStream(response.getInputStream(), out);
                    }
                    out.flush();
                }
                catch (IOException e)
                {
                    getLogger().info("IOException processing request : " + request.getResource(), e);
                    return;
                }
            }
            catch (Exception e)
            {
                // write out error response
                out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
                out.writeBytes("Content-Type: text/html\r\n\r\n");
                out.flush();
            }

        }
        catch (IOException ex)
        {
            getLogger().warn("error writing response: " + ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    /**
     * Create a new thread to listen for http requests with.
     */
    private void newListener()
    {
        (new Thread(this)).start();
    }

    //inherit javadoc
    public void start() throws Exception
    {
        getLogger().info( "Starting." );

        stopping = false;
        server = new ServerSocket(port);
        behaviour = (HttpBehaviour) serviceManager.lookup(behaviourRole);
        newListener();

        getLogger().info( "Started." );
    }

    //inherit javadoc
    public void stop() throws Exception
    {
        getLogger().info( "Stopping." );

        stopping = true;
        server.close();

        getLogger().info( "Stopped." );
    }

    private void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }

    //inherit javadoc
    public void configure(Configuration conf) throws ConfigurationException
    {
        setPort(conf.getChild("http.port").getValueAsInteger(80));
        behaviourRole = conf.getChild("http.behaviour.role").getValue();
    }

    //inherit javadoc
    public void service(ServiceManager sm) throws ServiceException
    {
        this.serviceManager = sm;
    }

    /**
     * Transfers all remaining data in the input stream to the output stream
     * 
     * Neither stream will be closed at completion.
     * @param is the input stream
     * @param os the output stream
     */
    public static void transferStream(InputStream is, OutputStream os) throws IOException
    {
        final byte[] buffer = new byte[1024];
        while (true)
        {
            int bytesRead = is.read(buffer, 0, buffer.length);
            if (bytesRead == -1)
                break;
            os.write(buffer, 0, bytesRead);
        }
    }
}
