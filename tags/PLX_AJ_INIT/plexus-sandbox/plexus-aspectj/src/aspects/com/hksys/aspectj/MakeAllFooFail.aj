package com.hksys.aspectj.foo;

/**
 * An aspect to make any class named *Foo throw an
 * UnsupportedOperationException.
 *
 * @author Stephen Haberman
 */
public aspect MakeAllFooFail
{

    /**
     * Weave in the advice to throw the exception.
     */
    before(): execution(void *.shouldThrowAnException())
    {
        throw new UnsupportedOperationException("Intended behavior...");
    }

}
