package org.codehaus.plexus.cotlet.samples;

/**
 * Description of implementation component c
 *
 * @component.implementation
 */
public class DefaultComponentC implements ComponentC
{
    /**
     * @component.requirement
     */
    private ComponentA componentA;

    /**
     * @component.requirement
     */
    private ComponentA componentB;

    public void foo()
    {
    }


}