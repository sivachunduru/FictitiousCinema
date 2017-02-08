#!/bin/bash

echo "Starting ShowTimes-DB..."
docker run --name showtimes-db -d \
    -e MYSQL_ROOT_PASSWORD=welcome1 \
    -e MYSQL_DATABASE=cinema -e MYSQL_USER=cinema_service \
    -e MYSQL_PASSWORD=welcome1 -p 3308:3306 mysql:latest

docker exec showtimes-db sleep 60

echo "Waiting for DB to start up..."
docker exec showtimes-db mysqladmin --silent --wait=10 -ucinema_service -pwelcome1 ping || exit 1

echo "Setting up sample data..."
docker exec -i showtimes-db mysql -ucinema_service -pwelcome1 cinema < showtimes.sql
