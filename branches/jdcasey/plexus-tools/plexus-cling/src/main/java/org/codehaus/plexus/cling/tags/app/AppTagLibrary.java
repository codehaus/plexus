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
        registerTag("mainClass", MainClassTag.class);
        registerTag("mainMethod", MainMethodTag.class);
        registerTag("main", MainTag.class);
        registerTag("optionDescription", OptionDescriptionTag.class);
        registerTag("optionLongName", OptionLongNameTag.class);
        registerTag("optionMultiArg", OptionMultiArgTag.class);
        registerTag("optionProperty", OptionPropertyTag.class);
        registerTag("optionRequired", OptionRequiredTag.class);
        registerTag("optionSet", OptionSetTag.class);
        registerTag("optionShortName", OptionShortNameTag.class);
        registerTag("option", OptionTag.class);
        registerTag("optionType", OptionTypeTag.class);
    }

}
