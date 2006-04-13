package org.codehaus.plexus.util;

import java.net.URL;

/**
  * 
  * <p>Created on 21/08/2003</p>
  *
  * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
  * @revision $Revision$
  */
public class ResourceUtils
{
    /**
     * Return a resource relative to some class. For the class
     * <tt>bob.foo.Bar</tt> and a file <tt>alice.xml</tt> will try to find 
     * <tt>bob/foo/alice.xml</tt> in the classpath
     * 
     * 
     * @param clazz 
     * @param name the path of the file relative to the given class
     * @return
     */
    public static final URL getRelativeResourceURL(Class clazz, String name)
    {
        String className = clazz.getName();
        //ie bob.foo.bar --> bob.foo. --> bob/foo/
        String base = className.substring(0, className.lastIndexOf(".") + 1);

		//replace '.' with '/'
        StringBuffer buff = new StringBuffer(base);        
        for (int i = 0; i < buff.length(); i++)
        {
            char c = buff.charAt(i);
            if (c == '.')
            {
                buff.setCharAt(i, '/');
            }
        }
        base = buff.toString();
        //DebugUtils.debug(ResourceUtils.class, "Relative path for class:" + clazz.getName() + " with name:"+name + " is: " + base + name );
        return clazz.getClassLoader().getResource(base + name);
    }

    public static final String getRelativeResourcePath(Class clazz, String name)
    {
        URL url = getRelativeResourceURL(clazz, name);
        if (url != null)
        {
            return url.getPath();
        }
        else
            return null;
    }

    /**
     * Return the path of the xml file of the same name and package as
     * the given class. For the class <tt>bob.foo.Bar</tt> will look for
     * <tt>bob/foo/Bar.xml</tt> in the classpath
     * 
     * @param clazz
     * @return
     */
    public static final URL getXMLClassResourceURL(Class clazz)
    {
        String className = clazz.getName();

		//replace '.' with '/'
        StringBuffer buff = new StringBuffer(className);
        for (int i = 0; i < buff.length(); i++)
        {
            char c = buff.charAt(i);
            if (c == '.')
            {
                buff.setCharAt(i, '/');
            }
        }
        String path = buff.toString();
        return clazz.getClassLoader().getResource(path + ".xml");
    }

    public static final String getXMLClassResourcePath(Class clazz)
    {
        URL url = getXMLClassResourceURL(clazz);
        if (url != null)
        {
            return url.getPath();
        }
        else
            return null;
    }
}
