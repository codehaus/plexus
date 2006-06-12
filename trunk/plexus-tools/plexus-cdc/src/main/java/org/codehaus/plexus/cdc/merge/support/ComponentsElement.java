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
public class ComponentsElement
    extends AbstractMergeableElementList
{

    private ComponentsXmlTag[] allowedTags = { ComponentsXmlTag.COMPONENT };

    private List conflictVerificationkeys = new ArrayList();

    {
        conflictVerificationkeys.add( ComponentsXmlTag.ROLE.getTagName() );
        conflictVerificationkeys.add( ComponentsXmlTag.ROLE_HINT.getTagName() );
    }

    public ComponentsElement( Element element )
    {
        super( element );
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#getAllowedTags()
     */
    protected ComponentsXmlTag[] getAllowedTags()
    {
        return this.allowedTags;
    }

    /** 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#isExpectedElementType(org.codehaus.plexus.cdc.merge.model.Mergeable)
     */
    protected boolean isExpectedElementType( Mergeable me )
    {
        return ( me instanceof ComponentsElement );
    }

    /** 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#getElementNamesForConflictChecks(java.util.List)
     */
    protected List getElementNamesForConflictChecks( List defaultList )
    {
        // Allow to return custom keys for conflict checks/resolution.
        return this.conflictVerificationkeys;
    }

    /** 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElementList#getTagNameForRecurringMergeable()
     */
    protected String getTagNameForRecurringMergeable()
    {
        return ComponentsXmlTag.COMPONENT.getTagName();
    }

    /** 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElementList#getElementNamesForConflictResolution(java.util.List)
     */
    protected List getElementNamesForConflictResolution( List defaultList )
    {
        List l = new ArrayList();
        l.add( ComponentsXmlTag.ROLE.getTagName() );
        l.add( ComponentsXmlTag.ROLE_HINT.getTagName() );
        return l;
    }

}
