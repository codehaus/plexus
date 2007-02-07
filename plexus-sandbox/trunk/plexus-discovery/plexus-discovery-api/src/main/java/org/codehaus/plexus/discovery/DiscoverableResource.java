package org.codehaus.plexus.discovery;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.net.URL;

/**
 * Resource found via ResourceDiscoverer
 *
 * @author Aldrin Leal
 * @author Jason van Zyl
 */
public class DiscoverableResource
    implements Comparable
{
    /**
     * Service ID
     */
    private String id;

    /**
     * Service Name
     */
    private String name;

    /**
     * Service Type
     */
    private String type;

    /**
     * Parent Resource Discoverer
     */
    private ResourceDiscoverer resourceDiscoverer;

    /**
     * Parent Resource Publisher
     */
    private ResourcePublisher resourcePublisher;

    /**
     * URL
     */
    private URL url;

    /**
     * Clean constructor
     */
    public DiscoverableResource()
    {
        this.id = "" + System.identityHashCode( this );
    }

    /**
     * gets the id
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * sets the id
     *
     * @param id the id to set
     */
    public void setId( String id )
    {
        this.id = id;
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name
     *
     * @param name the name to set
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /**
     * Gets the resource discoverer
     *
     * @return the resourceDiscoverer
     */
    public ResourceDiscoverer getResourceDiscoverer()
    {
        return resourceDiscoverer;
    }

    /**
     * Sets the resource discoverer
     *
     * @param resourceDiscoverer the resourceDiscoverer to set
     */
    public void setResourceDiscoverer( ResourceDiscoverer resourceDiscoverer )
    {
        this.resourceDiscoverer = resourceDiscoverer;
    }

    /**
     * Gets the URL
     *
     * @return the url
     */
    public final URL getUrl()
    {
        return url;
    }

    /**
     * Sets the URL
     *
     * @param url the url to set
     */
    public final void setUrl( URL url )
    {
        this.url = url;
    }

    /**
     * Gets the Type
     *
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Sets the Type
     *
     * @param type the type to set
     */
    public void setType( String type )
    {
        this.type = type;
    }

    /**
     * Gets the Current Resource Publisher for this Resource
     *
     * @return Resource Publisher in Use
     */
    public ResourcePublisher getResourcePublisher()
    {
        return resourcePublisher;
    }

    /**
     * Sets the Resource Publisher for this Resource
     *
     * @param resourcePublisher Resource Publisher to Set
     */
    public void setResourcePublisher( ResourcePublisher resourcePublisher )
    {
        this.resourcePublisher = resourcePublisher;
    }

    /**
     * Shorthand for releasing a DiscoverableResource on demand
     *
     * @throws ResourceDeregistrationException
     *          Someone has set us up the bomb
     */
    public void release()
        throws ResourceDeregistrationException
    {
        if ( null == this.resourcePublisher )
        {
            throw new IllegalStateException( "If we aren't being published, where we do release from? :))" );
        }

        this.resourcePublisher.deregisterResource( this );
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return new ToStringBuilder( this, ToStringStyle.DEFAULT_STYLE ).append( "id", this.id ).append( "name",
                                                                                                        this.name ).append(
            "type", this.type ).append( "url", this.url ).append( "resourceDiscoverer",
                                                                  this.resourceDiscoverer ).append( "resourcePublisher",
                                                                                                    this.resourcePublisher ).toString();
    }

    /**
     * {@inheritDoc}
     *
     * @return hashCode
     */
    public int hashCode()
    {
        return new HashCodeBuilder().append( this.id ).append( this.name ).append( this.type ).append(
            this.url ).append( this.resourceDiscoverer )
            .append( this.resourcePublisher ).toHashCode();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * <p/>
     * In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of <i>expression</i>
     * is negative, zero or positive.
     * <p/>
     * The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)<p>
     * <p/>
     * The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.<p>
     * <p/>
     * Finally, the implementer must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.<p>
     * <p/>
     * It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     *
     * @param o the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     * @throws ClassCastException if the specified object's type prevents it
     *                            from being compared to this Object.
     */
    public final int compareTo( Object o )
    {
        if ( o == this )
        {
            return 0;
        }

        DiscoverableResource anotherResource = (DiscoverableResource) o;

        return new CompareToBuilder().append( this.getUrl().toString(),
                                              anotherResource.getUrl().toString() ).toComparison();
    }
}
