#
# I really don't want this to be necessary. I'm not sure why the classpath
# isn't enough. I'm sure I'm not initializing something correctly.
#
import sys
sys.add_package("org.codehaus.plexus.component.factory.jython")

from org.codehaus.plexus.component.factory.jython import Hello

class DefaultHello(Hello):
        
    def initialize(self):
        print "initialize()"
        
    def start(self):
        print "start()"

    def stop(self):
        print "stop()"

    def dispose(self):
        print "dispose()"
        
    def hello(self):
        print "hello!"
