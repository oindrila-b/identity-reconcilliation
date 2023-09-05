# identity-reconcilliation
A spring boot application to keep a track of the users logging with same email or phone number.

This project runs locally on the machine using Docker.

# Steps Performed :

1. Logged In using Docker, installed Docker Desktop.
2. Since Docker container cannot connect to the Mysql server running in the local host, we need to pull a mysql image from docker hub, this is to run an instance of mysql on a seperate docker container.
3. Then we create a  new docker network and give it a name.
4. We run the mysql container.
5. Then we create the Dockerfile.
6. Package the project using maven.
7. build the project as an image.
8. Run the docker container. 
