/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class AbstractMergeableElement
    extends AbstractMergeableSupport
{

    public AbstractMergeableElement( Element element )
    {
        super( element );
    }

    // 
    // Methods delegated on wrapped JDOM element.
    // 

    /**
     * Detects if there was a conflict, that is the specified element was
     * present in both dominant and recessive element-sets.
     * <p>
     * This delegates to
     * {@link #isRecessiveElementInConflict(AbstractMergeableElement, List)}.
     * 
     * @param re
     *            Recessive element.
     * @param eltName
     *            Element name to test for.
     * @return <code>true</code> if there was a conflict of element.
     * @deprecated <em>use {@link #isRecessiveElementInConflict(AbstractMergeableElement, List)} instead.</em>
     */
    protected boolean isRecessiveElementInConflict( AbstractMergeableElement re, String eltName )
    {
        // return (null != getChild (eltName) && null != re.getChild (eltName));
        List l = new ArrayList();
        l.add( eltName );
        return isRecessiveElementInConflict( re, l );
    }

    /**
     * Detects if there was a conflict, that is the specified element was
     * present in both dominant and recessive element-sets.
     * <p>
     * Use this to determine conflicts when the Dominant and Recessive element
     * sets are keyed with Composite keys.<br>
     * For instance: <code>&lt;component&gt;</code> is keyed on
     * <code>&lt;role&gt;</code> and <code>&lt;role-hint&gt;</code>.
     * 
     * @param re
     * @param eltNameList
     *            List of elements that will be checked for values in both
     *            dominant and recessive sets.
     * @return
     */
    protected boolean isRecessiveElementInConflict( AbstractMergeableElement re, List eltNameList )
    {
        // give opportunity to subclasses to provide any custom Composite keys
        // for conflict checks.
        eltNameList = getElementNamesForConflictResolution( eltNameList );

        if ( null == eltNameList || eltNameList.size() == 0 )
            return false;

        // assuming the elements will conflict.
        for ( Iterator it = eltNameList.iterator(); it.hasNext(); )
        {
            String eltName = (String) it.next();
            String dEltValue = getChildTextTrim( eltName );
            String rEltValue = re.getChildTextTrim( eltName );
            if ( null == dEltValue || null == rEltValue || !dEltValue.equals( rEltValue ) )
                return false;
        }
        return true;
    }

    /**
     * Determines if the Element to be merged is to be sourced from Recessive
     * Element set.
     * 
     * @param re
     *            Recessive element.
     * @param eltName
     *            Element name to test for.
     * @return
     */
    protected boolean mergeableElementComesFromRecessive( AbstractMergeableElement re, String eltName )
    {
        return null == getChildText( eltName ) && null != re.getChildText( eltName );
    }

    /**
     * Merges the passed in recessive {@link Mergeable} instance with the
     * current one (which is treated as Dominant).
     */
    public void merge( Mergeable me )
        throws MergeException
    {
        if ( !isExpectedElementType( me ) )
        {
            // if (getLogger().isErrorEnabled)
            //     getLogger().error ("Cannot Merge dissimilar elements. (Expected : '" + getClass ().getName () + "', found '" + me.getClass ().getName () + "')");
            throw new MergeException( "Cannot Merge dissimilar elements. (Expected : '" + getClass().getName()
                + "', found '" + me.getClass().getName() + "')" );
        }
        // recessive Component Element.
        AbstractMergeableElement rce = (AbstractMergeableElement) me;
        for ( int i = 0; i < getAllowedTags().length; i++ )
        {
            String tagName = getAllowedTags()[i].getTagName();

            List defaultConflictChecklist = new ArrayList();
            defaultConflictChecklist.add( tagName );

            if ( !isRecessiveElementInConflict( rce, defaultConflictChecklist )
                && mergeableElementComesFromRecessive( rce, tagName ) )
            {
                this.addContent( (Element) rce.getChild( tagName ).clone() );
                // else dominant wins in anycase!
            }
            else if ( getAllowedTags()[i].isMergeable() && isRecessiveElementInConflict( rce, defaultConflictChecklist ) )
            {
                // this allows for merging multiple/list of elements.
                try
                {
                    getAllowedTags()[i].createMergeable( this.getChild( tagName ) )
                        .merge( getAllowedTags()[i].createMergeable( rce.getChild( tagName ) ) );
                }
                catch ( Exception e )
                {
                    // TODO log to error
                    throw new MergeException( "Unable to create Mergeable instance for tag '" + getAllowedTags()[i]
                        + "'.", e );
                }
            }
        }
    }

}
