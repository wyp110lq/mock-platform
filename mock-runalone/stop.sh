#!/bin/bash

pid_name="mock-platform"

ps -ef|grep $pid_name|grep -v grep|awk '{print $2}'|xargs kill -9

 

