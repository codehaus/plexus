package com.rectang.dpmr;

import com.rectang.dpmr.event.*;

import java.io.*;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class Config {

  protected Hashtable db, listeners;
  protected String fileName;

  Config(String fileName) {
    this.db = new Hashtable();
    this.listeners = new Hashtable();

    this.fileName = fileName;
    createDir();
    try {
      load();
    } catch (FileNotFoundException e) {
      System.err.println("***No config found at " + fileName + "***");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  Config(DPMR dpmr) {
    this("dpmr_" + dpmr.getId() + ".properties");
  }

  public Config getInstance(DPMR dpmr) {
    return dpmr.getConfig();
  }

  public void load() throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    String line;
    while ((line = reader.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.charAt(0) == '!' || line.charAt(0) == '#')
        continue;

      int pos = line.indexOf('=');
      if (pos != -1)
        db.put(line.substring(0, pos).toLowerCase(), line.substring(pos + 1));
      else
        db.put(line.toLowerCase(), "");
    }
    reader.close();
  }

  public Enumeration listKeys() {
    return db.keys();
  }

  public String get(String key) {
    return (String) db.get(key.toLowerCase());
  }

  public String getString(String key) {
    return get(key);
  }

  public boolean getBoolean(String key) {
    return Boolean.valueOf(get(key)).booleanValue();
  }

  public int getInt(String key) {
    try {
      return Integer.parseInt(get(key));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public float getFloat(String key) {
    try {
      return Float.parseFloat(get(key));
    } catch (NumberFormatException e) {
      return 0.0f;
    } 
  }

  private void createDir() {
    File dir = (new File(fileName)).getAbsoluteFile().getParentFile();

    if (!dir.exists()) {
      if (!dir.mkdir()) {
        System.err.println("Config: could not create data dir");
      }
    } else if (!dir.isDirectory()) {
      System.err.println("Config: data dir is not a dir");
    }

    if (!dir.canWrite()) {
      System.err.println("Config: data dir is not writable");
    }
  }

  public void save() throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

    Enumeration keys = db.keys();
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();

      writer.write(key + "=" + db.get(key));
      writer.newLine();
    }
    writer.close();
  }

  public String set(String key, String value) {
    String theKey = key.toLowerCase();
    String ret = get(theKey);
    db.put(theKey, value);

    try {
      save(); /* FIXME - we should not be saving all the time */
    } catch (Exception e) {
      e.printStackTrace();
    }

    /* inform listeners */
    Iterator inform = listListeners(key);
    if (inform.hasNext()) {
      ConfigEvent e = new ConfigEvent(key, value, ret);
      while (inform.hasNext())
        ((ConfigListener) inform.next()).configChanged(e);
    }
    return ret;
  }

  public boolean setBoolean(String key, boolean value) {
    boolean ret = getBoolean(key);

    set(key, String.valueOf(value));
    return ret;
  }

  public int setInt(String key, int value) {
    int ret = getInt(key);

    set(key, String.valueOf(value));
    return ret;
  }

  public float setFloat(String key, float value) {
    float ret = getFloat(key);

    set(key, String.valueOf(value));
    return ret;
  }

  public String remove(String key) {
    String ret = (String) db.remove(key.toLowerCase());

    try {
      save(); /* FIXME - we should not be saving all the time */
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }

  public void addListener(String key, ConfigListener listener) {
    Vector list = (Vector) listeners.get(key);
    if (list == null)
      list = new Vector();

    list.add(listener);
    listeners.put(key, list);
  }

  public boolean removeListener(String key, ConfigListener listener) {
    Vector list = (Vector) listeners.get(key);
    if (list == null)
      return false;

    boolean ret = list.remove(listener);
    listeners.put(key, list);
    return ret;
  }

  public Iterator listListeners(String key) {
    Vector list = (Vector) listeners.get(key);
    if (list == null)
      list = new Vector();

    return list.iterator();
  }
}
