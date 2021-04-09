sudo docker build -t smartmoney_challenge -f smartmoney_challenge.Dockerfile . && \
sudo docker run --name smartmoney_challenge -p 8080:8080 -it smartmoney_challenge
