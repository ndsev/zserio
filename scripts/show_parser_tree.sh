#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Show parser tree using grun.
show_parser_tree()
{
    exit_if_argc_ne $# 4
    local PARAM_ZSERIO_SOURCE="${1}" ; shift
    local PARAM_PARSER_RULE="${1}" ; shift
    local ZSERIO_PROJECT_ROOT="${1}" ; shift
    local PARSER_TREE_BUILD_DIR="${1}" ; shift

    local ANTLR4_JAR="${ZSERIO_PROJECT_ROOT}/3rdparty/java/antlr-4.7.2-complete.jar"
    local ANTLR4_GRAMMAR_DIR="${ZSERIO_PROJECT_ROOT}/compiler/core/antlr"

    "${JAVA_BIN}" -jar "${ANTLR4_JAR}" "${ANTLR4_GRAMMAR_DIR}"/Zserio*.g4 -o "${PARSER_TREE_BUILD_DIR}"
    if [ $? -ne 0 ]; then
        return 1
    fi
    echo

    "${JAVAC_BIN}" "${PARSER_TREE_BUILD_DIR}"/Zserio*.java -cp .:"${ANTLR4_JAR}" 
    if [ $? -ne 0 ]; then
        return 1
    fi
    echo

    pushd "${PARSER_TREE_BUILD_DIR}" > /dev/null
    java -cp .:"${ANTLR4_JAR}" org.antlr.v4.gui.TestRig Zserio "${PARAM_PARSER_RULE}" -tokens -gui \
            "${PARAM_ZSERIO_SOURCE}"
    local GRUN_RESULT=$?
    popd > /dev/null

    return ${GRUN_RESULT}
}

# Set and check global variables used by this script.
set_parser_tree_global_variables()
{
    # check java binary
    if [ -n "${JAVA_HOME}" ] ; then
        JAVA_BIN="${JAVA_HOME}/bin/java"
    fi
    JAVA_BIN="${JAVA_BIN:-java}"
    if [ ! -f "`which "${JAVA_BIN}"`" ] ; then
        stderr_echo "Cannot find java! Set JAVA_HOME or JAVA_BIN environment variable."
        return 1
    fi

    # check javac binary
    if [ -n "${JAVA_HOME}" ] ; then
        JAVAC_BIN="${JAVA_HOME}/bin/javac"
    fi
    JAVAC_BIN="${JAVAC_BIN:-javac}"
    if [ ! -f "`which "${JAVAC_BIN}"`" ] ; then
        stderr_echo "Cannot find java compiler! Set JAVA_HOME or JAVAC_BIN environment variable."
        return 1
    fi

    return 0
}

# Print help on the environment variables used by this script.
print_parser_tree_help_env()
{
    cat << EOF
Uses the following environment variables:
    JAVAC_BIN   Java compiler executable to use. Default is "javac".
    JAVA_BIN    Java executable to use. Default is "java".

EOF
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Shows ANTLR4 parser tree.

Usage:
    $0 [-h] [-c] [-p] [-o <dir>] [-r rule] zserio_source

Arguments:
    -h, --help      Show this help.
    -c, --clean     Clean output directory and exit.
    -p, --purge     Purge output directory before start.
    -o <dir>, --output-directory <dir>
                    Output directory where to store ANTLR4 outputs.
    -r <rule>, --parser-rule <rule>
                    Parser rule to accept (default is packageDeclaration).
    zserio_source   Zserio source for which to show ANTLR4 parser tree.

Examples:
    $0 test.zs

EOF

    print_parser_tree_help_env
}

# Parse all command line arguments.
#
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    local NUM_OF_ARGS=5
    exit_if_argc_lt $# ${NUM_OF_ARGS}
    local PARAM_ZSERIO_SOURCE_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local PARAM_PARSER_RULE_OUT="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${SWITCH_CLEAN_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=();
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-c" | "--clean")
                eval ${SWITCH_CLEAN_OUT}=1
                shift
                ;;

            "-p" | "--purge")
                eval ${SWITCH_PURGE_OUT}=1
                shift
                ;;

            "-o" | "--output-directory")
                eval ${PARAM_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-r" | "--parser-rule")
                eval ${PARAM_PARSER_RULE_OUT}="$2"
                shift 2
                ;;

            "-"*)
                stderr_echo "Invalid switch ${ARG}!"
                echo
                return 1
                ;;

            *)
                if [ ${NUM_PARAMS} -eq 1 ] ; then
                    stderr_echo "Invalid argument ${PARAM}!"
                    echo
                    return 1
                fi
                eval ${PARAM_ZSERIO_SOURCE_OUT}="${ARG}"
                NUM_PARAMS=1
                shift
                ;;
        esac
        ARG="$1"
    done

    if [[ ${!SWITCH_CLEAN_OUT} == 0 && ${NUM_PARAMS} == 0 ]] ; then
        stderr_echo "Zserio source is not specified!"
        echo
        return 1
    fi

    return 0
}

main()
{
    # get the project root
    local ZSERIO_PROJECT_ROOT="${SCRIPT_DIR}/.."

    # parse command line arguments
    local PARAM_ZSERIO_SOURCE
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local PARAM_PARSER_RULE="packageDeclaration"
    local SWITCH_CLEAN
    local SWITCH_PURGE
    parse_arguments PARAM_ZSERIO_SOURCE PARAM_OUT_DIR PARAM_PARSER_RULE SWITCH_CLEAN SWITCH_PURGE $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # set global variables if needed
    set_parser_tree_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # purge if requested and then create build directory
    local PARSER_TREE_BUILD_DIR="${PARAM_OUT_DIR}/build/parser_tree"
    if [[ ${SWITCH_PURGE} == 1 || ${SWITCH_CLEAN} == 1 ]] ; then
        echo "Purging build directory."
        echo
        rm -rf "${PARSER_TREE_BUILD_DIR}/"
    fi
    mkdir -p "${PARSER_TREE_BUILD_DIR}"

    # show parser tree using grun
    if [[ ${SWITCH_CLEAN} == 0 ]] ; then
        # grun needs absolute paths
        convert_to_absolute_path "${PARAM_ZSERIO_SOURCE}" PARAM_ZSERIO_SOURCE
        convert_to_absolute_path "${ZSERIO_PROJECT_ROOT}" ZSERIO_PROJECT_ROOT
        show_parser_tree "${PARAM_ZSERIO_SOURCE}" "${PARAM_PARSER_RULE}" "${ZSERIO_PROJECT_ROOT}" \
                         "${PARSER_TREE_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    return 0
}

# call main function
main "$@"

