package org.codehaus.plexus.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;


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
	private String bundleName;
	private Locale locale;
//	private ResourceBundle rb;
	private DefaultI18N i18n = new DefaultI18N();
	private String error;
	//-------------------------------------------------------------------------------------
	public DefaultLanguage()
	{
	}
	//-------------------------------------------------------------------------------------
	public DefaultLanguage( Class clazz )
	{
    this.bundleName = clazz.getPackage().getName()+"."+DEFAULT_NAME;
    try
    {
      i18n.initialize();
    }
    catch( InitializationException e )
    {
      error = e.getMessage();
    }
	}
	//-------------------------------------------------------------------------------------
	public DefaultLanguage( Class clazz, Locale locale )
	{
		this( clazz );
		this.locale = locale;
//		rb = ResourceBundle.getBundle( clazz.getPackage().getName()+"."+DEFAULT_NAME, locale, clazz.getClassLoader() );
	}
	//-------------------------------------------------------------------------------------
  public String getMessage( String key, String... args )
  {
    
    if( error != null )
      return error;
    
    if( args == null || args.length == 0)
      return i18n.getString( bundleName, locale, key );

    return i18n.format( bundleName, locale, key, args );
  }
	//-------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------
}
