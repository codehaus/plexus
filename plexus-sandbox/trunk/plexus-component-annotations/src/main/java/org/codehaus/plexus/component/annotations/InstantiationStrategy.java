package org.codehaus.plexus.component.annotations;

/**
 * Component instantiationstrategy.
 *
 * @author <a href="mailto:kenney@neonics.com">Kenney Westerhof</a>
 */
public enum InstantiationStrategy
{
    /**
     * The component is only instantiated once. Subsequent lookups will return the same instance.
     */
    SINGLETON,

    /**
     * Every time the component is looked up, a new instance will be returned.
     */
    PER_LOOKUP;
}
