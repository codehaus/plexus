package org.codehaus.plexus.redback.xwork.util;

/*
 * Copyright 2005-2006 The Codehaus.
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

import java.util.Comparator;

import org.codehaus.plexus.redback.role.model.ModelTemplate;

/**
 * ModelTemplateSorter
 *
 * @author <a href="mailto:hisidro@exist.com">Henry Isidro</a>
 */
public class ModelTemplateSorter
    implements Comparator
{
    public int compare( Object o1, Object o2 )
    {
        if ( !( o1 instanceof ModelTemplate ) )
        {
            return 0;
        }

        if ( !( o2 instanceof ModelTemplate ) )
        {
            return 0;
        }

        if ( ( o1 == null ) && ( o2 == null ) )
        {
            return 0;
        }

        if ( ( o1 == null ) && ( o2 != null ) )
        {
            return -1;
        }

        if ( ( o1 != null ) && ( o2 == null ) )
        {
            return 1;
        }

        ModelTemplate r1 = (ModelTemplate) o1;
        ModelTemplate r2 = (ModelTemplate) o2;

        return r1.getNamePrefix().compareTo( r2.getNamePrefix() );
    }
}
