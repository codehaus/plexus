/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.model.AbstractMarmaladeTag;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.plexus.cling.cli.MultiArgOption;
import org.codehaus.plexus.cling.cli.NoArgOption;
import org.codehaus.plexus.cling.cli.Option;
import org.codehaus.plexus.cling.cli.OptionFormat;
import org.codehaus.plexus.cling.cli.SingleArgOption;

/**
 * @author jdcasey
 */
public class OptionTag
    extends AbstractMarmaladeTag
{

    private Character shortName;

    private String longName;

    private String objectProperty;

    private boolean required = false;

    private String type;

    private boolean isArgumentMultiple = false;

    private String description;

    protected void doExecute( MarmaladeExecutionContext context ) throws MarmaladeExecutionException
    {
        processChildren( context );

        Option option = null;
        if ( type != null )
        {
            OptionFormat format = OptionFormat.valueOf(type);
            if ( isArgumentMultiple )
            {
                option = new MultiArgOption(required, shortName, longName, format, ",", description, objectProperty);
            }
            else
            {
                option = new SingleArgOption(required, shortName, longName, format, description, objectProperty);
            }
        }
        else
        {
            if ( isArgumentMultiple )
            {
                throw new MarmaladeExecutionException( "option cannot have empty type and accept multiple args" );
            }
            else
            {
                option = new NoArgOption( required, shortName, longName, description, objectProperty );
            }
        }
        
        OptionSetTag parent = (OptionSetTag)requireParent(OptionSetTag.class);
        parent.addOption(option);
    }

    public void setLongName( String longName )
    {
        this.longName = longName;
    }

    public void setObjectProperty( String objectProperty )
    {
        this.objectProperty = objectProperty;
    }

    public void setRequired( boolean required )
    {
        this.required = required;
    }

    public void setShortName( Character shortName )
    {
        this.shortName = shortName;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setArgumentMultiple( boolean isArgumentMultiple )
    {
        this.isArgumentMultiple = isArgumentMultiple;
    }
}