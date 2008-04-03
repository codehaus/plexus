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

import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

import java.util.Collections;
import java.util.List;

public class PrefixedValueSourceWrapper
    implements FeedbackEnabledValueSource, QueryEnabledValueSource
{

    private final ValueSource valueSource;

    private final List possiblePrefixes;

    private boolean allowUnprefixedExpressions;

    private String lastExpression;

    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       String prefix )
    {
        this.valueSource = valueSource;
        possiblePrefixes = Collections.singletonList( prefix );
    }

    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       String prefix,
                                       boolean allowUnprefixedExpressions )
    {
        this.valueSource = valueSource;
        possiblePrefixes = Collections.singletonList( prefix );
        this.allowUnprefixedExpressions = allowUnprefixedExpressions;
    }

    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       List possiblePrefixes )
    {
        this.valueSource = valueSource;
        this.possiblePrefixes = possiblePrefixes;
    }

    public PrefixedValueSourceWrapper( ValueSource valueSource,
                                       List possiblePrefixes,
                                       boolean allowUnprefixedExpressions )
    {
        this.valueSource = valueSource;
        this.possiblePrefixes = possiblePrefixes;
        this.allowUnprefixedExpressions = allowUnprefixedExpressions;
    }

    public Object getValue( String expression )
    {
        lastExpression = ValueSourceUtils.trimPrefix( expression, possiblePrefixes, allowUnprefixedExpressions );

        if ( lastExpression == null )
        {
            return null;
        }

        return valueSource.getValue( lastExpression );
    }

    public List getFeedback()
    {
        return ( valueSource instanceof FeedbackEnabledValueSource )
                        ? ( (FeedbackEnabledValueSource) valueSource ).getFeedback()
                        : Collections.EMPTY_LIST;
    }

    public String getLastExpression()
    {
        return ( valueSource instanceof QueryEnabledValueSource )
                        ? ( (QueryEnabledValueSource) valueSource ).getLastExpression()
                        : lastExpression;
    }

    public void clearFeedback()
    {
        if ( valueSource instanceof FeedbackEnabledValueSource )
        {
            ((FeedbackEnabledValueSource) valueSource).clearFeedback();
        }
    }

}
