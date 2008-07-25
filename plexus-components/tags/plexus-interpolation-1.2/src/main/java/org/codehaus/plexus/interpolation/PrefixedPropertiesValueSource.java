package org.codehaus.plexus.interpolation;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;
import java.util.Properties;

/**
 * Legacy support. Allow trimming one of a set of expression prefixes, the lookup
 * the remaining expression as a literal key from the wrapped properties instance.
 * <br/>
 * This is just a convenience implementation to provide a shorthand for constructing
 * the properties value source and then wrapping it with a prefixed value-source wrapper.
 * @version $Id$
 */
public class PrefixedPropertiesValueSource
    implements QueryEnabledValueSource
{

    private final PrefixedValueSourceWrapper delegate;

    /**
     * Wrap the specified properties file with a new {@link PropertiesBasedValueSource}, then
     * wrap that source with a new {@link PrefixedValueSourceWrapper} that uses the specified
     * expression prefix. Finally, set this wrapper source as a delegate for this
     * instance to use.
     *
     * @param prefix The expression prefix to trim
     * @param properties The properties instance to wrap
     */
    public PrefixedPropertiesValueSource( String prefix, Properties properties )
    {
        delegate = new PrefixedValueSourceWrapper( new PropertiesBasedValueSource( properties ), prefix );
    }

    /**
     * Wrap the specified properties file with a new {@link PropertiesBasedValueSource}, then
     * wrap that source with a new {@link PrefixedValueSourceWrapper} that uses the specified
     * expression-prefix list. Finally, set this wrapper source as a delegate for this
     * instance to use.
     *
     * @param possiblePrefixes The expression-prefix list to trim
     * @param properties The properties instance to wrap
     */
    public PrefixedPropertiesValueSource( List possiblePrefixes, Properties properties, boolean allowUnprefixedExpressions )
    {
        delegate = new PrefixedValueSourceWrapper( new PropertiesBasedValueSource( properties ), possiblePrefixes, allowUnprefixedExpressions );
    }

    /**
     * {@inheritDoc}
     */
    public void clearFeedback()
    {
        delegate.clearFeedback();
    }

    /**
     * {@inheritDoc}
     */
    public List getFeedback()
    {
        return delegate.getFeedback();
    }

    /**
     * {@inheritDoc}
     */
    public String getLastExpression()
    {
        return delegate.getLastExpression();
    }

    /**
     * Delegates to {@link PrefixedValueSourceWrapper#getValue(String)} for the
     * instance wrapping the {@PropertiesValueSource} instance.
     */
    public Object getValue( String expression )
    {
        return delegate.getValue( expression );
    }

}
