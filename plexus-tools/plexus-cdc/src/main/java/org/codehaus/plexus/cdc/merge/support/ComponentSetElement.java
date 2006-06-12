/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public class ComponentSetElement
    extends AbstractMergeableElement
{

    private final ComponentsXmlTag[] allowedTags = { ComponentsXmlTag.COMPONENTS };

    public ComponentSetElement( Element element )
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
        return ( me instanceof ComponentSetElement );
    }

}
