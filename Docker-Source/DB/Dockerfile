FROM mysql
RUN apt-get update && apt-get install -y wget
RUN cd ~
RUN wget https://github.com/NipunEranda/Docker-Group-Assignment/blob/main/Docker-Source/DB/dbScript.sql
RUN sudo mysql < ~/dbScript.sql