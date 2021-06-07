#!/bin/sh

nohup /usr/jdk1.8.0_191/bin/java -Xms256m -Xmx256m -Xmn128m -jar ./electSeal-0.0.1-SNAPSHOT.jar >./electronicSeal.log 2>&1 &

