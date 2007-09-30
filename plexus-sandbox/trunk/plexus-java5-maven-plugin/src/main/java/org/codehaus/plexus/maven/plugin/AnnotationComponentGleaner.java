/*
 * Copyright (C) 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.plexus.maven.plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Configuration;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;
import org.codehaus.plexus.component.repository.cdc.ComponentRequirement;
import org.codehaus.plexus.component.repository.cdc.ComponentRequirementList;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;

/**
 * ???
 *
 * @version $Id$
 */
public class AnnotationComponentGleaner
{
    private static final String EMPTY_STRING = "";

    public ComponentDescriptor glean(final Class<?> type) {
        assert type != null;

        Component anno = type.getAnnotation(Component.class);

        if (anno == null) {
            return null;
        }

        ComponentDescriptor desc = new ComponentDescriptor();

        desc.setImplementation(type.getName());
        desc.setRole(anno.role().getName());
        desc.setRoleHint(filterEmptyAsNull(anno.hint()));
        desc.setVersion(filterEmptyAsNull(anno.version()));
        desc.setAlias(filterEmptyAsNull(anno.alias()));
        desc.setLifecycleHandler(filterEmptyAsNull(anno.lifecycleHandler()));
        desc.setInstantiationStrategy(filterEmptyAsNull(anno.instantiationStrategy()));
        desc.setComponentFactory(filterEmptyAsNull(anno.factory()));
        desc.setComponentType(filterEmptyAsNull(anno.type()));
        desc.setComponentProfile(filterEmptyAsNull(anno.profile()));
        desc.setComponentComposer(filterEmptyAsNull(anno.composer()));
        desc.setComponentConfigurator(filterEmptyAsNull(anno.configurator()));

        for (Class t : getClasses(type)) {
            for (Field field : t.getDeclaredFields()) {
                ComponentRequirement requirement = findRequirement(field);

                if (requirement != null) {
                    desc.addRequirement(requirement);
                }

                PlexusConfiguration config = findConfiguration(field);

                if (config != null) {
                    if (desc.getConfiguration() == null) {
                        desc.setConfiguration(new XmlPlexusConfiguration("configuration"));
                    }
                    
                    desc.getConfiguration().addChild(config);
                }
            }

            //
            // TODO: Inspect methods?
            //
        }

        return desc;
    }

    private String filterEmptyAsNull(final String value) {
        if (value == null) {
            return null;
        }
        else if (EMPTY_STRING.equals(value.trim())) {
            return null;
        }
        else {
            return value;
        }
    }

    private List<Class> getClasses(Class<?> type) {
        assert type != null;

        List<Class> classes = new ArrayList<Class>();

        while (type != null) {
            classes.add(type);
            type = type.getSuperclass();
        }

        return classes;
    }

    private boolean isRequirementListType(final Class<?> type) {
        assert type != null;

        return Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type);
    }

    private ComponentRequirement findRequirement(final Field field) {
        assert field != null;

        Requirement anno = field.getAnnotation(Requirement.class);

        if (anno == null) {
            return null;
        }

        Class<?> type = field.getType();

        ComponentRequirement requirement;

        if (isRequirementListType(type)) {
            requirement = new ComponentRequirementList();

            String[] hints = anno.hints();

            if (hints != null && hints.length > 0) {
                ((ComponentRequirementList)requirement).setRoleHints(Arrays.asList(hints));
            }

            //
            // TODO: See if we can glean any type details out of any generic information from the map or collection
            //
        }
        else {
            requirement = new ComponentRequirement();

            requirement.setRoleHint(filterEmptyAsNull(anno.hint()));
        }

        if (anno.role().isAssignableFrom(Object.class)) {
            requirement.setRole(type.getName());
        }
        else {
            requirement.setRole(anno.role().getName());
        }
        
        requirement.setFieldName(field.getName());
        
        requirement.setFieldMappingType(type.getName());

        return requirement;
    }

    private PlexusConfiguration findConfiguration(final Field field) {
        assert field != null;

        Configuration anno = field.getAnnotation(Configuration.class);

        if (anno == null) {
            return null;
        }

        String name = filterEmptyAsNull(anno.name());
        if (name == null) {
            name = field.getName();
        }

        XmlPlexusConfiguration config = new XmlPlexusConfiguration(name);

        String value = filterEmptyAsNull(anno.value());
        if (value != null) {
            config.setValue(value);
        }

        return config;
    }
}
