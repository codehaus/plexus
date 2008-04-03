package org.codehaus.plexus.interpolation;

public class InterpolationException
    extends Exception
{

    private final String expression;

    protected InterpolationException( String message,
                                      String expression,
                                      Throwable cause )
    {
        super( buildMessage( message, expression ), cause );
        this.expression = expression;
    }

    private static String buildMessage( String message,
                                        String expression )
    {
        return "Resolving expression: '" + expression + "': " + message;
    }

    protected InterpolationException( String message, String expression )
    {
        super( buildMessage( message, expression ) );
        this.expression = expression;
    }

    public String getExpression()
    {
        return expression;
    }

}
