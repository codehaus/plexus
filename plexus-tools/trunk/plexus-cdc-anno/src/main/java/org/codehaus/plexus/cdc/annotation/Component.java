package org.codehaus.plexus.cdc.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface Component
{
//    ComponentType type() default PLEXUS;
    String role();
    String roleHint() default "";
    String version() default "";
    String lifecycleHandler() default "";
    String instantiationStrategy() default "";
    String alias() default "";
}
