package org.codehaus.plexus.formica;

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

import org.codehaus.plexus.configuration.xml.xstream.PlexusXStream;
import org.codehaus.plexus.formica.population.Populator;
import org.codehaus.plexus.formica.population.TargetPopulationException;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.formica.validation.Validator;
import org.codehaus.plexus.formica.validation.group.GroupValidator;
import org.codehaus.plexus.formica.validation.manager.ValidatorManager;
import org.codehaus.plexus.formica.validation.manager.ValidatorNotFoundException;
import org.codehaus.plexus.i18n.I18N;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class DefaultFormManager
    extends AbstractLogEnabled
    implements FormManager, Initializable
{
    private Map formMap = new HashMap();

    private Populator populator;

    private I18N i18n;

    private ValidatorManager validatorManager;

    private Map groupValidatorMap;

    private List forms;

    private String formsDirectory;

    private boolean devMode = true;

    public DefaultFormManager()
    {
    }

    public void addForm( Form form )
    {
        forms.add( form );

        formMap.put( form.getId(), form );
    }

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public Validator getValidator( String validatorId )
        throws ValidatorNotFoundException
    {
        return validatorManager.getValidator( validatorId );
    }

    public GroupValidator getGroupValidator( String groupValidatorId )
    {
        if ( groupValidatorMap.containsKey( groupValidatorId ) )
        {
            return (GroupValidator) groupValidatorMap.get( groupValidatorId );
        }
        else
        {
            return null;
        }
    }

    public Form getForm( String formId )
        throws FormNotFoundException
    {
        if ( devMode )
        {
            try
            {
                loadForms();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        Form form = (Form) formMap.get( formId );

        if ( form == null )
        {
            throw new FormNotFoundException( "The form with the id " + formId + " cannot be found." );
        }

        return form;
    }


    public void initialize()
        throws Exception
    {
        loadForms();
    }

    public void loadForms()
        throws Exception
    {
        // This functionality of grabbing a list of files which provide configuration
        // information should be built into plexus generally, but this will do for now.

        forms = new ArrayList();

        PlexusXStream xstream = new PlexusXStream();

        List files = FileUtils.getFiles( new File( formsDirectory ), "**/*.xml", null );

        for ( Iterator i = files.iterator(); i.hasNext(); )
        {
            File f = (File) i.next();

            forms.add( xstream.build( new FileReader( f ), Form.class ) );
        }

        for ( Iterator iterator = forms.iterator(); iterator.hasNext(); )
        {
            Form form = (Form) iterator.next();

            formMap.put( form.getId(), form );
        }
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------


    public FormValidationResult validate( String formId, Map data )
        throws FormicaException, FormNotFoundException, ValidatorNotFoundException
    {
        return validate( getForm( formId ), data );
    }

    public FormValidationResult validate( Form form, Map data )
        throws FormicaException, ValidatorNotFoundException
    {
        FormValidationResult result = new FormValidationResult();

        for ( Iterator i = form.getElements().iterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();

            Validator validator = getValidator( element.getValidator() );

            String elementData = (String) data.get( element.getId() );

            boolean valid = validator.validate( elementData );

            String errorMessage = null;

            if ( !valid )
            {
                errorMessage = i18n.getString( element.getErrorMessageKey() );
            }

            result.addElementValidationResult( element.getId(), valid, errorMessage );
        }

        return result;
    }

    public void populate( Form form, Map data, Object target )
        throws TargetPopulationException
    {
        populator.populate( form, data, target );
    }
}
