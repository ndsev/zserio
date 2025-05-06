#!/bin/sh

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

set_global_common_variables

${JAVA_BIN} -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=y -jar ../distr/zserio.jar ${ZSERIO_EXTRA_ARGS} $*