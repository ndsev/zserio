#!/bin/bash
/usr/lib/jvm/java-8-openjdk-amd64/bin/javac \
        -cp ../../../../distr/runtime_libs/java/zserio_runtime.jar:grpc_jars/* \
        zserio_runtime/*.java service_poc/*.java zserio_service_grpc/*.java zserio_service_http/*.java *.java
/usr/lib/jvm/java-8-openjdk-amd64/bin/java \
        -cp .:../../../../distr/runtime_libs/java/zserio_runtime.jar:grpc_jars/* \
        ServiceTest
