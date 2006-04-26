package org.codehaus.plexus.component.factory.java;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.codehaus.plexus.component.factory.Component;
import org.codehaus.plexus.component.factory.ComponentImplA;
import org.codehaus.plexus.component.factory.ComponentImplB;
import org.codehaus.plexus.component.factory.ComponentImplC;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.embed.Embedder;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class JavaComponentFactoryTest
    extends TestCase
{
    public void testComponentCreation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplA.class.getName() );

        URLClassLoader cl = new URLClassLoader( new URL[0], Thread.currentThread().getContextClassLoader() );

        Embedder embedder = new Embedder();
        embedder.start( cl );
        
        Object component = factory.newInstance( componentDescriptor, cl, embedder.getContainer() );

        assertNotNull( component );
    }

    public void testComponentCreationWithNotMatchingRoleAndImplemenation()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplB.class.getName() );

        URLClassLoader cl = new URLClassLoader( new URL[0], Thread.currentThread().getContextClassLoader() );

        Embedder embedder = new Embedder();
        embedder.start( cl );
        
        factory.newInstance( componentDescriptor, cl, embedder.getContainer() );
    }

    public void testInstanciationOfAAbstractComponent()
        throws Exception
    {
        JavaComponentFactory factory = new JavaComponentFactory();

        ComponentDescriptor componentDescriptor = new ComponentDescriptor();

        componentDescriptor.setRole( Component.class.getName() );

        componentDescriptor.setImplementation( ComponentImplC.class.getName() );

        URLClassLoader cl = new URLClassLoader( new URL[0], Thread.currentThread().getContextClassLoader() );

        Embedder embedder = new Embedder();
        
        embedder.start( cl );

//        container.

        try
        {
            factory.newInstance( componentDescriptor, cl, embedder.getContainer() );

            fail( "Expected ComponentInstantiationException when instanciating a abstract class." );
        }
        catch( ComponentInstantiationException ex )
        {
            assertTrue( true );
        }
    }
}
