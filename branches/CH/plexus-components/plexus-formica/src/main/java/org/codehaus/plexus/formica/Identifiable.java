package org.codehaus.plexus.formica;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

/**
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 */
public class Identifiable
{
    private String id;

    public Identifiable()
    {

    }

    public Identifiable( String id )
    {
        this.id = id;
    }


    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof Identifiable ) )
        {
            return false;
        }

        Identifiable identificable = (Identifiable) o;

        if ( !id.equals( identificable.id ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return id.hashCode();
    }
}
