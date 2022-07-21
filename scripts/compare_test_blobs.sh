#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

set_common_variables()
{
    # bash command find to use, defaults to "/usr/bin/find" if not set
    # (bash command find makes trouble under MinGW because it clashes with Windows find command)
    FIND="${FIND:-/usr/bin/find}"
    if [ ! -f "`which "${FIND}"`" ] ; then
        stderr_echo "Cannot find bash command find! Set FIND environment variable."
        return 1
    fi
}

print_help_env()
{
    cat << EOF
Uses the following environment variables:
    FIND            Bash command find to use. Default is "/usr/bin/find".

    Either set these directly, or create 'scripts/build-env.sh' that sets
    these. It's sourced automatically if it exists.

EOF
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Compares BLOBs created by Zserio tests in build/test folder.
    Verifies taht BLOBs from all platforms are the same.

Usage:
    $0 [-h] [-e] dir dir...

Arguments:
    -h, --help                  Show this help.
    -e, --help-env              Show help for environment variables.
    -v, --verbose               Detailed report about the compared BLOBs.
    dir                         Specify the directories to use (at least 2).

Examples:
    $0 build/test/java build/test/cpp/linux64-gcc/debug python
    $0 build/test/java build/test/python

EOF
}

# Parse all command line arguments.
#
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
# 3 - Environment help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    exit_if_argc_lt $# 2
    local VERBOSE_OUT="$1"; shift
    local PARAM_DIRS_OUT="$1"; shift

    eval ${VERBOSE_OUT}=0

    local ARR=()
    local NUM_DIRS=0
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
                ;;

            "-v" | "--verbose")
                eval ${VERBOSE_OUT}=1
                shift
                ;;

            "-"*)
                stderr_echo "Invalid switch ${ARG}!"
                echo
                return 1
                ;;

            *)
                eval ${PARAM_DIRS_OUT}[${NUM_DIRS}]=\${ARG} # \$ forbids expansion and let it on eval
                NUM_DIRS=$((NUM_DIRS + 1))
                shift
                ;;
        esac
        ARG="$1"
    done

    if [ ${NUM_DIRS} -lt 2 ] ; then
        stderr_echo "Specify at least two directories!"
        echo
        return 1
    fi

    return 0
}

# Main function.
main()
{
    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    local VERBOSE
    local PARAM_DIRS=()
    parse_arguments VERBOSE PARAM_DIRS "$@"

    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        return 1
    fi

    set_common_variables

    local FIND_PARAMS="-name *.blob -o -name *.json -not -path *.mypy_cache*"
    local BLOBS_0=($("${FIND}" "${PARAM_DIRS[0]}" ${FIND_PARAMS} | sort))
    local NUM_BLOBS=${#BLOBS_0[@]}
    local CMP_RESULT=0
    for DIR in ${PARAM_DIRS[@]:1}; do
        local BLOBS_N=($("${FIND}" "${DIR}" ${FIND_PARAMS} | sort))

        if [ ${NUM_BLOBS} -ne ${#BLOBS_N[@]} ] ; then
            stderr_echo "Wrong number of BLOBs!"
            stderr_echo "\"${PARAM_DIRS[0]}\" contains ${NUM_BLOBS} BLOBs," \
                        "while \"${DIR}\" contains ${#BLOBS_N[@]} BLOBs!"
            echo
            return 1
        fi

        echo "Comparing ${PARAM_DIRS[0]} with ${DIR}"
        for i in ${!BLOBS_0[@]} ; do
            if [ ${VERBOSE} -ne 0 ] ; then
                echo "  ${BLOBS_0[$i]##*/} <-> ${BLOBS_N[$i]##*/}"
            fi
            if [ ${BLOBS_0[$i]} == *.blob ] ; then
                cmp "${BLOBS_0[$i]}" "${BLOBS_N[$i]}"
                if [ $? -ne 0 ] ; then
                    CMP_RESULT=1
                fi
            else
                diff --strip-trailing-cr "${BLOBS_0[$i]}" "${BLOBS_N[$i]}"
                if [ $? -ne 0 ] ; then
                    CMP_RESULT=1
                fi
            fi
        done
    done

    if [ ${CMP_RESULT} -ne 0 ] ; then
        stderr_echo "Comparison failed!"
        echo
        return 1
    fi

    echo
    echo "Successfully compared ${NUM_BLOBS} BLOBs in each directory."
    echo
}

# call main function
main "$@"
