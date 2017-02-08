#!/bin/bash

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

python PythonScripts/src/DeleteInstance.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}
