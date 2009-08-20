package org.codehaus.plexus.component.configurator;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.File;
import java.io.StringReader;
import java.lang.annotation.ElementType;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.configurator.expression.TypeAwareExpressionEvaluator;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.io.PlexusTools;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;

/**
 * @author <a href="mailto:michal@codehaus.org">Michal Maczka</a>
 * @version $Id$
 */
public abstract class AbstractComponentConfiguratorTest
    extends PlexusTestCase
{
    protected void configureComponent(Object component, ComponentDescriptor descriptor, ClassRealm realm) throws Exception {
        ComponentConfigurator cc = getComponentConfigurator();
        cc.configureComponent( component, descriptor.getConfiguration(), realm );
    }


    protected void configureComponent(Object component, ComponentDescriptor descriptor, ClassRealm realm, ExpressionEvaluator expressionEvaluator) throws Exception {
        ComponentConfigurator cc = getComponentConfigurator();
        cc.configureComponent( component, descriptor.getConfiguration(), expressionEvaluator, realm );
    }

    protected abstract ComponentConfigurator getComponentConfigurator() throws Exception;

    public void testComponentConfigurator()
        throws Exception
    {
        String xml = "<configuration>" +
                "  <boolean-value>true</boolean-value>" +
                "  <byte-value>64</byte-value>" +
                "  <short-value>-128</short-value>" +
                "  <int-value>-1</int-value>" +
                "  <float-value>1</float-value>" +
                "  <long-value>2</long-value>" +
                "  <double-value>3</double-value>" +
                "  <char-value>X</char-value>" +
                "  <string-value>foo</string-value>" +
                "  <file-value>test.txt</file-value>" +
                "  <uri-value>http://www.apache.org/</uri-value>" +
                "  <url-value>http://maven.apache.org/</url-value>" +
                "  <important-things>" +
                "    <important-thing><name>jason</name></important-thing>" +
                "    <important-thing><name>tess</name></important-thing>" +
                "  </important-things>" +
                "  <configuration>" +
                "      <name>jason</name>" +
                "  </configuration>" +
                "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ConfigurableComponent component = new ConfigurableComponent();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        assertEquals( "check boolean value", true, component.getBooleanValue() );

        assertEquals( "check byte value", 64, component.getByteValue() );

        assertEquals( "check short value", -128, component.getShortValue() );

        assertEquals( "check integer value", -1, component.getIntValue() );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertEquals( "check long value", 2L, component.getLongValue() );

        assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertEquals( 'X', component.getCharValue() );

        assertEquals( "foo", component.getStringValue() );

        assertEquals( new File( "test.txt" ), component.getFileValue() );

        assertEquals( new URI( "http://www.apache.org/" ), component.getUriValue() );

        assertEquals( new URL( "http://maven.apache.org/" ), component.getUrlValue() );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( "tess", ( (ImportantThing) list.get( 1 ) ).getName() );

        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );
    }

    public void testComponentConfiguratorWithAComponentThatProvidesSettersForConfiguration()
        throws Exception
    {
        String xml = "<configuration>" + "  <int-value>0</int-value>" + "  <float-value>1</float-value>"
            + "  <long-value>2</long-value>" + "  <double-value>3</double-value>"
            + "  <string-value>foo</string-value>" + "  <important-things>"
            + "    <important-thing><name>jason</name></important-thing>"
            + "    <important-thing><name>tess</name></important-thing>" + "  </important-things>"
            + "  <configuration>" + "      <name>jason</name>" + "  </configuration>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithSetters component = new ComponentWithSetters();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        assertEquals( "check integer value", 0, component.getIntValue() );

        assertTrue( component.intValueSet );

        assertEquals( "check float value", 1.0f, component.getFloatValue(), 0.001f );

        assertTrue( component.floatValueSet );

        assertEquals( "check long value", 2L, component.getLongValue() );

        assertTrue( component.longValueSet );

        assertEquals( "check double value", 3.0, component.getDoubleValue(), 0.001 );

        assertTrue( component.doubleValueSet );

        assertEquals( "foo", component.getStringValue() );

        assertTrue( component.stringValueSet );

        List list = component.getImportantThings();

        assertEquals( 2, list.size() );

        assertEquals( "jason", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( "tess", ( (ImportantThing) list.get( 1 ) ).getName() );

        assertTrue( component.importantThingsValueSet );

        // Embedded Configuration

        PlexusConfiguration c = component.getConfiguration();

        assertEquals( "jason", c.getChild( "name" ).getValue() );

        assertTrue( component.configurationValueSet );
    }

    public void testComponentConfigurationWhereFieldsToConfigureResideInTheSuperclass()
        throws Exception
    {
        String xml = "<configuration>" + "  <name>jason</name>" + "  <address>bollywood</address>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        DefaultComponent component = new DefaultComponent();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        assertEquals( "jason", component.getName() );

        assertEquals( "bollywood", component.getAddress() );
    }

    public void testComponentConfigurationWhereFieldsAreCollections()
        throws Exception
    {
        String xml = "<configuration>" + "  <vector>" + "    <important-thing>" + "       <name>life</name>"
            + "    </important-thing>" + "  </vector>" + "  <hashSet>" + "    <important-thing>"
            + "       <name>life</name>" + "    </important-thing>" + "  </hashSet>"
            + "   <list implementation=\"java.util.LinkedList\">" + "     <important-thing>"
            + "       <name>life</name>" + "    </important-thing>" + "  </list>" + "  <stringList>"
            + "    <something>abc</something>" + "    <somethingElse>def</somethingElse>" + "  </stringList>"
            + "   <set><something>abc</something></set>"
            + "   <sortedSet><something>abc</something></sortedSet>" +
            // TODO: implement List<int> etc..
            //  "<intList>" +
            //  "  <something>12</something>" +
            //  "  <somethingElse>34</somethingElse>" +
            //  "</intList>" +
            "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithCollectionFields component = new ComponentWithCollectionFields();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        Vector vector = component.getVector();

        assertEquals( "life", ( (ImportantThing) vector.get( 0 ) ).getName() );

        assertEquals( 1, vector.size() );

        Set set = component.getHashSet();

        assertEquals( 1, set.size() );

        Object[] setContents = set.toArray();

        assertEquals( "life", ( (ImportantThing) setContents[0] ).getName() );

        List list = component.getList();

        assertEquals( list.getClass(), LinkedList.class );

        assertEquals( "life", ( (ImportantThing) list.get( 0 ) ).getName() );

        assertEquals( 1, list.size() );

        List stringList = component.getStringList();

        assertEquals( "abc", (String) stringList.get( 0 ) );

        assertEquals( "def", (String) stringList.get( 1 ) );

        assertEquals( 2, stringList.size() );

        set = component.getSet();

        assertEquals( 1, set.size() );

        set = component.getSortedSet();

        assertEquals( 1, set.size() );
    }

    public void testComponentConfigurationWhereFieldsAreArrays()
        throws Exception
    {
        String xml = "<configuration>" + "  <stringArray>" + "    <first-string>value1</first-string>"
            + "    <second-string>value2</second-string>" + "  </stringArray>" + "  <integerArray>"
            + "    <firstInt>42</firstInt>" + "    <secondInt>69</secondInt>" + "  </integerArray>"
            + "  <importantThingArray>" + "    <importantThing><name>Hello</name></importantThing>"
            + "    <importantThing><name>World!</name></importantThing>" + "  </importantThingArray>"
            + "  <objectArray>" + "    <java.lang.String>some string</java.lang.String>"
            + "    <importantThing><name>something important</name></importantThing>"
            + "    <whatever implementation='java.lang.Integer'>303</whatever>" + "  </objectArray>" + "  <urlArray>"
            + "    <url>http://foo.com/bar</url>" + "    <url>file://localhost/c:/windows</url>" + "  </urlArray>"
            + "  <fileArray>" + "    <file>c:/windows</file>" + "    <file>/usr/local/bin/foo.sh</file>"
            + "  </fileArray>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithArrayFields component = new ComponentWithArrayFields();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        String[] stringArray = component.getStringArray();

        assertEquals( 2, stringArray.length );

        assertEquals( "value1", stringArray[0] );

        assertEquals( "value2", stringArray[1] );

        Integer[] integerArray = component.getIntegerArray();

        assertEquals( 2, integerArray.length );

        assertEquals( new Integer( 42 ), integerArray[0] );

        assertEquals( new Integer( 69 ), integerArray[1] );

        ImportantThing[] importantThingArray = component.getImportantThingArray();

        assertEquals( 2, importantThingArray.length );

        assertEquals( "Hello", importantThingArray[0].getName() );

        assertEquals( "World!", importantThingArray[1].getName() );

        Object[] objectArray = component.getObjectArray();

        assertEquals( 3, objectArray.length );

        assertEquals( "some string", objectArray[0] );

        assertEquals( "something important", ( (ImportantThing) objectArray[1] ).getName() );

        assertEquals( new Integer( 303 ), objectArray[2] );

        URL[] urls = component.getUrlArray();

        assertEquals( new URL( "http://foo.com/bar" ), urls[0] );

        assertEquals( new URL( "file://localhost/c:/windows" ), urls[1] );

        File[] files = component.getFileArray();

        assertEquals( new File( "c:/windows" ), files[0] );

        assertEquals( new File( "/usr/local/bin/foo.sh" ), files[1] );
    }

    public void testComponentConfigurationWithCompositeFields()
        throws Exception
    {

        String xml = "<configuration>"
            + "  <thing implementation=\"org.codehaus.plexus.component.configurator.ImportantThing\">"
            + "     <name>I am not abstract!</name>" + "  </thing>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithCompositeFields component = new ComponentWithCompositeFields();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        assertNotNull( component.getThing() );

        assertEquals( "I am not abstract!", component.getThing().getName() );

    }

    public void testInvalidComponentConfiguration()
        throws Exception
    {

        String xml = "<configuration><goodStartElement>theName</badStopElement></configuration>";

        try
        {
            PlexusTools.buildConfiguration( "<Test-Invalid>", new StringReader( xml ) );

            fail( "Should have caused an error because of the invalid XML." );
        }
        catch ( PlexusConfigurationException e )
        {
            // Error should be caught here.
        }
        catch ( Exception e )
        {
            fail( "Should have caught the invalid plexus configuration exception." );
        }

    }

    public void testComponentConfigurationWithPropertiesFields()
        throws Exception
    {

        String xml = "<configuration>" + "  <someProperties>" + "     <property>" + "        <name>firstname</name>"
            + "        <value>michal</value>" + "     </property>" + "     <property>"
            + "        <name>lastname</name>" + "        <value>maczka</value>" + "     </property>"
            + "  </someProperties>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithPropertiesField component = new ComponentWithPropertiesField();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        Properties properties = component.getSomeProperties();

        assertNotNull( properties );

        assertEquals( "michal", properties.get( "firstname" ) );

        assertEquals( "maczka", properties.get( "lastname" ) );

    }

    public void testComponentConfigurationWithPropertiesFieldsWithExpression()
        throws Exception
    {

        String xml = "<configuration>" + " <someProperties> ${injectedProperties} </someProperties>" + "</configuration>";

        final Properties propertiesInterpolated = new Properties();
        propertiesInterpolated.put( "firstname", "olivier" );
        propertiesInterpolated.put( "lastname", "lamy" );

        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator()
        {
            public Object evaluate( String expression )
            {
                return propertiesInterpolated;
            }

            public File alignToBaseDirectory( File file )
            {
                return null;
            }
        };

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithPropertiesField component = new ComponentWithPropertiesField();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm, expressionEvaluator);

        Properties properties = component.getSomeProperties();

        assertNotNull( properties );

        assertEquals( "olivier", properties.get( "firstname" ) );

        assertEquals( "lamy", properties.get( "lastname" ) );

    }

    public void testComponentConfigurationWithMapField()
        throws Exception
    {
        String xml = "<configuration>" + "  <map>" + "     <firstName>Kenney</firstName>"
            + "     <lastName>Westerhof</lastName>" + "  </map>" + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithMapField component = new ComponentWithMapField();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration(configuration);

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent(component, descriptor, realm);

        Map map = component.getMap();

        assertNotNull( map );

        assertEquals( "Kenney", map.get( "firstName" ) );

        assertEquals( "Westerhof", map.get( "lastName" ) );

    }

    public void testComponentConfigurationWhereFieldIsBadArray()
        throws Exception
    {
        String xml = "<configuration>" //
            + "  <integerArray><java.lang.String>string</java.lang.String></integerArray>" //
            + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithArrayFields component = new ComponentWithArrayFields();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration( configuration );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        try
        {
            configureComponent( component, descriptor, realm );
            fail( "Configuration did not fail" );
        }
        catch ( ComponentConfigurationException e )
        {
            // expected
            e.printStackTrace();
        }
    }

    public void testComponentConfigurationWhereFieldIsEnum()
        throws Exception
    {
        String xml = "<configuration>" //
            + "  <simpleEnum>TYPE</simpleEnum>" //
            + "  <nestedEnum>ONE</nestedEnum>" //
            + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        ComponentWithEnumFields component = new ComponentWithEnumFields();

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration( configuration );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent( component, descriptor, realm );

        assertEquals( ElementType.TYPE, component.getSimpleEnum() );

        assertEquals( ComponentWithEnumFields.NestedEnum.ONE, component.getNestedEnum() );
    }

    public void testComponentConfigurationWithAmbiguousExpressionValue()
        throws Exception
    {
        String xml = "<configuration>" //
            + "  <address>${address}</address>" //
            + "</configuration>";

        PlexusConfiguration configuration = PlexusTools.buildConfiguration( "<Test>", new StringReader( xml ) );

        DefaultComponent component = new DefaultComponent();

        ExpressionEvaluator expressionEvaluator = new TypeAwareExpressionEvaluator()
        {
            public Object evaluate( String expression )
                throws ExpressionEvaluationException
            {
                return evaluate( expression, null );
            }

            public File alignToBaseDirectory( File file )
            {
                return null;
            }

            public Object evaluate( String expression, Class<?> type )
                throws ExpressionEvaluationException
            {
                if ( String.class == type )
                {
                    return "PASSED";
                }
                else
                {
                    return Boolean.FALSE;
                }
            }
        };

        ComponentDescriptor descriptor = new ComponentDescriptor();

        descriptor.setRole( "role" );

        descriptor.setImplementation( component.getClass().getName() );

        descriptor.setConfiguration( configuration );

        ClassWorld classWorld = new ClassWorld();

        ClassRealm realm = classWorld.newRealm( "test", getClass().getClassLoader() );

        configureComponent( component, descriptor, realm, expressionEvaluator );

        assertEquals( "PASSED", component.getAddress() );
    }

}
