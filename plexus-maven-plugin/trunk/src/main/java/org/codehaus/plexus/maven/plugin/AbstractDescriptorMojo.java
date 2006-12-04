package org.codehaus.plexus.maven.plugin;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.cdc.ComponentDescriptorCreator;
import org.codehaus.plexus.cdc.ComponentDescriptorCreatorException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/**
 * Provides a template for extensions to process sources as specified by {@link #getSourceDirectories()}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @since 1.3.4
 */
public abstract class AbstractDescriptorMojo
    extends AbstractMojo
{

    /**
     * @parameter expression="${project.build.directory}/generated-resources/plexus/"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter expression="META-INF/plexus/components.xml"
     * @required
     */
    private String fileName;

    /**
     * Whether to generate a Plexus Container descriptor instead of a component descriptor.
     * @parameter default-value="false"
     * @required
     */
    private boolean containerDescriptor;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject mavenProject;

    /**
     * @parameter
     */
    private ComponentDescriptor[] roleDefaults;

    /**
     * @component
     */
    private ComponentDescriptorCreator cdc;

    /**
     * @component
     */
    private MavenProjectHelper mavenProjectHelper;

    public AbstractDescriptorMojo()
    {
        super();
    }

    public void execute()
        throws MojoExecutionException
    {
        // ----------------------------------------------------------------------
        // Create the component set descriptor from the source files
        // ----------------------------------------------------------------------

        File[] sources = new File[getSourceDirectories().size()];

        Iterator it = getSourceDirectories().iterator();

        for ( int i = 0; i < sources.length; i++ )
        {
            sources[i] = new File( (String) it.next() );
        }

        try
        {
            cdc.processSources( sources, new File( outputDirectory, fileName ), containerDescriptor, roleDefaults );
        }
        catch ( ComponentDescriptorCreatorException e )
        {
            throw new MojoExecutionException( "Error while executing component descritor creator.", e );
        }

        mavenProjectHelper.addResource( mavenProject, outputDirectory.getAbsolutePath(), Collections.EMPTY_LIST,
                                        Collections.EMPTY_LIST );
    }

    /**
     * Extensions are expected to override and return an appropriate list of 
     * source directories which this Mojo will process and generate 
     * <code>components.xml</code> descriptor.
     * 
     * @return list of Java source directories to process.
     */
    protected abstract List getSourceDirectories();

}