package org.codehaus.plexus.siterenderer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.codehaus.doxia.Doxia;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModel;
import org.codehaus.doxia.module.xhtml.decoration.model.DecorationModelReader;
import org.codehaus.doxia.module.xhtml.decoration.render.BannerRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.LinksRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.NavigationRenderer;
import org.codehaus.doxia.module.xhtml.decoration.render.RenderingContext;
import org.codehaus.doxia.site.module.SiteModule;
import org.codehaus.doxia.site.module.manager.SiteModuleManager;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.siterenderer.sink.SiteRendererSink;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.codehaus.plexus.velocity.VelocityComponent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

/**
 * @plexus.component
 * @plexus.role org.codehaus.plexus.siterenderer.Renderer
 * 
 * @author <a href="mailto:evenisse@codehaus.org>Emmanuel Venisse</a>
 * @version $Id$
 */
public class DefaultSiteRenderer
    extends AbstractLogEnabled
    implements Renderer
{
    // ----------------------------------------------------------------------
    // Requirements
    // ----------------------------------------------------------------------

    /** @plexus.requirement */
    private VelocityComponent velocity;

    /** @plexus.requirement */
    private SiteModuleManager siteModuleManager;

    /** @plexus.requirement */
    private Doxia doxia;

    // ----------------------------------------------------------------------
    // Fields
    // ----------------------------------------------------------------------

    private RenderingContext renderingContext;

    // ----------------------------------------------------------------------
    // Renderer implementation
    // ----------------------------------------------------------------------

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(File, File, InputStream, String, Map)
     */
    public void render( File siteDirectory, File outputDirectory, InputStream siteDescriptor, String templateName,
                       Map templateProperties )
        throws RendererException, IOException
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( siteDescriptor == null )
        {
            throw new RendererException( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel;

        try
        {
            decorationModel = decorationModelReader.createNavigation( new InputStreamReader( siteDescriptor ) );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Can't read the siteDescriptor.", e );
        }

        render( siteDirectory, outputDirectory, templateName, null, decorationModel );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#render(java.io.File, java.io.File, java.lang.String, java.lang.String, Map)
     */
    public void render( File siteDirectory, File outputDirectory, String siteDescriptor, String templateName,
                       Map templateProperties )
        throws RendererException, IOException
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( siteDescriptor == null )
        {
            throw new RendererException( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel;

        try
        {
            decorationModel = decorationModelReader.createNavigation( siteDescriptor );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Can't read the siteDescriptor.", e );
        }

        render( siteDirectory, outputDirectory, templateName, null, decorationModel );
    }

    private void render( File siteDirectory, File outputDirectory, String templateName, Map templateProperties,
                        DecorationModel decorationModel )
        throws RendererException, IOException
    {
        for ( Iterator i = siteModuleManager.getSiteModules().iterator(); i.hasNext(); )
        {
            SiteModule module = (SiteModule) i.next();

            File moduleBasedir = new File( siteDirectory, module.getSourceDirectory() );

            if ( !moduleBasedir.exists() )
            {
                continue;
            }

            List docs = FileUtils.getFileNames( moduleBasedir, "**/*." + module.getExtension(), null, false );

            for ( Iterator j = docs.iterator(); j.hasNext(); )
            {
                String doc = (String) j.next();

                String fullPathDoc = new File( moduleBasedir, doc ).getPath();

                SiteRendererSink sink = createSink( moduleBasedir, doc, decorationModel );

                try
                {
                    FileReader reader = new FileReader( fullPathDoc );

                    doxia.parse( reader, module.getParserId(), sink );

                    String outputName = doc.substring( 0, doc.indexOf( "." ) + 1 ) + "html";

                    File outputFile = new File( outputDirectory, outputName );

                    if ( !outputFile.getParentFile().exists() )
                    {
                        outputFile.getParentFile().mkdirs();
                    }

                    generateDocument( new FileWriter( outputFile ), templateName, templateProperties, sink );
                }
                catch ( Exception e )
                {
                    e.printStackTrace();

                    getLogger().error( "Error rendering " + fullPathDoc + ": " + e.getMessage(), e );
                }
                finally
                {
                    sink.flush();
                    sink.close();
                }
            }
        }
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#generateDocument(java.io.Writer, java.lang.String, java.util.Map, org.codehaus.plexus.siterenderer.sink.SiteRendererSink)
     */
    public void generateDocument( Writer writer, String templateName, Map templateProperties, SiteRendererSink sink )
        throws RendererException
    {
        VelocityContext context = new VelocityContext();

        // ----------------------------------------------------------------------
        // Data objects
        // ----------------------------------------------------------------------

        context.put( "relativePath", renderingContext.getRelativePath() );

        // Add infos from document
        context.put( "authors", sink.getAuthors() );

        context.put( "title", sink.getTitle() );

        context.put( "bodyContent", sink.getBody() );

        // Add infos from siteDescriptor
        NavigationRenderer r = new NavigationRenderer();

        StringWriter sw = new StringWriter();

        XMLWriter w = new PrettyPrintXMLWriter( sw );

        //r.render( w, renderingContext );

        context.put( "mainMenu", sw.toString() );

        sw = new StringWriter();

        w = new PrettyPrintXMLWriter( sw );

        LinksRenderer lr = new LinksRenderer();

        //lr.render( w, renderingContext );

        context.put( "links", sw.toString() );

        sw = new StringWriter();

        w = new PrettyPrintXMLWriter( sw );

        BannerRenderer br = new BannerRenderer( "bannerLeft" );

        //br.render( w, renderingContext );

        context.put( "bannerLeft", sw.toString() );

        sw = new StringWriter();

        w = new PrettyPrintXMLWriter( sw );

        br = new BannerRenderer( "bannerRight" );

        //br.render( w, renderingContext );

        context.put( "bannerRight", sw.toString() );

        context.put( "navBarLeft", "Last Published: " + new Date() );

        // Add user properties
        if ( templateProperties != null )
        {
            for ( Iterator i = templateProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                context.put( key, templateProperties.get( key ) );
            }
        }

        // ----------------------------------------------------------------------
        // Tools
        // ----------------------------------------------------------------------

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        writeTemplate( templateName, writer, context );
    }

    protected void writeTemplate( String templateName, Writer writer, Context context )
        throws RendererException
    {
        Template template = null;

        try
        {
            template = velocity.getEngine().getTemplate( templateName );
        }
        catch ( Exception e )
        {
            ClassLoader old = Thread.currentThread().getContextClassLoader();

            try
            {
                Thread.currentThread().setContextClassLoader( this.getClass().getClassLoader() );

                template = velocity.getEngine().getTemplate( templateName );
            }
            catch ( Exception e1 )
            {
                throw new RendererException( "Could not find the template '" + templateName + "'." );
            }
            finally
            {
                Thread.currentThread().setContextClassLoader( old );
            }
        }

        try
        {
            template.merge( context, writer );

            writer.close();
        }
        catch ( Exception e )
        {
            throw new RendererException( "Error while generating code.", e );
        }
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#createSink(java.io.File, java.lang.String, java.io.InputStream)
     */
    public SiteRendererSink createSink( File moduleBaseDir, String document, InputStream siteDescriptor )
        throws RendererException
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( siteDescriptor == null )
        {
            throw new RendererException( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel;

        try
        {
            decorationModel = decorationModelReader.createNavigation( new InputStreamReader( siteDescriptor ) );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Can't read the siteDescriptor.", e );
        }

        return createSink( moduleBaseDir, document, decorationModel );
    }

    /**
     * @see org.codehaus.plexus.siterenderer.Renderer#createSink(java.io.File, java.lang.String, java.lang.String)
     */
    public SiteRendererSink createSink( File moduleBaseDir, String document, String siteDescriptor )
        throws RendererException
    {
        DecorationModelReader decorationModelReader = new DecorationModelReader();

        if ( siteDescriptor == null )
        {
            throw new RendererException( "The site descriptor is not present!" );
        }

        DecorationModel decorationModel;

        try
        {
            decorationModel = decorationModelReader.createNavigation( siteDescriptor );
        }
        catch ( Exception e )
        {
            throw new RendererException( "Can't read the siteDescriptor.", e );
        }

        return createSink( moduleBaseDir, document, decorationModel );
    }

    private SiteRendererSink createSink( File moduleBaseDir, String document, DecorationModel decorationModel )
    {
        renderingContext = new RenderingContext( moduleBaseDir, document, decorationModel );

        return new SiteRendererSink( new StringWriter(), renderingContext );
    }
}
