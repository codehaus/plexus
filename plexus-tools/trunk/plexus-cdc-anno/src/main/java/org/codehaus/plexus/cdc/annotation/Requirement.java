package org.codehaus.plexus.cdc.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Even if a list or map, can this glean the role from the parameterized type?
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, METHOD})
@Inherited
public @interface Requirement
{
    String role() default "";
    String roleHint() default "";
}
