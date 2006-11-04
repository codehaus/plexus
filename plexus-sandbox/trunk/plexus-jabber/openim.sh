#! /bin/sh

# Checking for JAVA_HOME is required on *nix due
# to some distributions stupidly including kaffe in /usr/bin
if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

# Checking for MERLIN_HOME is required on *nix due
# to some distributions stupidly including kaffe in /usr/bin
if [ "$MERLIN_HOME" = "" ] ; then
  echo "ERROR: MERLIN_HOME not found in your environment."
  echo
  echo "Please, set the MERLIN_HOME variable in your environment to match the"
  echo "location of Merlin distribution."
  exit 1
fi

# Checking for OPENIM_HOME
if [ "$OPENIM_HOME" = "" ] ; then
   OPENIM_HOME=`pwd`
   echo "Setting OPENIM_HOME: $OPENIM_HOME"
fi



RUN_CMD="$MERLIN_HOME/bin/merlin $OPENIM_HOME/repository/openim/jars/openim-server-impl-1.2.2.jar -config file:$OPENIM_HOME/conf/config.xml -context $OPENIM_HOME -kernel file:$OPENIM_HOME/conf/kernel.xml $*"
echo "RUN CMD IS: $RUN_CMD"
exec $RUN_CMD
