package org.codehaus.jasf.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.impl.SimpleLog;
import org.xml.sax.SAXException;

import org.codehaus.jasf.ResourceController;
import org.codehaus.jasf.resources.PageResource;

/**
 * AbstractPageACcessController is a partial implementation that makes it easy
 * to create an xml resource to credential mapping.  This class handles the
 * loading of the list of pages and their necessary credentials to view them.
 * This class can be extended to use this credential and page relation.  The
 * only method that needs to be implemented is hasCredential().  In this method
 * you take care of your entity and credential relation.
 * 
 * @author Dan Diephouse
 * @since Nov 21, 2002
 */
public abstract class AbstractPageAccessController 
    extends AbstractLogEnabled
    implements ResourceController, Configurable, Initializable
{
    private String delimiter;
    
    private boolean defaultAuthorization;
    
    // The root resource
    List root;
    
    // The digester which reads in the pages and their necessary credentials
    Digester pageDigester;
    
    private File pagesFile;

    /**
     * Return a list of <code>PageResource</code>s.
     * 
     * @return List
     */
    public List getPages()
    {
        return root;
    }
    
    /**
     * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration)
     */
    public void configure(Configuration config) throws ConfigurationException
    {
        pagesFile = new File(config.getChild("pages").getValue());
        delimiter = config.getAttribute("delimiter", "/");
        defaultAuthorization =
            config.getAttributeAsBoolean("defaultAuthorization", false);
    }
    
    /**
     * @see org.apache.avalon.framework.activity.Initializable#initialize()
     */
    public void initialize() throws Exception
    {
        // Set up the digester so that it can read in the xml pages declarations.
        pageDigester = new Digester();
        SimpleLog log = new SimpleLog("digester");
        pageDigester.setLogger(log);
        
        pageDigester.addObjectCreate("resources", ArrayList.class);
        pageDigester.addObjectCreate("*/resource", XmlPageResource.class);
        pageDigester.addSetNext("*/resource", "add", 
            XmlPageResource.class.getName());
        pageDigester.addCallMethod("*/resource/name", "setName", 0);
        pageDigester.addCallMethod("*/resource/credential", "setCredential", 0);
        pageDigester.addObjectCreate("*/resource/resources", ArrayList.class);
        pageDigester.addSetNext("*/resource/resources", "setResources", 
            ArrayList.class.getName());  
        
        try
        {
            InputStream stream = new FileInputStream(pagesFile);
            root = (List) pageDigester.parse( stream );
        }
        catch (FileNotFoundException e)
        {
            getLogger().error("Could not find the pages file.");
            throw new ConfigurationException("Could not find the pages file!");
        } catch (IOException e)
        {
            getLogger().error("Error reading the pages file.");
            throw new ConfigurationException("Error reading the pages file.");
        } catch (SAXException e)
        {
            getLogger().error("Error parsing the pages file.");
            throw new ConfigurationException("Error parsing the pages file.");
        }
    }

    /**
     * @param entity the Entity being authorized
     * @param resource a <code>PageResource</code>
     * @see org.apache.fulcrum.jasf.ResourceAccessController#isAuthorized(Object, Object)
     */
    public boolean isAuthorized( Object entity, Object resource )
    {
        getLogger().debug("Checking to see if then entity can access " +
            ((PageResource)resource).getName() + ".");
            
        Iterator itr = getPages().iterator();
        while (itr.hasNext())
        {
            XmlPageResource r = (XmlPageResource) itr.next();
            if (isAuthorized(entity, ((PageResource) resource).getName(), r))
            {
                return true;
            }
        }
    
        return defaultAuthorization;
    }

    protected boolean isAuthorized( Object entity, String r1, 
        XmlPageResource presource )
    {
        int index = r1.indexOf( delimiter );
        // Check to see if we need to go deeper
        if (index >= 0 && index < r1.length()) {
            String child = r1.substring(index+1);
            
            Iterator itr = presource.getResources().iterator();
            while (itr.hasNext())
            {   
                XmlPageResource childResource = (XmlPageResource) itr.next();
                if(isAuthorized(entity, child, childResource ))
                    return true;
            }
        }
        
        if (presource.getName().equals(r1))
        {
            if ( presource.getCredential().equals("") )
                return true;

            if ( hasCredential( entity, presource.getCredential() ))
                return true;
        }
    
        return false;        
    }
    
    /**
     * Implement this method to determine if your entity has the necessary
     * credential to access the <code>PageResource</code> that has been
     * requested.
     * 
     * @param entity
     * @param credential
     * @return boolean
     */
    public abstract boolean hasCredential( Object entity, String credential );
}
