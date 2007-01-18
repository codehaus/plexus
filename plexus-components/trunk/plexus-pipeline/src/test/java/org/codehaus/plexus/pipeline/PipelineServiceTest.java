package org.codehaus.plexus.pipeline;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineServiceTest
    extends PlexusTestCase
{
    protected void setUp()
        throws Exception
    {
        super.setUp();
        Recorder.records.clear();
    }

    public void testEmptyPipeline()
        throws Exception
    {
        PipelineService pipelineService = (PipelineService) lookup( PipelineService.ROLE );

        PipelineDescriptor pipelineDescriptor = new PipelineDescriptor();
        pipelineDescriptor.setId( "empty" );
        pipelineService.addPipeline( pipelineDescriptor );

        pipelineService.processMessage( "empty", null );

        assertEquals( 0, Recorder.records.size() );
    }

    public void testBasic()
        throws Exception
    {
        PipelineService pipelineService = (PipelineService) lookup( PipelineService.ROLE );

        pipelineService.processMessage( "simple", null );

        assertEquals( 2, Recorder.records.size() );
    }
}
