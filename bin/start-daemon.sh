#!/bin/sh
if [ $# -eq 0 ]; then
  echo "you must type a parameter of 'icu|rest ? debug'"
  exit 0
fi

cmd=$1
cls='com.szl.icu.miner.rest.HttpBoot'
if [ "$cmd" = "icu" ] ; then
    cls='com.szl.icu.miner.Application'
elif [ "$cmd" = "rest" ] ; then
    if [ "$2" = "debug" ] ; then
        cls='com.szl.icu.miner.rest.HttpBoot'
    else
        cls='com.szl.icu.miner.rest.HttpBoot'
    fi
else
	echo "No application to start ."
	exit 1
fi

export way=$(cd "$(dirname "${0}")";cd ..;pwd)

export clspath="classes"

if [ -d $way/extLibs ]; then
    for k in $way/extLibs/*.jar
    do
        clspath=$clspath:$k
    done
fi

for l in $way/libs/*.jar
do
    clspath=$clspath:$l
done

clspath=$way/conf:$clspath:$way/bin

export java_cls="$cls"
#echo $clspath
echo "sh $cls.sh"

logFile="$way/log"
if [ ! -d $logFile ]; then
  echo "generate log file dir"
  mkdir $logFile
fi

export app_opts="-Xms128m -Xmx512m -XX:PermSize=64m -XX:MaxPermSize=128m -XX:CMSInitiatingOccupancyFraction=70 -DbaseHome=$way -Dfile.encoding=utf-8"
nohup nice java -classpath "$clspath" $app_opts "$java_cls" > "$way/log/$cmd-icu.log" 2>&1 < /dev/null &