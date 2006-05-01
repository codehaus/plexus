package org.codehaus.plexus.installer;

/*
 * Copyright 2005 The Apache Software Foundation.
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

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.codehaus.plexus.archiver.AbstractArchiver;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.DefaultConsumer;
import org.codehaus.plexus.velocity.VelocityComponent;

/**
 * Default implementation of an Installer
 *
 * @author <a href="mailto:vincent.siveton@gmail.com">Vincent Siveton</a>
 * @version $Id$
 */
public class DefaultInstaller
    extends AbstractArchiver
    implements Installer
{
    /** Template definition */
    /**
     * @parameter expression="${template}"
     * @required
     */
    protected String template;

    private Map templateProperties;

    private ClassLoader templateClassLoader;

    /**
     * @plexus.requirement
     */
    private VelocityComponent velocity;

    /** Third party compiler */
    /**
     * @parameter expression="${compiler}"
     * @required
     */
    protected File compiler;

    private File generatedScript;

    /** Properties for an installer */
    private String outputFileName;

    private String productName;

    private String productVersion;

    private String productCompany;

    private String productURL;

    private File productLicense;

    /**
     * @see org.codehaus.plexus.installer.Installer#setCompiler(File)
     */
    public void setCompiler( File compilerFile )
        throws InstallerException
    {
        this.compiler = compilerFile;

        if ( !this.compiler.exists() )
        {
            throw new InstallerException( "The compiler path ('" + compilerFile + "') doesnt exists" );
        }
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setInstallerName(java.lang.String)
     */
    public void setInstallerName( String installerName )
    {
        this.outputFileName = installerName;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setProductCompany(java.lang.String)
     */
    public void setProductCompany( String productCompany )
    {
        this.productCompany = productCompany;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setProductLicense(java.io.File)
     */
    public void setProductLicense( File productLicense )
    {
        this.productLicense = productLicense;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setProductName(java.lang.String)
     */
    public void setProductName( String productName )
    {
        this.productName = productName;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setProductURL(java.lang.String)
     */
    public void setProductURL( String productURL )
    {
        this.productURL = productURL;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setProductVersion(java.lang.String)
     */
    public void setProductVersion( String productVersion )
    {
        this.productVersion = productVersion;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setTemplate(java.lang.String)
     */
    public void setTemplate( String template )
    {
        this.template = template;
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#setTemplate(java.io.File, java.util.Map)
     */
    public void setTemplate( File template, Map templateProperties )
        throws InstallerException
    {
        if ( template == null )
        {
            throw new InstallerException( "The template can not be null." );
        }
        if ( !template.exists() )
        {
            throw new InstallerException( "The template '" + template.getAbsolutePath() + "' doesnt exist." );
        }

        this.templateProperties = templateProperties;
        this.template = template.getName();
        try
        {
            this.templateClassLoader = new URLClassLoader( new URL[] { template.getParentFile().toURL() } );
        }
        catch ( MalformedURLException e )
        {
            throw new InstallerException( "MalformedURLException: " + e.getMessage(), e );
        }
    }

    /**
     * @see org.codehaus.plexus.archiver.Archiver#createArchive()
     */
    public void createArchive()
        throws InstallerException
    {
        createInstallerScript();
        createInstaller();
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#createInstallerScript()
     */
    public void createInstallerScript()
        throws InstallerException
    {
        VelocityContext context = new VelocityContext();

        context.put( "currentDate", new Date() );

        if ( !StringUtils.isEmpty( outputFileName ) )
        {
            context.put( "outputFileName", outputFileName );
        }

        if ( StringUtils.isEmpty( productName ) )
        {
            throw new InstallerException( "The product name is required" );
        }
        context.put( "productName", productName );

        if ( StringUtils.isEmpty( productVersion ) )
        {
            throw new InstallerException( "The product version is required" );
        }
        context.put( "productVersion", productVersion );

        if ( !StringUtils.isEmpty( productCompany ) )
        {
            context.put( "productCompany", productCompany );
        }

        if ( !StringUtils.isEmpty( productURL ) )
        {
            context.put( "productURL", productURL );
        }

        if ( ( productLicense != null ) && ( productLicense.exists() ) )
        {
            context.put( "productLicense", productLicense.getAbsolutePath() );
        }

        if ( getFiles() == null )
        {
            throw new InstallerException( "Files are required" );
        }
        Map result = new HashMap();
        for ( Iterator iter = getFiles().keySet().iterator(); iter.hasNext(); )
        {
            String fileName = (String) iter.next();
            result.put( fileName.substring( fileName.indexOf( "/" ), fileName.length() ), ( (ArchiveEntry) getFiles()
                .get( fileName ) ).getFile() );
        }
        context.put( "files", result );

        if ( StringUtils.isEmpty( template ) )
        {
            throw new InstallerException( "A template is required." );
        }

        // User properties for the template
        if ( templateProperties != null )
        {
            for ( Iterator i = templateProperties.keySet().iterator(); i.hasNext(); )
            {
                String key = (String) i.next();

                context.put( key, templateProperties.get( key ) );
            }
        }

        ClassLoader old = null;
        if ( templateClassLoader != null )
        {
            old = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader( templateClassLoader );
        }

        try
        {
            try
            {
                if ( getDestFile() == null )
                {
                    throw new InstallerException( "destFile is not set" );
                }
                generatedScript = getDestFile();

                Writer scriptWriter = new FileWriter( generatedScript );

                velocity.getEngine().mergeTemplate( template, context, scriptWriter );

                scriptWriter.close();
            }
            catch ( Exception e )
            {
                throw new InstallerException( "Error while generating code.", e );
            }
        }
        finally
        {
            if ( old != null )
            {
                Thread.currentThread().setContextClassLoader( old );
            }
        }
    }

    /**
     * @see org.codehaus.plexus.installer.Installer#createInstaller()
     */
    public void createInstaller()
        throws InstallerException
    {
        Commandline cmd = new Commandline();

        if ( compiler == null )
        {
            throw new InstallerException( "A compiler is required." );
        }

        if ( !compiler.exists() )
        {
            throw new InstallerException( "The compiler path '" + compiler.getAbsolutePath() + "' doesn't exist." );
        }

        cmd.setWorkingDirectory( compiler.getParentFile().getAbsolutePath() );
        cmd.setExecutable( compiler.getAbsolutePath() );

        cmd.createArgument().setValue( generatedScript.getAbsolutePath() );

        getLogger().info( Commandline.toString( cmd.getCommandline() ) );

        CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
        try
        {
            int exitCode = CommandLineUtils.executeCommandLine( cmd, new DefaultConsumer(), err );

            if ( exitCode != 0 )
            {
                throw new InstallerException( "Exit code: " + exitCode + " - " + err.getOutput() );
            }
        }
        catch ( CommandLineException e )
        {
            throw new InstallerException( "Unable to execute " + compiler.getAbsolutePath() + " command", e );
        }
    }
}
