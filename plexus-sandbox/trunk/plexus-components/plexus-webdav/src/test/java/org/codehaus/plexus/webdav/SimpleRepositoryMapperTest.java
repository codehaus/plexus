package org.codehaus.plexus.webdav;

import org.codehaus.plexus.PlexusTestCase;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SimpleRepositoryMapperTest
    extends PlexusTestCase
{
    public void testRepositoryMapping()
        throws Exception
    {
        RepositoryMapper mapper = new SimpleRepositoryMapper( getTestFile( "" ) );

        RepositoryMapping mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/foo" ) );
        assertNotNull( mapping );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/foo", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/foo/bar" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/foo/bar", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/a/b" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/a/b", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/a/" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/a", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/a" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/a", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects", "/" ) );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "/", mapping.getLogicalPath() );
    }

    public void testRepositoryIdFromPath()
        throws Exception
    {
        RepositoryMapper mapper = new SimpleRepositoryMapper( getTestFile( "" ) );

        RepositoryMapping mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/foo" ) );
        mapping = SimpleRepositoryMapper.splitRepositoryIdFromPath( mapping );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "foo", mapping.getRepositoryId() );
        assertEquals( "/", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/projects/", "/foo/bar" ) );
        mapping = SimpleRepositoryMapper.splitRepositoryIdFromPath( mapping );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "foo", mapping.getRepositoryId() );
        assertEquals( "/bar", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/", "/foo/bar" ) );
        mapping = SimpleRepositoryMapper.splitRepositoryIdFromPath( mapping );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "foo", mapping.getRepositoryId() );
        assertEquals( "/bar", mapping.getLogicalPath() );

        mapping = mapper.getRepositoryMapping( new TestRequest( "/", "/foo/bar/" ) );
        mapping = SimpleRepositoryMapper.splitRepositoryIdFromPath( mapping );
        assertEquals( getTestPath( "" ), mapping.getRepositoryPath() );
        assertEquals( "foo", mapping.getRepositoryId() );
        assertEquals( "/bar", mapping.getLogicalPath() );
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private static class TestRequest
        implements HttpServletRequest
    {
        private String servletPath;

        private String pathInfo;

        public TestRequest( String servletPath, String pathInfo )
        {
            this.servletPath = servletPath;
            this.pathInfo = pathInfo;
        }

        public String getAuthType()
        {
            return null;
        }

        public Cookie[] getCookies()
        {
            return new Cookie[0];
        }

        public long getDateHeader( String name )
        {
            return 0;
        }

        public String getHeader( String name )
        {
            return null;
        }

        public Enumeration getHeaders( String name )
        {
            return null;
        }

        public Enumeration getHeaderNames()
        {
            return null;
        }

        public int getIntHeader( String name )
        {
            return 0;
        }

        public String getMethod()
        {
            return null;
        }

        public String getPathInfo()
        {
            return pathInfo;
        }

        public String getPathTranslated()
        {
            return null;
        }

        public String getContextPath()
        {
            return null;
        }

        public String getQueryString()
        {
            return null;
        }

        public String getRemoteUser()
        {
            return null;
        }

        public boolean isUserInRole( String role )
        {
            return false;
        }

        public Principal getUserPrincipal()
        {
            return null;
        }

        public String getRequestedSessionId()
        {
            return null;
        }

        public String getRequestURI()
        {
            return null;
        }

        public StringBuffer getRequestURL()
        {
            return null;
        }

        public String getServletPath()
        {
            return servletPath;
        }

        public HttpSession getSession( boolean create )
        {
            return null;
        }

        public HttpSession getSession()
        {
            return null;
        }

        public boolean isRequestedSessionIdValid()
        {
            return false;
        }

        public boolean isRequestedSessionIdFromCookie()
        {
            return false;
        }

        public boolean isRequestedSessionIdFromURL()
        {
            return false;
        }

        public boolean isRequestedSessionIdFromUrl()
        {
            return false;
        }

        public Object getAttribute( String name )
        {
            return null;
        }

        public Enumeration getAttributeNames()
        {
            return null;
        }

        public String getCharacterEncoding()
        {
            return null;
        }

        public void setCharacterEncoding( String string )
            throws UnsupportedEncodingException
        {
        }

        public int getContentLength()
        {
            return 0;
        }

        public String getContentType()
        {
            return null;
        }

        public ServletInputStream getInputStream()
            throws IOException
        {
            return null;
        }

        public String getParameter( String name )
        {
            return null;
        }

        public Enumeration getParameterNames()
        {
            return null;
        }

        public String[] getParameterValues( String name )
        {
            return new String[0];
        }

        public Map getParameterMap()
        {
            return null;
        }

        public String getProtocol()
        {
            return null;
        }

        public String getScheme()
        {
            return null;
        }

        public String getServerName()
        {
            return null;
        }

        public int getServerPort()
        {
            return 0;
        }

        public BufferedReader getReader()
            throws IOException
        {
            return null;
        }

        public String getRemoteAddr()
        {
            return null;
        }

        public String getRemoteHost()
        {
            return null;
        }

        public void setAttribute( String name, Object o )
        {
        }

        public void removeAttribute( String name )
        {
        }

        public Locale getLocale()
        {
            return null;
        }

        public Enumeration getLocales()
        {
            return null;
        }

        public boolean isSecure()
        {
            return false;
        }

        public RequestDispatcher getRequestDispatcher( String path )
        {
            return null;
        }

        public String getRealPath( String path )
        {
            return null;
        }
    }
}
