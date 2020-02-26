#!/bin/bash

javac -d build -cp ../../../../distr/runtime_libs/java/zserio_runtime.jar:3rdparty/org.eclipse.paho.client.mqttv3-1.2.2.jar \
      zserio_runtime/*.java pubsub_poc/*.java zserio_pubsub_mqtt/*.java *.java
