package org.codehaus.plexus.pipeline;

import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class PipelineTest
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
        Pipeline pipeline = (Pipeline) lookup( Pipeline.ROLE, "empty" );

        pipeline.processMessage( null );

        assertEquals( 0, Recorder.records.size() );
    }
    
    public void testBasic()
        throws Exception
    {
        Pipeline pipeline = (Pipeline) lookup( Pipeline.ROLE, "simple" );

        pipeline.processMessage( null );

        assertEquals( 2, Recorder.records.size() );
    }
}
