package org.codehaus.plexus.formica.web;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public interface ContentGenerator
{
    String ROLE = ContentGenerator.class.getName();
    
    String generate( Object data );
}
