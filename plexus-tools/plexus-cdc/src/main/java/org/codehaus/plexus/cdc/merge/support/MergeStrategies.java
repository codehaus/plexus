/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import java.util.ArrayList;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.codehaus.plexus.cdc.merge.MergeStrategy;

/**
 * Collection of available Merge Strategies.<p>
 * TODO: Revisit and factor {@link Mergeable#merge(Mergeable)} to use a {@link MergeStrategy}.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public class MergeStrategies
{

    /**
     * {@link MergeStrategy} implementation wherein the elements are merged 
     * down to the deepest available {@link Mergeable} instance in the DOM tree.
     */
    public static final MergeStrategy DEEP = new MergeStrategy()
    {

        /** 
         * @see org.codehaus.plexus.cdc.merge.MergeStrategy#apply(org.codehaus.plexus.cdc.merge.support.Mergeable, org.codehaus.plexus.cdc.merge.support.Mergeable)
         */
        public void apply( Mergeable dElt, Mergeable rElt )
            throws MergeException
        {
            dElt.merge( rElt );
        }

    };

    /**
     * {@link MergeStrategy} implementation wherein only the element on 
     * which the merge operation is called is 'merged'. The merge does not 
     * traverse the DOM tree any further.
     */
    public static final MergeStrategy SHALLOW = new MergeStrategy()
    {
        /** 
         * @throws MergeException 
         * @see org.codehaus.plexus.cdc.merge.MergeStrategy#apply(org.codehaus.plexus.cdc.merge.support.Mergeable, org.codehaus.plexus.cdc.merge.support.Mergeable)
         */
        public void apply( Mergeable dElt, Mergeable rElt )
            throws MergeException
        {
            AbstractMergeableElement dame = (AbstractMergeableElement) dElt;
            AbstractMergeableElement rame = (AbstractMergeableElement) rElt;

            // check if the dominant was in conflict with recessive.
            if ( !dame
                .isRecessiveElementInConflict( rame, dame.getElementNamesForConflictResolution( new ArrayList() ) ) )
            {
                // no conflict, simply add recessive to dominant's parent
                dame.getElement().addContent( rame.getElement() );
            }
        }

    };

}
