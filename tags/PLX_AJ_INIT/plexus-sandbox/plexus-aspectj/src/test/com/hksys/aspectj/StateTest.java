package com.hksys.aspectj;

import junit.framework.TestCase;

/**
 * Makes sure the {@link AspectJClassLoader} doesn't accept more library JARs
 * and aspects after it has started weaving.
 *
 * @author Stephen Haberman
 */
public class StateTest extends TestCase
{

    public void testLibraryState()
    {
        SamePackageFoo f = new SamePackageFoo();

        try
        {
            System.out.println(StateTest.class.getClassLoader());
            ((AspectJClassLoader) StateTest.class.getClassLoader())
                .addLibraryAspect("foo");
            super.fail("addLibraryAspect didn't fail.");
        }
        catch (IllegalStateException e)
        {
            // Supposed to happen
        }
    }

    public void testPackageState()
    {
        SamePackageFoo f = new SamePackageFoo();

        try
        {
            ((AspectJClassLoader) StateTest.class.getClassLoader())
                .addPackageToIgnore("foo");
            super.fail("addPackageToIgnore didn't fail.");
        }
        catch (IllegalStateException e)
        {
            // Supposed to happen
        }
    }

}

