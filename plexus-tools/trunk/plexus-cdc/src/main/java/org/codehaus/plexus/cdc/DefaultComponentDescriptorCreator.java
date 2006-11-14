package org.codehaus.plexus.cdc;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import org.codehaus.plexus.cdc.merge.Merger;
import org.codehaus.plexus.cdc.merge.MergeException;
import org.codehaus.plexus.component.repository.ComponentDependency;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentSetDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.PrettyPrintXMLWriter;
import org.codehaus.plexus.util.xml.XMLWriter;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * So, in this case it is easy enough to determine the role and the implementation.
 * We could also employ some secondary checks like looking for particular super classes
 * or whatever. We can always use the @tags to be explicit but in most cases we can
 * probably determine the correct component descriptor without requiring @tags.
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 * @todo glean configuration information from types of the parameters but also
 * allow OCL type constraints for validation. We'll hook in something simple like
 * regex as for most cases I think some simple regex could catch most problems. I
 * don't want to have to use MSV or something like that which which triple the size
 * of a deployment.
 * <p/>
 * This is for a single project with a single POM, multiple components
 * with all deps in the POM.
 */
public class DefaultComponentDescriptorCreator
    extends AbstractLogEnabled
    implements ComponentDescriptorCreator
{
    private static final String LS = System.getProperty( "line.separator" );

    // TODO: Make a list
    private ComponentGleaner gleaner;

    private Merger merger;

    // ----------------------------------------------------------------------
    // ComponentDescriptorCreator Implementation
    // ----------------------------------------------------------------------

    public void processSources( File[] sourceDirectories, File outputFile )
        throws ComponentDescriptorCreatorException
    {
        processSources( sourceDirectories, outputFile, false, new ComponentDescriptor[0] );
    }

    public void processSources( File[] sourceDirectories, File outputFile, boolean containerDescriptor, ComponentDescriptor[] roleDefaults )
        throws ComponentDescriptorCreatorException
    {
        // ----------------------------------------------------------------------
        // Check and register all directories to scan
        // ----------------------------------------------------------------------

        JavaSource[] javaSources;

        JavaDocBuilder builder = new JavaDocBuilder();

        getLogger().debug( "Source directories: " );

        for ( int it = 0; it < sourceDirectories.length; it++ )
        {
            File sourceDirectory = sourceDirectories[it];

            if ( !sourceDirectory.isDirectory() )
            {
                getLogger().debug( "Specified source directory isn't a directory: " +
                    "'" + sourceDirectory.getAbsolutePath() + "'." );
            }

            getLogger().debug( " - " + sourceDirectory.getAbsolutePath() );

            builder.addSourceTree( sourceDirectory );
        }

        // ----------------------------------------------------------------------
        // Scan the sources
        // ----------------------------------------------------------------------

        javaSources = builder.getSources();

        Map defaultsByRole = new HashMap();
        if ( roleDefaults != null )
        {
            for ( int i = 0; i < roleDefaults.length; i++ )
            {
                // TODO: fail if role is null
                defaultsByRole.put( roleDefaults[i].getRole(), roleDefaults[i] );
            }
        }

        List componentDescriptors = new ArrayList();

        for ( int i = 0; i < javaSources.length; i++ )
        {
            JavaClass javaClass = getJavaClass( javaSources[i] );

            ComponentDescriptor componentDescriptor = gleaner.glean( builder, javaClass );

            if ( componentDescriptor != null && !javaClass.isAbstract() )
            {
                // TODO: better merge, perhaps pass it into glean as the starting point instead
                if ( defaultsByRole.containsKey( componentDescriptor.getRole() ) )
                {
                    ComponentDescriptor desc = (ComponentDescriptor) defaultsByRole.get( componentDescriptor.getRole() );

                    if ( componentDescriptor.getInstantiationStrategy() == null )
                    {
                        componentDescriptor.setInstantiationStrategy( desc.getInstantiationStrategy() );
		    }
                }
                componentDescriptors.add( componentDescriptor );
            }
        }

        ComponentSetDescriptor componentSetDescriptor = new ComponentSetDescriptor();

        componentSetDescriptor.setComponents( componentDescriptors );

        // ----------------------------------------------------------------------
        // Convert the Maven dependencies to Plexus component dependencies
        // ----------------------------------------------------------------------

//        List componentDependencies = convertDependencies( mavenProject.getDependencies() );
//
//        componentSetDescriptor.setDependencies( componentDependencies );

        // TODO: for now
        componentSetDescriptor.setDependencies( Collections.EMPTY_LIST );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        validateConfiguration( componentSetDescriptor );

        // ----------------------------------------------------------------------
        // Write out the component descriptor
        // ----------------------------------------------------------------------

        if ( componentDescriptors.size() == 0 && componentSetDescriptor.getDependencies().size() == 0 )
        {
            getLogger().debug( "No components or dependencies found, not writing components.xml" );

            return;
        }

        File parentFile = outputFile.getParentFile();
        if ( !parentFile.exists() )
        {
            if ( !parentFile.mkdirs() )
            {
                throw new ComponentDescriptorCreatorException(
                    "Could not make parent directory: '" + parentFile.getAbsolutePath() + "'." );
            }
        }

        try
        {
            FileWriter writer = new FileWriter( outputFile );

            XMLWriter w = new PrettyPrintXMLWriter( writer );

            w.startElement( containerDescriptor ? "plexus" : "component-set" );

            writeComponents( w, componentDescriptors );

            writeDependencies( w, componentSetDescriptor );

            w.endElement();

            writer.write( LS );

            writer.close();
        }
        catch ( PlexusConfigurationException e )
        {
            throw new ComponentDescriptorCreatorException( "Internal error while writing out the configuration", e );
        }
        catch ( IOException e )
        {
            throw new ComponentDescriptorCreatorException(
                "Error while writing the component descriptor to: " + "'" + outputFile.getAbsolutePath() + "'.", e );
        }
    }

    public void mergeDescriptors( File outputDescriptor, List descriptors )
        throws ComponentDescriptorCreatorException
    {
        SAXBuilder builder = new SAXBuilder();

        Document finalDoc = null;

        for ( Iterator i = descriptors.iterator(); i.hasNext(); )
        {
            File f = (File) i.next();
            try
            {
                Document doc = builder.build( f );

                if ( finalDoc != null )
                {
                    // Last specified has dominance
                    finalDoc = merger.merge( doc, finalDoc );
                }
                else
                {
                    finalDoc = doc;
                }
            }
            catch ( JDOMException e )
            {
                throw new ComponentDescriptorCreatorException( "Invalid input descriptor for merge: " + f, e );
            }
            catch ( IOException e )
            {
                throw new ComponentDescriptorCreatorException( "Error reading input descriptor for merge: " + f, e );
            }
            catch ( MergeException e )
            {
                throw new ComponentDescriptorCreatorException( "Error merging descriptor: " + f, e );
            }
        }

        if ( finalDoc != null )
        {
            try
            {
                merger.writeMergedDocument( finalDoc, outputDescriptor );
            }
            catch ( IOException e )
            {
                throw new ComponentDescriptorCreatorException( "Error writing merged descriptor: " + outputDescriptor,
                                                               e );
            }
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void validateConfiguration( ComponentSetDescriptor componentSetDescriptor )
        throws ComponentDescriptorCreatorException
    {
        List dependencies = componentSetDescriptor.getDependencies();

        if ( dependencies == null )
        {
            return;
        }

        for ( Iterator it = dependencies.iterator(); it.hasNext(); )
        {
            ComponentDependency dependency = (ComponentDependency) it.next();

            if ( StringUtils.isEmpty( dependency.getGroupId() ) )
            {
                throw new ComponentDescriptorCreatorException( "Missing dependency element: 'groupId'." );
            }

            if ( StringUtils.isEmpty( dependency.getArtifactId() ) )
            {
                throw new ComponentDescriptorCreatorException( "Missing dependency element: 'artifactId'." );
            }

            if ( StringUtils.isEmpty( dependency.getVersion() ) )
            {
                throw new ComponentDescriptorCreatorException( "Missing dependency element: 'version'." );
            }

            if ( StringUtils.isEmpty( dependency.getType() ) )
            {
                throw new ComponentDescriptorCreatorException( "Missing dependency element: 'type'." );
            }
        }
    }

    private void writeComponents( XMLWriter w, List componentDescriptors )
        throws ComponentDescriptorCreatorException, PlexusConfigurationException
    {
        w.startElement( "components" );

        for ( Iterator i = componentDescriptors.iterator(); i.hasNext(); )
        {
            w.startElement( "component" );

            ComponentDescriptor cd = (ComponentDescriptor) i.next();

            element( w, "alias", cd.getAlias() );

            element( w, "role", cd.getRole() );

            element( w, "role-hint", cd.getRoleHint() );

            element( w, "implementation", cd.getImplementation() );

            element( w, "version", cd.getVersion() );

            element( w, "instantiation-strategy", cd.getInstantiationStrategy() );

            element( w, "lifecycle-handler", cd.getLifecycleHandler() );

            element( w, "description", cd.getDescription() );

            writeRequirements( w, cd.getRequirements() );

            writeConfiguration( w, cd.getConfiguration() );

            w.endElement();
        }

        w.endElement();
    }

    private void writeRequirements( XMLWriter w, List requirements )
    {
        if ( requirements == null || requirements.size() == 0 )
        {
            return;
        }

        w.startElement( "requirements" );

        for ( Iterator j = requirements.iterator(); j.hasNext(); )
        {
            ComponentRequirement cr = (ComponentRequirement) j.next();

            w.startElement( "requirement" );

            element( w, "role", cr.getRole() );

            element( w, "role-hint", cr.getRoleHint() );

            element( w, "field-name", cr.getFieldName() );

            w.endElement();
        }

        w.endElement();
    }

    private void writeConfiguration( XMLWriter w, PlexusConfiguration configuration )
        throws ComponentDescriptorCreatorException, PlexusConfigurationException
    {
        if ( configuration == null || configuration.getChildCount() == 0 )
        {
            return;
        }

        if ( !configuration.getName().equals( "configuration" ) )
        {
            throw new ComponentDescriptorCreatorException(
                "The root node of the configuration must be " + "'configuration'." );
        }

        writePlexusConfiguration( w, configuration );
    }

    public void writeDependencies( XMLWriter w, ComponentSetDescriptor componentSetDescriptor )
    {
        List deps = componentSetDescriptor.getDependencies();

        if ( deps == null || deps.size() == 0 )
        {
            return;
        }

        w.startElement( "dependencies" );

        for ( int i = 0; i < deps.size(); i++ )
        {
            writeDependencyElement( (ComponentDependency) deps.get( i ), w );
        }

        w.endElement();
    }

    private void writeDependencyElement( ComponentDependency dependency, XMLWriter w )
    {
        w.startElement( "dependency" );

        String groupId = dependency.getGroupId();

        element( w, "groupId", groupId );

        String artifactId = dependency.getArtifactId();

        element( w, "artifactId", artifactId );

        String type = dependency.getType();

        if ( type != null )
        {
            element( w, "type", type );
        }

        String version = dependency.getVersion();

        element( w, "version", version );

        w.endElement();
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private void writePlexusConfiguration( XMLWriter xmlWriter, PlexusConfiguration c )
        throws PlexusConfigurationException
    {
        if ( c.getAttributeNames().length == 0 && c.getChildCount() == 0 && c.getValue() == null )
        {
            return;
        }

        xmlWriter.startElement( c.getName() );

        // ----------------------------------------------------------------------
        // Write the attributes
        // ----------------------------------------------------------------------

        String[] attributeNames = c.getAttributeNames();

        for ( int i = 0; i < attributeNames.length; i++ )
        {
            String attributeName = attributeNames[i];

            xmlWriter.addAttribute( attributeName, c.getAttribute( attributeName ) );
        }

        // ----------------------------------------------------------------------
        // Write the children
        // ----------------------------------------------------------------------

        PlexusConfiguration[] children = c.getChildren();

        if ( children.length > 0 )
        {
            for ( int i = 0; i < children.length; i++ )
            {
                writePlexusConfiguration( xmlWriter, children[i] );
            }
        }
        else
        {
            String value = c.getValue();

            if ( value != null )
            {
                xmlWriter.writeText( value );
            }
        }

        xmlWriter.endElement();
    }

    private void element( XMLWriter w, String name, String value )
    {
        if ( value == null )
        {
            return;
        }

        w.startElement( name );

        w.writeText( value );

        w.endElement();
    }

//    private List convertDependencies( List dependencies )
//    {
//        List componentDependencies = new ArrayList();
//
//        for ( Iterator i = dependencies.iterator(); i.hasNext(); )
//        {
//            Dependency d = (Dependency) i.next();
//
//            ComponentDependency cd = new ComponentDependency();
//
//            cd.setGroupId( d.getGroupId() );
//
//            cd.setArtifactId( d.getArtifactId() );
//
//            cd.setVersion( d.getVersion() );
//
//            componentDependencies.add( cd );
//        }
//
//        return componentDependencies;
//    }

    private JavaClass getJavaClass( JavaSource javaSource )
    {
        return javaSource.getClasses()[0];
    }
}
