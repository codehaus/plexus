package org.codehaus.plexus.formica.runtime;

import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.ElementGroup;
import org.codehaus.plexus.formica.Form;
import org.codehaus.plexus.formica.FormValidationResult;
import org.codehaus.plexus.formica.FormicaException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class FormRuntime
{
    Form form;

    Object target;

    Locale locale;

    Map formData;

    FormValidationResult validationResult;

    List elements;

    private List elementGroups;

    private boolean valid;


    public String getTitle()
    {
        return getString( form.getTitleKey() );
    }

    public void populateTarget() throws FormicaException
    {
        form.populateTarget( target, formData );
    }

    public void populateFormData() throws FormicaException
    {
        formData = form.populateFormData( target );
    }

    public Form getForm()
    {
        return form;
    }

    public void setForm( final Form form )
    {
        this.form = form;
    }

    public Object getTarget() throws FormicaException
    {
        if ( target == null )
        {
            final String targetClass = form.getTargetClass();

            if ( targetClass == null )
            {
                throw new FormicaException( "Target class not provided for form '" + form.getId() + "'" );
            }
            try
            {
                //@todo classFor name smells
                final Class clazz = Class.forName( targetClass );

                target = clazz.newInstance();
            }
            catch ( Exception e )
            {
                final String msg = "Cannot instantiate target object of class '"
                    + targetClass
                    + "' for form '" +
                    form.getId() + "'";
                throw new FormicaException( msg, e );
            }
            populateTarget();
        }

        return target;
    }

    public void setTarget( final Object target )
    {
        this.target = target;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( final Locale locale )
    {
        this.locale = locale;
    }

    public FormValidationResult getValidationResult() throws FormicaException
    {
        if ( validationResult == null )
        {
            validationResult = validate();
        }
        return validationResult;
    }

    public Map getFormData() throws FormicaException
    {
        if ( formData == null )
        {
            populateFormData();
        }
        return formData;
    }

    public List getElements()
    {
        if ( elements == null )
        {
            elements = new ArrayList( form.getElements().size() );
            for ( Iterator iterator = form.getElements().iterator(); iterator.hasNext(); )
            {
                final Element element = (Element) iterator.next();

                final ElementRuntime elementRuntime = new ElementRuntime();

                elementRuntime.setElement( element );

                elementRuntime.setFormRuntime( this );

                elements.add( elementRuntime );
            }
        }
        return elements;
    }

    public List getGroups()
    {
        if ( elementGroups == null )
        {
            elementGroups = new ArrayList( form.getElementGroups().size() );

            for ( Iterator iterator = form.getElementGroups().iterator(); iterator.hasNext(); )
            {
                final ElementGroup group = (ElementGroup) iterator.next();

                final ElementGroupRuntime groupRuntime = new ElementGroupRuntime();

                groupRuntime.setGroup( group );

                groupRuntime.setFormRuntime( this );

                elementGroups.add( groupRuntime );
            }
        }
        return elementGroups;
    }


    public void setFormData( final Map formData )
    {
        this.formData = formData;
    }

    public String getString( final String key )
    {
        return form.getFormManager().getString( key, locale );
    }

    /**
     * Validates from
     */
    public FormValidationResult validate()
        throws FormicaException
    {
        final FormValidationResult result = new FormValidationResult( this );

        validateElements( result );

        validateGroups( result );

        valid = result.isValid();

        return result;
    }

    /**
     * Element Validation.
     *
     * @param result
     * @throws FormicaException
     */
    private void validateElements( final FormValidationResult result )
        throws FormicaException
    {

        for ( Iterator i = getElements().iterator(); i.hasNext(); )
        {
            final ElementRuntime elementRuntime = (ElementRuntime) i.next();

            final String data = (String) getFormData().get( elementRuntime.getId() );

            elementRuntime.validate( data, result );
        }
    }


    private void validateGroups( final FormValidationResult result )
        throws FormicaException
    {
        // Group validation
        if ( getGroups().size() == 0 )
        {
            return;
        }
        for ( Iterator iterator1 = elementGroups.iterator(); iterator1.hasNext(); )
        {
            final ElementGroupRuntime group = (ElementGroupRuntime) iterator1.next();

            group.validate( getFormData(), result );
        }

    }


    public ElementRuntime getElement( final String id )
    {
        ElementRuntime retValue = null;

        for ( Iterator iterator = getElements().iterator(); iterator.hasNext(); )
        {
            final ElementRuntime elementRuntime = (ElementRuntime) iterator.next();

            if ( elementRuntime.getId().equals( id ) )
            {
                retValue = elementRuntime;

                break;
            }
        }
        return retValue;
    }

    public ElementGroupRuntime getGroup( final String id )
    {
        ElementGroupRuntime retValue = null;

        for ( Iterator iterator = getGroups().iterator(); iterator.hasNext(); )
        {
            final ElementGroupRuntime group = (ElementGroupRuntime) iterator.next();

            if ( group.getId().equals( id ) )
            {
                retValue = group;

                break;
            }
        }
        return retValue;
    }

    public boolean isValid()
    {
        return valid;
    }


}
