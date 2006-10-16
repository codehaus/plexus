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
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 * @plexus.component lifecycle-handler="plexus-configurable"
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

        if ( form.getElements() != null )
        {
            validateElements( form.getElements(), data, result );
        }

        if ( form.getElementGroups() != null )
        {
            validateElementGroups( form, data, result );
        }

        return result;
    }

    private void validateElementGroups( Form form, Map data, FormValidationResult result )
        throws FormicaException, ValidatorNotFoundException
    {
        for ( Iterator i = form.getElementGroups().iterator(); i.hasNext(); )
        {
            ElementGroup group = (ElementGroup) i.next();

            GroupValidator validator = getGroupValidator( group.getValidator() );

            boolean valid = validator.validate( group, data );

            String errorMessage = null;

            if ( !valid )
            {
                if ( group.getErrorMessageKey() == null )
                {
                    errorMessage = "There is no i18n key defined for this group element with id = " + group.getId();
                }
                else
                {
                    errorMessage = i18n.getString( group.getErrorMessageKey() );
                }
            }

            result.addElementValidationResult( group.getId(), valid, errorMessage );

            validateElements( group.getElements(), data, result );
        }
    }

    private void validateElements( List elements, Map data, FormValidationResult result )
        throws ValidatorNotFoundException, FormicaException
    {
        for ( Iterator i = elements.iterator(); i.hasNext(); )
        {
            Element element = (Element) i.next();

            // ----------------------------------------------------------------------
            // Populate the default value if our data for the element is null and
            // there is a default value available.
            // ----------------------------------------------------------------------

            if ( data.get( element.getId() ) == null && element.getDefaultValue() != null )
            {
                data.put( element.getId(), element.getDefaultValue() );
            }

            List validators = element.getValidators();

            if ( validators != null )
            {
                for ( Iterator j = validators.iterator(); j.hasNext(); )
                {
                    org.codehaus.plexus.formica.Validator v = (org.codehaus.plexus.formica.Validator) j.next();

                    Validator validator = getValidator( v.getId() );

                    String elementData = (String) data.get( element.getId() );

                    boolean valid = validator.validate( elementData );

                    if ( valid )
                    {
                        result.addElementValidationResult( element.getId(), valid, null );
                    }
                    else
                    {
                        // ----------------------------------------------------------------------
                        // On the first sign of error fail fast and return the message to the
                        // user so that they can correct the first makes and then an attempt
                        // can be made to validate again.
                        // ----------------------------------------------------------------------

                        String errorMessage = null;

                        if ( v.getErrorMessageKey() == null )
                        {
                            errorMessage = "There is no i18n key defined for element with id = " + element.getId();
                        }
                        else
                        {
                            errorMessage = i18n.getString( v.getErrorMessageKey() );
                        }

                        result.addElementValidationResult( element.getId(), valid, errorMessage );

                        break;
                    }
                }
            }
        }
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
        throws InitializationException
    {
        formMap = new HashMap();

        for ( Iterator i = forms.iterator(); i.hasNext(); )
        {
            Form form = (Form) i.next();

            formMap.put( form.getId(), form );
        }

        // ----------------------------------------------------------------------
        // Walk through the forms to perform any inheritance that may be
        // required.
        // ----------------------------------------------------------------------

        // parent <--- child <--- grandchild

        for ( Iterator i = forms.iterator(); i.hasNext(); )
        {
            Form form = (Form) i.next();

            if ( form.getExtend() != null )
            {
                LinkedList lineage = new LinkedList();

                assembleLineage( form, lineage );

                for ( int j = 0; j < lineage.size() - 1; j++ )
                {
                    assembleInheritance( (Form) lineage.get( j ), (Form) lineage.get( j + 1 ) );
                }

                // ----------------------------------------------------------------------
                // This is to make sure a form in an inheritance chain already processed
                // is not processed again.
                // ----------------------------------------------------------------------

                form.setExtend( null );
            }
        }
    }

    private void assembleLineage( Form form, LinkedList lineage )
    {
        lineage.addFirst( form );

        if ( form.getExtend() != null )
        {
            assembleLineage( (Form) formMap.get( form.getExtend() ), lineage );
        }
    }

    private void assembleInheritance( Form parent, Form child )
    {
        if ( child.getKeyExpression() == null )
        {
            child.setKeyExpression( parent.getKeyExpression() );
        }

        if ( child.getLookupExpression() == null )
        {
            child.setLookupExpression( parent.getLookupExpression() );
        }

        if ( child.getSummaryCollectionExpression() == null )
        {
            child.setSummaryCollectionExpression( parent.getSummaryCollectionExpression() );
        }

        if ( child.getSourceRole() == null )
        {
            child.setSourceRole( parent.getSourceRole() );
        }

        // ----------------------------------------------------------------------
        // Add
        // ----------------------------------------------------------------------

        if ( child.getAdd() == null )
        {
            child.setAdd( parent.getAdd() );
        }

        // ----------------------------------------------------------------------
        // Update
        // ----------------------------------------------------------------------

        if ( child.getUpdate() == null )
        {
            child.setUpdate( parent.getUpdate() );
        }

        // ----------------------------------------------------------------------
        // View
        // ----------------------------------------------------------------------

        if ( child.getView() == null )
        {
            child.setView( parent.getView() );
        }

        // ----------------------------------------------------------------------
        // Delete
        // ----------------------------------------------------------------------

        if ( child.getDelete() == null )
        {
            child.setDelete( parent.getDelete() );
        }

        // ----------------------------------------------------------------------
        // Form elements
        //
        // If a child defines an element with an id that is also defined in the
        // parent then the child wins.
        // ----------------------------------------------------------------------

        for ( Iterator i = parent.getElements().iterator(); i.hasNext(); )
        {
            Element parentElement = (Element) i.next();

            if ( child.getElement( parentElement.getId() ) == null )
            {
                child.addElement( parentElement );
            }
        }
    }
}
