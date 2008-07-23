package org.codehaus.plexus.webdav.slide;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.slide.authenticate.SecurityToken;
import org.apache.slide.common.Domain;
import org.apache.slide.common.NamespaceAccessToken;
import org.apache.slide.util.conf.Configuration;
import org.apache.slide.util.conf.ConfigurationElement;
import org.apache.slide.util.conf.Populate;
import org.apache.slide.util.logger.Logger;
import org.apache.slide.webdav.WebdavException;
import org.apache.slide.webdav.WebdavMethod;
import org.apache.slide.webdav.WebdavMethodFactory;
import org.apache.slide.webdav.WebdavServlet;
import org.apache.slide.webdav.WebdavServletConfig;
import org.apache.slide.webdav.util.WebdavStatus;
import org.apache.slide.webdav.util.WebdavUtils;
import org.codehaus.plexus.evaluator.EvaluatorException;
import org.codehaus.plexus.evaluator.ExpressionEvaluator;
import org.codehaus.plexus.evaluator.sources.PropertiesExpressionSource;
import org.codehaus.plexus.evaluator.sources.SystemPropertyExpressionSource;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringInputStream;
import org.codehaus.plexus.webdav.AbstractDavServerComponent;
import org.codehaus.plexus.webdav.DavServerException;
import org.codehaus.plexus.webdav.servlet.DavServerRequest;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * SlideDavServerComponent 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 * 
 * @plexus.component role="org.codehaus.plexus.webdav.DavServerComponent" 
 *                   role-hint="slide" 
 *                   instantiation-strategy="per-lookup"
 */
