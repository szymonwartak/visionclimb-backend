#!/bin/bash

# deploy web visionclimb

# INSUFFICIENT - need java SDK (javac)
export JAVA_HOME=/home/ubuntu/cas/lib/java

export PATH=/home/ubuntu/cas/lib/java/bin:$PATH

BACKEND=backend
FRONTEND=PG1/assets/www

tar zcf visionclimb.tgz $BACKEND/app $BACKEND/conf $BACKEND/bin $BACKEND/project/build.properties \
    $BACKEND/project/Build.scala $BACKEND/project/plugins.sbt $FRONTEND

REMOTE_SRC=/home/ubuntu/cas/src
ssh amazon "mkdir $REMOTE_SRC"
scp visionclimb.tgz amazon:$REMOTE_SRC
ssh amazon "cd $REMOTE_SRC; tar zxf visionclimb.tgz; $BACKEND/bin/link.sh $REMOTE_SRC;"

