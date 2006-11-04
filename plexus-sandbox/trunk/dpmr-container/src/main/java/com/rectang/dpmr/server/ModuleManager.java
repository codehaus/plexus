package com.rectang.dpmr.server;

import java.util.Hashtable;

public class ModuleManager {

  Hashtable lookup;

  public ModuleManager() {
    lookup = new Hashtable();
  }

  public Class loadModule(String name) throws ClassNotFoundException {

    if (lookup.containsKey(name)) {
      LoadedModule mod = (LoadedModule) lookup.get(name);
      if (mod.getLoaded())
        return ((LoadedModule) lookup.get(name)).getClassObj();
      else {
        mod.reload();
        return mod.getClassObj();
      }
    }

    LoadedModule mod = new LoadedModule(name);
    lookup.put(name, mod);
    return mod.getClassObj();
  }

  public boolean unloadModule(String name) {
    if (!lookup.containsKey(name))
      return false;

    LoadedModule mod = (LoadedModule) lookup.get(name);
    mod.unload();
    return true;
  }

  public Class reloadModule(String name) throws ClassNotFoundException {
    if (!lookup.containsKey(name))
      return null;

    LoadedModule mod = (LoadedModule) lookup.get(name);
    mod.reload();
    return mod.getClassObj();
  }

  class LoadedModule {

    private boolean loaded;
    private String moduleName, className;
    private Class cls;
    private Loader loader;

    public LoadedModule(String name) throws ClassNotFoundException {
      this.moduleName = name;
      this.className = "ModuleMain";
      load();
    }

    public boolean getLoaded() {
      return loaded;
    }

    public void load() throws ClassNotFoundException {
      this.loader = new Loader(moduleName);
      this.cls = loader.loadClass(className, true);
      this.loaded = true;
    }

    public void unload() {
      if (!loaded)
        return;

      loaded = false;
      this.loader = null;
    }

    public void reload() throws ClassNotFoundException {
      if (loaded)
        unload();
      load();
    }

    public String getName() {
      return moduleName;
    }

    public Class getClassObj() {
      return cls;
    }
  }
}
