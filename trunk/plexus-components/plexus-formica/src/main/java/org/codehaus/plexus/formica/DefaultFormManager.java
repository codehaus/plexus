package org.codehaus.plexus.formica;

/*
 * Copyright (c) 2004, Codehaus.org
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
    private Map formMap;

    private Populator populator;

    private I18N i18n;

    private ValidatorManager validatorManager;

    private Map groupValidatorMap;

    private List forms;

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
        Form form = (Form) formMap.get( formId );

        if ( form == null )
        {
            throw new FormNotFoundException( "The form with the id " + formId + " cannot be found." );
        }

        return form;
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

    // ----------------------------------------------------------------------
    // Population
    // ----------------------------------------------------------------------

    public void populate( Form form, Map data, Object target )
        throws TargetPopulationException
    {
        populator.populate( form, data, target );
    }

    public Object populate( String formId, Map data, ClassLoader classLoader )
        throws TargetPopulationException, FormNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        Form form = getForm( formId );

        Object target = classLoader.loadClass( form.getTargetClass() ).newInstance();

        populator.populate( form, data, target );

        return target;
    }

    public void populate( String formId, Map data, Object target )
        throws TargetPopulationException, FormNotFoundException
    {
        populator.populate( getForm( formId ), data, target );
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        formMap = new HashMap();

        for ( Iterator i = forms.iterator(); i.hasNext(); )
        {
            Form form = (Form) i.next();

            formMap.put( form.getId(), form );
        }
    }
}
