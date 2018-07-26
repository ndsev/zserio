#!/bin/bash

# Source build-env.sh if found.
SCRIPT_DIR=`dirname $0`
if [ -e "${SCRIPT_DIR}/build-env.sh" ] ; then
    source "${SCRIPT_DIR}/build-env.sh"
fi

# Set and check global variables for Java projets.
#
# $1 - Zserio project root.
set_global_java_variables()
{
    exit_if_argc_ne $# 1
    local ZSERIO_PROJECT_ROOT="$1"

    # ANT to use, defaults to "ant" if not set
    ANT="${ANT:-ant}"
    if [ ! -f "`which "${ANT}"`" ] ; then
        stderr_echo "Cannot find Ant! Set ANT environment variable."
        return 1
    fi

    # Ant extra arguments are empty by default
    ANT_EXTRA_ARGS="${ANT_EXTRA_ARGS:-""}"

    # check java and javac binaries
    if [ -n "${JAVA_HOME}" ] ; then
        JAVAC_BIN="${JAVA_HOME}/bin/javac"
        JAVA_BIN="${JAVA_HOME}/bin/java"
    fi
    JAVAC_BIN="${JAVAC_BIN:-javac}"
    JAVA_BIN="${JAVA_BIN:-java}"
    if [ ! -f "`which "${JAVAC_BIN}"`" ] ; then
        stderr_echo "Cannot find java compiler! Set JAVA_HOME or JAVAC_BIN environment variable."
        return 1
    fi
    if [ ! -f "`which "${JAVA_BIN}"`" ] ; then
        stderr_echo "Cannot find java! Set JAVA_HOME or JAVA_BIN environment variable."
        return 1
    fi

    # findbugs home directory is empty by default
    FINDBUGS_HOME="${FINDBUGS_HOME:-""}"
}

# Set and check global variables for Java projets.
#
# $1 - Zserio project root.
set_global_cpp_variables()
{
    exit_if_argc_ne $# 1
    local ZSERIO_PROJECT_ROOT="$1"

    # CMake to use, defaults to "cmake" if not set
    CMAKE="${CMAKE:-cmake}"
    if [ ! -f "`which "${CMAKE}"`" ] ; then
        stderr_echo "Cannot find CMake! Set CMAKE environment variable."
        return 1
    fi

    # CMake extra arguments are empty by default
    CMAKE_EXTRA_ARGS="${CMAKE_EXTRA_ARGS:-""}"

    # CMake generator to use, defaults to "Eclipse CDT4 - Unix Makefiles" if not set
    CMAKE_GENERATOR="${CMAKE_GENERATOR:-Eclipse CDT4 - Unix Makefiles}"

    # make extra arguments are empty by default
    MAKE_EXTRA_ARGS="${MAKE_EXTRA_ARGS:-""}"

    # cppcheck home directoty is empty by default
    CPPCHECK_HOME="${CPPCHECK_HOME:-""}"

    return 0
}

# Print help on the environment variables used.
print_help_env()
{
    cat << EOF
Uses the following environment variables for building:
    ANT                    Ant executable to use. Default is "ant".
    ANT_EXTRA_ARGS         Extra arguments to Ant. Default is empty string.
    CMAKE                  CMake executable to use. Default is "cmake".
    CMAKE_EXTRA_ARGS       Extra arguments to CMake. Default is empty string.
    CMAKE_GENERATOR        CMake generator to use. Default is
                           "Eclipse CDT4 - Unix Makefiles".
    MAKE_EXTRA_ARGS        Extra arguments to Make. Default is empty string.
    JAVAC_BIN              Java compiler executable to use. Default is "javac".
    JAVA_BIN               Java executable to use. Default is "java".
    FINDBUGS_HOME          Home directory of findbugs tool where lib is located
                           (e.g. /usr/share/findbugs). If set, findbugs will be
                           called. Default is empty string.
    CPPCHECK_HOME          Home directory of cppcheck tool where cppcheck
                           binary is located. If set, cppcheck will be called.
                           Default is empty string.

    Either set these directly, or create 'scripts/build-env.sh' that sets
    these. It's sourced automatically if it exists.

EOF
}

# Print a message to stderr.
stderr_echo()
{
    echo "FATAL ERROR - $@" 2>&1
}

