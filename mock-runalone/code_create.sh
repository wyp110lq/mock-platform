#! /bin/sh

echo "wsdl2java start"

echo "code path: " $1
echo "package name: " $2
echo "wsdl url: " $3

/home/mock/apache-cxf-3.2.14/bin/wsdl2java -encoding utf-8 -d $1 -p $2 $3

echo "wsdl2java end"
