package org.codehaus.plexus.formica.population;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.formica.population.transformer.Transformer;
import org.codehaus.plexus.formica.population.transformer.TransformerNotFoundException;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public abstract class AbstractPopulator
    extends AbstractLogEnabled
    implements Populator
{
    protected final static String PASSIVE_EXPRESSION = "passive";

    protected Map transformers;

    protected Transformer getTransformer( String id )
        throws TransformerNotFoundException
    {
        Transformer transformer = (Transformer) transformers.get( id );

        if ( transformer == null )
        {
            throw new TransformerNotFoundException( "Transformer with the id = " + id + " cannot be found." );
        }

        return transformer;
    }
}
