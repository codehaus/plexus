package org.codehaus.plexus.appserver.management;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.codehaus.plexus.appserver.ApplicationServer;
import org.codehaus.plexus.appserver.ApplicationServerException;

import java.io.File;

/**
 * @author <a href="mailto:evenisse@apache.org">Emmanuel Venisse</a>
 * @version $Id$
 * @plexus.component role="org.codehaus.plexus.appserver.management.MBean" role-hint="applicationServer"
 */
public class DefaultApplicationServerMBean
    extends AbstractMBean
    implements ApplicationServerMBean
{
    /**
     * @plexus.requirement
     */
    ApplicationServer appserver;

    public String getName()
    {
        return "ApplicationServer";
    }

    public void deploy( String id, File location )
        throws ApplicationServerException
    {
        appserver.deploy( id, location );
    }

    public void redeploy( String id )
        throws ApplicationServerException
    {
        appserver.redeploy( id );
    }

    public void undeploy( String id )
        throws ApplicationServerException
    {
        appserver.undeploy( id );
    }
}
