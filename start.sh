#!/usr/bin/env bash
cd ./common/
git pull origin master
cd ../sms-sender/
git pull origin master
cd ..
git pull origin master
gradle bootJar -x test
ps -ef | grep java | grep -v grep | cut -b8-20 | xargs kill -9
cd build/libs
nohup java -jar tomato-0.0.1-SNAPSHOT.jar &
