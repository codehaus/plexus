/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import org.codehaus.plexus.cdc.merge.MergeException;
import org.jdom.Element;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class RequirementElement
    extends AbstractMergeableElement
{

    private final ComponentsXmlTag[] allowedTags = {
        ComponentsXmlTag.ROLE,
        ComponentsXmlTag.ROLE_HINT,
        ComponentsXmlTag.FIELD_NAME };

    public RequirementElement( Element element )
    {
        super( element );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#getAllowedTags()
     */
    protected ComponentsXmlTag[] getAllowedTags()
    {
        return allowedTags;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#merge(org.codehaus.plexus.cdc.merge.model.Mergeable)
     */
    public void merge( Mergeable me )
        throws MergeException
    {
        super.merge( me );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#isExpectedElementType(org.codehaus.plexus.cdc.merge.model.Mergeable)
     */
    protected boolean isExpectedElementType( Mergeable me )
    {
        return ( me instanceof RequirementElement );
    }

}
