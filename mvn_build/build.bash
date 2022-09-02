#!/bin/bash

SCRIPT_DIR=$(dirname $0)
cd $SCRIPT_DIR

ANTLR=src/main/antlr4/zserio
SOURCE=src/main/java
TEST=src/test/java
FREEMARKER=src/main/resources/freemarker
METAINF=src/main/resources/META-INF/services

# directory
if [ -d src ]; then
  rm -rf src
fi
mkdir -p $ANTLR $SOURCE $TEST $FREEMARKER $METAINF

# core
cp -r ../compiler/core/antlr $ANTLR
cp -r ../compiler/core/src/* $SOURCE
cp -r ../compiler/core/test/* $TEST

# extensions
extensions=(cpp doc java python xml)
for extension in ${extensions[@]}
do
    cp -r ../compiler/extensions/${extension}/src/* $SOURCE
    cp -r ../compiler/extensions/${extension}/test/* $TEST
    cp -r ../compiler/extensions/${extension}/freemarker $FREEMARKER/${extension}
    cat ../compiler/extensions/${extension}/metainf/services/zserio.tools.Extension >> $METAINF/zserio.tools.Extension
done

# build
mvn clean package

