package org.codehaus.plexus.i18n;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.codehaus.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact codehaus@codehaus.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.codehaus.org/>.
 */

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * <p>Provides i18n functionality using the interface provided
 * by <code>ResourceBundle</code>, plus leverages a "search path"
 * style traversal of the <code>ResourceBundle</code> objects named by
 * the <code>locale.default.bundles</code> to discover a value for a
 * given key.</p>
 *
 * <p>It is suggested that one handle
 * <a href="http://www.math.fu-berlin.de/~rene/www/java/tutorial/i18n/message/messageFormat.html">dealing with concatenated messages</a>
 * using <code>MessageFormat</code> and properties files.</p>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:leonardr@collab.net">Leonard Richardson</a>
 * @version $Id$
 */
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

    void setBundle( String defaultBundle );

    String format( String key, Object arg1 );

    String format( String key, Object arg1, Object arg2 );

    String format( String bundleName, Locale locale, String key, Object arg1 );

    String format( String bundleName, Locale locale, String key, Object arg1, Object arg2 );

    String format( String bundleName, Locale locale, String key, Object[] args );
}
