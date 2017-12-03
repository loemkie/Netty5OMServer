@echo off
title OM Message Platform
echo 正在加密，请稍后....
echo path:%~dp0

set base=%~dp0

set class=%base%\bin
set libs=%base%\lib

set class_path=%class%;%libs%\fastjson-1.2.8.jar;%libs%\druid-1.0.11.jar;%libs%\dom4j.jar;%libs%\dom4j-1.6.jar;%libs%\netty-all-4.1.8.Final.jar;%libs%\commons-logging.jar;%libs%\log4j-1.2.9.jar;%libs%\protobuf-java-3.0.0.jar

rem java -classpath %class_path% com.mylike.oms.netty.client.OmsHttpClient

java -classpath %class_path% com.mylike.oms.netty.client.OmsSocketClient