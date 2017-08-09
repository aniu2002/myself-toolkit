#!/bin/sh

if [ $# -eq 0 ]; then
  echo "you must type a parameter of 'icu|rest ? debug'"
  exit 0
fi
cmd=$1
cls='HttpBoot'
if [ "$cmd" = "icu" ] ; then
    cls='Application'
elif [ "$cmd" = "rest" ] ; then
    if [ "$2" = "debug" ] ; then
        cls='HttpBoot'
    else
        cls='HttpBoot'
    fi
else
	echo "No application to stop ."
	exit 1
fi	

jps | grep "$cls" | grep -v grep | awk '{print $1}'| sed -e 's/^/kill -9 /g' | sh -