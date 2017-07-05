#!/bin/bash

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

instance=`python PythonScripts/src/Instance_Name.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`

if [ ${#instance} -gt 0 ]; then
    echo "Compute VM already exists...Deleting Docker containers..."
    bash PythonScripts/src/delete-containers.sh
    #echo "Compute VM already exists...Deleting the instance..."
    #bash PythonScripts/src/delete-instance.sh
else
    echo "Compute VM does not exists...No cleaning required..."
fi
