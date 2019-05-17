#!/bin/bash

# Source build-env.sh if found.
SCRIPT_DIR=`dirname $0`
if [ -e "${SCRIPT_DIR}/build-env.sh" ] ; then
    source "${SCRIPT_DIR}/build-env.sh"
fi

# Set and check global variables for Java projects.
set_global_common_variables()
{
    # bash command find to use, defaults to "/usr/bin/find" if not set
    # (bash command find makes trouble under MinGW because it clashes with Windows find command)
    FIND="${FIND:-/usr/bin/find}"
    if [ ! -f "`which "${FIND}"`" ] ; then
        stderr_echo "Cannot find bash command find! Set FIND environment variable."
        return 1
    fi

    # check java binary
    if [ -n "${JAVA_HOME}" ] ; then
        JAVA_BIN="${JAVA_HOME}/bin/java"
    fi
    JAVA_BIN="${JAVA_BIN:-java}"
    if [ ! -f "`which "${JAVA_BIN}"`" ] ; then
        stderr_echo "Cannot find java! Set JAVA_HOME or JAVA_BIN environment variable."
        return 1
    fi
}

# Set and check global variables for Java projects.
set_global_java_variables()
{
    # ANT to use, defaults to "ant" if not set
    ANT="${ANT:-ant}"
    if [ ! -f "`which "${ANT}"`" ] ; then
        stderr_echo "Cannot find Ant! Set ANT environment variable."
        return 1
    fi

    # Ant extra arguments are empty by default
    ANT_EXTRA_ARGS="${ANT_EXTRA_ARGS:-""}"

    # check javac binary
    if [ -n "${JAVA_HOME}" ] ; then
        JAVAC_BIN="${JAVA_HOME}/bin/javac"
    fi
    JAVAC_BIN="${JAVAC_BIN:-javac}"
    if [ ! -f "`which "${JAVAC_BIN}"`" ] ; then
        stderr_echo "Cannot find java compiler! Set JAVA_HOME or JAVAC_BIN environment variable."
        return 1
    fi

    # findbugs home directory is empty by default
    FINDBUGS_HOME="${FINDBUGS_HOME:-""}"
}

