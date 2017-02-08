#!/bin/bash

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

ip=`python PythonScripts/src/Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`

echo "IP address: ${ip}"
cat <<EOF >ShowTimes/deployment.json
{
    "memory": "2G",
    "instances": "1",
    "environment": {
        "SHOWTIMES_DATABASE_HOST":"${ip}"
    }
}
EOF

cat ShowTimes/deployment.json

echo "Starting the deployment process...."
ls -ltr ShowTimes/target

echo "Creating a storage container..."
curl -i -X PUT \
  -u ${cloud_username}:${cloud_password} \
  https://${cloud_domain}.storage.oraclecloud.com/v1/Storage-${cloud_domain}/cloudnative-service
sleep 15

echo "Uploading the ZIP file in Storage Container..."
curl -i -X PUT \
    -u ${cloud_username}:${cloud_password} \
    https://${cloud_domain}.storage.oraclecloud.com/v1/Storage-${cloud_domain}/cloudnative-service/ShowTimesService.zip \
    -T ShowTimes/target/ShowTimes.zip
sleep 15

# See if application already exists
let httpCode=`curl -i -X GET  \
  -u ${cloud_username}:${cloud_password} \
  -H "X-ID-TENANT-NAME:${cloud_domain}" \
  -H "Content-Type: multipart/form-data" \
  -sL -w "%{http_code}" \
  ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}/ShowTimesService \
  -o /dev/null`

# If application exists...
if [ ${httpCode} -eq 200 ]; then
  # Update application
    echo '\n[info] Updating application...\n'
    curl -i -X PUT  \
        -u ${cloud_username}:${cloud_password} \
        -H "X-ID-TENANT-NAME:${cloud_domain}" \
        -H "Content-Type: multipart/form-data" \
        -F archiveURL=cloudnative-service/ShowTimesService.zip \
        ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}/ShowTimesService
	sleep 60
else
    echo "Deploying the application to ACCS..."
    curl -X POST -u ${cloud_username}:${cloud_password} \
        -H "X-ID-TENANT-NAME:${cloud_domain}" \
        -H "Content-Type: multipart/form-data" \
        -F "name=ShowTimesService" -F "runtime=java" -F "subscription=Monthly" \
        -F "deployment=@ShowTimes/deployment.json" \
        -F "archiveURL=cloudnative-service/ShowTimesService.zip" -F "notes=ShowTimes Service deploying..."  \
        ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}
	sleep 60
fi

echo "Deployment successfully completed!"
