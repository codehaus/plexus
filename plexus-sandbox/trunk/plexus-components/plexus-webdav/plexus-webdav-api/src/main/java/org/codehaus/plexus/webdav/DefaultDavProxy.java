package org.codehaus.plexus.webdav;

import it.could.webdav.DAVListener;
import it.could.webdav.DAVProcessor;
import it.could.webdav.DAVRepository;
import it.could.webdav.DAVResource;
import it.could.webdav.DAVTransaction;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @plexus.component
 */
public class DefaultDavProxy
    extends AbstractLogEnabled
    implements DavProxy
{
    // -----------------------------------------------------------------------
    // DavProxy Implementation
    // -----------------------------------------------------------------------

    public void service( HttpServletRequest request, HttpServletResponse response, RepositoryMapper repositoryMapper )
        throws ServletException
    {
        RepositoryMapping mapping;
        try
        {
            mapping = repositoryMapper.getRepositoryMapping( request );
        }
        catch ( Exception e )
        {
            throw new ServletException(
                "Error while mapping request to a repository. Request: " + request.getRequestURI(), e );
        }

        try
        {

            if ( mapping == null )
            {
                System.out.println( "404 on " + request.getPathInfo() );
                response.setStatus( HttpServletResponse.SC_NOT_FOUND );

                // TODO: redirect to error page.

                return;
            }

            // TODO: these should be cacheed
            DAVRepository davRepository = new DAVRepository( new File( mapping.getRepositoryPath() ) );
            DAVProcessor davProcessor = new DAVProcessor( davRepository );
            davRepository.addListener( new PlexusDavListener() );

            RepositoryRequest repositoryRequest = new RepositoryRequest( request, mapping.getLogicalPath() );
            DAVTransaction transaction = new DAVTransaction( repositoryRequest, response );

            davProcessor.process( transaction );
        }
        catch ( RuntimeException e )
        {
            final String header = request.getMethod() + ' ' + request.getRequestURI() + ' ' + request.getProtocol();
            getLogger().info( "Error processing: " + header );
            getLogger().info( "Exception processing DAV transaction", e );
            throw e;
        }
        catch ( IOException e )
        {
            throw new ServletException( "Error processing: " + request.getMethod() + ' ' + request.getRequestURI() +
                ' ' + request.getProtocol(), e );
        }
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private class PlexusDavListener
        implements DAVListener
    {
        /**
         * Receive notification of an event occurred in a specific {@link it.could.webdav.DAVRepository}.
         */
        public void notify( DAVResource resource, int event )
        {
            String message = "Unknown event";
            switch ( event )
            {
                case DAVListener.COLLECTION_CREATED:
                    message = "Collection created";
                    break;
                case DAVListener.COLLECTION_REMOVED:
                    message = "Collection removed";
                    break;
                case DAVListener.RESOURCE_CREATED:
                    message = "Resource created";
                    break;
                case DAVListener.RESOURCE_REMOVED:
                    message = "Resource removed";
                    break;
                case DAVListener.RESOURCE_MODIFIED:
                    message = "Resource modified";
                    break;
            }

            getLogger().info( message + ": '" + resource.getRelativePath() + "'." );
        }
    }
}
