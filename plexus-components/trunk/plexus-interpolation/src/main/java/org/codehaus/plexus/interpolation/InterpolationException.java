package org.codehaus.plexus.interpolation;

/**
 * Signals an unrecoverable error in the process of interpolating a string, such
 * as the detection of an expression cycle. Errors resolving individual values
 * from expressions, such as those that happen when an object wrapped in an
 * {@link ObjectBasedValueSource} doesn't have the property represented by a
 * particular expression part, should <b>NOT</b> result in InterpolationExceptions
 * being thrown. Instead, they should be reported in the feedback from the {@link ValueSource},
 * which is propagated out through {@link Interpolator#getFeedback()}.
 * @version $Id$
 */
public class InterpolationException
    extends Exception
{

    private final String expression;

    /**
     * @param message The general description of the problem
     * @param expression The expression that triggered the problem
     * @param cause The wrapped exception
     */
    public InterpolationException( String message,
                                      String expression,
                                      Throwable cause )
    {
        super( buildMessage( message, expression ), cause );
        this.expression = expression;
    }

    /**
     * @param message The general description of the problem
     * @param expression The expression that triggered the problem
     */
    public InterpolationException( String message, String expression )
    {
        super( buildMessage( message, expression ) );
        this.expression = expression;
    }

    private static String buildMessage( String message,
                                        String expression )
    {
        return "Resolving expression: '" + expression + "': " + message;
    }

    /**
     * @return The expression that triggered this exception.
     */
    public String getExpression()
    {
        return expression;
    }

}
