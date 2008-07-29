package org.codehaus.plexus.i18n;

import java.util.Locale;

public interface Language
{
  public static String ROLE = Language.class.getName();

  /** look for Messages.properties in the clazz package by default */
  public static final String DEFAULT_NAME = "Messages";

  public Language init();

	@SuppressWarnings("unchecked")
  public Language init( Class clazz );
	
	@SuppressWarnings("unchecked")
  public Language init( Class clazz, Locale locale );
	
	public String getMessage( String key )
	;
	
}
