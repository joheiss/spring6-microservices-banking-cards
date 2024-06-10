# Jib

Jib only works for Java applications - while buildpack works for others (node.js, ...) as well.

Jib doesn't need a docker daemon - buildpack does.

### web site
https://github.com/GoogleContainerTools/jib

## generate docker image using jib

### (1) copy jib-plug to <plugins> section of pom.xml - see documentation for maven in above github repo.

### (2) execute command - uses the docker daemon

mvn compile jib:dockerBuild

### (3) ... without docker daemon - must store the image in a remote docker repository. This muat be added to the <plugin><configuration> see documentation in github

mvn compile jib:build

### run docker container
docker run -d -p 8093:8093 joheiss/sb3-cards:v1

### push docker image to docker hub
docker image push docker.io/joheiss/sb3-cards:v1
