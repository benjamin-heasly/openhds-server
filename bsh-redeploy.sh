OPENHDS=~/cos576/openhds-server
TOMCAT=~/cos576/apache-tomcat-8.0.8

$TOMCAT/bin/catalina.sh stop

cd $OPENHDS
mvn -DskipTests=true clean install

sudo rm -rf $TOMCAT/webapps/openhds
sudo rm -rf $TOMCAT/webapps/openhds.war
cp $OPENHDS/web/target/openhds.war $TOMCAT/webapps

$TOMCAT/bin/catalina.sh start

tail -f $TOMCAT/logs/catalina.out

