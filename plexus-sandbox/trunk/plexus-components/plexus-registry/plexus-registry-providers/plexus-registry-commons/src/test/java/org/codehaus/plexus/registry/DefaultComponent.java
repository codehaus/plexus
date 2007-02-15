package org.codehaus.plexus.registry;

/*
 * Copyright 2007, Brett Porter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Test component.
 */
public class DefaultComponent
    implements Component
{
    private String key;

    private Properties properties;

    private Map map;

    private List list;

    private String configKey;

    private Properties configProperties;

    private Map configMap;

    private List configList;

    public String getConfigKey()
    {
        return configKey;
    }

    public Properties getConfigProperties()
    {
        return configProperties;
    }

    public String getKey()
    {
        return key;
    }

    public Properties getProperties()
    {
        return properties;
    }

    public Map getMap()
    {
        return map;
    }

    public List getList()
    {
        return list;
    }

    public Map getConfigMap()
    {
        return configMap;
    }

    public List getConfigList()
    {
        return configList;
    }
}
