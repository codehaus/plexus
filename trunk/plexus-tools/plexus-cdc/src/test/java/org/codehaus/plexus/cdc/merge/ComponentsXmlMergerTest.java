package org.codehaus.plexus.cdc.merge;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.cdc.merge.support.AbstractMergeableElement;
import org.codehaus.plexus.cdc.merge.support.AbstractMergeableElementList;
import org.codehaus.plexus.cdc.merge.support.ComponentElement;
import org.codehaus.plexus.cdc.merge.support.ComponentsElement;
import org.codehaus.plexus.cdc.merge.support.RequirementsElement;
import org.codehaus.plexus.util.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;

/**
 * Tests for {@link ComponentsXmlMerger}.
 *
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public class ComponentsXmlMergerTest
    extends PlexusTestCase
{
    private File dominantXml = new File( "src/test/merge/dominant.xml" );

    private File recessiveXml = new File( "src/test/merge/recessive.xml" );

    public void testBasic()
        throws Exception
    {
        ComponentsXmlMerger merger = (ComponentsXmlMerger) lookup( Merger.ROLE );
        assertNotNull( merger );
    }

    public void testComponentsXmlFileMerge()
        throws Exception
    {
        Document dDoc = new SAXBuilder().build( dominantXml );
        Document rDoc = new SAXBuilder().build( recessiveXml );

        // ComponentsXmlMerger merger = new ComponentsXmlMerger (dDoc);
        ComponentsXmlMerger merger = (ComponentsXmlMerger) lookup( Merger.ROLE );
        assertNotNull( merger );
        merger.setDominantDocument( dDoc );
        merger.merge( rDoc );

        File merged_xml = new File( "target/merged.xml" );
        if ( merged_xml.exists() )
        {
            FileUtils.forceDelete( merged_xml );
        }
        merger.writeMergedDescriptor( merged_xml );
        assertTrue( merged_xml.exists() );
    }

    public void testInvalidMergeableElements()
        throws Exception
    {
        // dominant Component Element
        AbstractMergeableElement dCE = new ComponentElement( new Element( "component" ) );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );

        AbstractMergeableElementList reqElt = new RequirementsElement( new Element( "requirement" ) );
        Exception e1 = null;
        // attempt and invalid merge
        try
        {
            dCE.merge( reqElt );
        }
        catch ( Exception e )
        {
            e1 = e;
        }
        assertNotNull( e1 );
    }

    /**
     * Tests if &lt;component&gt; elements from two sets are being merged properly.
     *
     * @throws Exception if there was an unexpected error.
     */
    public void testComponentsMerge()
        throws Exception
    {
        // dominant Components Element
        AbstractMergeableElement dParent = new ComponentsElement( new Element( "components" ) );
        Element dCE = new Element( "component" );
        dParent.addContent( dCE );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );
        Element roleHintElt = new Element( "role-hint" );
        roleHintElt.setText( "sample-role-hint" );
        dCE.addContent( roleHintElt );
        Element implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.DominantImplementation" );
        dCE.addContent( implElt );
        Element requirementsElt = new Element( "requirements" );
        Element reqElt = new Element( "requirement" );
        Element reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        dCE.addContent( requirementsElt );

        // recessive Component Element
        AbstractMergeableElement rParent = new ComponentsElement( new Element( "components" ) );
        Element rCE = new Element( "component" );
        rParent.addContent( rCE );
        roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        rCE.addContent( roleElt );
        roleHintElt = new Element( "role-hint" );
        roleHintElt.setText( "sample-role-hint" );
        rCE.addContent( roleHintElt );
        implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.RecessiveImplementation" );
        rCE.addContent( implElt );
        Element lifecycleHandlerElt = new Element( "lifecycle-handler" );
        rCE.addContent( lifecycleHandlerElt );
        lifecycleHandlerElt.setText( "plexus-configurable" );
        requirementsElt = new Element( "requirements" );
        reqElt = new Element( "requirement" );
        reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        Element reqRoleHintElt = new Element( "role-hint" );
        reqRoleHintElt.setText( "recessive-required-role-hint" );
        reqElt.addContent( reqRoleHintElt );
        rCE.addContent( requirementsElt );

        // attempt to merge
        dParent.merge( rParent );
        assertEquals( 1, dParent.getChildren( "component" ).size() );
        assertEquals( "org.codehaus.plexus.DominantImplementation",
                      dParent.getChild( "component" ).getChildText( "implementation" ) );
        assertEquals( 1,
                      dParent.getChild( "component" ).getChild( "requirements" ).getChildren( "requirement" ).size() );
    }

    /**
     * <em>This is deprecated as we dont' want to drill to merging
     * nested elements within a component.</em><p>
     * <em>Keeping this around for testing MergeStrategy implmentation.</em>
     *
     * @throws Exception
     */
    public void testDeepComponentsMerge()
        throws Exception
    {
        // FIXME: Review this after MergeStrategies are in place.
        if ( true )
        {
            return;
        }

        // dominant Component Element
        AbstractMergeableElement dCE = new ComponentElement( new Element( "component" ) );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );
        Element roleHintElt;
        // roleHintElt = new Element ("role-hint");
        // roleHintElt.setText ("sample-hint");
        // dCE.addContent (roleHintElt);
        Element implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.DominantImplementation" );
        dCE.addContent( implElt );
        Element requirementsElt = new Element( "requirements" );
        Element reqElt = new Element( "requirement" );
        Element reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        dCE.addContent( requirementsElt );

        // recessive Component Element
        AbstractMergeableElement rCE = new ComponentElement( new Element( "component" ) );
        roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        rCE.addContent( roleElt );
        roleHintElt = new Element( "role-hint" );
        roleHintElt.setText( "recessive-hint" );
        rCE.addContent( roleHintElt );
        implElt = new Element( "implementation" );
        implElt.setText( "org.codehaus.plexus.RecessiveImplementation" );
        rCE.addContent( implElt );
        Element lifecycleHandlerElt = new Element( "lifecycle-handler" );
        rCE.addContent( lifecycleHandlerElt );
        lifecycleHandlerElt.setText( "plexus-configurable" );
        requirementsElt = new Element( "requirements" );
        reqElt = new Element( "requirement" );
        reqRoleElt = new Element( "role" );
        reqRoleElt.setText( "org.codehaus.plexus.IRequiredRole" );
        reqElt.addContent( reqRoleElt );
        requirementsElt.addContent( reqElt );
        Element reqRoleHintElt = new Element( "role-hint" );
        reqRoleHintElt.setText( "recessive-required-role-hint" );
        reqElt.addContent( reqRoleHintElt );
        rCE.addContent( requirementsElt );

        // attempt to merge
        dCE.merge( rCE );

        // verify the merge
        assertTrue( null != dCE.getChild( "role" ) );
        assertEquals( "org.codehaus.plexus.ISampleRole", dCE.getChildText( "role" ) );
        assertTrue( null != dCE.getChild( "role-hint" ) );
        assertEquals( "recessive-hint", dCE.getChildText( "role-hint" ) );
        assertTrue( null != dCE.getChild( "lifecycle-handler" ) );
        assertEquals( "plexus-configurable", dCE.getChildText( "lifecycle-handler" ) );
        assertTrue( null != dCE.getChild( "requirements" ) );
        assertEquals( 1, dCE.getChild( "requirements" ).getChildren( "requirement" ).size() );
        assertEquals( "recessive-required-role-hint",
                      ( (Element) dCE.getChild( "requirements" ).getChildren( "requirement" ).get( 0 ) ).getChildText( "role-hint" ) );
    }
}
