#! /bin/sh

#
# Skeleton sh script suitable for starting and stopping 
# wrapped Java apps on the Solaris platform. 
#
# This script expects to find the 'realpath' executable
# in the same directory. 
#
# Make sure that PIDFILE points to the correct location,
# if you have changed the default location set in the 
# wrapper configuration file.
#

#-----------------------------------------------------------------------------
# These settings can be modified to fit the needs of your application

# Application
APP_NAME="@app.name@"
APP_LONG_NAME="@app.long.name@"

# Wrapper
WRAPPER_CMD="./wrapper"
WRAPPER_CONF="../../conf/wrapper.conf"

# Priority (see the start() method if you want to use this) 
PRIORITY=

# Do not modify anything beyond this point
#-----------------------------------------------------------------------------

# Get the fully qualified path to the script
case $0 in
    /*)
        SCRIPT="$0"
        ;;
    *)
        PWD=`pwd`
        SCRIPT="$PWD/$0"
        ;;
esac

# Get the real path to this script, resolving any symbolic links
TOKENS=`echo $SCRIPT | sed -e 's;/; ;g'`
REALPATH=
for C in $TOKENS; do
    REALPATH=$REALPATH/$C
    while [ -h "$REALPATH" ] ; do
        LS=`ls -ld "$REALPATH"`
        LINK=`expr "$LS" : '.*-> \(.*\)$'`
        if expr "$LINK" : '/.*' > /dev/null; then
            REALPATH="$LINK"
        else
            REALPATH=`dirname "$REALPATH"`"/$LINK"
        fi
    done
done

# Change the current directory to the location of the script
cd `dirname $REALPATH`

# Process ID
PIDDIR=`pwd`
PIDFILE="$PIDDIR/$APP_NAME.pid"
pid=""

getpid() {
    if [ -f $PIDFILE ]
    then
    if [ -r $PIDFILE ]
    then
        pid=`cat $PIDFILE`
        if [ "X$pid" != "X" ]
        then
        # Verify that a process with this pid is still running.
        pid=`/usr/bin/ps -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`
        if [ "X$pid" = "X" ]
        then
            # This is a stale pid file.
            rm -f $PIDFILE
            echo "Removed stale pid file: $PIDFILE"
        fi
        fi
    else
        echo "Cannot read $PIDFILE."
        rm -f $PIDFILE
        exit 1
    fi
    fi
}
 
console() {
    echo "Running $APP_LONG_NAME..."
    getpid
    if [ "X$pid" = "X" ]
    then
        # If you wanted to specify the priority with which
        # your app runs, you could use nice here:
        # exec nice -$PRIORITY $WRAPPER_CMD $WRAPPER_CONF wrapper.pidfile=$PIDFILE
        # See "man nice" for more details.
        exec $WRAPPER_CMD $WRAPPER_CONF wrapper.pidfile=$PIDFILE
    else
        echo "$APP_LONG_NAME is already running."
        exit 1
    fi
}
 
start() {
    echo "Starting $APP_LONG_NAME..."
    getpid
    if [ "X$pid" = "X" ]
    then
        # If you wanted to specify the priority with which
        # your app runs, you could use nice here:
        # exec nice -$PRIORITY $WRAPPER_CMD $WRAPPER_CONF wrapper.pidfile=$PIDFILE wrapper.daemonize=TRUE wrapper.console.loglevel=NONE
        # See "man nice" for more details.
        exec $WRAPPER_CMD $WRAPPER_CONF wrapper.pidfile=$PIDFILE wrapper.daemonize=TRUE wrapper.console.loglevel=NONE
    else
        echo "$APP_LONG_NAME is already running."
        exit 1
    fi
}
 
stopit() {
    echo "Stopping $APP_LONG_NAME..."
    getpid
    if [ "X$pid" = "X" ]
    then
        echo "$APP_LONG_NAME was not running."
    else
        kill $pid
        sleep 6

        pid=`/usr/bin/ps -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`

        if [ "X$pid" != "X" ]
        then
        # SIGTERM didn't work, send SIGKILL.
            kill -9 $pid
        rm -f $PIDFILE

            pid=`/usr/bin/ps -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`
        fi

        if [ "X$pid" != "X" ]
        then
            echo "Failed to stop $APP_LONG_NAME."
            exit 1
        else
            echo "Stopped $APP_LONG_NAME."
        fi
    fi
}

dump() {
    echo "Dumping $APP_LONG_NAME..."
    getpid
    if [ "X$pid" = "X" ]
    then
        echo "$APP_LONG_NAME was not running."

    else
        kill -3 $pid

        if [ $? -ne 0 ]
        then
            echo "Failed to dump $APP_LONG_NAME."
            exit 1
        else
            echo "Dumped $APP_LONG_NAME."
        fi
    fi
}

case "$1" in

    'console')
        console
        ;;

    'start')
        start
        ;;

    'stop')
        stopit
        ;;

    'restart')
        stopit
        start
        ;;

    'dump')
        dump
        ;;

    *)
        echo "Usage: $0 { console | start | stop | restart | dump }"
        exit 1
        ;;
esac

exit 0
