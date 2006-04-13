package com.hksys.aspectj;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Map;

import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.ResolvedTypeX;
import org.aspectj.weaver.bcel.BcelWeaver;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.aspectj.util.FileUtil;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * Weaves aspects from JARs into classes at runtime.
 *
 * To run the guantlet of easily plugging into to the Java parent-delegate
 * ClassLoader model, and yet not throw <code>ClassCastException</code>s or
 * <code>VerifyError</code>s all the time, {@link AspectJClassLoader} tip toes
 * around the parent-delegate model.
 *
 * When asked for a class in {@link loadClass}, the following logic occurs:
 *
 *   1. It checks to see if we ourselves have already loaded the class; if so,
 *   return it.
 *
 *   2. It checks to see if the class is within the packages we can weave:
 *
 *      a) If so, it passes it off to AspectJ and defines the result, creating a
 *      brand new class definition that is unique from any of the class
 *      definitions already loaded by other class loaders (e.g. the parent).
 *
 *      b) If not, the parent is asked to load the class, which follows the
 *      standard parent-delegate model.
 *
 * The result is that the {@link AspectJClassLoader} will work great as long as
 * you do not load and and instantiate classes from the packages {@link
 * AspectJClassLoader} will weave classes for before {@link AspectJClassLoader}
 * is put in to place, and then expect the old, parent-loaded classes to work
 * against the new, potentially woven classes. <code>ClassCastException</code>s
 * will occur as they really are different classes to the JVM.
 *
 * Also, to illustrate more precisely, because {@link AspectJClassLoader} always
 * defers to it's parent to get the actual bytes for a class, you could
 * potentially end up in this situation:
 *
 *           parent
 *         /   |   \
 *        /    |    \
 *       /     |     \
 *      /      |      \
 *  sibling sibling AspectJClassLoader
 *
 * As always, the {@link AspectJClassLoader} will redefine any classes that are
 * in the packages you authorize it to weave for.
 *
 * However, it's possible that because the actual bytes come from the parent,
 * conflicting definitions of the class could exist in the system.
 *
 * For example, if either the parent itself or one of the siblings (by asking
 * parent for it) instantiates a class from the list of packages {@link
 * AspectJClassLoader} can redefine, the parent and the {@link
 * AspectJClassLoader} will have conflicting definitions (one plain and one
 * redefined, potentially woven) and {@link AspectJClassLoader} will not be able
 * to communicate with code running under any of the other class loaders if a
 * class in one of the packages it can weave classes for is involved.
 *
 * <code>ClassCastException</code>s will ensue as the JVM doesn't think the
 * plain, parent-defined class is the same as the redefined, {@link
 * AspectJClassLoader}-manged class. Which it legitimately isn't.
 *
 * NOTE: The {@link AspectJClassLoader} has two states: it starts in a
 * 'loading-aspects' state, in which you can call {@link addLibraryJarFile} or
 * {@link addLibraryFile} multiple times. However, once {@link findClass} has
 * been called by {@link loadClass} to define a new class within one of the
 * packages {@link AspectJClassLoader} can weave, it enters a 'weaving-classes'
 * state and you can no longer add jar files or individual aspects to weave into
 * classes.
 *
 * Optimization note: To work around the parent-delegate model, this class
 * loader must define any class that depends on a class you want woven. However,
 * this doesn't mean it has to try and weave every class (and expensive
 * operation). You can call {@link addPackageToWeave} and the class loader
 * will continue defining lots of classes, but only weave ones you've told it
 * to.
*
 * @author Stephen Haberman
 */
public class AspectJClassLoader extends ClassLoader
{

    private BcelWorld world = new BcelWorld();
    private BcelWeaver weaver = new BcelWeaver(this.world);
    private List packagesToIgnore = new ArrayList();
    private List packagesToWeave = new ArrayList();

    private boolean hasBeenPrepared = false;
    private boolean trace = false;

    /**
     * Creates an instance to weave aspects into classes from the authorized
     * packages.
     *
     * Turns on tracing if the system property
     * <code>aspectj.classloader.trace</code> equals <code>true</code>.
     *
     * @param parent the parent class loader
     */
    public AspectJClassLoader(ClassLoader parent)
    {
        super(parent);

        this.packagesToIgnore.add("java.");
        this.packagesToIgnore.add("javax.");
        this.packagesToIgnore.add("sun.");
        this.packagesToIgnore.add("com.sun.");

        if ("true".equals(System.getProperty("aspectj.classloader.trace")))
        {
            this.trace = true;
        }
    }

    /**
     * @param p a package to add to the list of packages to ignore
     * @throws IllegalStateException if the 'weaving-classes' state has already begun
     */
    public void addPackageToIgnore(String p)
    {
        this.assertInLoadingAspectsState();

        if (trace) System.out.println("Ignoring package " + p);
        this.packagesToIgnore.add(p);
    }

    /**
     * @param packages a comma-delimited list of packages to ignore
     * @throws IllegalStateException if the 'weaving-classes' state has already begun
     */
    public void addPackagesToIgnore(String packages)
    {
        this.assertInLoadingAspectsState();

        if (packages != null)
        {
            StringTokenizer st = new StringTokenizer(packages, ",");
            while (st.hasMoreTokens())
            {
                this.addPackageToIgnore(st.nextToken());
            }
        }
    }

    /**
     * @param p a package to add to the list of packages to weave
     * @throws IllegalStateException if the 'weaving-classes' state has already begun
     */
    public void addPackageToWeave(String p)
    {
        this.assertInLoadingAspectsState();

        if (trace) System.out.println("Weaving package " + p);
        this.packagesToWeave.add(p);
    }

