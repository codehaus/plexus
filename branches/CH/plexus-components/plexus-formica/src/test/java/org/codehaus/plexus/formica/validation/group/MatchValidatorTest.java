package org.codehaus.plexus.formica.validation.group;

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

