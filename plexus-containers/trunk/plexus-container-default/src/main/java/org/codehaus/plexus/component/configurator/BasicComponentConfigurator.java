package org.codehaus.plexus.component.configurator;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
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

import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public class BasicComponentConfigurator
    extends AbstractComponentConfigurator
    implements Contextualizable
{
    private PlexusContainer plexusContainer;

    // TODO: configured as a component
    private ConverterLookup converterLookup;
    
    /**
     * <b>If construct with this without the container
     *  ObjectWithFieldsConverter#getObjectForImplementationHint won't work</b> 
     */
    public BasicComponentConfigurator()
    {
        this.converterLookup = new DefaultConverterLookup( null ); 
    }

    public void contextualize( Context context )
        throws ContextException
    {
        this.plexusContainer = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
        this.converterLookup = new DefaultConverterLookup( plexusContainer );

    }

    public BasicComponentConfigurator( PlexusContainer plexusContainer )
    {
        this.plexusContainer = plexusContainer;
        this.converterLookup = new DefaultConverterLookup( plexusContainer );
    }

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        // ----------------------------------------------------------------------
        // We should probably take into consideration the realm that the component
        // came from in order to load the correct classes.
        // ----------------------------------------------------------------------

        converterLookup.registerConverter( new ClassRealmConverter( containerRealm ) );

        ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter( this.plexusContainer );

        converter.processConfiguration( converterLookup, component, containerRealm, configuration, expressionEvaluator,
                                        listener );
    }

}
