#!/bin/bash

set -ev

SERVER_VERSION=$1
TEST_PROFILE=$1

MYPWD=`pwd`

WFLY_TARGET=$MYPWD"/wfly/wildfly/build/target/"
WFLY_HOME=$(find $WFLY_TARGET -name \wildfly\* -type d -maxdepth 1 -print | head -n1)
#echo "$WFLY_HOME"

mvn -s .travis-settings.xml -B -fae -Dnodeploy -P${TEST_PROFILE} -Dserver.home=${WFLY_HOME} integration-test


