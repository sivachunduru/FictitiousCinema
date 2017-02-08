#!/bin/bash

#cd Movies
#npm install
#ls -ltr

#rm -rf dist
#mkdir dist
#zip -r dist/MoviesService.zip index.js manifest.json package.json node_modules
# zip -r dist/MoviesService.zip api config repository server main.js manifest.json package.json node_modules

#ls -ltr
#cd ..

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

ip=`python PythonScripts/src/Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`


echo "IP address: ${ip}"
# printf '{' '"memory": "2G",' '"instances": "1",' '"environment": {' '"MOVIES_DATABASE_HOST":"${ip}"' '}' '}' > deployment.json
cat <<EOF >Movies/deployment.json
{
    "memory": "2G",
    "instances": "1",
    "environment": {
        "MOVIES_DATABASE_HOST":"${ip}"
    }
}
EOF

cat Movies/deployment.json

echo "Starting the deployment process...."

echo "Creating a storage container..."
curl -i -X PUT \
  -u ${cloud_username}:${cloud_password} \
  https://${cloud_domain}.storage.oraclecloud.com/v1/Storage-${cloud_domain}/cloudnative-service

echo "Uploading the ZIP file in Storage Container..."
curl -i -X PUT \
    -u ${cloud_username}:${cloud_password} \
    https://${cloud_domain}.storage.oraclecloud.com/v1/Storage-${cloud_domain}/cloudnative-service/MoviesService.zip \
    -T Movies/MoviesService.zip

# See if application already exists
let httpCode=`curl -i -X GET  \
  -u ${cloud_username}:${cloud_password} \
  -H "X-ID-TENANT-NAME:${cloud_domain}" \
  -H "Content-Type: multipart/form-data" \
  -sL -w "%{http_code}" \
  ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}/MoviesService \
  -o /dev/null`

# If application exists...
if [ ${httpCode} -eq 200 ]; then
  # Update application
    echo '\n[info] Updating application...\n'
    curl -i -X PUT  \
        -u ${cloud_username}:${cloud_password} \
        -H "X-ID-TENANT-NAME:${cloud_domain}" \
        -H "Content-Type: multipart/form-data" \
        -F archiveURL=cloudnative-service/MoviesService.zip \
        ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}/MoviesService
else
    echo "Deploying the application to ACCS..."
    curl -X POST -u ${cloud_username}:${cloud_password} \
        -H "X-ID-TENANT-NAME:${cloud_domain}" \
        -H "Content-Type: multipart/form-data" \
        -F "name=MoviesService" -F "runtime=node" -F "subscription=Monthly" \
        -F "deployment=@Movies/deployment.json" \
        -F "archiveURL=cloudnative-service/MoviesService.zip" -F "notes=Movies Service deploying..."  \
        ${cloud_paas_rest_url}/paas/service/apaas/api/v1.1/apps/${cloud_domain}
fi

echo "Deployment successfully completed!"
