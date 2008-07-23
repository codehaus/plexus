package org.codehaus.plexus.summit.pipeline.valve;

/*
 * Copyright 2004-2005 The Apache Software Foundation.
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

import org.codehaus.plexus.action.Action;
import org.codehaus.plexus.action.ActionManager;
import org.codehaus.plexus.action.ActionNotFoundException;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @plexus.component
 *    role="org.codehaus.plexus.summit.pipeline.valve.Valve"
 *    role-hint="org.codehaus.plexus.summit.pipeline.valve.ActionValve"
 */
public class ActionValve
    extends AbstractValve
{
    /**
     * @plexus.requirement
     */
    private ActionManager actionManager;

    public void invoke( RunData data )
        throws IOException, ValveInvocationException
    {
        String actionId = data.getParameters().getString( SummitConstants.ACTION, "" );

        if ( !actionId.equals( "" ) )
        {
            Action action;

            try
            {
                action = actionManager.lookup( actionId.trim() );

                Map m = createContext( data );

                m.put( SummitConstants.RUNDATA, data );

                action.execute( m );

                // ----------------------------------------------------------------------
                // Check to see if there are any result messages in the context that need
                // to be displayed to the user.
                // ----------------------------------------------------------------------

                if ( action.hasResultMessages( m ) )
                {
                    data.setResultMessages( action.getResultMessages( m ) );
                }
            }
            catch ( Exception e )
            {
                data.setError( e );
            }
        }
    }

    protected Map createContext( RunData data )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // The parameter map in the request consists of an array of values for
        // the given key so this is why this is being done.
        // ----------------------------------------------------------------------

        Map m = new HashMap();

        for ( Iterator i = data.getParameters().keys(); i.hasNext(); )
        {
            String key = (String) i.next();

            String value = null;

            FileItem fileItem = data.getParameters().getFileItem( key );

            if ( fileItem != null )
            {
                if ( !fileItem.isFormField() )
                {
                    File uploadedFile = File.createTempFile( "summit-", ".tmp" );

                    fileItem.write( uploadedFile );

                    value = uploadedFile.toURL().toExternalForm();
                }
                else
                {
                    value = fileItem.getString();
                }
            }
            else
            {
                value = data.getParameters().get( key );
            }

            m.put( key, value );

        }
        return m;
    }
}
