#!/bin/bash

# This script demonstrates end to end automated process of creating
# an Oracle Compute Cloud instance and setting up Docker platform
# on the new created Oracle Compute Cloud instance

config_file="PythonScripts/src/config.properties"
while IFS='=' read -r key value
do
  key=$(echo $key | tr '.' '_')
  eval "${key}='${value}'"
done < "$config_file"

instance=`python PythonScripts/src/Instance_Name.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`

if [ ${#instance} -gt 0 ]; then
    echo "Compute VM already exists..."  
    # Read the IP address of Compute from hosts file
    ip=`python PythonScripts/src/Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`
else
    python PythonScripts/src/CreateComputeCloudInstance.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}
    echo "Sleep for 60 seconds for the SSH port of the Compute instance to be accessible"
    sleep 60

    # Read the IP address of Compute from hosts file
    ip=`python PythonScripts/src/Public_IP.py ${cloud_username} ${cloud_password} ${cloud_domain} ${cloud_rest_url}`

    #    while (true)  
    #do
    	#	ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} "exit"
    	#case $? in
        	#	(0) echo "Successfully connected."; break ;;
        	#(*) echo "SSH Port not ready yet, waiting 10 seconds..." ;;
    	#esac
    	#sleep 10
    #done

	while (true); do exec 3>/dev/tcp/${ip}/22; if [ $? -eq 0 ]; then echo "SSH up" ; break ; else echo "SSH still down" ; sleep 60 ; fi done
                        
	# Connect to Compute instance and prepare ground for Docker installation
    ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} < PythonScripts/src/docker-pre-conf.sh
    echo "Sleep for 90 seconds while the Compute instance restarts"
    sleep 90
    # Ensure the compute is up and running after restart
    echo "Attempting to SSH to Compute..."  

	while (true); do exec 3>/dev/tcp/${ip}/22; if [ $? -eq 0 ]; then echo "SSH up" ; break ; else echo "SSH still down" ; sleep 60 ; fi done

    # Install Docker platform
    ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} < PythonScripts/src/docker-post-conf.sh
    #ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} "sudo service docker start; sleep 60; sudo service docker restart; exit;"
fi

sleep 30

if [ -d "Movies/movies-database" ]; then
    # Initialize and Start a new Docker container for Movies-Database
    scp -i PythonScripts/src/cloudnative -o StrictHostKeyChecking=no -r Movies/movies-database opc@${ip}:/tmp
    ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} "cd /tmp/movies-database; chmod +x start.sh; ./start.sh; exit;"
fi
sleep 5

if [ -d "Bookings/bookings-database" ]; then
    # Initialize and Start a new Docker container for Bookings-Database
    scp -i PythonScripts/src/cloudnative -o StrictHostKeyChecking=no -r Bookings/bookings-database opc@${ip}:/tmp
    ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} "cd /tmp/bookings-database; chmod +x start.sh; ./start.sh; exit;"
fi
sleep 5

if [ -d "ShowTimes/showtimes-database" ]; then
    # Initialize and Start a new Docker container for Showtimes-Database
    scp -i PythonScripts/src/cloudnative -o StrictHostKeyChecking=no -r ShowTimes/showtimes-database opc@${ip}:/tmp
    ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} "cd /tmp/showtimes-database; chmod +x start.sh; ./start.sh; exit;"
fi
sleep 5

if [ -d "Users/users-database" ]; then
    # Initialize and Start a new Docker container for Users-Database
    scp -i PythonScripts/src/cloudnative -o StrictHostKeyChecking=no -r Users/users-database opc@${ip}:/tmp
    ssh -i PythonScripts/src/cloudnative -tt -o StrictHostKeyChecking=no opc@${ip} "cd /tmp/users-database; chmod +x start.sh; ./start.sh; exit;"
fi
sleep 5
