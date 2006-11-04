package org.codehaus.plexus.cotlet;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.Type;
import org.codehaus.plexus.cotlet.model.ComponentConfigurationTag;
import org.codehaus.plexus.cotlet.model.ComponentImplementation;
import org.codehaus.plexus.cotlet.model.ComponentRequirement;
import org.codehaus.plexus.cotlet.model.ComponentSpecification;
import org.codehaus.plexus.cotlet.model.CotletModel;
import org.codehaus.plexus.cotlet.model.LeafTypes;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultCotlet implements Cotlet
{

    public CotletModel buildModel( File[] directories )
    {
        JavaDocBuilder builder = new JavaDocBuilder();

        for ( int i = 0; i < directories.length; i++ )
        {
            File directory = directories[ i ];

            builder.addSourceTree( directory );
        }

        CotletModel cotletModel = new CotletModel();

        JavaClass[] javaClasses = builder.getClasses();

        for ( int i = 0; i < javaClasses.length; i++ )
        {
            JavaClass javaClass = javaClasses[ i ];

            DocletTag implementationTag = javaClass.getTagByName( "component.implementation" );

            if ( implementationTag != null )
            {
                detectImplementation( implementationTag, javaClass, cotletModel, builder );
            }

            DocletTag specificationTag = javaClass.getTagByName( "component.specification" );

            if ( specificationTag != null )
            {
                detectSpecification( specificationTag, javaClass, cotletModel );
            }
        }

        return cotletModel;

    }

    private void detectImplementation( DocletTag tag, JavaClass javaClass, CotletModel cotletModel, JavaDocBuilder builder )
    {
        ComponentImplementation implementation = new ComponentImplementation();

        implementation.setDescription( javaClass.getComment() );

        implementation.setName( javaClass.getFullyQualifiedName() );

        String role = tag.getNamedParameter( "role" );

        if ( role == null )
        {
            System.out.println( "ROLE iS NULL --------------->" );

            JavaClass[] interfaces = javaClass.getImplementedInterfaces();

            if ( interfaces != null )
            {
                if ( interfaces.length == 1 )
                {
                    role = interfaces[ 0 ].getFullyQualifiedName();
                }
                else
                {
                    for ( int j = 0; j < interfaces.length; j++ )
                    {
                        JavaClass anInterface = interfaces[ j ];

                        if ( anInterface.getTagByName( "component.specification" ) != null )
                        {
                            role = anInterface.getFullyQualifiedName();

                            break;
                        }

                        if ( anInterface.getFieldByName( "ROLE" ) != null )
                        {
                            role = anInterface.getFullyQualifiedName();

                            break;

                        }
                    }
                }
            }
        }

        String roleHint = tag.getNamedParameter( "role-hint" );

        String version = tag.getNamedParameter( "version" );

        implementation.setRole( role );

        implementation.setRoleHint( roleHint );

        implementation.setVersion( version );

        JavaClass c = javaClass;

        while ( c != null && c.getSource() != null )
        {
            System.out.println( "processing class: " + c.getFullyQualifiedName() );

            detectRequirements( implementation, c );

            detectConfigurationTags( implementation, c, builder );

            c = c.getSuperJavaClass();
        }

        cotletModel.addImplementation( implementation );

    }

    private void detectConfigurationTags( ComponentImplementation implementation, JavaClass javaClass, JavaDocBuilder builder )
    {
        JavaField[] fields = javaClass.getFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[ i ];

            DocletTag configTag = field.getTagByName( "component.configuration" );

            if ( configTag != null )
            {
                ComponentConfigurationTag componentConfigurationTag = new ComponentConfigurationTag();

                String defaultValue = configTag.getNamedParameter( "defaultValue" );

                componentConfigurationTag.setDefaultValue( defaultValue );

                componentConfigurationTag.setClassName( field.getType().getValue() );

                componentConfigurationTag.setName( field.getName() );

                componentConfigurationTag.setDescription( field.getComment() );

                findChildernTags( componentConfigurationTag, field.getType().getJavaClass(), builder );

                implementation.addConfigurationTag( componentConfigurationTag );
            }
        }
    }

    private void findChildernTags( ComponentConfigurationTag configurationTag, JavaClass javaClass, JavaDocBuilder builder )
    {
        if ( LeafTypes.contains( javaClass.getFullyQualifiedName() ) )
        {
            return;
        }

        if ( javaClass.isA( Collection.class.getName() ) )
        {
            System.out.println( "configurationTag: is collection: " + configurationTag.getName() );

            configurationTag.setCollection( true );

            ComponentConfigurationTag childTag = new ComponentConfigurationTag();

            childTag.setClassName( configurationTag.getElementImlementation() );

            String implementation = configurationTag.getElementImlementation();

            System.out.println( "Implementation: " + implementation );

            if ( implementation != null )
            {
                String name = implementation.substring( implementation.lastIndexOf( "." ) + 1 );

                name = StringUtils.lowercaseFirstLetter( name );

                childTag.setName( name );

                configurationTag.setElementType( childTag );

                JavaClass childClass = builder.getClassByName( implementation );

                if ( childClass != null )
                {
                    findChildernTags( childTag, childClass, builder );
                }
            }

            return;
        }
        else
        {
            System.out.println( "configurationTag: is not collection: " + configurationTag.getName() );
        }

        if ( javaClass != null )
        {
            JavaField[] fields = javaClass.getFields();

            if ( configurationTag.getDescription() == null )
            {
                configurationTag.setDescription( javaClass.getComment() );
            }

            for ( int i = 0; i < fields.length; i++ )
            {
                JavaField field = fields[ i ];

                DocletTag configTag = field.getTagByName( "component.configuration" );

                if ( configTag != null )
                {
                    ComponentConfigurationTag childConfigurationTag = new ComponentConfigurationTag();

                    childConfigurationTag.setDescription( field.getComment() );

                    childConfigurationTag.setName( field.getName() );

                    String implementation = field.getType().getValue();

                    childConfigurationTag.setClassName( implementation );

                    configurationTag.addChild( childConfigurationTag );

                    JavaClass childClass = builder.getClassByName( implementation );


                    System.out.println( "configurationTag: " + configurationTag.getName() );

                    System.out.println( "javaClass: " + javaClass.getFullyQualifiedName() );

                    System.out.println( "javaClass is collection: " + javaClass.isA( Collection.class.getName() ) );

                    String elementImplementation = configTag.getNamedParameter( "elements" );

                    System.out.println( "elementImlementation " + elementImplementation );

                    System.out.println( "Element type should be: " + elementImplementation );

                    childConfigurationTag.setElementImlementation( elementImplementation );


                    findChildernTags( childConfigurationTag, childClass, builder );
                }
            }
        }
    }


    private void detectSpecification( DocletTag tag, JavaClass javaClass, CotletModel cotletModel )
    {
        ComponentSpecification specification = new ComponentSpecification();

        specification.setDescription( javaClass.getComment() );

        specification.setName( javaClass.getFullyQualifiedName() );

        cotletModel.addSpecification( specification );
    }


    private void detectRequirements( ComponentImplementation implementation, JavaClass javaClass )
    {
        JavaField[] fields = javaClass.getFields();

        for ( int i = 0; i < fields.length; i++ )
        {
            JavaField field = fields[ i ];

            DocletTag requirementTag = field.getTagByName( "component.requirement" );

            if ( requirementTag != null )
            {

                Type type = field.getType();

                ComponentRequirement requirement = new ComponentRequirement();

                String role = requirementTag.getNamedParameter( "role" );

                if ( role == null )
                {
                    role = type.getValue();
                }

                requirement.setRole( role );

                String property = requirementTag.getNamedParameter( "property" );

                if ( property == null )
                {
                    property = field.getName();
                }

                requirement.setProperty( property );

                String cardinality = "1";

                // @todo - this is a hack - cardinality should be discovered out in a smarter way
                if ( type.isArray() )
                {
                    cardinality = "N";
                }

                else if ( type.getValue().equals( Map.class.getName() ) )
                {
                    cardinality = "N";
                }

                else if ( type.getValue().equals( List.class.getName() ) )
                {
                    cardinality = "N";
                }
                else if ( type.getValue().equals( Set.class.getName() ) )
                {
                    cardinality = "N";
                }

                requirement.setCardinality( cardinality );

                implementation.addRequirement( requirement );
            }
        }
    }

}
