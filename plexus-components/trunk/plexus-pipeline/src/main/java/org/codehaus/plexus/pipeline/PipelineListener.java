package org.codehaus.plexus.pipeline;

/**
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface PipelineListener
{
    void beforeValve( PipelineRequest request );

    void afterValve( PipelineRequest request );
}
