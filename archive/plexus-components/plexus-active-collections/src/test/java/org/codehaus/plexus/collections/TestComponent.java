package org.codehaus.plexus.collections;

public class TestComponent
{
    
    private String value;
    
    public TestComponent( String value )
    {
        this.value = value;
    }
    
    public TestComponent()
    {
    }
    
    public String getValue()
    {
        return value;
    }

    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( value == null ) ? 0 : value.hashCode() );
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
        final TestComponent other = (TestComponent) obj;
        if ( value == null )
        {
            if ( other.value != null )
                return false;
        }
        else if ( !value.equals( other.value ) )
            return false;
        return true;
    }
    
    public String toString()
    {
        return "TestComponent[value: " + value + "; hashCode: " + hashCode() + "]"; 
    }
    
}
