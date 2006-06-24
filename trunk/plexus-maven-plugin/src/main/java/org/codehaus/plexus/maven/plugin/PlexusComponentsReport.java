/*
 * The MIT License
 *
 * Copyright (c) 2006, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.codehaus.plexus.maven.plugin;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;
import org.codehaus.doxia.site.renderer.SiteRenderer;
import org.codehaus.plexus.maven.plugin.report.ComponentSet;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * @goal components-report
 * @execute phase="process-resources" lifecycle="plexus-site"
 *
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PlexusComponentsReport
    extends AbstractMavenReport
{
    /**
     * @parameter default-value="${project.build.outputDirectory}/META-INF/plexus/components.xml"
     * @required
     */
    private File componentsXml;

    /**
     * @parameter expression="${project}"
     * @readonly
     */
    private MavenProject project;

    /**
     * @component org.codehaus.doxia.site.renderer.SiteRenderer
     */
    private SiteRenderer siteRenderer;

    /**
     * @parameter expression="${project.reporting.outputDirectory}
     */
    private String outputDirectory;

    // ----------------------------------------------------------------------
    // MavenReport Implementation
    // ----------------------------------------------------------------------

    public String getOutputName()
    {
        return "plexus/plexus-components";
    }

    public String getName( Locale locale )
    {
        return "Plexus Components";
    }

    public String getCategoryName()
    {
        return CATEGORY_PROJECT_REPORTS;
    }

    public String getDescription( Locale locale )
    {
        return "Plexus components report description";
    }

    protected MavenProject getProject()
    {
        return project;
    }

    protected SiteRenderer getSiteRenderer()
    {
        return siteRenderer;
    }

    protected String getOutputDirectory()
    {
        return outputDirectory;
    }

    public boolean canGenerateReport()
    {
        return componentsXml.isFile();
    }

    protected void executeReport( Locale locale )
        throws MavenReportException
    {
        String title = "Plexus Components Report";

        Sink sink = getSink();

        sink.head();
        sink.title();
        sink.text( title );
        sink.title_();
        sink.head_();

        sink.body();

        sink.section1();
        sink.sectionTitle1();
        sink.text( title );
        sink.sectionTitle1_();

        Document document;

        try
        {
            document = new SAXBuilder().build( componentsXml );
        }
        catch ( JDOMException e )
        {
            throw new MavenReportException( "Error while building document of " + componentsXml.getAbsolutePath() + ".", e);
        }
        catch ( IOException e )
        {
            throw new MavenReportException( "Error while building document of " + componentsXml.getAbsolutePath() + ".", e);
        }

        if ( document.getRootElement().getName().equals( "component-set" ) )
        {
            ComponentSet componentSet = new ComponentSet( document.getRootElement() );

            componentSet.print( sink );
        }

        sink.body_();
        sink.flush();
    }
}
