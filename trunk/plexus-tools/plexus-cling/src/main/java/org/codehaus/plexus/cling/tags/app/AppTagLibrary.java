/* Created on Sep 14, 2004 */
package org.codehaus.plexus.cling.tags.app;

import org.codehaus.marmalade.metamodel.AbstractMarmaladeTagLibrary;

/**
 * @author jdcasey
 */
public class AppTagLibrary extends AbstractMarmaladeTagLibrary
{

    public AppTagLibrary()
    {
        registerTag("application", AppTag.class);
        registerTag("applicationDescription", AppDescriptionTag.class);
        registerTag("argumentDescription", ArgDescriptionTag.class);
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
        registerTag("optionDescription", OptionDescriptionTag.class);
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
