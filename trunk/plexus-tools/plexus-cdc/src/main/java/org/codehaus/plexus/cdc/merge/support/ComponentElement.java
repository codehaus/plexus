/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public class ComponentElement
    extends AbstractMergeableElement
{

    /**
     * Allowed elements/tags that we can expect under this element.
     */
    private final ComponentsXmlTag[] allowedTags = {
        ComponentsXmlTag.ROLE,
        ComponentsXmlTag.ROLE_HINT,
        ComponentsXmlTag.IMPLEMENTATION,
        ComponentsXmlTag.FIELD_NAME,
        ComponentsXmlTag.LIFECYCLE_HANDLER,
        ComponentsXmlTag.REQUIREMENTS };

    public ComponentElement( Element element )
    {
        super( element );
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#isExpectedElementType(org.codehaus.plexus.cdc.merge.model.Mergeable)
     */
    protected boolean isExpectedElementType( Mergeable me )
    {
        return ( me instanceof ComponentElement );
    }

    /**
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#getAllowedTags()
     */
    protected ComponentsXmlTag[] getAllowedTags()
    {
        return allowedTags;
    }

}
