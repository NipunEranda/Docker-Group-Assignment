FROM mysql
RUN apt-get update && apt-get install -y wget
RUN wget https://github.com/NipunEranda/Docker-Group-Assignment/blob/main/Docker-Source/DB/dbScript.sql
RUN ALTER USER 'root'@'localhost' IDENTIFIED BY '1234';