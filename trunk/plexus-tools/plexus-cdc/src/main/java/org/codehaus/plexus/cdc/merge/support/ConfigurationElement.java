/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

import org.jdom.Element;

/**
 * TODO Implement merge for this.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ConfigurationElement
    extends AbstractMergeableElement
{

    public ConfigurationElement( Element element )
    {
        super( element );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#isExpectedElementType(org.codehaus.plexus.cdc.merge.model.Mergeable)
     */
    protected boolean isExpectedElementType( Mergeable me )
    {
        return ( me instanceof ConfigurationElement );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codehaus.plexus.cdc.merge.model.AbstractMergeableElement#getAllowedTags()
     */
    protected ComponentsXmlTag[] getAllowedTags()
    {
        // TODO Auto-generated method stub
        return new ComponentsXmlTag[0];
    }

}
