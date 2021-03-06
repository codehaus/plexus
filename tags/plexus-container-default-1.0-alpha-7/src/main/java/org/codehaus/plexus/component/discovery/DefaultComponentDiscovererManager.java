package org.codehaus.plexus.component.discovery;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultComponentDiscovererManager
    implements ComponentDiscovererManager
{
    private List componentDiscoverers;

    private List componentDiscoveryListeners;

    private List listeners;

    public List getComponentDiscoverers()
    {
        return componentDiscoverers;
    }

    public void registerComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( componentDiscoveryListeners == null )
        {
            componentDiscoveryListeners = new ArrayList();
        }

        componentDiscoveryListeners.add( listener );
    }

    public void removeComponentDiscoveryListener( ComponentDiscoveryListener listener )
    {
        if ( componentDiscoveryListeners != null )
        {
            componentDiscoveryListeners.remove( listener );
        }
    }

    public void fireComponentDiscoveryEvent( ComponentDiscoveryEvent event )
    {
        if ( componentDiscoveryListeners != null )
        {
            for ( Iterator i = componentDiscoveryListeners.iterator(); i.hasNext(); )
            {
                ComponentDiscoveryListener listener = (ComponentDiscoveryListener) i.next();

                listener.componentDiscovered( event );
            }
        }
    }

    public List getListenerDescriptors()
    {
        return listeners;
    }

    // ----------------------------------------------------------------------
    // Lifecylce Management
    // ----------------------------------------------------------------------

    public void initialize()
    {
        for ( Iterator i = componentDiscoverers.iterator(); i.hasNext(); )
        {
            ComponentDiscoverer componentDiscoverer = (ComponentDiscoverer) i.next();

            componentDiscoverer.setManager( this );
        }
    }
}
