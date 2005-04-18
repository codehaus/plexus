package org.codehaus.plexus.formica;

/*
 * Copyright (c) 2004, Codehaus.org
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

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
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

    private boolean immutable;

    private String type = "text";

    private String contentGenerator;

    public Element()
    {
    }

    public void setLabelKey( String labelKey )
    {
        this.labelKey = labelKey;
    }

    public String getLabelKey()
    {
        if ( labelKey == null )
            return getId() + ".label";

        return labelKey;
    }

    public void setMessageKey( String messageKey )
    {
        this.messageKey = messageKey;
    }

    public String getMessageKey()
    {
        if ( messageKey == null )
        {
            return getId() + ".message";
        }

        return messageKey;
    }

    public String getErrorMessageKey()
    {
        if ( errorMessageKey == null )
        {
            return getId() + ".error";
        }

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

    public boolean isImmutable()
    {
        return immutable;
    }

    public void setImmutable( boolean immutable )
    {
        this.immutable = immutable;
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

    public String getContentGenerator()
    {
        return contentGenerator;
    }
}
