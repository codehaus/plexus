/* Rectang.com DPMR - Dynamic Pluggable DPMRModule Registry
 *
 * A generic system for managing a large application with pluggable modules
 * and a load balancing layer underlying a solid application interface
 */
package com.rectang.dpmr;

import com.rectang.dpmr.server.*;
import com.rectang.dpmr.stub.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class DPMR {

  private static Hashtable instances = new Hashtable();

  private ModuleManager loader = new ModuleManager();
  private Hashtable modules = new Hashtable();
  private String id;
  private Stats stats;
  private Config config;

  protected DPMR(String id) {
    this.id = id;
    instances.put(id, this);

    stats = new Stats();
    config = new Config(this);
    System.out.println("Loading Rectang.com DPMR [" + id + "]...");

    String moduleList = config.getString("modules.loaded");
    if (moduleList != null && !moduleList.trim().equals("")) {
      String[] modules = moduleList.split(",");
      for (int i = 0; i < modules.length; i++)
        loadModule(modules[i].trim());
    }
  }

  public String getId() {
    return id;
  }

  public static DPMR getInstance(String id) {
    DPMR ret = (DPMR) instances.get(id);
    if (ret == null)
      ret = new DPMR(id);

    return ret;
  }

  public void shutdown() {
    System.out.println("Shutting down DPMR");

    Vector toRemove = new Vector();
    toRemove.addAll(modules.keySet());
    Iterator mods = toRemove.iterator();
    while (mods.hasNext())
      unloadModule((String) mods.next());
    instances.remove(id);
  }

  private boolean doLoadModule(String name, boolean fresh) {
    try {
      Class modClass;
      if (fresh)
        modClass = loader.loadModule(name);
      else
        modClass = loader.reloadModule(name);

      DPMRModuleImpl module = (DPMRModuleImpl) modClass.newInstance();

      if (isModuleLoaded(name))
        modules.remove(name);
      modules.put(name, module);
      return true;
    } catch (ClassNotFoundException e) {
      System.out.print("(ClassNotFound) ");
      e.printStackTrace();
    } catch (ClassCastException e) {
      System.out.print("(NotADPMRModule) ");
      e.printStackTrace();
    } catch (Exception e) {
      System.out.print("(" + e.getMessage() + ") ");
      e.printStackTrace();
    }
    return false;
  }

  public boolean loadModule(String name) {
    System.out.print("Loading module " + name + "... ");
    if (isModuleLoaded(name)) {
      System.out.println("already loaded");
      return false;
    } 

    boolean ret = doLoadModule(name, true); 
    if (ret)
      System.out.println("OK");
    else
      System.out.println("FAIL");
    return ret;
  }

  public boolean unloadModule(String name) {
    System.out.print("Unloading module " + name + "... ");
    if (!isModuleLoaded(name)) {
      System.out.println("not loaded");
      return false;
    }

    boolean ret = loader.unloadModule(name);
    if (ret) {
      System.out.println("OK");
      modules.remove(name);
    } else
      System.out.println("FAIL");
    return ret;
  }

  public boolean reloadModule(String name) {
    System.out.print("Reloading module " + name + "... ");
    if (!isModuleLoaded(name)) {
      System.out.println("not loaded");
      return false;
    }

    boolean ret = doLoadModule(name, false);
    if (ret)
      System.out.println("OK");
    else
      System.out.println("FAIL");
    return ret;
  }

  Iterator getModuleImplList() {
    return modules.values().iterator();
  }

  public Iterator getModuleList() {
    return modules.keySet().iterator();
  }

  public boolean isModuleLoaded(String name) {
    if (getModuleImpl(name) != null)
      return true;
    return false;
  }

  DPMRModuleImpl getModuleImpl(String name) {
    return (DPMRModuleImpl) modules.get(name);
  }

  static Method getModuleMethodImpl(DPMRModuleImpl mod, String name,
      Class[] params) throws NoSuchMethodException {

    /* FIXME - cache all this stuff, and wipe when we upgrade, as it may change */
    if (mod == null)
      throw new NoSuchMethodException(name + " not found");

    Method ret = mod.getClass().getDeclaredMethod(name, params);
    if (isVisibleMethod(ret))
      return ret;
    throw new NoSuchMethodException(name + " not a DPMRMethod");
  }

  static boolean isVisibleMethod(Method meth) {
    if (meth != null && Modifier.isPublic(meth.getModifiers()))
      return true;
    return false;
  }

  public DPMRModule getModule(String name) {
    return new DPMRModule(id, name);
  }

  public Stats getStats() {
    return stats;
  }

  public Config getConfig() {
    return config;
  }
}
