package org.codehaus.plexus.webdav.jackrabbit;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.server.AbstractWebdavServlet;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.apache.jackrabbit.webdav.lock.SimpleLockManager;
import org.apache.jackrabbit.webdav.simple.LocatorFactoryImplEx;
import org.apache.jackrabbit.webdav.simple.ResourceConfig;
import org.apache.jackrabbit.webdav.simple.ResourceFactoryImpl;
import org.codehaus.plexus.webdav.AbstractDavServerComponent;
import org.codehaus.plexus.webdav.DavServerException;
import org.codehaus.plexus.webdav.DavServerRequest;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

/**
 * JackrabbitDavServerComponent 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.webdav.DavServerComponent" 
 *                   role-hint="jackrabbit" 
 *                   instantiation-strategy="per-lookup"
 */
public class JackrabbitDavServerComponent
    extends AbstractDavServerComponent
{
    private String prefix;

    private File rootDirectory;

    private JackrabbitServlet jackrabbit;

    public String getPrefix()
    {
        return prefix;
    }

    public File getRootDirectory()
    {
        return rootDirectory;
    }

    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    public void setRootDirectory( File rootDirectory )
    {
        this.rootDirectory = rootDirectory;
    }

    public void init( ServletConfig servletConfig )
        throws DavServerException
    {
        try
        {
            jackrabbit = new JackrabbitServlet( rootDirectory );
        }
        catch ( RepositoryException e )
        {
            throw new DavServerException( "Unable to create JackrabbitServlet.", e );
        }
    }

    public void process( DavServerRequest request, HttpServletResponse response )
        throws DavServerException, ServletException, IOException
    {
        jackrabbit.service( request.getRequest(), response );
    }

    private static Repository getRepository( File rootDirectory )
        throws RepositoryException
    {
        final String REPOXML = "/org/codehaus/plexus/webdav/jackrabbit/repository.xml";
        URL repositoryXmlUrl = rootDirectory.getClass().getResource( REPOXML );

        if ( repositoryXmlUrl == null )
        {
            throw new RepositoryException( "Missing resource [" + REPOXML + "], Unable to configure repository." );
        }

        try
        {
            RepositoryConfig repositoryConfig = RepositoryConfig.create( repositoryXmlUrl.openStream(), rootDirectory
                .getAbsolutePath() );

            Repository repository = RepositoryImpl.create( repositoryConfig );

            return repository;
        }
        catch ( IOException e )
        {
            throw new RepositoryException( "Unable to load repository [" + rootDirectory.getAbsolutePath() + "]", e );
        }
    }

    class JackrabbitServlet
        extends AbstractWebdavServlet
    {
        private DavResourceFactory resourceFactory;

        private LockManager lockManager;

        private ResourceConfig config;

        private DavSessionProvider sessionProvider;

        private DavLocatorFactory locatorFactory;

        public JackrabbitServlet( File rootDirectory )
            throws RepositoryException
        {
            lockManager = new SimpleLockManager();
            config = new ResourceConfig();
            sessionProvider = new JackrabbitDavSessionProvider( getRepository( rootDirectory ) );
            locatorFactory = new LocatorFactoryImplEx( rootDirectory.getAbsolutePath() );
            resourceFactory = new ResourceFactoryImpl( lockManager, config );
        }

        public String getAuthenticateHeaderValue()
        {
            return null;
        }

        public DavSessionProvider getDavSessionProvider()
        {
            return sessionProvider;
        }

        public DavLocatorFactory getLocatorFactory()
        {
            return locatorFactory;
        }

        public DavResourceFactory getResourceFactory()
        {
            return resourceFactory;
        }

        protected boolean isPreconditionValid( WebdavRequest request, DavResource resource )
        {
            return !resource.exists() || request.matchesIfHeader( resource );
        }

        public void setDavSessionProvider( DavSessionProvider davSessionProvider )
        {

        }

        public void setLocatorFactory( DavLocatorFactory davLocatorFactory )
        {

        }

        public void setResourceFactory( DavResourceFactory davResourceFactory )
        {

        }
    }

    class JackrabbitDavSessionProvider
        implements DavSessionProvider
    {
        RepositoryConfig repositoryConfig;

        Repository repository;

        public JackrabbitDavSessionProvider( Repository repository )
            throws RepositoryException
        {
            this.repository = repository;
        }

        public boolean attachSession( WebdavRequest request )
            throws DavException
        {
            Session session;
            try
            {
                session = repository.login( JackrabbitCredentials.getDefaultCredentials( request ) );
            }
            catch ( LoginException e )
            {
                throw new DavException( 500, e );
            }
            catch ( RepositoryException e )
            {
                throw new DavException( 500, e );
            }
            JackrabbitDavSession davSession = new JackrabbitDavSession( session );
            request.setDavSession( davSession );
            return true;
        }

        public void releaseSession( WebdavRequest request )
        {
            request.setDavSession( null );
        }
    }

    class JackrabbitDavSession
        extends JcrDavSession
    {
        protected JackrabbitDavSession( Session session )
        {
            super( session );
        }

        public void addReference( Object reference )
        {

        }

        public void removeReference( Object reference )
        {

        }
    }
}
