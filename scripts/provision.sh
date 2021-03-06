#!/bin/bash
set -euo pipefail
IFS=$'\n\t'

sudo -v

echo "==> upgrading system"
sudo apt-get --assume-yes update
sudo apt-get --assume-yes upgrade

echo "==> installing docker"
curl -sSL https://get.docker.com/ubuntu/ | sudo sh

echo "==> installing leiningen"
sudo apt-get --assume-yes install default-jre
wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
sudo su -c "chown root:root ./lein && chmod a+x ./lein"
sudo su -c "mv ./lein /usr/local/bin/"

echo "==> installing useful tools"
sudo apt-get --assume-yes install htop redis-tools

echo "==> setting up ElasticSearch"
sudo docker pull dockerfile/elasticsearch
sudo docker run \
   --name elasticsearch \
   --detach \
   --restart=always \
   --publish 9200:9200 \
   --publish 9300:9300 \
   dockerfile/elasticsearch

echo "==> setting up REDIS"
sudo docker pull redis
sudo docker run \
   --name redis \
   --detach \
   --restart=always \
   --publish 6379:6379 \
   redis
