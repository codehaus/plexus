package org.codehaus.plexus.webdav.jackrabbit;

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

import org.codehaus.plexus.webdav.test.AbstractWebdavServer;

/**
 * JackrabbitWebdavServer 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JackrabbitWebdavServer
    extends AbstractWebdavServer
{
    public static void main( String[] args )
    {
        try
        {
            JackrabbitWebdavServer server = new JackrabbitWebdavServer();
            server.init();
            server.startServer();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    protected String getProviderHint()
    {
        return "jackrabbit";
    }
}
