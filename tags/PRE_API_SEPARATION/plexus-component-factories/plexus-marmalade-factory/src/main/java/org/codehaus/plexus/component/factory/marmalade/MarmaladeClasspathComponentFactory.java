/* Created on Aug 6, 2004 */
package org.codehaus.plexus.component.factory.marmalade;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.marmalade.metamodel.MarmaladeTaglibResolver;
import org.codehaus.marmalade.model.MarmaladeScript;
import org.codehaus.marmalade.model.MarmaladeTag;
import org.codehaus.marmalade.parsetime.CachingScriptParser;
import org.codehaus.marmalade.parsetime.DefaultParsingContext;
import org.codehaus.marmalade.parsetime.MarmaladeModelBuilderException;
import org.codehaus.marmalade.parsetime.MarmaladeParsetimeException;
import org.codehaus.marmalade.parsetime.MarmaladeParsingContext;
import org.codehaus.marmalade.parsetime.ScriptBuilder;
import org.codehaus.marmalade.parsetime.ScriptParser;
import org.codehaus.marmalade.runtime.DefaultContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionContext;
import org.codehaus.marmalade.runtime.MarmaladeExecutionException;
import org.codehaus.marmalade.util.RecordingReader;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.factory.AbstractComponentFactory;
import org.codehaus.plexus.component.factory.ComponentInstantiationException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;

/** Component factory for reading marmalade scripts off of the classpath (realm)
 * and executing them. The root of the script should be a tag which implements
 * PlexusComponentTag, and the result of the script execution is the 
 * component. Scripts are cached in the caching parser.
 * 
 * @author jdcasey
 */
public class MarmaladeClasspathComponentFactory extends AbstractMarmaladeComponentFactory
{

    protected String getScriptLocation(ComponentDescriptor componentDescriptor) {
        return componentDescriptor.getImplementation() + ".mmld";
    }
}
