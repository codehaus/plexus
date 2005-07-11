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

import org.codehaus.plexus.formica.population.TargetPopulationException;
import org.codehaus.plexus.formica.validation.FormValidationResult;
import org.codehaus.plexus.formica.validation.Validator;
import org.codehaus.plexus.formica.validation.group.GroupValidator;
import org.codehaus.plexus.formica.validation.manager.ValidatorNotFoundException;

import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface FormManager
{
    static String ROLE = FormManager.class.getName();

    void addForm( Form form );

    Form getForm( String formId )
        throws FormNotFoundException;

    Validator getValidator( String validatorId )
        throws ValidatorNotFoundException;

    GroupValidator getGroupValidator( String groupValidatorId );

    // ----------------------------------------------------------------------
    // Form validation
    // ----------------------------------------------------------------------

    FormValidationResult validate( String formId, Map data )
        throws FormicaException, FormNotFoundException, ValidatorNotFoundException;

    FormValidationResult validate( Form form, Map data )
        throws FormicaException, ValidatorNotFoundException;

    // ----------------------------------------------------------------------
    // Population
    // ----------------------------------------------------------------------

    public void populate( Form form, Map data, Object target )
        throws TargetPopulationException;

    public void populate( String formId, Map data, Object target )
        throws TargetPopulationException, FormNotFoundException;

    public Object populate( String formId, Map data, ClassLoader classLoader )
        throws TargetPopulationException, FormNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException;

}
