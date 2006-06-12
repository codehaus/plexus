/**
 * 
 */
package org.codehaus.plexus.cdc.merge.support;

/**
 * All allowable tags in <code>components.xml</code> and their bindings to
 * {@link Mergeable} counterparts (if required).
 * <p>
 * <em>This implementation may change.</em> <br>
 * TODO Might be an idea factor and set up the list of allowed tags here itself.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 * @version $Id:$
 */
public class ComponentsXmlTag
    extends AbstractDescriptorTag
{

    public static final ComponentsXmlTag COMPONENT_SET = new ComponentsXmlTag( "component-set", true,
                                                                               ComponentSetElement.class );

    public static final ComponentsXmlTag COMPONENTS = new ComponentsXmlTag( "components", true, ComponentsElement.class );

    public static final ComponentsXmlTag COMPONENT = new ComponentsXmlTag( "component", true, ComponentElement.class );

    public static final ComponentsXmlTag ROLE = new ComponentsXmlTag( "role" );

    public static final ComponentsXmlTag ROLE_HINT = new ComponentsXmlTag( "role-hint" );

    public static final ComponentsXmlTag FIELD_NAME = new ComponentsXmlTag( "field-name" );

    public static final ComponentsXmlTag IMPLEMENTATION = new ComponentsXmlTag( "implementation" );

    public static final ComponentsXmlTag LIFECYCLE_HANDLER = new ComponentsXmlTag( "lifecycle-handler", false, null );

    public static final ComponentsXmlTag REQUIREMENTS = new ComponentsXmlTag( "requirements", true,
                                                                              RequirementsElement.class );

    public static final ComponentsXmlTag CONFIGURATION = new ComponentsXmlTag( "configuration", true,
                                                                               ConfigurationElement.class );

    public static final ComponentsXmlTag REQUIREMENT = new ComponentsXmlTag( "requirement", true,
                                                                             RequirementElement.class );

    // All tags
    public static final ComponentsXmlTag[] values = {
        COMPONENT_SET,
        COMPONENTS,
        COMPONENT,
        ROLE,
        ROLE_HINT,
        FIELD_NAME,
        IMPLEMENTATION,
        LIFECYCLE_HANDLER,
        REQUIREMENTS,
        REQUIREMENT,
        CONFIGURATION };

    /**
     * @deprecated Use {@link #ComponentsXmlTag(String,boolean,Class)} instead
     */
    private ComponentsXmlTag( String tagName, boolean isMultipleAllowed )
    {
        this( tagName, isMultipleAllowed, null );
    }

    /**
     * @param tagName
     * @param isMultipleAllowed
     * @param mergeableClass
     *            Class that wraps this tag (as JDom element) and provides for
     *            merging same tags.
     */
    private ComponentsXmlTag( String tagName, boolean isMultipleAllowed, Class mergeableClass )
    {
        super( tagName, isMultipleAllowed, mergeableClass );
    }

    /**
     * By default we don't allow multiples of same tag names.
     * 
     * @param tagName
     */
    private ComponentsXmlTag( String tagName )
    {
        super( tagName, false, null );
    }

    /**
     * Returns an array of <b>all</b> tags that can appear in the
     * components.xml.
     * 
     * @return an array of <b>all</b> tags that can appear in the
     *         components.xml.
     */
    public static ComponentsXmlTag[] values()
    {
        return values;
    }

    /**
     * Looks up and returns an {@link ComponentsXmlTag} instance for the
     * specified tag name.
     * 
     * @param name
     *            key to look up the {@link ComponentsXmlTag} instance on.
     * @return {@link ComponentsXmlTag} instance whose name matches the name
     *         specified. Returns <code>null</code> if no match is found.
     */
    public static ComponentsXmlTag lookupTagInstanceByName( String name )
    {
        for ( int i = 0; i < values.length; i++ )
        {
            if ( values[i].getTagName().equals( name ) )
                return values[i];
        }
        // not found!
        return null;
    }

}
