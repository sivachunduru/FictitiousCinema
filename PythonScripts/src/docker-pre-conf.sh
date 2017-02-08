#!/bin/bash

#sudo yum -y update
sudo cp /etc/selinux/config /etc/selinux/config-old
awk -F"=" '/SELINUX=disabled/{$2="=enforcing";print;next}1' /etc/selinux/config
sudo service iptables start
sudo chkconfig iptables on
sudo shutdown -r +1 && exit
