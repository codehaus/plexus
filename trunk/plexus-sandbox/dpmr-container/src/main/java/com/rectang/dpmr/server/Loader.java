package com.rectang.dpmr.server;

import java.io.*;
import java.util.Hashtable;

public class Loader extends ClassLoader {

  Hashtable lookup;
  String moduleName;

  public Loader(String modName) {
    super(Loader.class.getClassLoader());

    this.lookup = new Hashtable();
    this.moduleName = modName;
  }

  public Class loadClass(String name, boolean resolve) 
        throws ClassNotFoundException {
    Class cls;

    /* first try delegating */
    try {
      cls = findSystemClass(name);
      if (cls != null)
        return cls;
    } catch(Exception e) {
      /* falling over to module loader */
    }

    /* then try our cache */
    if (lookup.containsKey(name))
      return (Class) lookup.get(name);

    /* then try our module loader */
    try {
      byte[] data = loadClassData("modules" + File.separatorChar + moduleName +
          File.separatorChar + name.replace ('.', File.separatorChar) +
          ".class");
      cls = defineClass(name, data, 0, data.length);

      if (cls != null) {
        lookup.put(name, cls);

        if(resolve) {
          resolveClass(cls);
        }
      }
    } catch (Exception e) {
      throw new ClassNotFoundException();
    }

    return cls;
  }

  private byte[] loadClassData(String filename) 
      throws IOException {

    File f = new File(filename);
    int size = (int)f.length();
    byte buff[] = new byte[size];

    DataInputStream dis = new DataInputStream(new FileInputStream(f));
    dis.readFully(buff);
    dis.close();

    return buff;
  }

}
