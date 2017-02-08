#!/bin/bash

echo "Starting Bookings-DB..."
docker run --name bookings-db -d \
    -e MYSQL_ROOT_PASSWORD=welcome1 \
    -e MYSQL_DATABASE=cinema -e MYSQL_USER=cinema_service \
    -e MYSQL_PASSWORD=welcome1 -p 3307:3306 mysql:latest

docker exec bookings-db sleep 60

echo "Waiting for DB to start up..."
docker exec bookings-db mysqladmin --silent --wait=10 -ucinema_service -pwelcome1 ping || exit 1

echo "Setting up sample data..."
docker exec -i bookings-db mysql -ucinema_service -pwelcome1 cinema < bookings.sql
