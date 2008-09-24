package org.codehaus.plexus.spring;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * DOM2UtilsTest
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DOM2UtilsTest
    extends TestCase
{
    public void testGetTextContext()
        throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( false );
        dbf.setValidating( false );
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse( new File( "src/test/resources/testPlexusLifecycleSupport.xml" ) );

        Element root = doc.getDocumentElement();
        String actual = DOM2Utils.getTextContext( root );
        assertNotNull( "Should have content", actual );
        actual = actual.trim();

        String expected = "org.codehaus.plexus.spring.PlexusBean\n" + "      default\n"
            + "      org.codehaus.plexus.spring.PlexusBeanImpl";

        assertEquals( expected, actual );
    }
}
