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

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.util.dag.DAG;

import java.util.Iterator;
import java.util.List;



/**
 *
 *
 * @author <a href="mailto:michal.maczka@dimatics.com">Michal Maczka</a>
 *
 * @version $Id$
 */
public class DefaultCompositionResolver implements CompositionResolver
{
    private DAG dag = new DAG();
    

    public void addComponentDescriptor( final ComponentDescriptor componentDescriptor ) throws CompositionException
    {
        final String componentKey = componentDescriptor.getComponentKey();

        final List requirements = componentDescriptor.getRequirements();

        for ( final Iterator iterator = requirements.iterator(); iterator.hasNext(); )
        {
            final ComponentRequirement requirement = ( ComponentRequirement ) iterator.next();

            try
            {
                dag.addEdge( componentKey, requirement.getRole() );
            }
            catch( Exception e)
            {



            }
        }



    }

    /**
     * 
     * @see org.codehaus.plexus.component.composition.CompositionResolver#getRequirements(java.lang.String)
     */
    public List getRequirements( final String componentKey )
    {
        return dag.getChildLabels( componentKey );        
    }
    
    
    /**
     * @see org.codehaus.plexus.component.composition.CompositionResolver#findRequirements(java.lang.String)
     */
    public List findRequirements( final String componentKey )
    {        
        return dag.getParentLabels( componentKey );   
    }
}
