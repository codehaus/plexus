package org.codehaus.plexus.formica.validation;

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
 * Base class for all validators.  Validators must implement the
 * <code>validate()</code> method to provide validation logic.
 * <p/>
 * Validator hashCode uses only id field.
 * <p/>
 * The gerneral assumption is that <code>validatorA.equals( validatorB )</code>
 * if and only if <code>validatorA.getId().equals( validatorB.getId() )</code>
 *
 * @author Anthony Eden
 * @author <a href="mailto:mmaczka@interia.pl">Michal Maczka</a>
 */
public abstract class AbstractValidator implements Validator
{
    protected String message;
    protected String errorMessageKey;


    /**
     * Return the validator error messageKey using the default Locale.
     *
     * @return The error messageKey
     */

    public String getErrorMessageKey()
    {
        return errorMessageKey;
    }


}
