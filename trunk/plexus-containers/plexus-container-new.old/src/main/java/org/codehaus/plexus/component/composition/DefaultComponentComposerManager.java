package org.codehaus.plexus.component.composition;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.internal.util.StringUtils;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * @author <a href="mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class DefaultComponentComposerManager implements ComponentComposerManager
{
    private Map composerMap = new HashMap();

    private List componentComposers;

    private String defaultComponentComposerId = "field";

    public void assembleComponent( final Object component,
                                   final ComponentDescriptor componentDescriptor,
                                   final PlexusContainer container )
            throws CompositionException, UndefinedComponentComposerException, ComponentLookupException
    {

        if ( componentDescriptor.getRequirements().size() == 0 )
        {
            //nothing to do
            return;
        }

        String componentComposerId = componentDescriptor.getComponentComposer();

        if ( StringUtils.isEmpty( componentComposerId ) )
        {
            componentComposerId = defaultComponentComposerId ;
        }

        final ComponentComposer componentComposer = getComponentComposer( componentComposerId );

        componentComposer.assembleComponent( component, componentDescriptor, container );

        // @todo: michal: we need to build the graph of component dependencies
        // and detect cycles.  Not sure exactly when and how it should happen
        //assembleComponents( descriptors, container );
    }

    protected ComponentComposer getComponentComposer( final String id ) throws UndefinedComponentComposerException
    {
        ComponentComposer retValue = null;

        if ( composerMap.containsKey( id ) )
        {
            retValue = ( ComponentComposer ) composerMap.get( id );
        }
        else
        {
            retValue = findComponentComposer( id );
        }

        if ( retValue == null )
        {
            throw new UndefinedComponentComposerException( "Specified component composer cannot be found: " + id );
        }

        return retValue;
    }

    private ComponentComposer findComponentComposer( final String id )
    {
        ComponentComposer retValue = null;

        for ( Iterator iterator = componentComposers.iterator(); iterator.hasNext(); )
        {
            final ComponentComposer componentComposer = ( ComponentComposer ) iterator.next();

            if ( componentComposer.getId().equals( id ) )
            {
                retValue = componentComposer;

                break;
            }
        }

        return retValue;
    }
}
