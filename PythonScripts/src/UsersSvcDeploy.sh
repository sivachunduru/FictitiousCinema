#!/bin/bash

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

ip=`python PythonScripts/src/Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`

echo "IP address: ${ip}"
cat <<EOF >Users/deployment.json
{
    "memory": "2G",
    "instances": "1",
    "environment": {
        "USERS_DATABASE_HOST":"${ip}"
    }
}
EOF

cat Users/deployment.json

echo "Starting the deployment process...."
ls -ltr Users/target

echo "Creating a storage container..."
curl -i -X PUT \
  -u ${cloud_username}:${cloud_password} \
  https://${cloud_domain}.storage.oraclecloud.com/v1/Storage-${cloud_domain}/cloudnative-service
sleep 15

echo "Uploading the ZIP file in Storage Container..."
curl -i -X PUT \
    -u ${cloud_username}:${cloud_password} \
    https://${cloud_domain}.storage.oraclecloud.com/v1/Storage-${cloud_domain}/cloudnative-service/UsersService.zip \
    -T Users/target/UsersService.zip
sleep 15

# See if application already exists
let httpCode=`curl -i -X GET  \
  -u ${cloud_username}:${cloud_password} \
  -H "X-ID-TENANT-NAME:${cloud_domain}" \
  -H "Content-Type: multipart/form-data" \
  -sL -w "%{http_code}" \
  ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}/UsersService \
  -o /dev/null`

# If application exists...
if [ ${httpCode} -eq 200 ]; then
  # Update application
    echo '\n[info] Updating application...\n'
    curl -i -X PUT  \
        -u ${cloud_username}:${cloud_password} \
        -H "X-ID-TENANT-NAME:${cloud_domain}" \
        -H "Content-Type: multipart/form-data" \
        -F archiveURL=cloudnative-service/UsersService.zip \
        ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}/UsersService
	sleep 60
else
    echo "Deploying the application to ACCS..."
    curl -X POST -u ${cloud_username}:${cloud_password} \
        -H "X-ID-TENANT-NAME:${cloud_domain}" \
        -H "Content-Type: multipart/form-data" \
        -F "name=UsersService" -F "runtime=java" -F "subscription=Monthly" \
        -F "deployment=@Users/deployment.json" \
        -F "archiveURL=cloudnative-service/UsersService.zip" -F "notes=Users Service deploying..."  \
        ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}
	sleep 60
fi

echo "Deployment successfully completed!"
