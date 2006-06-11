/**
 * 
 */
package org.codehaus.plexus.cdc.merge;

import org.jdom.Document;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public interface Merger
{

    static final String ROLE = Merger.class.getName();

    /**
     * Merge with the recessive document.
     * 
     * @param rDocument
     *            the recessive document.
     * @throws MergeException
     *             if there was an error in merge.
     */
    void merge( Document rDocument )
        throws MergeException;

}
