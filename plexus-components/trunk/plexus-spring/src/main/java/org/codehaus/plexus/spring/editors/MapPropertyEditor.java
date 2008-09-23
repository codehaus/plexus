/**
 * Copyright - Accor - All Rights Reserved www.accorhotels.com
 */
package org.codehaus.plexus.spring.editors;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.PropertiesEditor;

/**
 * @author <a href="mailto:Olivier.LAMY@accor.com">olamy</a>
 *
 * @version $Id$
 */
public class MapPropertyEditor
    extends PropertiesEditor
    implements PropertyEditorRegistrar
{

    private Class<?> type, implementation;
    
    public MapPropertyEditor( Class type, Class implementation )
    {
        this.type = type;
        this.implementation = implementation;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.springframework.beans.PropertyEditorRegistrar#registerCustomEditors(org.springframework.beans.PropertyEditorRegistry)
     */
    public void registerCustomEditors( PropertyEditorRegistry registry )
    {
        registry.registerCustomEditor( type, this );
    }
    
    /**
     * @see org.springframework.beans.propertyeditors.PropertiesEditor#setAsText(java.lang.String)
     */
    public void setAsText( String text )
        throws IllegalArgumentException
    {
        if ( StringUtils.isBlank( text ) )
        {
            setValue( null );
            return;
        }

        Map c = (Map) BeanUtils.instantiateClass( implementation );

        try
        {
            SAXReader reader = new SAXReader();
            Document doc = reader.read( new StringReader( text ) );
            Element root = doc.getRootElement();
            for ( Iterator i = root.elementIterator(); i.hasNext(); )
            {
                Element element = (Element) i.next();
                String key = element.getName();
                String value = element.getTextTrim();
                c.put( key, value );
            }
            setValue( c );
        }
        catch ( DocumentException e )
        {
        }
}    

}
