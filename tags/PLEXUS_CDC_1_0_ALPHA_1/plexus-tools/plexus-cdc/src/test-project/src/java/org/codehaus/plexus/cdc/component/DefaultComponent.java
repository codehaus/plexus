package org.codehaus.plexus.cdc.component;

public class DefaultComponent
  implements Component
{
    private ComponentA componentA;

    /** @default localhost */
    private String host;

    /** @default 8080 */
    private int port;
}
