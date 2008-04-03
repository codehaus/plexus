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

public abstract class AbstractFunctionValueSourceWrapper
    implements ValueSource
{

    private final ValueSource valueSource;

    protected AbstractFunctionValueSourceWrapper( ValueSource valueSource )
    {
        this.valueSource = valueSource;
    }

    public Object getValue( String expression )
    {
        Object value = valueSource.getValue( expression );

        String expr = expression;

        if ( valueSource instanceof QueryEnabledValueSource )
        {
            expr = ((QueryEnabledValueSource) valueSource).getLastExpression();
        }

        return executeFunction( expr, value );
    }

    protected ValueSource getValueSource()
    {
        return valueSource;
    }

    protected abstract Object executeFunction( String expression, Object value );

}
