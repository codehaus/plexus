/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.jdom.Element;

/**
 * Base class that allows for handling merging two element lists.
 * <p>
 * <em>TODO Refactor and make this extend {@link AbstractMergeableElement} which is what
 * this actually is, but with added bits for merging child element lists.</em>
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class AbstractMergeableElementList
    extends AbstractMergeableSupport
{

    public AbstractMergeableElementList( Element element )
    {
        super( element );
    }

    /**
     * Parses &gt;component&lt; elements and builds a map key by
     * <code>role</code> + <code>role-hint</code>.
     * 
     * @param tagName
     *            Name of the tag that appears multiple times *
     * @return Map of {@link Mergeable} instances keyed on the composite key
     *         obtained from {@link #getElementNamesForConflictResolution(List)}
     * @throws Exception
     */
    protected Map parseRecurringMergeables( String tagName, List compositeKeyList )
        throws Exception
    {
        Map mergeables = new LinkedHashMap();
        List list = this.getChildren( tagName );
        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            Element ce = (Element) it.next();

            // use the composite key specified by the passed in list
            String compositeKey = "";
            for ( Iterator itr = compositeKeyList.iterator(); itr.hasNext(); )
            {
                String key = (String) itr.next();
                if ( null != ce.getChildText( key ) )
                    compositeKey = compositeKey + ce.getChildText( key );
            }

            // create a Mergeable instance and store it in the map.
            ComponentsXmlTag tag = ComponentsXmlTag.lookupTagInstanceByName( tagName );
            Mergeable mergeable = tag.createMergeable( ce );
            // register the Mergeable instance based on composite key
            mergeables.put( compositeKey, mergeable );
        }
        return mergeables;
    }

    /**
     * Merge the passed in {@link Mergeable} instance with the current
     * {@link Mergeable} (which is also the dominant one).
     */
    public void merge( Mergeable me )
        throws MergeException
    {
        try
        {
            Map dRequirementsMap = parseRecurringMergeables( getTagNameForRecurringMergeable(),
                                                             getElementNamesForConflictResolution( new ArrayList() ) );
            Map rRequirementsMap = ( (AbstractMergeableElementList) me )
                .parseRecurringMergeables( getTagNameForRecurringMergeable(),
                                           getElementNamesForConflictResolution( new ArrayList() ) );
            merge( getElement(), dRequirementsMap, rRequirementsMap );
        }
        catch ( Exception e )
        {
            // TODO: log to error
            // TODO: better error message
            throw new MergeException( "Unable to merge Mergeable lists for element '" + getName() + "'.", e );
        }

    }

    /**
     * Identifies the conflicting elements in the dominant and recessive
     * {@link Map} instance and merges as required.
     * 
     * @param parent
     *            {@link Element} that is parent for the children in the
     *            dominant Map instance. Merged content is added to this
     *            element.
     * @param dMap
     *            Dominant Map keyed by the composite key obtained from
     *            {@link #getElementNamesForConflictResolution(List)}
     * @param rMap
     *            Recessive Map keyed by the composite key obtained from
     *            {@link #getElementNamesForConflictResolution(List)}
     * @throws Exception
     *             if there was an error merging both the maps.
     */
    protected void merge( Element parent, Map dMap, Map rMap )
        throws Exception
    {
        Set dKeySet = dMap.keySet();
        Set rKeySet = rMap.keySet();
        // check if there are any entities to merge
        if ( !isMergeRequired( dKeySet, rKeySet ) )
            return;

        // iterate over components and process them
        for ( Iterator it = dKeySet.iterator(); it.hasNext(); )
        {
            String dKey = (String) it.next();
            if ( rMap.containsKey( dKey ) )
            {
                // conflict ! merge this component                
                Mergeable dMeregeable = (Mergeable) dMap.get( dKey );
                Mergeable rMergeable = (Mergeable) rMap.get( dKey );

                dMeregeable.merge( rMergeable );

                // and remove from the recessive list to mark it as merged.
                rMap.remove( dKey );
            }
        }

        // check if any unmerged components are left in the recessive map.
        if ( rMap.keySet().size() > 0 )
        {
            // add them to results
            for ( Iterator it = rKeySet.iterator(); it.hasNext(); )
            {
                String rKey = (String) it.next();
                // add to parent
                parent.addContent( (Element) ( (Mergeable) rMap.get( rKey ) ).getElement().clone() );
            }
        }
    }

    /**
     * @param dKeySet
     * @param rKeySet
     * @return
     */
    private boolean isMergeRequired( Set dKeySet, Set rKeySet )
    {
        return ( dKeySet.size() > 0 || rKeySet.size() > 0 );
    }

    /**
     * Allows the sub classes to provided a tag name that they expect to recurr
     * within them.
     * <p>
     * For instance: <br>
     * <ul>
     * <li>&lt;components&gt; expects &lt;component&gt; to recurr within
     * itself.</li>
     * <li>&lt;requirements&gt; expects &lt;requirement&gt; to recurr within
     * itself.</li>
     * </ul>
     * 
     * @return
     */
    protected abstract String getTagNameForRecurringMergeable();

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableSupport#getElementNamesForConflictResolution(java.util.List)
     */
    protected abstract List getElementNamesForConflictResolution( List defaultList );

}
