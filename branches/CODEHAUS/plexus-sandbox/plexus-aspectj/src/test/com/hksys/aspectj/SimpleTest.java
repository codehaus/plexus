package com.hksys.aspectj;

import junit.framework.TestCase;

import com.hksys.ParentPackageFoo;
import com.hksys.aspectj.foo.ChildPackageFoo;

/**
 * A class that should have an aspect woven in at runtime.
 *
 * @author Stephen Haberman
 */
public class SimpleTest extends TestCase
{

    /** A method for the aspect to weave in to. */
    public void testSamePackageFoo() throws Exception
    {
        SamePackageFoo f = new SamePackageFoo();

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

    /** Test child packages. */
    public void testChildPackageFoo() throws Exception
    {
        ChildPackageFoo f = new ChildPackageFoo();

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

    /** Test parent packages. */
    public void testParentPackageFoo() throws Exception
    {
        ParentPackageFoo f = new ParentPackageFoo();

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
}
