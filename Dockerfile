FROM tomcat:9.0.53-jdk17-corretto
ENV DATABASE_HOST 127.0.0.1:3306
ENV DATABASE_NAME varane
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
COPY usables/index.html /usr/local/tomcat/webapps/index.html
EXPOSE 8080
CMD ["catalina.sh", "run"]