package org.codehaus.plexus.formica.action;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import ognl.Ognl;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.summit.rundata.RunData;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class DeleteEntity
    extends AbstractEntityAction
{
    public void execute( Map map )
        throws Exception
    {
        if ( map.containsKey( "cancel" ) )
        {
            return;
        }

        String entityId = (String) map.get( ID );

        String formId = (String) map.get( FORM_ID );

        Form form = formManager.getForm( formId );

        uponSuccessfulValidation( form, entityId, map );
    }

    protected void uponSuccessfulValidation( Form form, String entityId, Map map )
        throws Exception
    {
        Map m = new HashMap();

        m.put( ID, validateEntityId( entityId ) );

        Ognl.getValue( validateExpression( validateExpression( form.getDelete().getExpression() ) ), m, getApplicationComponent( form ) );
    }
}
