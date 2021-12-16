#!/usr/bin/env bash
export IMAGE_NAME=gcr.io/bootiful/hello-knj-native:latest
mvnw -Pnative  -DskipTests=true clean package spring-boot:build-image \
 -Dspring-boot.build-image.imageName=${IMAGE_NAME}
docker images | grep $IMAGE_NAME
docker push $IMAGE_NAME