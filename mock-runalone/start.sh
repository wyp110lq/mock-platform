#!/bin/bash

echo "mock-platform starting"

nohup /usr/jdk1.8.0_191/bin/java -XX:+HeapDumpOnOutOfMemoryError -Xms512m -Xmx1024m -jar mock-platform.jar &>/dev/null &

echo "mock-platform started"
