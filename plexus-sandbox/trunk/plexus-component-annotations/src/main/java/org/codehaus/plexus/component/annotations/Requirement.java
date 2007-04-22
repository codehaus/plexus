package org.codehaus.plexus.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field or method as a requirement. This is similar to the {@link Parameter} annotation, except that one
 * deals with configuration whereas this one deals with injecting required components.
 *
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.FIELD, ElementType.METHOD } )
public @interface Requirement
{
    /**
     * The role of the required component. This will be detected by looking at the type of the field or method.
     */
    Class<?> role() default Object.class;

    /**
     * The hint of the component. Note that this is not always used, for instance when injecting into
     * {@link java.util.List}s, {@link java.util.Map}s, {@link java.util.Set}s or {@link java.util.Collection}s.
     */
    String hint() default "default";

    /**
     * A list of configuration parameters used to configure the required component. Note that when the required
     * component has {@link InstantiationStrategy#SINGLETON}, this only has effect on the first lookup.
     */
    Configuration[] configuration() default {};
}
