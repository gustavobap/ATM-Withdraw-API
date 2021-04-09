FROM tomcat:9

MAINTAINER Gustavo Peixoto <gustavo.bul.mobile@gmail.com>

COPY challenge.war /usr/local/tomcat/webapps/
RUN chown root:root /usr/local/tomcat/webapps/challenge.war
RUN chmod ug+rx /usr/local/tomcat/webapps/challenge.war
