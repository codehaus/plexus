package org.codehaus.plexus.spe.services.io;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.spe.model.ProcessDescriptor;
import org.codehaus.plexus.spe.model.StepDescriptor;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ProcessDescriptorIoTest
    extends PlexusTestCase
{
    public void testXmlReading()
        throws Exception
    {
        ProcessDescriptorIo descriptorIo = (ProcessDescriptorIo) lookup( ProcessDescriptorIo.ROLE );

        ProcessDescriptor descriptor = descriptorIo.loadDescriptor( getClass().getResource( "descriptor-1.xml" ) );

        assertNotNull( descriptor );

        assertEquals( "plexus-action", descriptor.getDefaultExecutorId() );
        assertEquals( "ant-based-process", descriptor.getId() );
        assertNotNull( descriptor.getSteps() );
        assertEquals( 3, descriptor.getSteps().size() );

        StepDescriptor stepDescriptor = descriptor.getSteps().get( 0 );
        assertNotNull( stepDescriptor );
        assertNotNull( stepDescriptor.getExecutorConfiguration() );
        assertEquals( 1, stepDescriptor.getExecutorConfiguration().getChildNodes().getLength() );

        Node childNode = stepDescriptor.getExecutorConfiguration().getDocumentElement().getChildNodes().item( 0 );

        assertEquals( Node.ELEMENT_NODE, childNode.getNodeType() );
        assertEquals( "actionId", childNode.getNodeName() );
        assertEquals( "echo-message", childNode.getTextContent() );
    }
}
