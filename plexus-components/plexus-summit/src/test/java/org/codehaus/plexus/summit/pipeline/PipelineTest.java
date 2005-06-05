package org.codehaus.plexus.summit.pipeline;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 */
public class PipelineTest
    extends PlexusTestCase
{
    public void testPipeline()
        throws Exception
    {
        Pipeline pipeline = (Pipeline) lookup( Pipeline.ROLE );

        assertNotNull( pipeline );

        assertEquals( 5, pipeline.getValves().size() );
    }
}
