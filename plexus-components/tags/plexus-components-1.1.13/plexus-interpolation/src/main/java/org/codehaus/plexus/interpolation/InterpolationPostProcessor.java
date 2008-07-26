package org.codehaus.plexus.interpolation;

/**
 * Used to allow full recursion of value interpolation before applying some rule
 * to the result.
 * @version $Id$
 */
public interface InterpolationPostProcessor
{
    
    /**
     * Given the starting expression and the fully-recursively-interpolated value,
     * perform some post-processing step and return the resulting [possibly different]
     * value, or null if no change was made..
     * 
     * @param expression the original expression
     * @param value the original value after fully recursive interpolation of expression
     * @return the processed value if a change was made; else return null
     */
    Object execute( String expression, Object value );

}
