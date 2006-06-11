package org.codehaus.plexus.cdc.merge;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.cdc.merge.support.AbstractMergeableElement;
import org.codehaus.plexus.cdc.merge.support.AbstractMergeableElementList;
import org.codehaus.plexus.cdc.merge.support.ComponentElement;
import org.codehaus.plexus.cdc.merge.support.RequirementsElement;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Tests for {@link ComponentsXmlMerger}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ComponentsXmlMergerTest
    extends PlexusTestCase
{

    private File dominant_xml = new File( "src/test/merge/dominant.xml" );

    private File recessive_xml = new File( "src/test/merge/recessive.xml" );

    public void testBasic()
        throws Exception
    {
        ComponentsXmlMerger merger = (ComponentsXmlMerger) lookup( Merger.ROLE );
        assertNotNull( merger );
    }

    public void testComponentsXmlFileMerge()
        throws Exception
    {
        Document dDoc = new SAXBuilder().build( dominant_xml );
        Document rDoc = new SAXBuilder().build( recessive_xml );

        // ComponentsXmlMerger merger = new ComponentsXmlMerger (dDoc);
        ComponentsXmlMerger merger = (ComponentsXmlMerger) lookup( Merger.ROLE );
        assertNotNull( merger );
        merger.setDominantDocument( dDoc );
        merger.merge( rDoc );

        File merged_xml = new File( "target/merged.xml" );
        merger.writeMergedDescriptor( merged_xml );
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

    public void testComponentsMerge()
        throws Exception
    {
        // dominant Component Element
        AbstractMergeableElement dCE = new ComponentElement( new Element( "component" ) );
        Element roleElt = new Element( "role" );
        roleElt.setText( "org.codehaus.plexus.ISampleRole" );
        dCE.addContent( roleElt );
        Element roleHintElt = null;
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
        assertEquals( "recessive-required-role-hint", ( (Element) dCE.getChild( "requirements" )
            .getChildren( "requirement" ).get( 0 ) ).getChildText( "role-hint" ) );
    }
}
