package org.codehaus.plexus.cotlet.samples;

/**
 * Description of implementation component a
 *
 * @component.implementation
 */
public class DefaultComponentA  implements ComponentA
{
    public void foo()
    {
    }

    /**
     * @component.configuration
     */
    private Baa baa;
}