# Set and check global variables for C++ projects.
set_global_cpp_variables()
{
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

    PYTHON_VIRTUALENV="${PYTHON_VIRTUALENV:-""}"

    if [ -z "${PYTHON_VIRTUALENV}" ] ; then
        # python to use, defaults to "python3" if not set
        PYTHON="${PYTHON:-python3}"
        if [ ! -f "`which "${PYTHON}"`" ] ; then
            stderr_echo "Cannot find Python! Set PYTHON environment variable."
            return 1
        fi

        check_python_version ${PYTHON}

        # check that python pip and virtualenv modules are installed
        local PYTHON_REQUIREMENTS=("virtualenv" "pip")
        check_python_requirements "${PYTHON}" PYTHON_REQUIREMENTS[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # prevent __pycache__ and *.pyc being created in sources directory
    export PYTHONDONTWRITEBYTECODE=1

    # Pylint configuration - pylint disabled by default
    PYLINT_ENABLED="${PYLINT_ENABLED:-0}"

    # Pylint extra arguments are empty by default
    PYLINT_EXTRA_ARGS="${PYLINT_EXTRA_ARGS:-""}"

    return 0
}

# Check python version.
check_python_version()
{
    exit_if_argc_ne $# 1
    local PYTHON_BIN="$1"; shift

    local PYTHON_VERSION=$(${PYTHON_BIN} -V 2>&1 | cut -d\  -f 2)
    PYTHON_VERSION=(${PYTHON_VERSION//./ }) # python version as an array
    if [[ ${#PYTHON_VERSION[@]} -lt 2 || ${PYTHON_VERSION[0]} -lt 3 ]] ||
       [[ ${PYTHON_VERSION[0]} -eq 3 && ${PYTHON_VERSION[1]} -lt 5 ]] ; then
        stderr_echo "Python 3.5+ is required! Current Python is '$(${PYTHON_BIN} -V 2>&1)'"
        return 1
    fi

    return 0
}

# Check python requirements.
check_python_requirements()
{
    exit_if_argc_ne $# 2
    local PYTHON_BIN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PYTHON_REQUIREMENTS=("${MSYS_WORKAROUND_TEMP[@]}")

    "${PYTHON_BIN}" << EOF
try:
    import sys
    import pkg_resources
    reqs = "${PYTHON_REQUIREMENTS[@]}".split()
    pkg_resources.require(reqs)
except Exception as e:
    print(e, file=sys.stderr)
    exit(1)
EOF
    if [ $? -ne 0 ] ; then
        stderr_echo "Required python packages are not installed!"
        return 1
    fi
}

# Detect path to python virtualenv activate scripts which differs on Linux and Windows.
detect_python_virtualenv_activate()
{
    exit_if_argc_ne $# 2
    local PYTHON_VIRTUALENV_ROOT="$1"; shift
    local PYTHON_VIRTUALENV_ACTIVATE_OUT="$1"; shift

    local ACTIVATE=
    if [ -f "${PYTHON_VIRTUALENV_ROOT}/bin/activate" ] ; then
        ACTIVATE="${PYTHON_VIRTUALENV_ROOT}/bin/activate"
    elif [ -f "${PYTHON_VIRTUALENV_ROOT}/Scripts/activate" ] ; then
        ACTIVATE="${PYTHON_VIRTUALENV_ROOT}/Scripts/activate"
    fi

    eval ${PYTHON_VIRTUALENV_ACTIVATE_OUT}="${ACTIVATE}"
}

# Activate python virtualenv.
#
# When PYTHON_VIRTUALENV is set, just try to use it and check that it fullfils all requirements.
# When PYTHON_VIRTUALENV is not set, try to create new python virtualenv and install all required packages.
activate_python_virtualenv()
{
    exit_if_argc_ne $# 2
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift

    local PYTHON_VIRTUALENV_ROOT="${PYTHON_VIRTUALENV:-"${ZSERIO_BUILD_DIR}/pyenv"}"
    local PYTHON_VIRTUALENV_ACTIVATE
    detect_python_virtualenv_activate "${PYTHON_VIRTUALENV_ROOT}" PYTHON_VIRTUALENV_ACTIVATE

    if [ ! -z ${PYTHON_VIRTUALENV} ] ; then # forced python virtualenv
        if [ -z "${PYTHON_VIRTUALENV_ACTIVATE}" ] ; then
            stderr_echo "Failed to find virtualenv activate script in '${PYTHON_VIRTUALENV_ROOT}'!"
            return 1
        fi
    else
        if [ -z "${PYTHON_VIRTUALENV_ACTIVATE}" ] ; then
            ${PYTHON} -m virtualenv -p ${PYTHON} "${PYTHON_VIRTUALENV_ROOT}"
            if [ $? -ne 0 ] ; then
                stderr_echo "Failed to create virtualenv!"
                return 1
            fi

            detect_python_virtualenv_activate "${PYTHON_VIRTUALENV_ROOT}" PYTHON_VIRTUALENV_ACTIVATE
            if [ -z "${PYTHON_VIRTUALENV_ACTIVATE}" ] ; then
                stderr_echo "Failed to find virtualenv activate script in '${PYTHON_VIRTUALENV_ROOT}'!"
                return 1
            fi
        fi
    fi

    echo
    echo "Activating python virtualenv '${PYTHON_VIRTUALENV_ACTIVATE}'."

    source "${PYTHON_VIRTUALENV_ACTIVATE}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to activate virtualenv!"
        return 1
    fi

    check_python_version python
    if [ $? -ne 0 ] ; then
        return 1
    fi

    local STANDARD_REQUIREMENTS=("coverage>=4.5.1" "sphinx-automodapi>=0.8" "pylint>=2.1.1" "grpcio==1.17.1")
    local APSW_REQUIREMENTS=("apsw")

    if [ ! -z "${PYTHON_VIRTUALENV}" ] ; then  # forced python virtualenv
        local REQUIREMENTS=(${STANDARD_REQUIREMENTS[@]} ${APSW_REQUIREMENTS[@]})
        check_python_requirements python REQUIREMENTS[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    else
        check_python_requirements python STANDARD_REQUIREMENTS[@] 2> /dev/null
        if [ $? -ne 0 ] ; then
            pip install ${STANDARD_REQUIREMENTS[@]}
            if [ $? -ne 0 ] ; then
                stderr_echo "Failed to install python requirements!"
                return 1
            fi
        fi

        check_python_requirements python APSW_REQUIREMENTS[@] 2> /dev/null
        if [ $? -ne 0 ] ; then
            install_python_apsw "${ZSERIO_PROJECT_ROOT}" "${PYTHON_VIRTUALENV_ROOT}"
            if [ $? -ne 0 ] ; then
                return 1
            fi
        fi
    fi

    return 0
}

# Build and install python apsw package using same sqlite sources as for C++ projects.
install_python_apsw()
{
    exit_if_argc_ne $# 2
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local PYTHON_VIRTUALENV_ROOT="$1"; shift

    local SQLITE_ROOT="${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/sqlite"

    pushd "${PYTHON_VIRTUALENV_ROOT}" > /dev/null
    if [ ! -d "${PYTHON_VIRTUALENV_ROOT}/apsw" ] ; then
        git clone --depth 1 https://github.com/rogerbinns/apsw.git -b 3.24.0-r1
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to clone apsw repository!"
            popd > /dev/null
            return 1
        fi
    fi

    cd apsw

    # copy 3rdparty sqlite3 to be built within apsw module
    cp -r "${SQLITE_ROOT}" sqlite3
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to copy 3rdparty sqlite to apsw build directory!"
        popd > /dev/null
        return 1
    fi

    python setup.py build --enable=fts4,fts5 install
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to build python apsw module!"
        popd > /dev/null
        return 1
    fi
    popd > /dev/null

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
    PYTHON_VIRTUALENV      Custom python virtualenv to use. Default is empty string.
    FIND                   Bash command find to use. Default is "/usr/bin/find".
    FINDBUGS_HOME          Home directory of findbugs tool where lib is located
                           (e.g. /usr/share/findbugs). If set, findbugs will be
                           called. Default is empty string.
    CPPCHECK_HOME          Home directory of cppcheck tool where cppcheck
                           binary is located. If set, cppcheck will be called.
                           Default is empty string.
    PYLINT_ENABLED         Defines whether to run pylint. Default is 0 (disabled).
    PYLINT_EXTRA_ARGS      Extra arguments to pylint. Default is empty string.

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

    local DIR_TO_CONVERT="${PATH_TO_CONVERT}"
    local FILE_TO_CONVERT=""
    if [ ! -d "${DIR_TO_CONVERT}" ] ; then
        DIR_TO_CONVERT="${PATH_TO_CONVERT%/*}"
        FILE_TO_CONVERT="${PATH_TO_CONVERT##*/}"
        if [[ "${DIR_TO_CONVERT}" == "${FILE_TO_CONVERT}" ]] ; then
            DIR_TO_CONVERT="."
        else
            if [ ! -d "${DIR_TO_CONVERT}" ] ; then
                stderr_echo "${FUNCNAME[0]}() called with a non-existing directory ${DIR_TO_CONVERT}!"
                return 1
            fi
        fi
    fi

    pushd "${DIR_TO_CONVERT}" > /dev/null
    local ABSOLUTE_PATH="`pwd`"
    popd > /dev/null

    if [ -n "${FILE_TO_CONVERT}" ] ; then
        ABSOLUTE_PATH="${ABSOLUTE_PATH}/${FILE_TO_CONVERT}"
    fi

    eval ${ABSOLUTE_PATH_OUT}="${ABSOLUTE_PATH}"

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
    if [ $? -ne 0 ] ; then
        return 1
    fi

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

# Run pylint on given python sources.
run_pylint()
{
    if [[ ${PYLINT_ENABLED} != 1 ]] ; then
        echo "Pylint is disabled."
        echo
        return 0
    fi

    exit_if_argc_lt $# 3
    local PYLINT_RCFILE="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PYLINT_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local SOURCES=("$@")

    local HOST_PLATFORM
    get_host_platform HOST_PLATFORM
    if [[ "${HOST_PLATFORM}" == "windows"* && ${#SOURCES[@]} -gt 50 ]] ; then
        # prevent bad file number under msys caused by too long command line
        for SOURCE in "${SOURCES[@]}"; do
            python -m pylint --init-hook="import sys; sys.setrecursionlimit(5000)" ${PYLINT_EXTRA_ARGS} \
                            --rcfile "${PYLINT_RCFILE}" --persistent=n --score=n "${PYLINT_ARGS[@]}" \
                            ${SOURCE}
            local PYLINT_RESULT=$?
            if [ ${PYLINT_RESULT} -ne 0 ] ; then
                stderr_echo "Running pylint failed with return code ${PYLINT_RESULT}!"
                return 1
            fi
        done
    else
        python -m pylint --init-hook="import sys; sys.setrecursionlimit(5000)" ${PYLINT_EXTRA_ARGS} \
                         --rcfile "${PYLINT_RCFILE}" --persistent=n --score=n "${PYLINT_ARGS[@]}" \
                         "${SOURCES[@]}"
        local PYLINT_RESULT=$?
        if [ ${PYLINT_RESULT} -ne 0 ] ; then
            stderr_echo "Running pylint failed with return code ${PYLINT_RESULT}!"
            return 1
        fi
    fi

    echo "Pylint done."
    echo

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
