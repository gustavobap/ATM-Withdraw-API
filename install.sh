#!/bin/bash

sudo docker build -t smartmoney_challenge -f ./Docker/smartmoney_challenge.Dockerfile ./Docker && \
sudo docker run --name smartmoney_challenge -p 8080:8080 -it smartmoney_challenge