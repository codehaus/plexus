package org.codehaus.plexus.interpolation.multi;

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
