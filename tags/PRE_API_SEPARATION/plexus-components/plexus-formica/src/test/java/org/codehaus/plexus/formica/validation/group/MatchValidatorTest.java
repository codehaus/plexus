package org.codehaus.plexus.formica.validation.group;

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

import junit.framework.TestCase;
import org.codehaus.plexus.formica.Element;
import org.codehaus.plexus.formica.ElementGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class MatchValidatorTest
    extends TestCase
{
    GroupValidator groupValidator;

    public MatchValidatorTest( final String s )
    {
        super( s );
    }

    public void setUp()
        throws Exception
    {
        groupValidator = new MatchValidator();
    }

    public void testWhereAllElementsMatch()
        throws Exception
    {


        final ElementGroup elementGroup = getElementGroup();

        // Procsss the form
        final Map formData = new HashMap();
        formData.put( "password-one", "secret-password" );
        formData.put( "password-two", "secret-password" );
        formData.put( "password-three", "secret-password" );

        final boolean result = groupValidator.validate( elementGroup, formData );
        assertTrue( "all fields match so result should be true", result );
    }

    public void testWhereElementsMismatch()
        throws Exception
    {
        final ElementGroup elementGroup = getElementGroup();

        // Procsss the form
        final Map formData = new HashMap();
        formData.put( "password-one", "secret-password" );
        formData.put( "password-two", "secret-password" );
        formData.put( "password-three", "secret-password-mismatch" );

        final boolean result = groupValidator.validate( elementGroup, formData );
        assertFalse( "last field does not match so result should be false", result );
    }


    private ElementGroup getElementGroup()
    {
        final Element passwordOneElement = new Element();
        passwordOneElement.setId( "password-one" );

        final Element passwordTwoElement = new Element();
        passwordTwoElement.setId( "password-two" );

        final Element passwordThreeElement = new Element();
        passwordThreeElement.setId( "password-three" );

        final ElementGroup elementGroup = new ElementGroup();
        elementGroup.addElement( passwordOneElement );
        elementGroup.addElement( passwordTwoElement );
        elementGroup.addElement( passwordThreeElement );
        return elementGroup;
    }

}

