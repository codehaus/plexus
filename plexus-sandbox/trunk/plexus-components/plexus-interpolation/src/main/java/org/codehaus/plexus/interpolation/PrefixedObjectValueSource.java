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

/**
 * @version $Id$
 */
public class PrefixedObjectValueSource
    implements FeedbackEnabledValueSource, QueryEnabledValueSource
{

    private final PrefixedValueSourceWrapper delegate;

    public PrefixedObjectValueSource( String prefix, Object root )
    {
        delegate = new PrefixedValueSourceWrapper( new ObjectBasedValueSource( root ), prefix );
    }

    public PrefixedObjectValueSource( List possiblePrefixes, Object root, boolean allowUnprefixedExpressions )
    {
        delegate = new PrefixedValueSourceWrapper( new ObjectBasedValueSource( root ), possiblePrefixes, allowUnprefixedExpressions );
    }

    public Object getValue( String expression )
    {
        return delegate.getValue( expression );
    }

    public void clearFeedback()
    {
        delegate.clearFeedback();
    }

    public List getFeedback()
    {
        return delegate.getFeedback();
    }

    public String getLastExpression()
    {
        return delegate.getLastExpression();
    }
}
