#!/bin/bash

# Source build-env.sh if found.
SCRIPT_DIR=`dirname $0`
if [ -e "${SCRIPT_DIR}/build-env.sh" ] ; then
    source "${SCRIPT_DIR}/build-env.sh"
fi

# Set and check global variables for Java projets.
set_global_java_variables()
{
    exit_if_argc_ne $# 1
    local ZSERIO_PROJECT_ROOT="$1"; shift

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
set_global_cpp_variables()
{
    exit_if_argc_ne $# 1
    local ZSERIO_PROJECT_ROOT="$1"; shift

    # CMake to use, defaults to "cmake" if not set
    CMAKE="${CMAKE:-cmake}"
    if [ ! -f "`which "${CMAKE}"`" ] ; then
        stderr_echo "Cannot find CMake! Set CMAKE environment variable."
        return 1
    fi

    # CMake extra arguments are empty by default
    CMAKE_EXTRA_ARGS="${CMAKE_EXTRA_ARGS:-""}"

    # CTest to use, defaults to "ctest" if not set
    CTEST="${CTEST:-ctest}"
    if [ ! -f "`which "${CTEST}"`" ] ; then
        stderr_echo "Cannot find CTest! Set CTEST environment variable."
        return 1
    fi

    GCC_CMAKE_GENERATOR="${GCC_CMAKE_GENERATOR:-Eclipse CDT4 - Unix Makefiles}"
    MSVC_CMAKE_GENERATOR="${MSVC_CMAKE_GENERATOR:-Visual Studio 14 2015}"

    # Extra arguments to be passed by CMake to a native build tool
    CMAKE_BUILD_OPTIONS="${CMAKE_BUILD_OPTIONS:-""}"

    # cppcheck home directoty is empty by default
    CPPCHECK_HOME="${CPPCHECK_HOME:-""}"

    return 0
}

# Set and check global variables for Python projets.
set_global_python_variables()
{
    exit_if_argc_ne $# 1
    local ZSERIO_PROJECT_ROOT="$1"; shift

    # python to use, defaults to "python" if not set
    PYTHON="${PYTHON:-python}"
    if [ ! -f "`which "${PYTHON}"`" ] ; then
        stderr_echo "Cannot find Python! Set PYTHON environment variable."
        return 1
    fi
    local PYTHON_VERSION=$(${PYTHON} -V 2>&1 | cut -d\  -f 2)
    PYTHON_VERSION=(${PYTHON_VERSION//./ }) # python version as an array
    if [[ ${#PYTHON_VERSION[@]} -lt 2 || ${PYTHON_VERSION[0]} -lt 3 ]] ||
       [[ ${PYTHON_VERSION[0]} -eq 3 && ${PYTHON_VERSION[1]} -lt 5 ]] ; then
        stderr_echo "Python 3.5+ is required! Current Python is '$(python -V 2>&1)'"
        return 1
    fi

    # check python requirements
    local PIP_REQUIREMENTS_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/runtime/requirements.txt"
    local HOST_PIP_REQUIREMENTS_FILE
    posix_to_host_path "${PIP_REQUIREMENTS_FILE}" HOST_PIP_REQUIREMENTS_FILE
    ${PYTHON} << EOF
try:
    import sys
    import pkg_resources
    reqs = []
    with open(r'${HOST_PIP_REQUIREMENTS_FILE}', 'r') as reqsFile:
        for req in reqsFile:
            reqs.append(req.rstrip())
    pkg_resources.require(reqs)
except Exception as e:
    print(e)
    exit(1)
EOF
    if [ $? -ne 0 ] ; then
        stderr_echo "Required python packages are not installed!"
        stderr_echo "Try: pip install -r ${PYTHON_RUNTIME_ROOT}/requirements.txt"
        return 1
    fi

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
    GCC_CMAKE_GENERATOR    CMake generator to use with GCC compiler. Default is
                           "Eclipse CDT4 - Unix Makefiles".
    MSVC_CMAKE_GENERATOR   CMake generator to use with MSVC compiler. Default is
                           "Visual Studio 14 2015". Note that "Win64" suffix is
                           added automatically for windows64-mscv target.
    CMAKE_EXTRA_ARGS       Extra arguments to CMake. Default is empty string.
    CMAKE_BUILD_OPTIONS    Arguments to be passed by CMake to a native build tool.
    CTEST                  Ctest executable to use. Default is "ctest".
    JAVAC_BIN              Java compiler executable to use. Default is "javac".
    JAVA_BIN               Java executable to use. Default is "java".
    PYTHON                 Python 3.5+ executable. Default is "python".
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
    echo "FATAL ERROR - $@" 1>&2
}

# Exit if number of input arguments is not equal to number required by function.
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

    local NUM_OF_CALLER_ARGS=$1; shift
    local REQUIRED_NUM_OF_CALLED_ARGS=$1; shift
    if [ ${NUM_OF_CALLER_ARGS} -ne ${REQUIRED_NUM_OF_CALLED_ARGS} ] ; then
        stderr_echo "${FUNCNAME[1]}() called with ${NUM_OF_CALLER_ARGS} arguments but ${REQUIRED_NUM_OF_CALLED_ARGS} is required."
        exit 3
    fi
}

# Exit if number of input arguments is less than number required by function.
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

    local NUM_OF_CALLER_ARGS=$1; shift
    local REQUIRED_NUM_OF_CALLED_ARGS=$1; shift
    if [ ${NUM_OF_CALLER_ARGS} -lt ${REQUIRED_NUM_OF_CALLED_ARGS} ] ; then
        stderr_echo "${FUNCNAME[1]}() called with ${NUM_OF_CALLER_ARGS} arguments but ${REQUIRED_NUM_OF_CALLED_ARGS} is required."
        exit 3
    fi
}

# Convert input argument to absolute path.
convert_to_absolute_path()
{
    exit_if_argc_ne $# 2
    local PATH_TO_CONVERT="$1"; shift
    local ABSOLUTE_PATH_OUT="$1"; shift

    if [ ! -d "${PATH_TO_CONVERT}" ] ; then
        stderr_echo "${FUNCNAME[0]}() called with a non-directory ${PATH_TO_CONVERT}!"
        return 1
    fi

    pushd "${PATH_TO_CONVERT}" > /dev/null
    eval ${ABSOLUTE_PATH_OUT}="`pwd`"
    popd > /dev/null

    return 0
}

# Get Zserio version from Zserio sources.
get_zserio_version()
{
    exit_if_argc_ne $# 2
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_VERSION_OUT="$1"; shift

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
get_release_dir()
{
    exit_if_argc_ne $# 3
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_RELEASE_DIR_OUT="$1"; shift
    local ZSERIO_VERSION_OUT="$1"; shift

    local ZSERIO_VERSION_GET_RELEASE_DIR
    get_zserio_version "${ZSERIO_PROJECT_ROOT}" ZSERIO_VERSION_GET_RELEASE_DIR

    eval ${ZSERIO_RELEASE_DIR_OUT}="'${ZSERIO_PROJECT_ROOT}/release-${ZSERIO_VERSION_GET_RELEASE_DIR}'"
    eval ${ZSERIO_VERSION_OUT}="'${ZSERIO_VERSION_GET_RELEASE_DIR}'"

    return 0
}

# Compile Java by running Ant target.
compile_java()
{
    exit_if_argc_ne $# 3
    local ANT_BUILD_FILE="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local ANT_PROPS=("${MSYS_WORKAROUND_TEMP[@]}")
    local ANT_TARGET="$1"; shift

    if [ -n "${FINDBUGS_HOME}" ] ; then
        ANT_PROPS+=("-Dfindbugs.home_dir=${FINDBUGS_HOME}")
    fi

    "${ANT}" ${ANT_EXTRA_ARGS} -f "${ANT_BUILD_FILE}" "${ANT_PROPS[@]}" ${ANT_TARGET}
    local ANT_RESULT=$?
    if [ ${ANT_RESULT} -ne 0 ] ; then
        stderr_echo "Running ant failed with return code ${ANT_RESULT}!"
        return 1
    fi

    return 0
}

# Compile and test C++ code by running cmake and make for all targets.
compile_cpp()
{
    exit_if_argc_ne $# 7;
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local BUILD_DIR="$1"; shift
    local CMAKELISTS_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CMAKE_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CTEST_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MAKE_TARGET="$1"; shift

    local TARGET
    for TARGET in "${TARGETS[@]}" ; do
        compile_cpp_for_target "${ZSERIO_PROJECT_ROOT}" "${BUILD_DIR}/cpp-${TARGET}" "${CMAKELISTS_DIR}" "${TARGET}" \
                               CMAKE_ARGS[@] CTEST_ARGS[@] "${MAKE_TARGET}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    done

    return 0
}

# Compile and test C++ code by running cmake and make for one target.
compile_cpp_for_target()
{
    exit_if_argc_ne $# 7
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local BUILD_DIR="$1"; shift
    local CMAKELISTS_DIR="$1"; shift
    local TARGET="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CMAKE_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CTEST_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local MAKE_TARGET="$1"; shift

    local TOOLCHAIN_FILE="${ZSERIO_PROJECT_ROOT}/cmake/toolchain-${TARGET}.cmake"
    CMAKE_ARGS=("--no-warn-unused-cli"
                "${CMAKE_ARGS[@]}"
                "-DCMAKE_TOOLCHAIN_FILE=${TOOLCHAIN_FILE}"
                "-DCMAKE_PREFIX_PATH=${SQLITE_RELEASE_ROOT}/${TARGET}")

    if [ -n "${CPPCHECK_HOME}" ] ; then
        CMAKE_ARGS=("${CMAKE_ARGS[@]}"
                    "-DCPPCHECK_HOME=${CPPCHECK_HOME}")
    fi

    mkdir -p "${BUILD_DIR}"
    pushd "${BUILD_DIR}" > /dev/null

    local CMAKE_BUILD_TARGET=${MAKE_TARGET}

    # resolve CMake generator
    if [[ ${TARGET} == *"-msvc" ]] ; then
        local CMAKE_BUILD_CONFIG="--config Release"
        CTEST_ARGS+=("-C Release")
        if [[ ${MAKE_TARGET} == "all" ]] ; then
            CMAKE_BUILD_TARGET="ALL_BUILD" # all target doesn't exist in MSVC solution
        fi
        if [[ ${TARGET} == "windows32-msvc" ]] ; then
            local CMAKE_GENERATOR="${MSVC_CMAKE_GENERATOR}";
        else
            local CMAKE_GENERATOR="${MSVC_CMAKE_GENERATOR} Win64";
        fi
    else
        local CMAKE_GENERATOR="${GCC_CMAKE_GENERATOR}"
        local CMAKE_BUILD_CONFIG=""
    fi

    "${CMAKE}" ${CMAKE_EXTRA_ARGS} -G "${CMAKE_GENERATOR}" "${CMAKE_ARGS[@]}" "${CMAKELISTS_DIR}"
    local CMAKE_RESULT=$?
    if [ ${CMAKE_RESULT} -ne 0 ] ; then
        stderr_echo "Running CMake failed with return code ${CMAKE_RESULT}!"
        popd > /dev/null
        return 1
    fi

    "${CMAKE}" --build . --target ${CMAKE_BUILD_TARGET} ${CMAKE_BUILD_CONFIG} -- ${CMAKE_BUILD_OPTIONS}
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
        CTEST_OUTPUT_ON_FAILURE=1 "${CTEST}" ${CTEST_ARGS[@]}
        local CTEST_RESULT=$?
        if [ ${CTEST_RESULT} -ne 0 ] ; then
            stderr_echo "Tests on target ${TARGET} failed with return code ${CTEST_RESULT}."
            popd > /dev/null
            return 1
        fi
    fi

    popd > /dev/null

    return 0
}

# Test python code by runnig python -m unittest.
test_python()
{
    exit_if_argc_ne $# 4
    local BUILD_DIR="${1}"; shift
    local PYTHON_SOURCES_ROOT="${1}" ; shift
    local SOURCES_DIR="${1}"; shift
    local TESTS_DIR="${1}"; shift

    rm -rf "${BUILD_DIR}"
    mkdir -p "${BUILD_DIR}"
    pushd "${BUILD_DIR}" > /dev/null

    # run tests by coverage
    PYTHONDONTWRITEBYTECODE=1 PYTHONPATH="${SOURCES_DIR}" "${PYTHON}" \
                                                          -m coverage run --source "${PYTHON_SOURCES_ROOT}/" \
                                                          -m unittest discover -s "${TESTS_DIR}" -v
    local PYTHON_RESULT=$?
    if [ ${PYTHON_RESULT} -ne 0 ] ; then
        stderr_echo "Running python coverage tests  failed with return code ${PYTHON_RESULT}!"
        popd > /dev/null
        return 1
    fi
    echo

    # report tests coverage
    "${PYTHON}" -m coverage report -m --fail-under=100
    local COVERAGE_RESULT=$?
    if [ ${COVERAGE_RESULT} -ne 0 ] ; then
        stderr_echo "Running python coverage report failed with return code ${COVERAGE_RESULT}!"
        popd > /dev/null
        return 1
    fi

    popd > /dev/null
    echo

    # check sources
    local PYLINT_RCFILE="${PYTHON_SOURCES_ROOT}/pylintrc.txt"
    "${PYTHON}" -m pylint "${SOURCES_DIR}"/* --rcfile "${PYLINT_RCFILE}" --persistent=n
    local PYLINT_RESULT=$?
    if [ ${PYLINT_RESULT} -ne 0 ] ; then
        stderr_echo "Running pylint failed with return code ${PYLINT_RESULT}!"
        return 1
    fi

    # check test sources
    PYTHONPATH="${SOURCES_DIR}" "${PYTHON}" -m pylint "${TESTS_DIR}"/* --disable=missing-docstring \
                                --rcfile "${PYLINT_RCFILE}" --persistent=n
    local PYLINT_RESULT=$?
    if [ ${PYLINT_RESULT} -ne 0 ] ; then
        stderr_echo "Running pylint failed with return code ${PYLINT_RESULT}!"
        return 1
    fi

    return 0
}

# Test if it's possible to run tests for given target on current host.
can_run_tests()
{
    exit_if_argc_ne $# 1
    local TARGET_PLATFORM="$1"; shift

    local HOST_PLATFORM
    get_host_platform HOST_PLATFORM
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # assume on 64bit both 32bit and 64bit executables can be run
    case "${HOST_PLATFORM}" in
    ubuntu32)
        [[ "${TARGET_PLATFORM}" == "linux32" ]]
        ;;
    ubuntu64)
        [[ "${TARGET_PLATFORM}" == "linux32" || "${TARGET_PLATFORM}" = "linux64" ]]
        ;;
    windows32)
        [[ "${TARGET_PLATFORM}" == "windows32-"* ]]
        ;;
    windows64)
        [[ "${TARGET_PLATFORM}" == "windows32-"* || "${TARGET_PLATFORM}" == "windows64-"* ]]
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
get_host_platform()
{
    exit_if_argc_ne $# 1
    local HOST_PLATFORM_OUT="$1"; shift

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
        if [ $? -ne 0 ] ; then
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

# Returns path according to the current host.
#
# On Linux the given path is unchanged, on Windows the path is converted to windows path.
posix_to_host_path()
{
    exit_if_argc_lt $# 2
    local POSIX_PATH="$1"; shift
    local HOST_PATH_OUT="$1"; shift
    local DISABLE_SLASHES_CONVERSION=0
    if [ $# -ne 0 ] ; then
        DISABLE_SLASHES_CONVERSION="$1"; shift # optional, default is false
    fi

    local HOST_PLATFORM
    get_host_platform HOST_PLATFORM
    if [[ "${HOST_PLATFORM}" == "windows"* ]] ; then
        # change drive specification in case of full path, e.g. '/d/...' to 'd:/...'
        local SEARCH_PATTERN="/?/"
        if [ "${POSIX_PATH}" != "${POSIX_PATH/${SEARCH_PATTERN}/}" ] ; then
            POSIX_PATH="${POSIX_PATH:1:1}:${POSIX_PATH:2}"
        fi

        if [ ${DISABLE_SLASHES_CONVERSION} -ne 1 ] ; then
            # replace all Posix '/' to Windows '\'
            local SEARCH_PATTERN="/"
            local REPLACE_PATTERN="\\"
            POSIX_PATH="${POSIX_PATH//${SEARCH_PATTERN}/${REPLACE_PATTERN}}"
        fi
    fi

    eval ${HOST_PATH_OUT}="'${POSIX_PATH}'"
}
