package org.codehaus.plexus.formica.validation;

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
