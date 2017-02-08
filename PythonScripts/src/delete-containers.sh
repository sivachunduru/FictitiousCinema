#!/bin/bash

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

ip=`python PythonScripts/src/Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`

# Delete all docker container and keep the compute instance clean
ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} < PythonScripts/src/docker-clean.sh

echo 'Deleted all Docker containers!!!'