# Exit if number of input arguments is not equal to number required by function.
#
# Parameters:
# -----------
# $1 - Number of input arguments to check.
# $2 - Required number of input arguments.
#
# Usage:
# ------
# exit_if_argc_ne $# 2
#
# Return codes:
# -------------
# 0 - Always success. In case of failure, function exits with error code 3.
exit_if_argc_ne()
{
    local NUM_OF_ARGS=2
    if [ $# -ne ${NUM_OF_ARGS} ] ; then
        stderr_echo "${FUNCNAME[0]}() called with $# arguments but ${NUM_OF_ARGS} is required."
        exit 3
    fi

    local NUM_OF_CALLER_ARGS=$1
    local REQUIRED_NUM_OF_CALLED_ARGS=$2
    if [ ${NUM_OF_CALLER_ARGS} -ne ${REQUIRED_NUM_OF_CALLED_ARGS} ] ; then
        stderr_echo "${FUNCNAME[1]}() called with ${NUM_OF_CALLER_ARGS} arguments but ${REQUIRED_NUM_OF_CALLED_ARGS} is required."
        exit 3
    fi
}

# Exit if number of input arguments is less than number required by function.
#
# Parameters:
# -----------
# $1 - Number of input arguments to check.
# $2 - Required number of input arguments.
#
# Usage:
# ------
# exit_if_argc_lt $# 2
#
# Return codes:
# -------------
# 0 - Always success. In case of failure, function exits with error code 3.
exit_if_argc_lt()
{
    local NUM_OF_ARGS=2
    if [ $# -ne ${NUM_OF_ARGS} ] ; then
        stderr_echo "${FUNCNAME[0]}() called with $# arguments but ${NUM_OF_ARGS} is required."
        exit 3
    fi

    local NUM_OF_CALLER_ARGS=$1
    local REQUIRED_NUM_OF_CALLED_ARGS=$2
    if [ ${NUM_OF_CALLER_ARGS} -lt ${REQUIRED_NUM_OF_CALLED_ARGS} ] ; then
        stderr_echo "${FUNCNAME[1]}() called with ${NUM_OF_CALLER_ARGS} arguments but ${REQUIRED_NUM_OF_CALLED_ARGS} is required."
        exit 3
    fi
}

# Convert input argument to absolute path.
#
# $1 - The path which must exist and it must be a directory.
# $2 - The variable name to fill with converted absolute path.
convert_to_absolute_path()
{
    exit_if_argc_ne $# 2
    local PATH_TO_CONVERT="$1"
    local ABSOLUTE_PATH_OUT="$2"

    if [ ! -d "${PATH_TO_CONVERT}" ]; then
        stderr_echo "${FUNCNAME[0]}() called with a non-directory ${PATH_TO_CONVERT}!"
        return 1
    fi

    pushd "${PATH_TO_CONVERT}" > /dev/null
    eval ${ABSOLUTE_PATH_OUT}="`pwd`"
    popd > /dev/null

    return 0
}

# Get Zserio version from Zserio sources.
#
# $1 - Zserio project root.
# $2 - The name of variable to fill with the Zserio version.
get_zserio_version()
{
    exit_if_argc_ne $# 2
    local ZSERIO_PROJECT_ROOT="$1"
    local ZSERIO_VERSION_OUT="$2"

    local ZSERIO_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/core/src/zserio/tools/ZserioVersion.java"
    local ZSERIO_VERSION_GET_ZSERIO_VERSION=`
            grep 'VERSION_STRING' "${ZSERIO_VERSION_FILE}" | cut -d\" -f 2`
    if [ $? -ne 0 -o -z "${ZSERIO_VERSION_GET_ZSERIO_VERSION}" ] ; then
        stderr_echo "${FUNCNAME[0]}() failed to grep Zserio version from ZserioVersion.java!"
        return 1
    fi

    eval ${ZSERIO_VERSION_OUT}="'${ZSERIO_VERSION_GET_ZSERIO_VERSION}'"

    return 0
}

# Get Zserio release directory and version from Zserio sources.
#
# $1 - Zserio project root.
# $2 - The name of variable to fill with the release directory.
# $3 - The name of variable to fill with the Zserio version.
get_release_dir()
{
    exit_if_argc_ne $# 3
    local ZSERIO_PROJECT_ROOT="$1"
    local ZSERIO_RELEASE_DIR_OUT="$2"
    local ZSERIO_VERSION_OUT="$3"

    local ZSERIO_VERSION_GET_RELEASE_DIR
    get_zserio_version "${ZSERIO_PROJECT_ROOT}" ZSERIO_VERSION_GET_RELEASE_DIR

    eval ${ZSERIO_RELEASE_DIR_OUT}="'${ZSERIO_PROJECT_ROOT}/release-${ZSERIO_VERSION_GET_RELEASE_DIR}'"
    eval ${ZSERIO_VERSION_OUT}="'${ZSERIO_VERSION_GET_RELEASE_DIR}'"

    return 0
}

# Compile Java by running Ant target.
#
# $1 - The Ant XML file to use.
# $2 - The name of variable which contains array of Ant properties to use.
# $3 - The Ant target to run.
compile_java()
{
    exit_if_argc_ne $# 3
    local ANT_BUILD_FILE="$1"
    local MSYS_WORKAROUND_TEMP=("${!2}")
    local ANT_PROPS=("${MSYS_WORKAROUND_TEMP[@]}")
    local ANT_TARGET="$3"

    if [ -n "${FINDBUGS_HOME}" ] ; then
        ANT_PROPS=("${ANT_PROPS[@]}"
                   "-Dfindbugs.home_dir=${FINDBUGS_HOME}")
    fi

    "${ANT}" ${ANT_EXTRA_ARGS} -f "${ANT_BUILD_FILE}" "${ANT_PROPS[@]}" ${ANT_TARGET}
    local ANT_RESULT=$?
    if [ ${ANT_RESULT} -ne 0 ] ; then
        stderr_echo "Running ant failed with return code ${ANT_RESULT}!"
        return 1
    fi

    return 0
}

# Compile and test C++ code running cmake and make for all targets.
#
# $1 - The directory of Zserio project root.
# $2 - Directory where to build.
# $3 - Directory where CMakeLists.txt is located.
# $4 - The name of variable which contains array of targets for which to compile.
# $5 - The name of variable which contains array of CMake arguments to use.
# $6 - Make target to run.
compile_cpp()
{
    exit_if_argc_ne $# 6
    local ZSERIO_PROJECT_ROOT="$1"
    local BUILD_DIR="$2"
    local CMAKELISTS_DIR="$3"
    local MSYS_WORKAROUND_TEMP=("${!4}")
    local TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MSYS_WORKAROUND_TEMP=("${!5}")
    local CMAKE_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MAKE_TARGET="$6"

    local TARGET
    for TARGET in "${TARGETS[@]}" ; do
        compile_cpp_for_target "${ZSERIO_PROJECT_ROOT}" "${BUILD_DIR}/${TARGET}" "${CMAKELISTS_DIR}" "${TARGET}" \
                               CMAKE_ARGS[@] "${MAKE_TARGET}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    done

    return 0
}

# Compile and test C++ code running cmake and make for one target.
#
# $1 - The directory of Zserio project root.
# $2 - Directory where to build.
# $3 - Directory where CMakeLists.txt is located.
# $4 - Target for which to compile.
# $5 - The name of variable which contains array of CMake arguments to use.
# $6 - Make target to run.
compile_cpp_for_target()
{
    exit_if_argc_ne $# 6
    local ZSERIO_PROJECT_ROOT="$1"
    local BUILD_DIR="$2"
    local CMAKELISTS_DIR="$3"
    local TARGET="$4"
    local MSYS_WORKAROUND_TEMP=("${!5}")
    local CMAKE_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MAKE_TARGET="$6"

    local TOOLCHAIN_FILE="${ZSERIO_PROJECT_ROOT}/cmake/toolchain-${TARGET}.cmake"
    CMAKE_ARGS=(--no-warn-unused-cli
                "${CMAKE_ARGS[@]}"
                "-DCMAKE_TOOLCHAIN_FILE=${TOOLCHAIN_FILE}"
                "-DCMAKE_PREFIX_PATH=${SQLITE_RELEASE_ROOT}/${TARGET}")

    if [ -n "${CPPCHECK_HOME}" ] ; then
        CMAKE_ARGS=("${CMAKE_ARGS[@]}"
                    "-DCPPCHECK_HOME=${CPPCHECK_HOME}")
    fi

    mkdir -p "${BUILD_DIR}"
    pushd "${BUILD_DIR}" > /dev/null

    "${CMAKE}" ${CMAKE_EXTRA_ARGS} -G "${CMAKE_GENERATOR}" "${CMAKE_ARGS[@]}" "${CMAKELISTS_DIR}"
    local CMAKE_RESULT=$?
    if [ ${CMAKE_RESULT} -ne 0 ] ; then
        stderr_echo "Running CMake failed with return code ${CMAKE_RESULT}!"
        popd > /dev/null
        return 1
    fi

    "${CMAKE}" --build . --target ${MAKE_TARGET} -- ${MAKE_EXTRA_ARGS}
    local MAKE_RESULT=$?
    if [ ${MAKE_RESULT} -ne 0 ] ; then
        stderr_echo "Make failed with return code ${MAKE_RESULT}!"
        popd > /dev/null
        return 1
    fi

    # only run "make test" if we can actually run it on current host
    can_run_tests "${TARGET}"
    local CAN_RUN_TESTS_RESULT=$?
    if [[ ${MAKE_TARGET} != "clean" && ${CAN_RUN_TESTS_RESULT} == 0 ]] ; then
        CTEST_OUTPUT_ON_FAILURE=1 "${CMAKE}" --build . --target test -- ${MAKE_EXTRA_ARGS}
        local MAKE_TEST_RESULT=$?
        if [ ${MAKE_TEST_RESULT} -ne 0 ] ; then
            stderr_echo "Make test on target ${TARGET} failed with return code ${MAKE_RESULT}."
            popd > /dev/null
            return 1
        fi
    fi

    popd > /dev/null

    return 0
}

# Tests if it's possible to run tests for given target on current host.
#
# $1 - Platform name.
can_run_tests()
{
    exit_if_argc_ne $# 1
    local TARGET="$1"

    local HOST_PLATFORM
    get_host_platform HOST_PLATFORM
    if [ $? -ne 0 ]; then
        return 1
    fi

    # assume on 64bit both 32bit and 64bit executables can be run
    case "${HOST_PLATFORM}" in
    ubuntu32)
        [ "${TARGET}" = "linux32" ]
        ;;
    ubuntu64)
        [ "${TARGET}" = "linux32" -o "${TARGET}" = "linux64" ]
        ;;
    windows32)
        [ "${TARGET}" = "windows32" ]
        ;;
    windows64)
        [ "${TARGET}" = "windows32" -o "${TARGET}" = "windows64" ]
        ;;
    *)
        stderr_echo "can_run_tests: unknown current platform ${HOST_PLATFORM}!"
        return 1
    esac
}

