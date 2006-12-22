package org.codehaus.plexus.personality.pico.composition.array;


public class DefaultBaa implements Baa
{
    private Foo[] foos;

    public DefaultBaa( Foo[] foos )
    {
        this.foos = foos;
    }

    public void baa()
    {

    }

    public Foo[] getFoos()
    {
        return foos;
    }
}
