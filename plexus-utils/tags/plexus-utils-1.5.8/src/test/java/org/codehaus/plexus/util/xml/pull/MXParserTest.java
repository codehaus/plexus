package org.codehaus.plexus.util.xml.pull;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.StringReader;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MXParserTest
    extends TestCase
{
    public void testHexadecimalEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&#x41;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "A", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testDecimalEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&#65;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "A", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testPredefinedEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        parser.defineEntityReplacementText( "test", "replacement" );

        String input = "<root>&lt;&gt;&amp;&apos;&quot;</root>";

        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "<>&'\"", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testCustomEntities()
        throws Exception
    {
        MXParser parser = new MXParser();

        String input = "<root>&myentity;</root>";

        parser.setInput( new StringReader( input ) );

        parser.defineEntityReplacementText( "myentity", "replacement" );

        assertEquals( XmlPullParser.START_TAG, parser.next() );

        assertEquals( XmlPullParser.TEXT, parser.next() );

        assertEquals( "replacement", parser.getText() );

        assertEquals( XmlPullParser.END_TAG, parser.next() );
    }

    public void testProcessingInstruction()
        throws Exception
    {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><test>nnn</test>";

        MXParser parser = new MXParser();
        parser.setInput( new StringReader( input ) );

        assertEquals( XmlPullParser.PROCESSING_INSTRUCTION, parser.nextToken() );
        assertEquals( XmlPullParser.START_TAG, parser.nextToken() );
        assertEquals( XmlPullParser.TEXT, parser.nextToken() );
        assertEquals( XmlPullParser.END_TAG, parser.nextToken() );
    }
}
