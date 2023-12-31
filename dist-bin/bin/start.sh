#/usr/bin
#cat zbus.sh | col -b > zbus2.sh  ==> fix win=>lin
if [ -z ${JAVA_HOME} ]; then
JAVA_HOME=/apps/jdk
fi
ZBUS_HOME=../
JAVA_OPTS="-Dfile.encoding=UTF-8 -server -Xms1024m -Xmx4096m -XX:+UseParallelGC"
#JAVA_OPTS="-Dfile.encoding=UTF-8"
MAIN_CLASS=io.seika.mq.MqServer
if [ -z "$1" ]
  then
    MAIN_OPTS="-conf ../conf/seika.xml"
else
	MAIN_OPTS="-conf $1"
fi

LIB_OPTS="$ZBUS_HOME/lib/*:$ZBUS_HOME/classes:$ZBUS_HOME/*:$ZBUS_HOME/conf/"
#nohup $JAVA_HOME/bin/java $JAVA_OPTS -cp $LIB_OPTS $MAIN_CLASS $MAIN_OPTS > /dev/null 2>&1&
$JAVA_HOME/bin/java $JAVA_OPTS -cp $LIB_OPTS $MAIN_CLASS $MAIN_OPTS


