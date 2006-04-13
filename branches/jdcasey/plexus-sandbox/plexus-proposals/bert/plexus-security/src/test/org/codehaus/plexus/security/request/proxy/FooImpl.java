package org.codehaus.plexus.security.request.proxy;

/**
  * 
  * <p>Created on 22/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class FooImpl implements Foo
{

    /**
     * 
     */
    public FooImpl()
    {
        super();
    }

    /**
     * @see org.codehaus.plexus.security.request.proxy.Foo#addIt(int, int)
     */
    public int addIt(int a, int b)
    {
        return a+b;
    }

}
