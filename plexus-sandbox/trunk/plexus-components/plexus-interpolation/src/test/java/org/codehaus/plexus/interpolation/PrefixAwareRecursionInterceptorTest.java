package org.codehaus.plexus.interpolation;

/*
 * Copyright 2001-2008 Codehaus Foundation.
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

import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class PrefixAwareRecursionInterceptorTest
    extends TestCase
{

    public void testDefaultTokensInPattern()
    {
        String pattern = PrefixAwareRecursionInterceptor.DEFAULT_START_TOKEN + "(.+?)" + PrefixAwareRecursionInterceptor.DEFAULT_END_TOKEN;

        Pattern p = Pattern.compile( pattern );

        assertTrue( p.matcher( "${key}" ).matches() );
        assertTrue( "${prefix.first}".matches( pattern ) );
    }

    public void testFindExpression()
    {
        PrefixAwareRecursionInterceptor receptor = new PrefixAwareRecursionInterceptor( Collections.singleton( "prefix." ) );

        String nakedExpr = "prefix.first";
        String expr = "${" + nakedExpr + "}";

        receptor.expressionResolutionStarted( nakedExpr );

        assertTrue( receptor.hasRecursiveExpression( expr ) );

        receptor.expressionResolutionFinished( nakedExpr );

        assertFalse( receptor.hasRecursiveExpression( expr ) );
    }

    public void testFindExpressionWithDifferentPrefix()
    {
        PrefixAwareRecursionInterceptor receptor = new PrefixAwareRecursionInterceptor( Arrays.asList( new String[]{"prefix.", "other."} ) );

        String nakedExpr = "prefix.first";
        String expr = "${other.first}";

        receptor.expressionResolutionStarted( nakedExpr );

        assertTrue( receptor.hasRecursiveExpression( expr ) );

        receptor.expressionResolutionFinished( nakedExpr );

        assertFalse( receptor.hasRecursiveExpression( expr ) );
    }

    public void testFindExpressionWithoutPrefix()
    {
        PrefixAwareRecursionInterceptor receptor = new PrefixAwareRecursionInterceptor( Arrays.asList( new String[]{"prefix.", "other."} ) );

        String nakedExpr = "prefix.first";
        String expr = "${first}";

        receptor.expressionResolutionStarted( nakedExpr );

        assertTrue( receptor.hasRecursiveExpression( expr ) );

        receptor.expressionResolutionFinished( nakedExpr );

        assertFalse( receptor.hasRecursiveExpression( expr ) );
    }

}
