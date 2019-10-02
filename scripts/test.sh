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
Uses the following environment variables for testing:
    UNZIP       Unzip executable to use. Default is "unzip".
    GRPC_ROOT   Root path to GRPC repository. GRPC is disabled by default.
EOF
}

# Run python tests.
test_python()
{
    exit_if_argc_ne $# 3
    local PYTHON_RUNTIME_ROOT="$1"; shift
    local TEST_SRC_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")

    local TEST_FILE="${TEST_SRC_DIR}/tests.py"
    local PYLINT_RCFILE="${PYTHON_RUNTIME_ROOT}/pylintrc.txt"

    echo
    echo "Running python tests."
    echo

    python "${TEST_FILE}" "${TEST_ARGS[@]}" --pylint_rcfile="${PYLINT_RCFILE}"
    local PYTHON_RESULT=$?
    if [ ${PYTHON_RESULT} -ne 0 ] ; then
        stderr_echo "Running python failed with return code ${PYTHON_RESULT}!"
        return 1
    fi

    echo "Running pylint on python test utilities."

    local PYLINT_ARGS=("--disable=missing-docstring")
    run_pylint "${PYLINT_RCFILE}" PYLINT_ARGS[@] "${TEST_FILE}" "${TEST_SRC_DIR}/utils/python"/*
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

# Run zserio tests.
test()
{
    exit_if_argc_ne $# 12
    local ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP98_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local SWITCH_CLEAN="$1"; shift
    local SWITCH_GRPC="$1"; shift
    local SWITCH_TEST_NAME="$1"; shift

    local TEST_SRC_DIR="${ZSERIO_PROJECT_ROOT}/test"

    # unpack testing release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${TEST_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # run C++ zserio tests
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        local MESSAGE="zserio C++ tests"
        echo "STARTING - ${MESSAGE}"

        local HOST_PLATFORM
        get_host_platform HOST_PLATFORM
        if [ $? -ne 0 ] ; then
            return 1
        fi

        local CPP_TEST_NAME="${SWITCH_TEST_NAME}"
        if [[ "${CPP_TEST_NAME}" == "" ]]; then
            CPP_TEST_NAME="*"
        fi
        local CMAKE_ARGS=("-DZSERIO_RUNTIME_INCLUDE_INSPECTOR=ON"
                          "-DZSERIO_RELEASE_ROOT=${UNPACKED_ZSERIO_RELEASE_DIR}"
                          "-DZSERIO_TEST_NAME=${CPP_TEST_NAME}"
                          "-DGRPC_ENABLED=${SWITCH_GRPC}"
                          "-DGRPC_ROOT=${GRPC_ROOT}")
        local CTEST_ARGS=()
        if [[ ${SWITCH_CLEAN} == 1 ]] ; then
            local CPP_TARGET="clean"
        else
            local CPP_TARGET="all"
        fi
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/cpp" "${TEST_SRC_DIR}" CPP_TARGETS[@] \
                    CMAKE_ARGS[@] CTEST_ARGS[@] ${CPP_TARGET}
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    fi

    # run C++98 zserio tests
    if [[ ${#CPP98_TARGETS[@]} != 0 ]] ; then
        local MESSAGE="zserio C++98 tests"
        echo "STARTING - ${MESSAGE}"

        local HOST_PLATFORM
        get_host_platform HOST_PLATFORM
        if [ $? -ne 0 ] ; then
            return 1
        fi

        local CPP98_TEST_NAME="${SWITCH_TEST_NAME}"
        if [[ "${CPP98_TEST_NAME}" == "" ]]; then
            CPP98_TEST_NAME="*"
        fi
        local CMAKE_ARGS=("-DZSERIO_RUNTIME_INCLUDE_INSPECTOR=ON"
                          "-DZSERIO_RELEASE_ROOT=${UNPACKED_ZSERIO_RELEASE_DIR}"
                          "-DZSERIO_TEST_NAME=${CPP98_TEST_NAME}"
                          "-DZSERIO_CPP_STANDARD=c++98"
                          "-DGRPC_ENABLED=${SWITCH_GRPC}"
                          "-DGRPC_ROOT=${GRPC_ROOT}")
        local CTEST_ARGS=()
        if [[ ${SWITCH_CLEAN} == 1 ]] ; then
            local CPP98_TARGET="clean"
        else
            local CPP98_TARGET="all"
        fi
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/cpp98" "${TEST_SRC_DIR}" CPP98_TARGETS[@] \
                    CMAKE_ARGS[@] CTEST_ARGS[@] ${CPP98_TARGET}
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    fi

    # run Java zserio tests
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        local MESSAGE="zserio Java tests"
        echo "STARTING - ${MESSAGE}"
        local ANT_ARGS=("-Dzserio.release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                        "-Dzserio_java_test.build_dir=${TEST_OUT_DIR}/java")
        if [[ ${SWITCH_GRPC} == 1 ]] ; then
            ANT_ARGS+=("-Dzserio_java_test.grpc=yes")
        fi
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

    # run Python zserio tests
    if [[ ${PARAM_PYTHON} != 0 ]]; then
        local MESSAGE="zserio Python tests"
        echo "STARTING - ${MESSAGE}"

        activate_python_virtualenv "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi

        if [[ ${SWITCH_CLEAN} == 1 ]] ; then
            rm -rf "${TEST_OUT_DIR}/python"
        else
            local TEST_ARGS=("--release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                             "--build_dir=${TEST_OUT_DIR}/python"
                             "--java=${JAVA_BIN}")
            if [[ ${SWITCH_GRPC} == 1 ]] ; then
                TEST_ARGS+=("--grpc=1")
            fi
            if [[ ${SWITCH_TEST_NAME} != "" ]] ; then
                TEST_ARGS+=("--filter=${SWITCH_TEST_NAME}")
            fi
            local PYTHON_RUNTIME_ROOT="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/runtime"
            test_python "${PYTHON_RUNTIME_ROOT}" "${TEST_SRC_DIR}" TEST_ARGS[@]
            if [ $? -ne 0 ] ; then
                return 1
            fi
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    fi

    return 0
}

# Unpack zserio release zips.
unpack_release()
{
    exit_if_argc_ne $# 4
    local TEST_OUT_DIR="$1"; shift
    local ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift
    local UNPACKED_ZSERIO_RELEASE_DIR_OUT="$1"; shift

    local UNPACKED_ZSERIO_RELEASE_DIR_LOC="${TEST_OUT_DIR}/tested_release"
    rm -rf "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}" # always use fresh release
    mkdir -p "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"

    # bin
    "${UNZIP}" -q "${ZSERIO_RELEASE_DIR}/zserio-${ZSERIO_VERSION}-bin.zip" \
        -d "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip zserio binaries to ${UNPACKED_ZSERIO_RELEASE_DIR_LOC}!"
        return 1
    fi

    # runtime-libs
    "${UNZIP}" -q "${ZSERIO_RELEASE_DIR}/zserio-${ZSERIO_VERSION}-runtime-libs.zip" \
        -d "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip zserio runtime libraries to ${UNPACKED_ZSERIO_RELEASE_DIR_LOC}!"
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
    Runs zserio tests on zserio release compiled in release-ver directory.

Usage:
    $0 [-h] [-t TEST_NAME] package...

Arguments:
    -h, --help                Show this help.
    -c, --clean               Clean package instead of build.
    -p, --purge               Purge test build directory.
    -o <dir>, --output-directory <dir>
                              Output directory where tests will be run.
    -g, --grpc                Enable gRPC language tests (disabled by default).
    -t, --test-name TEST_NAME Run only TEST_NAME test.
    package                   Specify the package to test.

Package can be a combination of:
    cpp-linux32           Zserio C++ tests for linux32 target using gcc compiler.
    cpp-linux64           Zserio C++ tests for linux64 target using gcc compiler.
    cpp-windows32-mingw   Zserio C++ tests for windows32 target (MinGW).
    cpp-windows64-mingw   Zserio C++ tests for windows64 target (MinGW64).
    cpp-windows32-msvc    Zserio C++ tests for windows32 target (MSVC).
    cpp-windows64-msvc    Zserio C++ tests for windows64 target (MSVC).
    cpp98-linux32         Zserio C++98 tests for linux32 target using gcc compiler.
    cpp98-linux64         Zserio C++98 tests for linux64 target using gcc compiler.
    cpp98-windows32-mingw Zserio C++98 tests for windows32 target (MinGW).
    cpp98-windows64-mingw Zserio C++98 tests for windows64 target (MinGW64).
    cpp98-windows32-msvc  Zserio C++98 tests for windows32 target (MSVC).
    cpp98-windows64-msvc  Zserio C++98 tests for windows64 target (MSVC).
    java                  Zserio Java tests.
    python                Zserio Python tests.
    all-linux32           Zserio tests - all available linux32 packages.
    all-linux64           Zserio tests - all available linux64 packages.
    all-windows32-mingw   Zserio tests - all available windows32 packages (MinGW).
    all-windows64-mingw   Zserio tests - all available windows64 packages (MinGW64).
    all-windows32-msvc    Zserio tests - all available windows32 packages (MSVC).
    all-windows64-msvc    Zserio tests - all available windows64 packages (MSVC).

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
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    exit_if_argc_lt $# 8
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_CPP98_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift
    local SWITCH_GRPC_OUT="$1"; shift
    local SWITCH_TEST_NAME_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${SWITCH_CLEAN_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0
    eval ${SWITCH_GRPC_OUT}=0
    eval ${SWITCH_TEST_NAME_OUT}=""

    local NUM_PARAMS=0
    local PARAM_ARRAY=()
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

            "-g" | "--grpc")
                eval ${SWITCH_GRPC_OUT}=1
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

    local NUM_CPP_TARGETS=0
    local NUM_CPP98_TARGETS=0
    local PARAM
    for PARAM in "${PARAM_ARRAY[@]}" ; do
        case "${PARAM}" in
            "cpp-linux32" | "cpp-linux64" | "cpp-windows32-"* | "cpp-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#cpp-}"
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                ;;

            "cpp98-linux32" | "cpp98-linux64" | "cpp98-windows32-"* | "cpp98-windows64-"*)
                eval ${PARAM_CPP98_TARGET_ARRAY_OUT}[${NUM_CPP98_TARGETS}]="${PARAM#cpp98-}"
                NUM_CPP98_TARGETS=$((NUM_CPP98_TARGETS + 1))
                ;;

            "java")
                eval ${PARAM_JAVA_OUT}=1
                ;;

            "python")
                eval ${PARAM_PYTHON_OUT}=1
                ;;

            "all-linux32" | "all-linux64" | "all-windows32-"* | "all-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#all-}"
                eval ${PARAM_CPP98_TARGET_ARRAY_OUT}[${NUM_CPP98_TARGETS}]="${PARAM#all-}"
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                NUM_CPP98_TARGETS=$((NUM_CPP98_TARGETS + 1))
                ;;

            *)
                stderr_echo "Invalid argument ${PARAM}!"
                echo
                return 1
        esac
    done

    if [[ ${NUM_CPP_TARGETS} -eq 0 &&
          ${NUM_CPP98_TARGETS} -eq 0 &&
          ${!PARAM_JAVA_OUT} == 0 &&
          ${!PARAM_PYTHON_OUT} == 0 &&
          ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Package to test is not specified!"
        echo
        return 1
    fi

    return 0
}

main()
{
    echo "Compilation and testing of zserio sources."
    echo

    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # parse command line arguments
    local PARAM_CPP_TARGET_ARRAY=()
    local PARAM_CPP98_TARGET_ARRAY=()
    local PARAM_JAVA
    local PARAM_PYTHON
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_CLEAN
    local SWITCH_PURGE
    local SWITCH_GRPC
    local SWITCH_TEST_NAME
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_CPP98_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_OUT_DIR \
                    SWITCH_CLEAN SWITCH_PURGE SWITCH_GRPC SWITCH_TEST_NAME $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # set global variables
    set_global_common_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    set_test_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 || ${#PARAM_CPP98_TARGET_ARRAY[@]} -ne 0 ]] ; then
        set_global_cpp_variables
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_JAVA} == 1 ]] ; then
        set_global_java_variables
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_PYTHON} != 0 ]] ; then
        set_global_python_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # get zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # purge if requested and then create test output directory
    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"
    local TEST_OUT_DIR="${ZSERIO_BUILD_DIR}/test"
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
    test "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
         "${TEST_OUT_DIR}" PARAM_CPP_TARGET_ARRAY[@] PARAM_CPP98_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} \
         ${SWITCH_CLEAN} ${SWITCH_GRPC} "${SWITCH_TEST_NAME}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

main "$@"
