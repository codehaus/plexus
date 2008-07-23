package org.codehaus.plexus.cotlet.samples;

import java.util.List;

/**
 */
public class Baa
{
    /**
     *
     * Field foo1
     * @component.configuration
     */
    private int foo1;

    /**
     *
     * Field foo2
     *
     * @component.configuration
     */
    private String foo2;


    /**
     *
     * Field foos - this is a collection of Foo objects
     *
     * @component.configuration
     *   elements="org.codehaus.plexus.cotlet.samples.Foo"
     */
    private List foos;


}