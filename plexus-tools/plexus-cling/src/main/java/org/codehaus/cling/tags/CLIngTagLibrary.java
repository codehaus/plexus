/* Created on Sep 14, 2004 */
package org.codehaus.cling.tags;

import org.codehaus.marmalade.metamodel.AbstractMarmaladeTagLibrary;

/**
 * @author jdcasey
 */
public class CLIngTagLibrary extends AbstractMarmaladeTagLibrary
{

    public CLIngTagLibrary()
    {
        registerTag("application", AppTag.class);
        registerTag("classpath", ClasspathTag.class);
        registerTag("artifactId", DependencyArtifactTag.class);
        registerTag("groupId", DependencyGroupTag.class);
        registerTag("version", DependencyVersionTag.class);
        registerTag("dependency", DependencyTag.class);
        registerTag("environment", EnvironmentTag.class);
        registerTag("legalUsage", LegalUsageTag.class);
        registerTag("local", LocalClasspathEntryTag.class);
        registerTag("class", MainClassTag.class);
        registerTag("method", MainMethodTag.class);
        registerTag("main", MainTag.class);
        registerTag("description", OptionDescriptionTag.class);
        registerTag("longName", OptionLongNameTag.class);
        registerTag("multiArg", OptionMultiArgTag.class);
        registerTag("property", OptionPropertyTag.class);
        registerTag("required", OptionRequiredTag.class);
        registerTag("optionSet", OptionSetTag.class);
        registerTag("shortName", OptionShortNameTag.class);
        registerTag("option", OptionTag.class);
        registerTag("type", OptionTypeTag.class);
    }

}
