# Installation Instructions

First of all, Navigate to Docker-Source and run this command -> sudo chmod 777 deploy* setup

After that run this command -> ./setup

./setup command will run each deployment bash scripts to install the complete system.

Install the system in a clean docker installation, otherwise if there is an existing mysql container, the script will fail.

to enable autoscaling run ./autoscale, ./setup will automatically run the autoscale script.
