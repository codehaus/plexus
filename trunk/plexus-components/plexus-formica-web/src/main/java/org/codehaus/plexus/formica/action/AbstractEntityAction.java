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

import org.codehaus.plexus.action.AbstractAction;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractEntityAction
    extends AbstractAction
    implements Contextualizable
{
    protected FormManager fm;

    protected PlexusContainer container;

    protected abstract void uponSuccessfulValidation( Form form, String entityId, Map map )
        throws Exception;

    //TODO: we probably want the action interface to return an Object. For example after adding
    // an entity I might want the Object reference.
    public void execute( Map map )
        throws Exception
    {
        fm = (FormManager) lookup( FormManager.ROLE );

        String entityId = (String) map.get( "id" );

        String formId = (String) map.get( "formId" );

        FormValidationResult fvr = fm.validate( formId, map );

        // ----------------------------------------------------------------------
        // If validation is sucessful then we want to allow the add/update/delete
        // operation to continue.
        // ----------------------------------------------------------------------

        if ( fvr.valid() )
        {
            uponSuccessfulValidation( fm.getForm( formId ), entityId, map );
        }
    }

    protected Object getApplicationComponent( Form form )
        throws ComponentLookupException
    {
        return container.lookup( form.getSourceRole() );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }
}
