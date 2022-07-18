mvn clean process-resources -DskipTests -f  embedded-script-pom.xml
    CLI batch file  ${basedir}/src/test/cli/jbws-testsuite-default-elytron.cli
    properties passed to batch file ${project.build.directory}/CLI-testsuite-default.properties
    reports output of batch file processing  target/XX-embedded-cli.log

-- manually running CLI batch files
    cd $WILDLFY_HOME
    cp $WILDLFY_HOME/standalone/configuration/standalone.xml $WILDLFY_HOME/standalone/configuration/XX-jbws-testsuite-default.xml
    ./bin/jboss-cli.sh -c --properties=$PROJECT_HOME/modules/testsuite/cxf-tests/src/target/CLI-testsuite-default.properties
             --file=$PROJECT_HOME/modules/testsuite/cxf-tests/src/test/cli/jbws-testsuite-default-elytron.cli
        # May need to adjust the values in the properties files to resolve file refs.
  # properties files are generated

./src/test/cli
./src/test/cli/properties

-------------
mvn clean process-resources -DskipTests -f  embedded-script-pom.xml