public class SlideDavServerComponent
    extends AbstractDavServerComponent
{
    private static final String DOMAIN_XML = "/org/codehaus/plexus/webdav/slide/Domain.xml";

    private static final String LOG_CHANNEL = SlideDavServerComponent.class.getName();

    private String prefix;

    private File rootDirectory;

    private WebdavServletConfig config;

    private NamespaceAccessToken token;

    private WebdavMethodFactory methodFactory;

    /**
     * @plexus.requirement
     */
    private ExpressionEvaluator exeval;

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
        servletConfig.getServletContext().log( "Initializing " + this.getClass().getName() );

        Domain.init( createConfiguration() );

        String namespaceName = Domain.getDefaultNamespace();
        token = Domain.accessNamespace( new SecurityToken( this ), namespaceName );

        SlideServletConfigWrapper configWrapper = new SlideServletConfigWrapper( servletConfig, token );
        configWrapper.setIsDefaultServlet( false );
        config = new WebdavServletConfig( configWrapper );

        methodFactory = WebdavMethodFactory.newInstance( config );
        if ( methodFactory == null )
        {
            throw new DavServerException( "Unable to create method factory." );
        }
    }

    public void process( DavServerRequest request, HttpServletResponse response )
        throws DavServerException, ServletException, IOException
    {
        HttpServletRequest req = request.getRequest();

        SimpleDateFormat sdf = new SimpleDateFormat();
        if ( token.getLogger().isEnabled( LOG_CHANNEL, Logger.DEBUG ) )
            token.getLogger().log(
                                   "==> " + req.getMethod() + " start: "
                                       + sdf.format( new Date( System.currentTimeMillis() ) ) + " ["
                                       + Thread.currentThread().getName() + "]", LOG_CHANNEL, Logger.DEBUG );

        req.setAttribute( "slide_uri", WebdavUtils.getRelativePath( req, config ) );

        try
        {
            response.setStatus( WebdavStatus.SC_OK );

            String methodName = req.getMethod();
            WebdavMethod method = methodFactory.createMethod( methodName );
            if ( method == null )
            {
                throw new WebdavException( WebdavStatus.SC_METHOD_NOT_ALLOWED );
            }
            else
            {
                method.run( req, response );
            }
        }
        catch ( WebdavException e )
        {
            // There has been an error somewhere ...
            token.getLogger().log( e, LOG_CHANNEL, Logger.ERROR );
            try
            {
                response.sendError( e.getStatusCode() );
            }
            catch ( Throwable ex )
            {
            }
        }
        catch ( Throwable e )
        {
            // If something goes really wrong ...
            token.getLogger().log( e, LOG_CHANNEL, Logger.ERROR );
            try
            {
                response.sendError( WebdavStatus.SC_INTERNAL_SERVER_ERROR );
            }
            catch ( Throwable ex )
            {
            }
        }
        finally
        {
            if ( token.getLogger().isEnabled( LOG_CHANNEL, Logger.DEBUG ) )
                token.getLogger().log(
                                       "<== " + req.getMethod() + " end: "
                                           + sdf.format( new Date( System.currentTimeMillis() ) ) + " ["
                                           + Thread.currentThread().getName() + "]", LOG_CHANNEL, Logger.DEBUG );
        }
    }

    private Configuration createConfiguration()
        throws DavServerException
    {
        InputStream configInputStream = null;
        try
        {
            configInputStream = getConfigurationInputStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware( false );
            factory.setValidating( false );
            SAXParser parser = factory.newSAXParser();

            Populate pop = new Populate();
            InputSource configInputSource = new InputSource( configInputStream );
            XMLReader xmlReader = parser.getXMLReader();
            Configuration slideConfiguration = new ConfigurationElement( pop.load( configInputSource, xmlReader ) );

            return slideConfiguration;
        }
        catch ( IOException e )
        {
            throw new DavServerException( "Unable to configure DAV Component.", e );
        }
        catch ( EvaluatorException e )
        {
            throw new DavServerException( "Unable to configure DAV Component.", e );
        }
        catch ( ParserConfigurationException e )
        {
            throw new DavServerException( "Unable to configure DAV Component.", e );
        }
        catch ( SAXException e )
        {
            throw new DavServerException( "Unable to configure DAV Component.", e );
        }
        finally
        {
            IOUtil.close( configInputStream );
        }

    }

    private InputStream getConfigurationInputStream()
        throws IOException, EvaluatorException
    {
        // Find raw configuration url.
        URL rawConfigurationURL = this.getClass().getResource( DOMAIN_XML );

        if ( rawConfigurationURL == null )
        {
            throw new IllegalStateException( "Unable to find Domain Configuration file: " + DOMAIN_XML );
        }

        // Setup expression evaluator
        Properties props = new Properties();
        props.setProperty( "dav.root", rootDirectory.getAbsolutePath() );
        PropertiesExpressionSource propSource = new PropertiesExpressionSource();
        propSource.setProperties( props );
        exeval.addExpressionSource( propSource );
        exeval.addExpressionSource( new SystemPropertyExpressionSource() );

        // Read raw configuration file
        InputStream iStream = null;
        BufferedReader rawReader = null;
        InputStreamReader rawInput = null;

        try
        {
            rawInput = new InputStreamReader( rawConfigurationURL.openStream() );
            rawReader = new BufferedReader( rawInput );
            // Setup writer for post-expression-evaluated domain.xml
            StringWriter writer = new StringWriter( 1024 );

            String line = rawReader.readLine();
            while ( line != null )
            {
                writer.write( exeval.expand( line ) + "\n" );
                line = rawReader.readLine();
            }

            // Return post-expression-evaluated domain.xml (from memory)
            return new StringInputStream( writer.toString() );
        }
        finally
        {
            IOUtil.close( iStream );
            IOUtil.close( rawReader );
        }
    }

    /**
     * SlideServletContext - This wrapped ServletContext only exists to hand out multiple NamespaceAccessToken's based
     * on the DavServetComponent, not based on the servlet or application scope of the servlet container.  
     *
     * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
     * @version $Id$
     */
    class SlideServletContext
        implements ServletContext
    {
        public static final String ATTRIBUTE_NAME = "org.apache.slide.NamespaceAccessToken";

        private ServletContext impl;

        private NamespaceAccessToken token;

        public SlideServletContext( ServletContext impl, NamespaceAccessToken token )
        {
            this.impl = impl;
            this.token = token;
        }

        public Object getAttribute( String name )
        {
            // This is this class's reason for existance.
            if ( name.equals( ATTRIBUTE_NAME ) )
            {
                return token;
            }
            else
            {
                return impl.getAttribute( name );
            }
        }

        public Enumeration getAttributeNames()
        {
            return impl.getAttributeNames();
        }

        public ServletContext getContext( String uripath )
        {
            return impl.getContext( uripath );
        }

//        public String getContextPath()
//        {
//            return impl.getContextPath();
//        }

        public String getInitParameter( String name )
        {
            return impl.getInitParameter( name );
        }

        public Enumeration getInitParameterNames()
        {
            return impl.getInitParameterNames();
        }

        public int getMajorVersion()
        {
            return impl.getMajorVersion();
        }

        public String getMimeType( String file )
        {
            return impl.getMimeType( file );
        }

        public int getMinorVersion()
        {
            return impl.getMinorVersion();
        }

        public RequestDispatcher getNamedDispatcher( String name )
        {
            return impl.getNamedDispatcher( name );
        }

        public String getRealPath( String path )
        {
            return impl.getRealPath( path );
        }

        public RequestDispatcher getRequestDispatcher( String path )
        {
            return impl.getRequestDispatcher( path );
        }

        public URL getResource( String path )
            throws MalformedURLException
        {
            return impl.getResource( path );
        }

        public InputStream getResourceAsStream( String path )
        {
            return impl.getResourceAsStream( path );
        }

        public Set getResourcePaths( String path )
        {
            return impl.getResourcePaths( path );
        }

        public String getServerInfo()
        {
            return impl.getServerInfo();
        }

        public Servlet getServlet( String name )
            throws ServletException
        {
            return getServlet( name );
        }

        public String getServletContextName()
        {
            return impl.getServletContextName();
        }

        public Enumeration getServletNames()
        {
            return impl.getServletNames();
        }

        public Enumeration getServlets()
        {
            return impl.getServlets();
        }

        public void log( String msg )
        {
            impl.log( msg );
        }

        public void log( Exception exception, String msg )
        {
            impl.log( exception, msg );
        }

        public void log( String msg, Throwable throwable )
        {
            impl.log( msg, throwable );
        }

        public void removeAttribute( String name )
        {
            impl.removeAttribute( name );
        }

        public void setAttribute( String name, Object value )
        {
            impl.setAttribute( name, value );
        }
    }

    class SlideServletConfigWrapper
        implements ServletConfig
    {
        private Properties initParams;

        private ServletContext servletContext;

        private String servletName;

        public SlideServletConfigWrapper( ServletConfig impl, NamespaceAccessToken token )
        {
            this.servletContext = new SlideServletContext( impl.getServletContext(), token );
            this.servletName = impl.getServletName();
            initParams = new Properties();
            Enumeration enKeys = impl.getInitParameterNames();
            while ( enKeys.hasMoreElements() )
            {
                String key = (String) enKeys.nextElement();
                String value = impl.getInitParameter( key );
                initParams.setProperty( key, value );
            }
        }

        public void setScopeParameter( String scope )
        {
            initParams.setProperty( "scope", scope );
        }

        public void setDepthLimit( int depthLimit )
        {
            initParams.setProperty( "depth-limit", String.valueOf( depthLimit ) );
        }

        public void setDefaultMimeType( String mimeType )
        {
            initParams.setProperty( "default-mime-type", mimeType );
        }

        public void setIsDefaultServlet( boolean isdefault )
        {
            initParams.setProperty( "default-servlet", String.valueOf( isdefault ) );
        }

        public void setMethodFactory( String factory )
        {
            initParams.setProperty( "method-factory", factory );
        }

        public String getInitParameter( String key )
        {
            return initParams.getProperty( key );
        }

        public Enumeration getInitParameterNames()
        {
            return initParams.keys();
        }

        public ServletContext getServletContext()
        {
            return servletContext;
        }

        public String getServletName()
        {
            return servletName;
        }

    }
}
