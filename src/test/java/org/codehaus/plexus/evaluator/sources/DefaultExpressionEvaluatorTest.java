package org.codehaus.plexus.evaluator.sources;

/*
 * Copyright 2001-2006 The Codehaus.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.evaluator.EvaluatorException;
import org.codehaus.plexus.evaluator.ExpressionEvaluator;

import java.util.Properties;

/**
 * DefaultExpressionEvaluatorTest 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class DefaultExpressionEvaluatorTest
    extends PlexusTestCase
{
    private ExpressionEvaluator evaluator;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        evaluator = (ExpressionEvaluator) lookup( ExpressionEvaluator.ROLE, "default" );
    }

    public void testSimple()
        throws EvaluatorException
    {
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        String userHome = System.getProperty( "user.home" );
        String expression = "My HOME directory is ${user.home}";
        String expected = "My HOME directory is " + userHome;

        String actual = evaluator.expand( expression );
        assertEquals( expected, actual );
    }

    public void testMultiExpression()
        throws EvaluatorException
    {
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        String userName = System.getProperty( "user.name" );
        String userHome = System.getProperty( "user.home" );
        String expression = "${user.name}'s home directory is ${user.home}";
        String expected = userName + "'s home directory is " + userHome;

        String actual = evaluator.expand( expression );
        assertEquals( expected, actual );
    }

    public void testEscaping()
        throws EvaluatorException
    {
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        String userName = System.getProperty( "user.name" );
        String userHome = System.getProperty( "user.home" );
        String expression = "${user.name}'s home directory is ${user.home} (fetched via $${user.home} expression)";
        String expected = userName + "'s home directory is " + userHome + " (fetched via ${user.home} expression)";

        String actual = evaluator.expand( expression );
        assertEquals( expected, actual );
    }
    
    public void testRecursiveSimple() throws EvaluatorException
    {
        PropertiesExpressionSource propsource = new PropertiesExpressionSource();
        Properties props = new Properties();

        // Create intentional recursive lookup.
        props.setProperty( "main.dir", "${target.dir}/classes" );
        props.setProperty( "target.dir", "./target" );

        propsource.setProperties( props );
        
        evaluator.addExpressionSource( propsource );
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );
        
        String expression = "My classes directory is ${main.dir}";
        String expected = "My classes directory is ./target/classes";

        String actual = evaluator.expand( expression );
        assertEquals( expected, actual );
    }

    public void testRecursiveCycle()
    {
        PropertiesExpressionSource propsource = new PropertiesExpressionSource();
        Properties props = new Properties();

        // Create intentional recursive lookup.
        props.setProperty( "main.dir", "${test.dir}/target/classes" );
        props.setProperty( "test.dir", "${main.dir}/target/test-classes" );
        
        propsource.setProperties( props );

        evaluator.addExpressionSource( propsource );
        evaluator.addExpressionSource( new SystemPropertyExpressionSource() );

        try
        {
            evaluator.expand( "My main dir is ${main.dir}" );
            fail( "Should have thrown an EvaluatorException due to recursive cycle." );
        }
        catch ( EvaluatorException e )
        {
            // Expected path.
        }
    }
}
