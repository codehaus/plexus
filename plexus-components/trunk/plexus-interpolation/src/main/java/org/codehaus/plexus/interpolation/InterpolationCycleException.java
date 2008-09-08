package org.codehaus.plexus.interpolation;

public class InterpolationCycleException
    extends InterpolationException
{
    
    private static final long serialVersionUID = 1L;

    public InterpolationCycleException( RecursionInterceptor recursionInterceptor, String realExpr, String wholeExpr )
    {
        super( "Detected the following recursive expression cycle: "
                        + recursionInterceptor.getExpressionCycle( realExpr ), wholeExpr );
        
    }

}