# Determines the current host platform.
#
# Returns one of the supported platforms:
# ubuntu32, ubuntu64, windows32, windows64
#
# $1 - The variable name to fill with the current host platform.
get_host_platform()
{
    exit_if_argc_ne $# 1
    local HOST_PLATFORM_OUT="$1"

    local OS=`uname -s`
    local HOST=""
    case "${OS}" in
    Linux)
        HOST="ubuntu"
        ;;
    MINGW32*)
        HOST="windows"
        ;;
    *)
        stderr_echo "uname returned unsupported OS!"
        return 1
        ;;
    esac

    if [ "${HOST}" = "windows" ] ; then
        # can't use uname on windows - MSYS always says it's i686
        local CURRENT_ARCH
        CURRENT_ARCH=`wmic OS get OSArchitecture 2> /dev/null`
        if [ $? -ne 0 ]; then
            # wmic failed, assume it's Windows XP 32bit
            NATIVE_TARGET="windows32"
        else
            case "${CURRENT_ARCH}" in
            *64-bit*)
                NATIVE_TARGET="windows64"
                ;;
            *32-bit*)
                NATIVE_TARGET="windows32"
                ;;
            *)
                stderr_echo "wmic returned unsupported architecture!"
                return 1
            esac
        fi
    else
        local CURRENT_ARCH=`uname -m`
        case "${CURRENT_ARCH}" in
        x86_64)
            NATIVE_TARGET="${HOST}64"
            ;;
        i686)
            NATIVE_TARGET="${HOST}32"
            ;;
        *)
            stderr_echo "unname returned unsupported architecture!"
            return 1
        esac
    fi

    eval ${HOST_PLATFORM_OUT}="${NATIVE_TARGET}"

    return 0
}

