package org.codehaus.plexus.formica;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @author <a href="mailto:mmmaczka@interia.pl">Michal Maczka</a>
 * @version $Id$
 */
public class Element
    extends Identifiable
{
    private String labelKey;

    private String messageKey;

    private String errorMessageKey;

    private String defaultValue;

    private boolean optional;

    private String expression;

    private String validator;

    private String type = "text";

    public Element()
    {
    }

    public void setLabelKey( String labelKey )
    {
        this.labelKey = labelKey;
    }

    public String getLabelKey()
    {
        return labelKey;
    }

    public void setMessageKey( String messageKey )
    {
        this.messageKey = messageKey;
    }

    public String getMessageKey()
    {
        return messageKey;
    }

    public String getErrorMessageKey()
    {
        return errorMessageKey;
    }

    public void setErrorMessageKey( String errorMessageKey )
    {
        this.errorMessageKey = errorMessageKey;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue( String defaultValue )
    {
        this.defaultValue = defaultValue;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public void setOptional( boolean optional )
    {
        this.optional = optional;
    }

    public void setExpression( String populatorExpression )
    {
        this.expression = populatorExpression;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setValidator( String validator )
    {
        this.validator = validator;
    }

    public String getValidator()
    {
        return validator;
    }

    public String getType()
    {
        return type;
    }
}
