package org.codehaus.plexus.cache.test;

/*
 * Copyright 2001-2007 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;

/**
 * EnglishNumberFormatTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class EnglishNumberFormatTest
    extends TestCase
{
    public void testConversion()
    {
        EnglishNumberFormat fmt = new EnglishNumberFormat();

        assertEquals( "zero", fmt.toText( 0 ) );
        assertEquals( "one", fmt.toText( 1 ) );
        assertEquals( "fourty two", fmt.toText( 42 ) );
        assertEquals( "one hundred", fmt.toText( 100 ) );
        assertEquals( "one thousand twenty four", fmt.toText( 1024 ) );
        assertEquals( "one million eight hundred twenty two", fmt.toText( 1000822 ) );
        assertEquals( "sixteen million four hundred twenty thousand eight hundred sixty one", fmt.toText( 16420861 ) );
    }
}
