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

import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.repository.cdc.ComponentDescriptor;

/**
 * An abstraction to allow pluggable {@link ComponentDescriptor} extraction to be dropped.
 *
 * @version $Id$
 */
public interface ComponentDescriptorExtractor
{
    String ROLE = ComponentDescriptorExtractor.class.getName();

    String COMPILE_SCOPE = "compile";

    String TEST_SCOPE = "test";

    List/*<ComponentDescriptor>*/ extract(MavenProject project, String scope, ComponentDescriptor[] roleDefaults) throws Exception;
}
