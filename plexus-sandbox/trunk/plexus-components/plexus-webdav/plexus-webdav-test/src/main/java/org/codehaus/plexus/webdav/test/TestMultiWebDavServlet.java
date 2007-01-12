package org.codehaus.plexus.webdav.test;

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

import org.codehaus.plexus.webdav.DavServerException;
import org.codehaus.plexus.webdav.servlet.multiplexed.MultiplexedWebDavServlet;

import java.io.File;

import javax.servlet.ServletConfig;

/**
 * TestServlet 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class TestMultiWebDavServlet
    extends MultiplexedWebDavServlet
{
    public void initServers( ServletConfig config )
        throws DavServerException
    {
        String rootSandbox = config.getInitParameter( "root.sandbox" );
        String rootSnapshots = config.getInitParameter( "root.snapshots" );

        createServer( "sandbox", new File( rootSandbox ), config );
        createServer( "snapshots", new File( rootSnapshots ), config );
    }
}