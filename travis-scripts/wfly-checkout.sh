#!/bin/bash

set -ev

# checkout master branch and build
rm -rf wfly
mkdir wfly
cd wfly
git clone https://github.com/wildfly/wildfly.git
cd wildfly

# compile in silence.  The bld output is too much for travis log
set -e
mvn clean install -DskipTests
