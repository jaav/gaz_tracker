#!/bin/bash

#export JAVA_HOME=/usr/local/java
#PATH=/usr/local/java/bin:${PATH}

#---------------------------------#
# dynamically build the classpath #
#---------------------------------#
REPO="/Users/jefw/Documents/virtualsushi/projects/tick5/code/gaz_tracker/target/test-classes:/Users/jefw/Documents/virtualsushi/projects/tick5/code/gaz_tracker/target/classes:/Users/jefw/.m2/repository/org/springframework/spring-core/3.2.3.RELEASE/spring-core-3.2.3.RELEASE.jar:/Users/jefw/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:/Users/jefw/.m2/repository/junit/junit/4.11/junit-4.11.jar:/Users/jefw/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/Users/jefw/.m2/repository/mysql/mysql-connector-java/5.1.22/mysql-connector-java-5.1.22.jar:/Users/jefw/.m2/repository/org/springframework/data/spring-data-jpa/1.3.2.RELEASE/spring-data-jpa-1.3.2.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/data/spring-data-commons/1.5.1.RELEASE/spring-data-commons-1.5.1.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-orm/3.1.4.RELEASE/spring-orm-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-jdbc/3.1.4.RELEASE/spring-jdbc-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-context/3.1.4.RELEASE/spring-context-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-expression/3.1.4.RELEASE/spring-expression-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-asm/3.1.4.RELEASE/spring-asm-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-aop/3.1.4.RELEASE/spring-aop-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-tx/3.1.4.RELEASE/spring-tx-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/springframework/spring-beans/3.1.4.RELEASE/spring-beans-3.1.4.RELEASE.jar:/Users/jefw/.m2/repository/org/aspectj/aspectjrt/1.7.2/aspectjrt-1.7.2.jar:/Users/jefw/.m2/repository/org/slf4j/slf4j-api/1.7.1/slf4j-api-1.7.1.jar:/Users/jefw/.m2/repository/org/slf4j/jcl-over-slf4j/1.7.1/jcl-over-slf4j-1.7.1.jar:/Users/jefw/.m2/repository/org/hibernate/hibernate-entitymanager/4.1.8.Final/hibernate-entitymanager-4.1.8.Final.jar:/Users/jefw/.m2/repository/org/jboss/logging/jboss-logging/3.1.0.GA/jboss-logging-3.1.0.GA.jar:/Users/jefw/.m2/repository/org/jboss/spec/javax/transaction/jboss-transaction-api_1.1_spec/1.0.0.Final/jboss-transaction-api_1.1_spec-1.0.0.Final.jar:/Users/jefw/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/Users/jefw/.m2/repository/org/hibernate/hibernate-core/4.1.8.Final/hibernate-core-4.1.8.Final.jar:/Users/jefw/.m2/repository/antlr/antlr/2.7.7/antlr-2.7.7.jar:/Users/jefw/.m2/repository/org/hibernate/javax/persistence/hibernate-jpa-2.0-api/1.0.1.Final/hibernate-jpa-2.0-api-1.0.1.Final.jar:/Users/jefw/.m2/repository/org/javassist/javassist/3.15.0-GA/javassist-3.15.0-GA.jar:/Users/jefw/.m2/repository/org/hibernate/common/hibernate-commons-annotations/4.0.1.Final/hibernate-commons-annotations-4.0.1.Final.jar:/Users/jefw/.m2/repository/org/twitter4j/twitter4j-core/3.0.5/twitter4j-core-3.0.5.jar:/Users/jefw/.m2/repository/org/twitter4j/twitter4j-stream/3.0.5/twitter4j-stream-3.0.5.jar:/Users/jefw/.m2/repository/org/im4java/im4java/1.2.0/im4java-1.2.0.jar:/Users/jefw/.m2/repository/cglib/cglib/2.2.2/cglib-2.2.2.jar:/Users/jefw/.m2/repository/asm/asm/3.3.1/asm-3.3.1.jar:/Users/jefw/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:/Users/jefw/.m2/repository/org/apache/httpcomponents/httpclient/4.2.2/httpclient-4.2.2.jar:/Users/jefw/.m2/repository/org/apache/httpcomponents/httpcore/4.2.2/httpcore-4.2.2.jar:/Users/jefw/.m2/repository/commons-codec/commons-codec/1.6/commons-codec-1.6.jar:/Users/jefw/.m2/repository/org/springframework/spring-test/3.2.3.RELEASE/spring-test-3.2.3.RELEASE.jar:/Users/jefw/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:/Users/jefw/.m2/repository/org/jsoup/jsoup/1.7.1/jsoup-1.7.1.jar:/Users/jefw/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar:/Users/jefw/.m2/repository/org/slf4j/slf4j-log4j12/1.6.6/slf4j-log4j12-1.6.6.jar:/Users/jefw/.m2/repository/org/springframework/spring-web/3.2.3.RELEASE/spring-web-3.2.3.RELEASE.jar:/Users/jefw/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar:/Users/jefw/.m2/repository/org/codehaus/jackson/jackson-jaxrs/1.9.11/jackson-jaxrs-1.9.11.jar:/Users/jefw/.m2/repository/org/codehaus/jackson/jackson-core-asl/1.9.11/jackson-core-asl-1.9.11.jar:/Users/jefw/.m2/repository/org/codehaus/jackson/jackson-mapper-asl/1.9.11/jackson-mapper-asl-1.9.11.jar:/Users/jefw/.m2/repository/com/amazonaws/aws-java-sdk/1.4.1/aws-java-sdk-1.4.1.jar:/Users/jefw/.m2/repository/commons-lang/commons-lang/2.6/commons-lang-2.6.jar"
THE_CLASSPATH=
for i in `ls /Library/Java/JavaVirtualMachines/jdk1.7.0_06.jdk/Contents/Home/lib/*.jar`
do
  THE_CLASSPATH=${THE_CLASSPATH}:${i}
done
for i in `ls /Library/Java/JavaVirtualMachines/jdk1.7.0_06.jdk/Contents/Home/jre/lib/*.jar`
do
  THE_CLASSPATH=${THE_CLASSPATH}:${i}
done
#for i in `ls /Users/jefw/Documents/virtualsushi/projects/tick5/code/gaz_tracker/target/lib/*.jar`
#do
#  THE_CLASSPATH=${THE_CLASSPATH}:${i}
#done


THE_CLASSPATH=${THE_CLASSPATH}:${REPO}:/Users/jefw/Documents/virtualsushi/projects/tick5/code/gaz_tracker/target/classes/

echo ${THE_CLASSPATH}

#---------------------------#
# run the application #
#---------------------------#
java -Xms512M -Xmx1G -Dfile.encoding=UTF-8 -classpath ".:${THE_CLASSPATH}" be.virtualsushi.tick5.datatracker.DatatrackerApplication > log.out 2>err.txt &  
echo $! > /Users/jefw/Documents/virtualsushi/projects/tick5/code/gaz_tracker/GAZ_JAVA.pid
