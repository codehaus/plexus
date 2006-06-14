package org.codehaus.plexus.cdc.merge.support;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.codehaus.plexus.cdc.merge.MergeStrategy;
import org.jdom.Element;

/**
 * Interface that marks an implementing entity as <b>mergeable</b>.<p>
 * Not all the elements/tags are expected to implement this interface. <br>
 * It should be implemented by elements/tags that need to have a certain control on how elements of the same type are merged with them.
 *  
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id$
 */
public interface Mergeable
{

    /**
     * Merges an element of same type.
     * 
     * @param me
     *            Another entity that is mergeable.
     * @throws MergeException if there was an error merging the mergeables.
     */
    public abstract void merge( Mergeable me )
        throws MergeException;

    /**
     * Applies the passed in {@link MergeStrategy} to merge two {@link Mergeable} instance.<p>
     * 
     * @param me Recessive {@link Mergeable} instance.
     * @param strategy {@link MergeStrategy} to apply for merging.
     * @throws MergeException if there was an error while merging.
     */
    public abstract void merge( Mergeable me, MergeStrategy strategy )
        throws MergeException;

    /**
     * Returns the wrapped up JDom {@link Element} instance that was used to
     * create this Mergeable.
     * 
     * @return the wrapped up JDom {@link Element} instance.
     */
    public Element getElement();

}
