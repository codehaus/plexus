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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.action.AbstractAction;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.FormManager;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.codehaus.plexus.summit.SummitConstants;
import org.codehaus.plexus.summit.rundata.RunData;
import org.codehaus.plexus.summit.view.ViewContext;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractEntityAction
    extends AbstractAction
    implements EntityAction, Contextualizable
{
    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public static final String ID = "id";

    public static final String FORM_ID = "formId";

    public static final String MODE = "mode";

    public static final String ENTITY = "entity";

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected FormManager formManager;

    protected PlexusContainer container;

    abstract void uponSuccessfulValidation( Form form, String entityId, Map map )
        throws Exception;

    protected String getSuccessTarget( Form form )
    {
        return form.getAdd().getView();
    }

    protected String getFailureTarget( Form form )
    {
        return "Add.vm";
    }

    public void execute( Map map )
        throws Exception
    {
        String entityId = (String) map.get( ID );

        String formId = (String) map.get( FORM_ID );

        FormValidationResult fvr = formManager.validate( formId, map );

        Form form = formManager.getForm( formId );

        // ----------------------------------------------------------------------
        // If validation is sucessful then we want to allow the add/update/delete
        // operation to continue. Otherwise we need to pass back the form
        // validation result so that the information can be displayed to
        // the user.
        // ----------------------------------------------------------------------

        //TODO need a way to set views easily

        RunData data = (RunData) map.get( "data" );

        if ( fvr.valid() )
        {
            uponSuccessfulValidation( form, entityId, map );

            data.setTarget( getSuccessTarget( form ) );
        }
        else
        {
            // ----------------------------------------------------------------------
            // If we have a validation error then we need to make sure the target
            // goes back to the form the user was just working on with the
            // validation error high lighted.
            // ----------------------------------------------------------------------

            data.setTarget( getFailureTarget( form ) );

            ViewContext vc = (ViewContext) data.getMap().get( SummitConstants.VIEW_CONTEXT );

            vc.put( "fid", formId );

            vc.put( "fvr", fvr );
        }
    }

    protected Object getApplicationComponent( Form form )
        throws ComponentLookupException
    {
        return container.lookup( form.getSourceRole() );
    }

    // ----------------------------------------------------------------------
    // Validations
    // ----------------------------------------------------------------------

    protected String validateExpression( String expression )
    {
        if ( expression == null )
        {
            throw new IllegalStateException( "The entity expression cannot be null." );
        }

        return expression;
    }

    protected String validateEntityId( String expression )
    {
        if ( expression == null )
        {
            throw new IllegalStateException( "The entity ID cannot be null." );
        }

        return expression;
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
