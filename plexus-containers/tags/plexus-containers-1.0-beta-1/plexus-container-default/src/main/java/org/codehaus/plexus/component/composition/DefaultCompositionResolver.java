package org.codehaus.plexus.component.composition;

/*
 * Copyright 2001-2006 Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.ComponentRequirementList;
import org.codehaus.plexus.util.dag.CycleDetectedException;
import org.codehaus.plexus.util.dag.DAG;

import java.util.Iterator;
import java.util.List;


/**
 * @author Jason van Zyl
 * @author <a href="mailto:michal.maczka@dimatics.com">Michal Maczka</a>
 * @version $Id$
 */
public class DefaultCompositionResolver
    implements CompositionResolver
{
    private DAG dag = new DAG();

    public void addComponentDescriptor( ComponentDescriptor componentDescriptor )
        throws CompositionException
    {
        String key = getDAGKey( componentDescriptor.getRole(), componentDescriptor.getRoleHint() );

        List requirements = componentDescriptor.getRequirements();

        for ( Iterator iterator = requirements.iterator(); iterator.hasNext(); )
        {
            ComponentRequirement requirement = (ComponentRequirement) iterator.next();

            try
            {
                if ( requirement instanceof ComponentRequirementList )
                {
                    Iterator iter = ( (ComponentRequirementList) requirement ).getRoleHints().iterator();

                    while (iter.hasNext()) {
                        String hint = (String) iter.next();
                        dag.addEdge( key, getDAGKey( requirement.getRole(), hint ) );
                    }
                }
                else
                {
                    dag.addEdge( key, getDAGKey( requirement.getRole(), requirement.getRoleHint() ) );
                }
            }
            catch ( CycleDetectedException e )
            {
                throw new CompositionException( "Cyclic requirement detected", e );
            }
        }
    }

    /**
     * @see org.codehaus.plexus.component.composition.CompositionResolver#getRequirements(String,String)
     */
    public List getRequirements( String role, String roleHint )
    {
        return dag.getChildLabels( getDAGKey( role, roleHint ) );
    }


    /**
     * @see org.codehaus.plexus.component.composition.CompositionResolver#findRequirements(String,String)
     */
    public List findRequirements( String role, String roleHint )
    {
        return dag.getParentLabels( getDAGKey( role, roleHint ) );
    }

    private String getDAGKey( String role, String roleHint )
    {
        return role + SEPARATOR_CHAR + roleHint;
    }
}
