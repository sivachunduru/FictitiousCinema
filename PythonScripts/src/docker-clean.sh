#!/bin/bash

if [ $(docker ps -a -q) ]; then
    docker stop $(docker ps -a -q)
    docker rm $(docker ps -a -q)
fi

sleep 30
exit
