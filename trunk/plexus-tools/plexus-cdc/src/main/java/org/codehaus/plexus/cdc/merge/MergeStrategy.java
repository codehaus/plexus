/**
 * 
 */
package org.codehaus.plexus.cdc.merge;

import org.codehaus.plexus.cdc.merge.support.Mergeable;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public interface MergeStrategy
{

    /**
     * Merges a <b>dominant</b> {@link Mergeable} instance with a <b>recessive</b> one.
     * @param dElt Dominant {@link Mergeable} instance.
     * @param rElt Recessive {@link Mergeable} instance.
     * @throws MergeException TODO
     */
    void apply( Mergeable dElt, Mergeable rElt )
        throws MergeException;

}
