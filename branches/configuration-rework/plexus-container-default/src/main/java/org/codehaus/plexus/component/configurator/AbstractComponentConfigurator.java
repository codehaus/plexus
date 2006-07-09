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

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.converters.lookup.DefaultConverterLookup;
import org.codehaus.plexus.component.configurator.expression.DefaultExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.repository.ComponentConfigurationFieldDescriptor;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * @author <a href="mailto:brett@codehaus.org">Brett Porter</a>
 * @version $Id$
 */
public abstract class AbstractComponentConfigurator
    implements ComponentConfigurator
{
    // TODO: configured as a component
    protected ConverterLookup converterLookup = new DefaultConverterLookup();

    public void configureComponent( Object component, PlexusConfiguration configuration, ClassRealm containerRealm )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, new DefaultExpressionEvaluator(), containerRealm );
    }

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, expressionEvaluator, containerRealm, null );
    }

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                    ConfigurationListener listener )
        throws ComponentConfigurationException
    {
        configureComponent( component, configuration, expressionEvaluator, containerRealm, listener, null);
    }

    public void configureComponent( Object component, PlexusConfiguration configuration,
                                    ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm,
                                    ConfigurationListener listener,
                                    ComponentConfigurationFieldDescriptor fieldDescriptor )
        throws ComponentConfigurationException
    {
        if ( fieldDescriptor == null )
        {
            throw new ComponentConfigurationException( "Missing required component field descriptor" );
        }

        // TODO: here so extended classes without the method continue to work. should be removed
        // this won't hit the method above going into a loop - instead, it will hit the overridden one
        configureComponent( component, configuration, expressionEvaluator, containerRealm );
    }
}
