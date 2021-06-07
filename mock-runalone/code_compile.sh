#! /bin/sh

echo "compile code start"

cd /home/mock/webservice

echo "code package: " $1

/usr/jdk1.8.0_191/bin/javac -classpath ../lib/*.jar -encoding UTF-8 -d ./classes src/$1/*.java

echo "compile code end"

