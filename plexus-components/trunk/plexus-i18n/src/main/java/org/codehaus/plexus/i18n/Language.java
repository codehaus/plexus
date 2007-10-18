package org.codehaus.plexus.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class Language
{
	public static String DEFAULT_NAME = "Messages";
	
	private Class clazz;
	ResourceBundle rb;
	
	public Language()
	{
	}
	
	public Language( Class clazz )
	{
		this( clazz, Locale.getDefault() );
	}
	
	public Language( Class clazz, Locale locale )
	{
		this.clazz = clazz;
		rb = ResourceBundle.getBundle( clazz.getPackage().getName()+"."+DEFAULT_NAME, locale, clazz.getClassLoader() );
	}
	
	public String getMessage( String key )
	throws LanguageException
	{
		if( rb == null )
			throw new LanguageException("resourceBundle not initialized for "+(clazz==null?"null":clazz.getName() ) );
		
		return rb.getString(key);
	}
	
}
