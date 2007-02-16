package org.codehaus.plexus.formica.population.transformer;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface Transformer
{
    /**
     * Combine multiple elements of form into a single piece of data.
     *
     * @param formData
     * @return
     */
    String combine( Map formData );

    /**
     * Decompose a single piece of data into multiple pieces of data whrere
     * each piece will populate a different form element. 
     * @param data
     * @return
     */
    Map decompose( String data );
}
