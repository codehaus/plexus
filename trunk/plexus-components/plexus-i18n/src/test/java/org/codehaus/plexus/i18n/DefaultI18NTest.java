package org.codehaus.plexus.i18n;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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

import org.codehaus.plexus.PlexusTestCase;

import java.util.Locale;

/**
 * Tests the API of the
 * {@link org.codehaus.plexus.i18n.I18N}.
 * <br>
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:jason@zenplex.com">Jason van Zyl</a>
 */
public class DefaultI18NTest
    extends PlexusTestCase
{
    public DefaultI18NTest( String name )
    {
        super( name );
    }

    public void testLocalization()
        throws Exception
    {
        I18N i18n = (I18N) lookup( I18N.ROLE );
        String s0 = i18n.getString( null, null, "key1" );
        assertEquals( "Unable to retrieve localized text for locale: default", s0, "value1" );

        String s1 = i18n.getString( null, new Locale( "en", "US" ), "key2" );
        assertEquals( "Unable to retrieve localized text for locale: en-US", s1, "value2" );

        String s2 = i18n.getString( "org.codehaus.plexus.i18n.BarBundle", new Locale( "ko", "KR" ), "key3" );
        assertEquals( "Unable to retrieve localized text for locale: ko-KR", s2, "[ko] value3" );

        String s4 = i18n.getString( "org.codehaus.plexus.i18n.FooBundle", new Locale( "fr" ), "key3" );
        assertEquals( "Unable to retrieve localized text for locale: fr-US", s4, "[fr] value3" );

        String s5 = i18n.getString( "org.codehaus.plexus.i18n.i18n", null, "key1" );
        assertEquals( "value1", s5 );

        // Format methods

        String s6 = i18n.format( "org.codehaus.plexus.i18n.i18n", null, "thanks.message", "jason" );
        assertEquals( s6, "Thanks jason!" );

        String s7 = i18n.format( "org.codehaus.plexus.i18n.i18n", null, "thanks.message1", "jason", "van zyl" );
        assertEquals( s7, "Thanks jason van zyl!" );

        String s8 = i18n.format( "org.codehaus.plexus.i18n.i18n", null, "thanks.message2", new Object[]{ "jason", "van zyl" } );
        assertEquals( s8, "Thanks jason van zyl!" );
    }
}
