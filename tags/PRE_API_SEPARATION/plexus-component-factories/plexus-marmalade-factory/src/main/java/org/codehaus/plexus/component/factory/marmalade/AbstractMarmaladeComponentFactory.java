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

/** Component factory for reading marmalade scripts and executing them to return
 * a component. The root of the script should be a tag which implements
 * PlexusComponentTag, and this root tag will return a component via the 
 * getComponent() method. Scripts are cached in the caching parser.
 * 
 * @author jdcasey
 */
public abstract class AbstractMarmaladeComponentFactory extends AbstractComponentFactory
{
    private CachingScriptParser cachingParser = new CachingScriptParser();
    
    protected AbstractMarmaladeComponentFactory() {}
    
    public Object newInstance( ComponentDescriptor componentDescriptor,
        ClassRealm classRealm, PlexusContainer container )
        throws ComponentInstantiationException
    {
        String scriptLocation = getScriptLocation(componentDescriptor);
        MarmaladeParsingContext parsingContext = buildParsingContext(scriptLocation, classRealm);
        MarmaladeScript script = getScriptInstance(parsingContext);
        
        Object result = executeScript(script);
        
        return result;
    }
    
    protected abstract String getScriptLocation(ComponentDescriptor componentDescriptor);

    protected Object executeScript(MarmaladeScript script) 
    throws ComponentInstantiationException
    {
        PlexusComponentTag componentTag = (PlexusComponentTag)script.getRoot();
        
        MarmaladeExecutionContext execCtx = new DefaultContext();
        try {
            script.execute(execCtx);
        }
        catch (MarmaladeExecutionException e) {
            throw new ComponentInstantiationException("failed to execute component script: " + script.getLocation(), e);
        }
        
        Object result = componentTag.getComponent();
        
        return result;
    }
    
    protected MarmaladeScript getScriptInstance(MarmaladeParsingContext parsingContext) 
    throws ComponentInstantiationException
    {
        ScriptBuilder builder = null;
        try{
            builder = cachingParser.parse(parsingContext);
        }
        catch(MarmaladeParsetimeException e) {
            throw new ComponentInstantiationException("failed to parse component script: " + parsingContext.getInputLocation(), e);
        }
        catch(MarmaladeModelBuilderException e) {
            throw new ComponentInstantiationException("failed to parse component script: " + parsingContext.getInputLocation(), e);
        }
        
        MarmaladeScript script = null;
        try {
            script = builder.build();
        }
        catch(MarmaladeModelBuilderException e) {
            throw new ComponentInstantiationException("failed to build component script: " + parsingContext.getInputLocation(), e);
        }
        
        MarmaladeTag root = script.getRoot();
        
        if(!(root instanceof PlexusComponentTag)) {
            throw new ComponentInstantiationException("marmalade script does not build a plexus component");
        }
        
        return script;
    }
    
    protected MarmaladeParsingContext buildParsingContext(String scriptLocation, ClassRealm classRealm) 
    throws ComponentInstantiationException
    {
        InputStream scriptResource = classRealm.getResourceAsStream(scriptLocation);
        if(scriptResource == null) {
            throw new ComponentInstantiationException("can't get script from classpath: " + scriptLocation);
        }
        
        RecordingReader scriptIn = new RecordingReader(new InputStreamReader(scriptResource));
        
        MarmaladeTaglibResolver resolver = new MarmaladeTaglibResolver(MarmaladeTaglibResolver.DEFAULT_STRATEGY_CHAIN);
        
        MarmaladeParsingContext context = new DefaultParsingContext();
        context.setInput(scriptIn);
        context.setInputLocation(scriptLocation);
        context.setTaglibResolver(resolver);
        
        return context;
    }
    
}
