/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public class RequirementsElement
    extends AbstractMergeableElementList
{

    private final ComponentsXmlTag[] allowedTags = { ComponentsXmlTag.REQUIREMENT };

    public RequirementsElement( Element element )
    {
        super( element );
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#isExpectedElementType(org.codehaus.plexus.cdc.merge.model.Mergeable)
     */
    protected boolean isExpectedElementType( Mergeable me )
    {
        return ( me instanceof RequirementsElement );
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#getAllowedTags()
     */
    protected ComponentsXmlTag[] getAllowedTags()
    {
        if ( null == allowedTags[0] )
            throw new RuntimeException( "Allowed Tags cannot be NULL." );
        return allowedTags;
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableSupport#getElementNamesForConflictResolution(java.util.List)
     */
    protected List getElementNamesForConflictResolution( List defaultList )
    {
        // we return the keys that we know we want to lookup to identify and
        // resolve conflicts.
        List l = new ArrayList();
        l.add( ComponentsXmlTag.ROLE.getTagName() );
        // l.add (ComponentsXmlTag.ROLE_HINT.getTagName ());
        return l;
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElementList#getTagNameForRecurringMergeable()
     */
    protected String getTagNameForRecurringMergeable()
    {
        return ComponentsXmlTag.REQUIREMENT.getTagName();
    }

}
