package org.codehaus.plexus.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used inside the {@link Requirement} annotation to set configuration parameters for the component.
 * <p>
 * An example:
 *
 * <pre>
 * &#064;Component( role = ComponentA.class )
 * public class ComponentA
 * {
 *   &#064;Parameter
 *   private String foo;
 *
 *   &#064;Parameter
 *   private String[] bar;
 * }
 *
 * &#064;Component( role = Object.class, hint = "b")
 * public class ComponentA
 * {
 *   &#064;Requirement( configuration =
 *     {
 *       &#064;Configuration( key = 'foo', value = 'fooValue' ),
 *       &#064;Configuration( key = 'bar', value = {'barValue1', 'barValue2'} )
 *     } )
 *   private ComponentA a;
 * }
 *</pre>
 *
 * </p>
 *
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface Configuration
{
    /**
     * The name of the field or method.
     */
    String key();

    /**
     * The value or list of values to configure the field with.
     */
    String[] value() default "";
}
