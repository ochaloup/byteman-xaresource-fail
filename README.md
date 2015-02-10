Reproducer for exception troubles java.lang.LinkageError with byteman rule to show stack trace from XAResource class.
https://developer.jboss.org/message/917423

 1.  Download&Unzip JBoss EAP 6.3.0.GA (http://www.redhat.com/en/technologies/jboss-middleware/application-platform)
 2.  `export JBOSS_HOME=...`
 3.  `git clone https://github.com/ochaloup/byteman-xaresource-fail`
 4.  `export WORKSPACE=$PWD/byteman-xaresource-fail`
 5.  `export JAVA_OPTS="-Dorg.jboss.byteman.debug=true -Dorg.jboss.byteman.verbose=true -Djboss.modules.system.pkgs=org.jboss.byteman -javaagent:/home/ochaloup/my-testing/byteman-xaresource-fail/lib/byteman-2.2.1.jar=script:$WORKSPACE/src/main/resources/byteman.btm"`
 6.  `unzip -d $JBOSS_HOME $WORKSPACE/lib/postgres-module.zip`
 7.  `$JBOSS_HOME/bin/standalone.sh -c standalone-full.xml &`
 8.  change connection properties to PostgreSQL database in $WORKSPACE/src/main/resources/jboss-config.cli
 9.  `$JBOSS_HOME/bin/jboss-cli.sh -c --file=$WORKSPACE/src/main/resources/jboss-config.cli`
 10.  `cp $WORKSPACE/lib/byteman-xaresource-fail.war $JBOSS_HOME/standalone/deployments/`
 11.  hit http://localhost:8080/byteman-xaresource-fail/

 NOTE: If you experience trouble with byteman exception then this leaves a not finished transactions in database
 Try to connect with `psql databasename` and `select *  from pg_prepared_xacts` and `rollback prepared '<gid>';`
