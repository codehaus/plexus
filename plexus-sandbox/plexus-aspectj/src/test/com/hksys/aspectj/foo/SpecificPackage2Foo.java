package com.hksys.aspectj.foo;

/**
 * A class that should have an aspect woven in at runtime.
 *
 * @author Stephen Haberman
 */
public class SpecificPackage2Foo
{

    /**
     * A method for the aspect to NOT weave in to.
     */
    public void shouldThrowAnException()
    {
    }

}

