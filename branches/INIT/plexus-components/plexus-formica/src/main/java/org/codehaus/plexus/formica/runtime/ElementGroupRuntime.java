package org.codehaus.plexus.formica.runtime;

import org.codehaus.plexus.formica.ElementGroup;
import org.codehaus.plexus.formica.FormValidationResult;
import org.codehaus.plexus.formica.FormicaException;
import org.codehaus.plexus.formica.validation.group.GroupValidator;

import java.util.Map;


/**
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class ElementGroupRuntime
{
    private FormRuntime formRuntime;
    private boolean valid;
    private ElementGroup group;


    public void setGroup( final ElementGroup group )
    {
        this.group = group;
    }

    public FormRuntime getFormRuntime()
    {
        return formRuntime;
    }

    public void setFormRuntime( final FormRuntime formRuntime )
    {
        this.formRuntime = formRuntime;
    }

    public boolean isValid()
    {
        return valid;
    }

    public void setValid( final boolean valid )
    {
        this.valid = valid;
    }

    public String getId()
    {
        return group.getId();
    }

    public GroupValidator getValidator()
    {
        return group.getValidatorObject();
    }

    public ElementGroup getGroup()
    {
        return group;
    }

    public void validate( final Map formData, final FormValidationResult result ) throws FormicaException
    {
        final GroupValidator validator = getValidator();
        if ( validator != null )
        {
            System.out.println( "validator = " + validator + " for group " + group.getId() );
            valid = validator.validate( group, formData );
            result.addGroupValidationResult( this, valid );
        }
    }

    public String getErrorMessage()
    {
        return formRuntime.getString( group.getErrorMessageKey() );
    }

}
