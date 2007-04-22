package org.codehaus.plexus.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This field or method level annotation marks the field as a configurable parameter. Only a single string or list of
 * strings are supported.
 * <p>
 * TODO: add expression etc.
 * </p>
 *
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD } )
public @interface Parameter
{
    /**
     * The default value for the parameter.
     *
     * @return
     */
    String[] value() default "";
}
