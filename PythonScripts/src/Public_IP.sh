#!/bin/bash

config_file="config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

ip=`python ./Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`
echo "${ip}"

