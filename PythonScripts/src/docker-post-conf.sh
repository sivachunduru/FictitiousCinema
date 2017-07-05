#!/bin/bash

sudo curl -fsSL https://get.docker.com/ | sh
sleep 10
sudo usermod -aG docker opc
sudo service docker start
sudo chkconfig docker on
logout && exit

#sudo shutdown -r +1 && exit