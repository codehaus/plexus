package org.codehaus.plexus.service.irc;

import org.codehaus.plexus.appserver.service.PlexusService;
import org.codehaus.plexus.appserver.application.profile.AppRuntimeProfile;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * The plexus service wrapper for the IRCServiceManager
 * User: aje
 * Date: 23-Jul-2006
 * Time: 22:42:20
 *
 * @plexus.component
 *   role="org.codehaus.plexus.appserver.service.PlexusService"
 *   role-hint="irc"
 */
public class PlexusIRCService implements PlexusService {

  /**
   * @plexus.configuration default-value="irc.codehaus.org"
   */
  private String host;

  /**
   * @plexus.configuration default-value="PlexusIRC"
   */
  private String nick;

  /**
   * FIXME - this should default to "" but the CDC cannot parse that yet...
   * @plexus.configuration default-value="none"
   */
  private String pass;

  /**
   * @plexus.configuration default-value="plexus"
   */
  private String username;

  /**
   * @plexus.configuration default-value="Plexus IRC Framework"
   */
  private String realname;

  /**
   * @plexus.configuration default-value="#test"
   */
  private String channel;

  /**
   * @plexus.requirement
   */
  private org.codehaus.plexus.service.irc.IRCServiceManager manager;

  public void beforeApplicationStart(AppRuntimeProfile applicationRuntimeProfile, PlexusConfiguration plexusConfiguration) throws Exception {
    manager.connect(host, nick, pass, username, realname);
    manager.join(channel);

  }

  public void afterApplicationStart(AppRuntimeProfile applicationRuntimeProfile, PlexusConfiguration plexusConfiguration) throws Exception {
  }
}
