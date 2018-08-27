#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check test global variables.
set_test_global_variables()
{
    # UNZIP to use, defaults to "unzip" if not set
    UNZIP="${UNZIP:-unzip}"
    if [ ! -f "`which "${UNZIP}"`" ] ; then
        stderr_echo "Cannot find unzip! Set UNZIP environment variable."
        return 1
    fi

    # GRPC setup
    GRPC_ROOT="${GRPC_ROOT:-""}"

    return 0
}

# Print help on the environment variables used for this release script.
print_test_help_env()
{
    cat << EOF
Uses the following environment variables for releasing:
    UNZIP       Unzip executable to use. Default is "unzip".

Uses the following environment variables for buidling:
    GRPC_ROOT   Root path to GRPC repository. GRPC is disabled by default.
EOF
}

# Run Zserio tests.
#
# $1 - The directory where Zserio release is located.
# $2 - The Zserio release version to test.
# $3 - The directory of Zserio project root.
# $4 - The directory where to store test outputs.
# $5 - '1' to run Java tests.
# $6 - The name of variable which contains the array of C++ targets for which to compile.
# $7 - Whether to clean projects instead of run tests.
# $8 - The name of example test to run.
test()
{
    exit_if_argc_ne $# 8
    local ZSERIO_RELEASE_DIR="$1"
    local ZSERIO_VERSION="$2"
    local ZSERIO_PROJECT_ROOT="$3"
    local TEST_OUT_DIR="$4"
    local PARAM_JAVA=$5
    local MSYS_WORKAROUND_TEMP=("${!6}")
    local TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local SWITCH_CLEAN="$7"
    local SWITCH_TEST_NAME="$8"

    local TEST_SRC_DIR="${ZSERIO_PROJECT_ROOT}/test"

    # unpack testing release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${TEST_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # run Java Zserio tests
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        local MESSAGE="Zserio Java tests"
        echo "STARTING - ${MESSAGE}"
        local ANT_ARGS=("-Dzserio.release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                        "-Dzserio_java_test.build_dir=${TEST_OUT_DIR}/java")
        if [[ ${SWITCH_TEST_NAME} != "" ]] ; then
            ANT_ARGS+=("-Dzserio_java_test.filter=${SWITCH_TEST_NAME}")
        fi
        if [[ ${SWITCH_CLEAN} == 1 ]] ; then
            local JAVA_TARGET="clean"
        else
            local JAVA_TARGET="run"
        fi
        compile_java "${TEST_SRC_DIR}/build.xml" ANT_ARGS[@] "${JAVA_TARGET}"
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    fi

    # run C++ Zserio tests
    if [[ ${#TARGETS[@]} != 0 ]] ; then
        local MESSAGE="Zserio C++ tests"
        echo "STARTING - ${MESSAGE}"

        local HOST_PLATFORM
        get_host_platform HOST_PLATFORM
        if [ $? -ne 0 ]; then
            return 1
        fi

        local CMAKE_ARGS=("-DZSERIO_RUNTIME_INCLUDE_INSPECTOR=ON"
                          "-DZSERIO_RELEASE_ROOT=${UNPACKED_ZSERIO_RELEASE_DIR}"
                          "-DGRPC_ROOT=${GRPC_ROOT}")
        local CTEST_ARGS=()
        if [[ ${SWITCH_TEST_NAME} != "" ]]; then
            CTEST_ARGS+=("-L ${SWITCH_TEST_NAME}")
        fi
        if [[ ${SWITCH_CLEAN} == 1 ]] ; then
            local CPP_TARGET="clean"
        else
            local CPP_TARGET="all"
        fi
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" "${TEST_SRC_DIR}" TARGETS[@] \
                    CMAKE_ARGS[@] CTEST_ARGS[@] ${CPP_TARGET}
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    fi

    return 0
}

# Unpack Zserio release zips.
#
# $1 - Zserio tests output directory.
# $2 - Directory of Zserio release to unpack.
# $3 - Zserio release version.
# $4 - The name of variable to fill with the directory of unpacked Zserio release.
unpack_release()
{
    exit_if_argc_ne $# 4
    local TEST_OUT_DIR="$1"
    local ZSERIO_RELEASE_DIR="$2"
    local ZSERIO_VERSION="$3"
    local UNPACKED_ZSERIO_RELEASE_DIR_OUT="$4"

    local UNPACKED_ZSERIO_RELEASE_DIR_LOC="${TEST_OUT_DIR}/tested_release"
    rm -rf "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}" # always use fresh release
    mkdir -p "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"

    # bin
    "${UNZIP}" -q "${ZSERIO_RELEASE_DIR}/zserio-${ZSERIO_VERSION}-bin.zip" -d "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip Zserio binaries to ${UNPACKED_ZSERIO_RELEASE_DIR_LOC}!"
        return 1
    fi

    # runtime-libs
    "${UNZIP}" -q "${ZSERIO_RELEASE_DIR}/zserio-${ZSERIO_VERSION}-runtime-libs.zip" -d "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip Zserio runtime libraries to ${UNPACKED_ZSERIO_RELEASE_DIR_LOC}!"
        return 1
    fi

    eval ${UNPACKED_ZSERIO_RELEASE_DIR_OUT}="'${UNPACKED_ZSERIO_RELEASE_DIR_LOC}'"

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs Zserio tests on Zserio release compiled in release-ver directory.

Usage:
    $0 [-h] [-t TEST_NAME] package...

Arguments:
    -h, --help                Show this help.
    -c, --clean               Clean package instead of build.
    -p, --purge               Purge test build directory.
    -t, --test-name TEST_NAME Run only TEST_NAME test from examples test suite.
    package                   Specify the package to test.

Package can be a combination of:
    java           Zserio Java tests.
    cpp-linux32    Zserio C++ tests for linux32 target using gcc compiler.
    cpp-linux64    Zserio C++ tests for linux64 target using gcc compiler.
    cpp-windows32  Zserio C++ tests for windows32 target using MinGW 4.5.4 compiler.
    cpp-windows64  Zserio C++ tests for windows64 target using MinGW64 4.5.4 compiler.
    all-linux32    Zserio tests - all available linux32 packages.
    all-linux64    Zserio tests - all available linux64 packages.
    all-windows32  Zserio tests - all available windows32 packages.
    all-windows64  Zserio tests - all available windows64 packages.

Examples:
    $0 java cpp-linux64
    $0 -t language/sql_tables cpp-linux64
    $0 all-linux64

EOF

    print_test_help_env
    echo
    print_help_env
}

# Parse all command line arguments.
#
# Parameters:
# -----------
# $1 - The name of variable to set to 1 if "java" argument is present.
# $2 - The name of variable to fill with array of targets given as arguments.
# $3 - The name of variable to fill with example test name given by switch '-t'.
# $@ - The command line arguments to parse.
#
# Usage:
# ------
# local PARAM
# local SWITCH
# parse_arguments PARAM SWITCH $@
# if [[ ${SWITCH} == 1 ]] ; then
#     SWITCH has been present, do something
# fi
#
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    exit_if_argc_lt $# 5
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift
    local SWITCH_TEST_NAME_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${SWITCH_TEST_NAME_OUT}=""
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

            "-t" | "--test-name")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Test name is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_NAME_OUT}="${ARG}"
                shift
                ;;

            "-"*)
                stderr_echo "Invalid switch ${ARG}!"
                echo
                return 1
                ;;

            *)
                PARAM_ARRAY[NUM_PARAMS]=${ARG}
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift
                ;;
        esac
        ARG="$1"
    done

    local NUM_TARGETS=0
    local PARAM
    for PARAM in "${PARAM_ARRAY[@]}" ; do
        case "${PARAM}" in
            "java")
                eval ${PARAM_JAVA_OUT}=1
                ;;

            "cpp-linux32" | "cpp-linux64" | "cpp-windows32" | "cpp-windows64")
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#cpp-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
                ;;

            "all-linux32" | "all-linux64" | "all-windows32" | "all-windows64")
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#all-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
                ;;

            *)
                stderr_echo "Invalid argument ${PARAM}!"
                echo
                return 1
        esac
    done

    if [[ ${!PARAM_JAVA_OUT} == 0 && ${NUM_TARGETS} -eq 0 && ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Package to test is not specified!"
        echo
        return 1
    fi

    return 0
}

main()
{
    echo "Compilation and testing of Zserio sources."
    echo

    # parse command line arguments
    local PARAM_JAVA
    local PARAM_CPP_TARGET_ARRAY
    local SWITCH_CLEAN
    local SWITCH_PURGE
    local SWITCH_TEST_NAME
    parse_arguments PARAM_JAVA PARAM_CPP_TARGET_ARRAY SWITCH_CLEAN SWITCH_PURGE SWITCH_TEST_NAME $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # set global variables
    set_test_global_variables "${PARAM_NDS_ZSERIO}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        set_global_java_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi
    if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ]] ; then
        set_global_cpp_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # get Zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # purge if requested and then create test output directory
    local TEST_OUT_DIR="${ZSERIO_PROJECT_ROOT}/build/test"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test directory."
        echo
        rm -rf "${TEST_OUT_DIR}/"
    fi
    mkdir -p "${TEST_OUT_DIR}"

    # print information
    echo "Testing release: ${ZSERIO_RELEASE_DIR}"
    echo "Test output directory: ${TEST_OUT_DIR}"
    echo

    # run test
    test "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" \
         ${PARAM_JAVA} PARAM_CPP_TARGET_ARRAY[@] ${SWITCH_CLEAN} "${SWITCH_TEST_NAME}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

main "$@"
