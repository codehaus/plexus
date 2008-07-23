#!/bin/sh

CLASSPATH=
CLASSPATH=$CLASSPATH:$HOME/.m2/repository/org/apache/derby/derby/10.2.1.6/derby-10.2.1.6.jar
CLASSPATH=$CLASSPATH:$HOME/.m2/repository/org/apache/derby/derbytools/10.2.1.6/derbytools-10.2.1.6.jar

~/opt/jdk1.6.0/bin/java -cp $CLASSPATH org.apache.derby.tools.ij -p ij.properties