    /**
     * @param package a comma-delimited list of packages to weave
     * @throws IllegalStateException if the 'weaving-classes' state has already begun
     */
    public void addPackagesToWeave(String packages)
    {
        this.assertInLoadingAspectsState();

        if (packages != null)
        {
            StringTokenizer st = new StringTokenizer(packages, ",");
            while (st.hasMoreTokens())
            {
                this.addPackageToWeave(st.nextToken());
            }
        }
    }

    /**
     * @return whether the given class is in our list of packages.
     */
    private boolean isInPackagesToIgnore(String className)
    {
        for (Iterator i = this.packagesToIgnore.iterator(); i.hasNext();)
        {
            if (className.startsWith((String) i.next()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @return whether the class has been specifically chosen to be woven,
     *  or all classes are being woven
     */
    private boolean isInPackagesToWeave(String className)
    {
        if (this.packagesToWeave.size() == 0)
        {
            return true;
        }

        for (Iterator i = this.packagesToWeave.iterator(); i.hasNext();)
        {
            if (className.startsWith((String) i.next()))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * @param aspect the class name for an aspect to weave into the code
     * @throws IllegalStateException if the 'weaving-classes' state has already begun
     * @TODO test this
     */
    public void addLibraryAspect(String aspect)
    {
        this.assertInLoadingAspectsState();

        if (trace) System.out.println("Adding aspect" + aspect);
        this.weaver.addLibraryAspect(aspect);
    }

    /**
     * @param file a jar file with aspects to weave into the code
     * @throws IOException if the <code>file</code> is invalid
     * @throws IllegalStateException if the 'weaving-classes' state has already begun
     */
    public void addLibraryJarFile(File file) throws IOException
    {
        this.assertInLoadingAspectsState();

        if (trace) System.out.println("Adding aspect library " + file.getName());
        this.weaver.addLibraryJarFile(file);
    }

    /**
     * Ensure the weaver hasn't been prepared yet.
     *
     * @throws IllegalStateException if the weaver has been prepared
     */
    private void assertInLoadingAspectsState()
    {
        if (this.hasBeenPrepared)
        {
            throw new IllegalStateException("loadClass has already been called and the aspects have been prepared.");
        }
    }

    /**
     * Override the loadClass method so that we can weave any new classes that
     * haven't been loaded yet.
     *
     * @see ClassLoader#loadClass
     */
    public Class loadClass(String className, boolean resolve) throws ClassNotFoundException
    {
        Class clazz;

        // Look for a definition
        clazz = super.findLoadedClass(className);
        if (clazz != null)
        {
            return clazz;
        }

        // Any class we're not supposed to define, pass to the super implementation
        if (this.isInPackagesToIgnore(className))
        {
            return super.loadClass(className, resolve);
        }

        // Look for classNames wandering around without their 'java.lang' prefix
        if (Character.isUpperCase(className.charAt(0)))
        {
            try
            {
                return super.loadClass("java.lang." + className, false);
            }
            catch (ClassNotFoundException e)
            {
                // Wasn't a built-in class masquerading without a fully qualified name, continue looking
            }
        }

        // We haven't loaded it yet, and we can play with it, have our findClass load and possibly weave it
        clazz = this.findClass(className);

        if (resolve)
        {
            super.resolveClass(clazz);
        }

        return clazz;
    }

    /**
     * Override the find method to weave any new classes we load.
     *
     * @param className the name of a class to find
     */
    protected Class findClass(String className) throws ClassNotFoundException
    {
        if (trace) System.out.println("Finding " + className);

        if (!this.hasBeenPrepared)
        {
            if (trace) System.out.println("Changing to 'weaving-classes' state.");
            this.weaver.prepareForWeave();
            this.hasBeenPrepared = true;
        }

        // Hack, we must have been defined already, so don't re-defined ourselves
        if (className.equals("com.hksys.aspectj.AspectJClassLoader"))
        {
            return super.loadClass(className, false);
        }

        try
        {
            String resourceName = className.replace('.', '/') + ".class";
            InputStream stream = super.getResourceAsStream(resourceName);

            if (stream == null)
            {
                throw new ClassNotFoundException("Could not find resource " + resourceName);
            }

            // Load the raw, unwoven bytes of the class
            byte[] bytes = FileUtil.readAsByteArray(new DataInputStream(stream));

            // If we are optimizing for only weaving certain classes, see if this is
            // one to not weave, but still defined
            if (this.isInPackagesToWeave(className))
            {
                UnwovenClassFile classFile = new UnwovenClassFile(resourceName, bytes);

                // Register the file with the world
                BcelObjectType objectType = this.world.addSourceObjectType(classFile.getJavaClass());

                // Don't weave stuff that is already woven (e.g. aspects loaded from a binary jar)
                if (objectType.getLazyClassGen().getWeaverState().isWoven())
                {
                    if (trace) System.out.println("Skipping already woven class " + className);
                }
                else
                {
                    // Weave it
                    LazyClassGen generated = this.weaver.weaveWithoutDump(classFile, objectType);

                    if (generated != null)
                    {
                        if (trace) System.out.println("Successfully wove " + className);
                        bytes = generated.getJavaClass().getBytes();
                    }
                }
            }

            if (trace) System.out.println("Defining " + className);

            // Turn the bytes (either changed or not) back into a class
            return super.defineClass(className, bytes, 0, bytes.length);
        }
        catch (IOException e)
        {
            if (trace) System.out.println("Error loading " + className + ": " + e.getMessage());
            return null;
        }
    }

}
