package org.codehaus.plexus.formica.runtime;

import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.FormValidationResult;
import org.codehaus.plexus.formica.FormicaException;
import org.codehaus.plexus.formica.validation.Validator;


/**
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ElementRuntime
{
    private Element element;

    private FormRuntime formRuntime;

    private String data;

    private boolean valid;


    public String getLabel()
    {
        return formRuntime.getString( element.getLabelKey() );
    }

    public String getMessage()
    {
        return formRuntime.getString( element.getMessageKey() );
    }

    public String getErrorMessage()
    {
        return formRuntime.getString( element.getErrorMessageKey() );
    }

    public String getData()
    {
        return data;
    }


    public boolean isValid()
    {
        return valid;
    }


    public void setElement( final Element element )
    {
        this.element = element;
    }

    public void setFormRuntime( final FormRuntime formRuntime )
    {
        this.formRuntime = formRuntime;
    }

    public void setData( final String data )
    {
        this.data = data;
    }

    public void setValid( final boolean valid )
    {
        this.valid = valid;
    }

    public void validate( final String data, final FormValidationResult result ) throws FormicaException
    {
        final Validator validator = element.getValidatorObject();
        if ( validator != null )
        {
            valid = validator.validate( data );
            result.addElementValidationResult( this, valid );
        }
    }

    public String getId()
    {
        return element.getId();
    }

}
