package org.codehaus.plexus.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class-level annotation to mark that class as a component.
 *
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Component
{
    /**
     * The role of the component, usually an interface.
     */
    Class<?> role();

    /**
     * The hint.
     */
    String hint() default "default";

    /**
     * Component's version.
     */
    String version() default "";

    /**
     * The lifecyclehandler hint for this component.
     */
    String lifecycleHandler() default "plexus";

    /**
     * The instantiation strategy for this component.
     */
    InstantiationStrategy instantiationStrategy() default InstantiationStrategy.SINGLETON;

    /**
     * The component's alias. TODO: what's this?
     */
    String alias() default "";
}
