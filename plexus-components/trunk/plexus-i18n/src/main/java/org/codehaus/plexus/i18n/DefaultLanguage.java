package org.codehaus.plexus.i18n;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 *
 * 
 * @author <a href="oleg@codehaus.org">Oleg Gusakov</a>
 * 
 * @plexus.component
 *   role="org.codehaus.plexus.i18n.Language"
 */
public class DefaultLanguage
implements Language
{
	public static String DEFAULT_NAME = "Messages";
	
	private Class clazz;
	ResourceBundle rb;
	//-------------------------------------------------------------------------------------
	public DefaultLanguage()
	{
	}
	//-------------------------------------------------------------------------------------
	public DefaultLanguage( Class clazz )
	{
		this( clazz, Locale.getDefault() );
	}
	//-------------------------------------------------------------------------------------
	public DefaultLanguage( Class clazz, Locale locale )
	{
		this.clazz = clazz;
		rb = ResourceBundle.getBundle( clazz.getPackage().getName()+"."+DEFAULT_NAME, locale, clazz.getClassLoader() );
	}
	//-------------------------------------------------------------------------------------
	public Language init()
	{
		return new DefaultLanguage();
	}
	//-------------------------------------------------------------------------------------
	public Language init(Class clazz, Locale locale)
	{
		return new DefaultLanguage( clazz, locale );
	}
	//-------------------------------------------------------------------------------------
	public Language init(Class clazz)
	{
		return new DefaultLanguage( clazz );
	}
	//-------------------------------------------------------------------------------------
	public String getMessage( String key )
	{
		if( rb == null )
			return "resourceBundle not initialized for "+(clazz==null?"null":clazz.getName() );
		
		return rb.getString(key);
	}
	//-------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------
}
