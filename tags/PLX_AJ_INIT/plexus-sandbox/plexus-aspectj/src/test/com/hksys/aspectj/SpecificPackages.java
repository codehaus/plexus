package com.hksys.aspectj;

import junit.framework.TestCase;

import com.hksys.aspectj.foo.SpecificPackage1Foo;
import com.hksys.aspectj.foo.SpecificPackage2Foo;

/**
 * A class that should have an aspect woven in at runtime.
 *
 * @author Stephen Haberman
 */
public class SpecificPackages extends TestCase
{

    /** Ensure SpecificPackage1Foo was woven. */
    public void testPackage1Foo() throws Exception
    {
        SpecificPackage1Foo f = new SpecificPackage1Foo();

        try
        {
            f.shouldThrowAnException();
            super.fail("Foo.shouldThrowAnException() did not throw an exception.");
        }
        catch (UnsupportedOperationException e)
        {
            // supposed to happen
        }
    }

    /** Ensure SpecificPackage2Foo was not woven. */
    public void testChildPackageFoo() throws Exception
    {
        SpecificPackage2Foo f = new SpecificPackage2Foo();
        f.shouldThrowAnException();
    }

}
