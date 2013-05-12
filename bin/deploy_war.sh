#!/bin/bash

PLAY_DIR=~/lib/play-2.1.1
TOMCAT_DIR=~/lib/tomcat


$PLAY_DIR/play war

scp target/backend-1.0.war amazon:$TOMCAT_DIR/webapps/ROOT.war

$TOMCAT_DIR/bin/shutdown.sh
rm -rf $TOMCAT_DIR/webapps/ROOT

$TOMCAT_DIR/bin/startup.sh
tail -f $TOMCAT_DIR/logs/catalina.out

