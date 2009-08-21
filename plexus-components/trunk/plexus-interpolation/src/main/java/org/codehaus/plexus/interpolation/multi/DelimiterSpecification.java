package org.codehaus.plexus.interpolation.multi;

/*
 * Copyright 2001-2009 Codehaus Foundation.
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

public final class DelimiterSpecification
{
    public static final DelimiterSpecification DEFAULT_SPEC = DelimiterSpecification.parse( "${*}" );
    
    private String begin;
    
    private String end;

    private int nextStart;
    
    public DelimiterSpecification( String begin, String end )
    {
        this.begin = begin;
        this.end = end;
    }
    
    public int getNextStartIndex()
    {
        return nextStart;
    }
    
    public void setNextStartIndex( int nextStart )
    {
        this.nextStart = nextStart;
    }
    
    public void clearNextStart()
    {
        nextStart = -1;
    }

    public static DelimiterSpecification parse( String delimiterSpec )
    {
        final String[] spec = new String[2];
        
        int splitIdx = delimiterSpec.indexOf( '*' );
        if ( splitIdx < 0 )
        {
            spec[0] = delimiterSpec;
            spec[1] = spec[0];
        }
        else if ( splitIdx == delimiterSpec.length() - 1 )
        {
            spec[0] = delimiterSpec.substring( 0, delimiterSpec.length() - 1 );
            spec[1] = spec[0];
        }
        else
        {
            spec[0] = delimiterSpec.substring( 0, splitIdx );
            spec[1] = delimiterSpec.substring( splitIdx + 1 );
        }
        
        return new DelimiterSpecification( spec[0], spec[1] );
    }

    public String getBegin()
    {
        return begin;
    }

    public String getEnd()
    {
        return end;
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( begin == null ) ? 0 : begin.hashCode() );
        result = prime * result + ( ( end == null ) ? 0 : end.hashCode() );
        return result;
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        DelimiterSpecification other = (DelimiterSpecification) obj;
        if ( begin == null )
        {
            if ( other.begin != null )
                return false;
        }
        else if ( !begin.equals( other.begin ) )
            return false;
        if ( end == null )
        {
            if ( other.end != null )
                return false;
        }
        else if ( !end.equals( other.end ) )
            return false;
        return true;
    }
    
    public String toString()
    {
        return "Interpolation delimiter [begin: '" + begin + "', end: '" + end + "']";
    }
    
}
