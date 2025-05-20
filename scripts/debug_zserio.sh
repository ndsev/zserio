#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# prints help on command line
print_help()
{
    cat << EOF
Description:
    Runs distr/zserio.jar with attached debugger (VS Code).
    
Example:
    $0 -src ../build -cpp ../build/debug my_test.zs

EOF
}

# main entry point
main()
{
    if [ "$1" == "-h" ] ; then
        print_help
        return 1
    fi

    set_global_common_variables

    ${JAVA_BIN} -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=y -jar ../distr/zserio.jar ${ZSERIO_EXTRA_ARGS} $*
}

# call main function
main "$@"