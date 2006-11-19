package org.codehaus.plexus.ldap.helper.factory;

import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultLdapFactoryHelper
    extends AbstractLogEnabled
    implements LdapFactoryHelper, Initializable
{
    // ----------------------------------------------------------------------
    // Configuration
    // ----------------------------------------------------------------------

    private List<AttributeType> attributeTypes;

    private List<ObjectClass> objectClasses;

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    private Map<String, AttributeTypeDescriptor> attributeDescriptors = new HashMap<String, AttributeTypeDescriptor>();

    private Map<String, ObjectClassDescriptor> objectClassDescriptors = new HashMap<String, ObjectClassDescriptor>();

    // ----------------------------------------------------------------------
    // LdapFactoryHelper Implementation
    // ----------------------------------------------------------------------

    public ObjectClassDescriptor getObjectClass( String objectClassName )
        throws LdapFactoryHelperException
    {
        ObjectClassDescriptor descriptor = objectClassDescriptors.get( objectClassName );

        if ( descriptor == null )
        {
            throw new LdapFactoryHelperException( "Missing object class descriptior for object class '" + objectClassName + "'." );
        }

        return descriptor;
    }

    public ObjectClassDescriptor getAggregatedObjectClass( String[] requiredObjectClasses )
        throws LdapFactoryHelperException
    {
        // TODO: Cache the aggregated descriptior

        ObjectClassDescriptor descriptor = new ObjectClassDescriptor();

        String name = null;

        for ( String requiredObjectClass : requiredObjectClasses )
        {
            if ( name == null )
            {
                name = requiredObjectClass;
            }
            else
            {
                name += ":" + requiredObjectClass;
            }
        }

        descriptor.setName( name );

        for ( String requiredObjectClass : requiredObjectClasses )
        {
            ObjectClassDescriptor objectClass = getObjectClass( requiredObjectClass );

            aggregate( objectClass, descriptor );

            ObjectClassDescriptor parent = objectClass.getParent();

            while( parent != null )
            {
                aggregate( parent, descriptor );

                parent = parent.getParent();
            }
        }

        return descriptor;
    }

    public ObjectClassDescriptor aggregate( ObjectClassDescriptor objectClass, ObjectClassDescriptor aggregatedDescriptor )
        throws LdapFactoryHelperException
    {
        // Add all the required attributes
        aggregatedDescriptor.getRequiredAttributes().putAll( objectClass.getRequiredAttributes() );

        // Add all the optional attributes
        aggregatedDescriptor.getOptionalAttributes().putAll( objectClass.getOptionalAttributes() );

        // Remove all attributes from the optional set that's in the required set
        aggregatedDescriptor.getOptionalAttributes().keySet().removeAll( aggregatedDescriptor.getRequiredAttributes().keySet() );

        return aggregatedDescriptor;
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
        if ( attributeTypes == null )
        {
            attributeTypes = new ArrayList<AttributeType>();
        }

        if ( objectClasses == null )
        {
            objectClasses = new ArrayList<ObjectClass>();
        }

        // ----------------------------------------------------------------------
        // Attribute types
        // ----------------------------------------------------------------------

        getLogger().debug( "Parsting attribute types. There are " + attributeTypes.size() + " attribute types registered.");

        for ( AttributeType attributeType : attributeTypes )
        {
            AttributeTypeDescriptor descriptor = new AttributeTypeDescriptor();

            // ----------------------------------------------------------------------
            // Name
            // ----------------------------------------------------------------------

            String name = StringUtils.clean( attributeType.getName() ).trim();

            if ( name.length() == 0 )
            {
                throw new InitializationException( "Missing required field 'name' on attribute type." );
            }

            descriptor.setPrimaryName( name );

            getLogger().debug( "Adding attribute '" + name + "'." );

            attributeDescriptors.put( name, descriptor );

            // ----------------------------------------------------------------------
            // Aliases
            // ----------------------------------------------------------------------

            StringTokenizer tokenizer = new StringTokenizer( StringUtils.clean( attributeType.getAliases() ), "," );

            while( tokenizer.hasMoreTokens() )
            {
                String alias = tokenizer.nextToken().trim();

                if ( alias.length() == 0 )
                {
                    continue;
                }

                descriptor.getAliases().add( alias );

                if ( attributeDescriptors.containsKey( alias ) )
                {
                    throw new InitializationException( "Attribute type is already aliased: '" + alias + "'." );
                }

                getLogger().debug( "Aliasing '" + descriptor.getPrimaryName() + "' to '" + alias + "'." );

                attributeDescriptors.put( alias, descriptor );
            }
        }

        getLogger().debug( "Parsting object classes. There are " + objectClasses.size() + " object classes registered.");

        for ( ObjectClass objectClass : objectClasses )
        {
            ObjectClassDescriptor descriptor = new ObjectClassDescriptor();

            // ----------------------------------------------------------------------
            // Name
            // ----------------------------------------------------------------------

            String name = StringUtils.clean( objectClass.getName() ).trim();

            if ( name.length() == 0 )
            {
                throw new InitializationException( "Missing required field 'name' on object class." );
            }

            descriptor.setName( name );

            getLogger().debug( "Adding object class: " + name );

            // ----------------------------------------------------------------------
            // Attributes
            // ----------------------------------------------------------------------

            String requiredAttributes = StringUtils.clean( objectClass.getRequiredAttributes() ).trim();

            StringTokenizer tokenizer = new StringTokenizer( requiredAttributes, "," );

            while( tokenizer.hasMoreTokens() )
            {
                String attributeTypeName = tokenizer.nextToken().trim();

                AttributeTypeDescriptor attribute = attributeDescriptors.get( attributeTypeName );

                if ( attribute == null )
                {
                    throw new InitializationException( "Missing attribute type '" + attributeTypeName + "' for object class '" + name + "'." );
                }

                getLogger().debug( " Required attribute '" + attribute.getPrimaryName() + "'." );
                descriptor.getRequiredAttributes().put( attribute.getPrimaryName(), attribute );
            }

            String optionalAttributes = StringUtils.clean( objectClass.getOptionalAttributes() ).trim();

            tokenizer = new StringTokenizer( optionalAttributes, "," );

            while( tokenizer.hasMoreTokens() )
            {
                String attributeTypeName = tokenizer.nextToken().trim();

                AttributeTypeDescriptor attribute = attributeDescriptors.get( attributeTypeName );

                if ( attribute == null )
                {
                    throw new InitializationException( "Missing attribute type '" + attributeTypeName + "' for object class '" + name + "'." );
                }

                getLogger().debug( " Optional attribute '" + attribute.getPrimaryName() + "'." );
                descriptor.getOptionalAttributes().put( attribute.getPrimaryName(), attribute );
            }

            objectClassDescriptors.put( name, descriptor );
        }

        // ----------------------------------------------------------------------
        // Set the parent references
        // ----------------------------------------------------------------------

        for ( ObjectClass objectClass : objectClasses )
        {
            ObjectClassDescriptor descriptor = objectClassDescriptors.get( objectClass.getName() );

            if ( StringUtils.isEmpty( objectClass.getInherits() ) )
            {
                continue;
            }

            ObjectClassDescriptor parent = objectClassDescriptors.get( objectClass.getInherits() );

            if ( parent == null )
            {
                throw new InitializationException( "No such parent '" + objectClass.getInherits() + "'." );
            }

            if ( descriptor.getParent() != null )
            {
                throw new InitializationException( "Configuration error, parent is already set for '" + objectClass.getName() + "'." );
            }

            descriptor.setParent( parent );
        }
    }
}
