package org.codehaus.plexus.cdc.merge.support;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.jdom.Element;

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
     * Returns the wrapped up JDom {@link Element} instance that was used to
     * create this Mergeable.
     * 
     * @return the wrapped up JDom {@link Element} instance.
     */
    public Element getElement();

}
