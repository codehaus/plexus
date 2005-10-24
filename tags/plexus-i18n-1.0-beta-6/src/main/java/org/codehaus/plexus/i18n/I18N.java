package org.codehaus.plexus.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public interface I18N
{
    public static String ROLE = I18N.class.getName();

    String ACCEPT_LANGUAGE = "Accept-Language";

    String getDefaultLanguage();

    String getDefaultCountry();

    String getDefaultBundleName();

    String[] getBundleNames();

    ResourceBundle getBundle();

    ResourceBundle getBundle( String bundleName );

    ResourceBundle getBundle( String bundleName, String languageHeader );

    ResourceBundle getBundle( String bundleName, Locale locale );

    Locale getLocale( String languageHeader );

    String getString( String key );

    String getString( String key, Locale locale );

    String getString( String bundleName, Locale locale, String key );

    String format( String key, Object arg1 );

    String format( String key, Object arg1, Object arg2 );

    String format( String bundleName, Locale locale, String key, Object arg1 );

    String format( String bundleName, Locale locale, String key, Object arg1, Object arg2 );

    String format( String bundleName, Locale locale, String key, Object[] args );
}
