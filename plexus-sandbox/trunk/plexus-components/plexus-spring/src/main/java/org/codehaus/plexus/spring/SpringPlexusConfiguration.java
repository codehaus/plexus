package org.codehaus.plexus.spring;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SpringPlexusConfiguration
	implements PlexusConfiguration
{

	private Element element;

	public SpringPlexusConfiguration(Element child) {
		this.element = child;
	}

	public void addChild(PlexusConfiguration configuration) {
		throw new UnsupportedOperationException( "immutable" );
	}

	public String getAttribute(String paramName)
			throws PlexusConfigurationException {
		throw new UnsupportedOperationException( "not done yet" );
	}

	public String getAttribute(String name, String defaultValue) {
		throw new UnsupportedOperationException( "not done yet" );
	}

	public String[] getAttributeNames() {
		throw new UnsupportedOperationException( "not done yet" );
	}

	public PlexusConfiguration getChild(String child) {
		if ( element == null )
		{
			return null;
		}
		
		return new SpringPlexusConfiguration( (Element) element.getElementsByTagName(child).item(0) );
	}

	public PlexusConfiguration getChild(int i) {
		if ( element == null )
		{
			return null;
		}
		
		return new SpringPlexusConfiguration( (Element) element.getChildNodes().item(i) );
	}

	public PlexusConfiguration getChild(String child, boolean createChild) {
		throw new UnsupportedOperationException( "immutable" );
	}

	public int getChildCount() {
		if ( element == null )
		{
			return 0;
		}
		
		return element.getChildNodes().getLength();
	}

	public PlexusConfiguration[] getChildren() {
		if ( element == null )
		{
			return new PlexusConfiguration[0];
		}
		
		NodeList l = element.getChildNodes();
		PlexusConfiguration[] value = new PlexusConfiguration[l.getLength()];
		for ( int i = 0; i < l.getLength(); i++ )
		{
			value[i] = new SpringPlexusConfiguration((Element) l.item(i));
		}
		return value;
	}

	public PlexusConfiguration[] getChildren(String name) {
		if ( element == null )
		{
			return new PlexusConfiguration[0];
		}
		
		NodeList l = element.getElementsByTagName(name);
		PlexusConfiguration[] value = new PlexusConfiguration[l.getLength()];
		for ( int i = 0; i < l.getLength(); i++ )
		{
			value[i] = new SpringPlexusConfiguration((Element) l.item(i));
		}
		return value;
	}

	public String getName() {
		if ( element == null )
		{
			return null;
		}
		return element.getLocalName();
	}

	public String getValue() {
		if ( element == null )
		{
			return null;
		}
		return element.getTextContent();
	}

	public String getValue(String defaultValue) {
		String value = getValue();

		return value != null ? value : defaultValue;
	}
	
}
