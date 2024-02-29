#!/bin/bash

# Source build-env.sh if found.
CWD=`readlink -f $0`
SCRIPT_DIR=`dirname ${CWD}`
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

    # clang-format binary to use for formatting check, by default is empty
    CLANG_FORMAT_BIN="${CLANG_FORMAT_BIN:-""}"
    if [[ (! -z "${CLANG_FORMAT_BIN}" && ! -f "`which "${CLANG_FORMAT_BIN}"`") ]] ; then
        stderr_echo "Provided CLANG_FORMAT_BIN=\"${CLANG_FORMAT_BIN}\" does not exist!"
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

    # spotbugs home directory is empty by default
    SPOTBUGS_HOME="${SPOTBUGS_HOME:-""}"
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

    # Doxygen to use, defaults to "doxygen" if not set
    DOXYGEN="${DOXYGEN:-doxygen}"
    if [ ! -f "`which "${DOXYGEN}"`" ] ; then
        stderr_echo "Cannot find Doxygen! Set DOXYGEN environment variable."
        return 1
    fi

    MAKE_CMAKE_GENERATOR="${MAKE_CMAKE_GENERATOR:-Unix Makefiles}"
    NMAKE_CMAKE_GENERATOR="${NMAKE_CMAKE_GENERATOR:-NMake Makefiles}"
    MSVC_CMAKE_GENERATOR="${MSVC_CMAKE_GENERATOR:-Visual Studio 17 2022}"
    MSVC_CMAKE_TOOLSET="${MSVC_CMAKE_TOOLSET:-v141}"

    # Extra arguments to be passed by CMake to a native build tool
    CMAKE_BUILD_OPTIONS="${CMAKE_BUILD_OPTIONS:-""}"

    # cppcheck home directoty is empty by default
    CPPCHECK_HOME="${CPPCHECK_HOME:-""}"

    # gcovr binary to use for coverage report (gcc), by default is empty
    GCOVR_BIN="${GCOVR_BIN:-""}"
    if [[ (! -z "${GCOVR_BIN}" && ! -f "`which "${GCOVR_BIN}"`") ]] ; then
        stderr_echo "Provided GCOVR_BIN=\"${GCOVR_BIN}\" does not exist!"
        return 1
    fi

    # llvm-profdata binary to use for coverage report (clang), by default is empty
    LLVM_PROFDATA_BIN="${LLVM_PROFDATA_BIN:-""}"
    if [[ (! -z "${LLVM_PROFDATA_BIN}" && ! -f "`which "${LLVM_PROFDATA_BIN}"`") ]] ; then
        stderr_echo "Provided LLVM_PROFDATA_BIN=\"${LLVM_PROFDATA_BIN}\" does not exist!"
        return 1
    fi

    # llvm-cov binary to use for coverage report (clang), by default is empty
    LLVM_COV_BIN="${LLVM_COV_BIN:-""}"
    if [[ (! -z "${LLVM_COV_BIN}" && ! -f "`which "${LLVM_COV_BIN}"`") ]] ; then
        stderr_echo "Provided LLVM_COV_BIN=\"${LLVM_COV_BIN}\" does not exist!"
        return 1
    fi

    if [[ (! -z "${LLVM_PROFDATA_BIN}" && -z "${LLVM_COV_BIN}") ||
            (-z "${LLVM_PROFDATA_BIN}" && ! -z "${LLVM_COV_BIN}") ]] ; then
        stderr_echo "Both LLVM_PROFDATA_BIN and LLVM_COV_BIN environment variable must be set!"
        return 1
    fi

    # Sanitizers configuration - sanitizers disabled by default
    SANITIZERS_ENABLED="${SANITIZERS_ENABLED:-0}"

    # clang-tidy binary to use for static code check, by default is empty
    CLANG_TIDY_BIN="${CLANG_TIDY_BIN:-""}"
    if [[ (! -z "${CLANG_TIDY_BIN}" && ! -f "`which "${CLANG_TIDY_BIN}"`") ]] ; then
        stderr_echo "Provided CLANG_TIDY_BIN=\"${CLANG_TIDY_BIN}\" does not exist!"
        return 1
    fi

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
        if [ $? -ne 0 ] ; then
            return 1
        fi

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

    # Mypy configuration - mypy disabled by default
    MYPY_ENABLED="${MYPY_ENABLED:-0}"

    # Mypy extra arguments are empty by default
    MYPY_EXTRA_ARGS="${MYPY_EXTRA_ARGS:-""}"

    # documentation variables for sphinx
    set_global_doc_variables
}

# Set and check global variables for documentation projets.
set_global_doc_variables()
{
    # Dot executable to use, defaults to "dot" if not set
    DOT="${DOT:-dot}"
    if [ ! -f "`which "${DOT}"`" ] ; then
        stderr_echo "Cannot find Dot! Set DOT environment variable."
        return 1
    fi

    return 0
}

# Get python version as an array.
get_python_version()
{
    exit_if_argc_ne $# 2
    local PYTHON_BIN="$1"; shift
    local PYTHON_VERSION_OUT="$1"; shift

    local PYTHON_VERSION_ARRAY=$(${PYTHON_BIN} -V 2>&1 | cut -d\  -f 2)
    PYTHON_VERSION_ARRAY=(${PYTHON_VERSION_ARRAY//./ }) # python version as an array

    for i in ${!PYTHON_VERSION_ARRAY[@]} ; do
        eval ${PYTHON_VERSION_OUT}[$i]="${PYTHON_VERSION_ARRAY[$i]}"
    done
}

# Get python subdirectory name according to the python version.
get_python_version_string()
{
    exit_if_argc_ne $# 1
    local PYTHON_VERSION_STRING_OUT="$1"; shift

    local PYTHON_VERSION=()
    get_python_version "${PYTHON}" PYTHON_VERSION
    local PYTHON_VERSION_NAME="${PYTHON_VERSION[@]}"

    eval ${PYTHON_VERSION_STRING_OUT}="'${PYTHON_VERSION_NAME// /.}'"
}

# Check python version.
check_python_version()
{
    exit_if_argc_ne $# 1
    local PYTHON_BIN="$1"; shift

    local PYTHON_VERSION=()
    get_python_version "${PYTHON_BIN}" PYTHON_VERSION
    if [[ ${#PYTHON_VERSION[@]} -lt 2 || ${PYTHON_VERSION[0]} -lt 3 ]] ||
       [[ ${PYTHON_VERSION[0]} -eq 3 && ${PYTHON_VERSION[1]} -lt 8 ]] ; then
        stderr_echo "Python 3.8+ is required! Current Python is '$(${PYTHON_BIN} -V 2>&1)'"
        return 1
    fi

    return 0
}

# Check python requirements.
#
# Note: From Python 3.12, setuptools must be installed.
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

    local ACTIVATE
    if [ -f "${PYTHON_VIRTUALENV_ROOT}/bin/activate" ] ; then
        ACTIVATE="${PYTHON_VIRTUALENV_ROOT}/bin/activate"
    elif [ -f "${PYTHON_VIRTUALENV_ROOT}/Scripts/activate" ] ; then
        ACTIVATE="${PYTHON_VIRTUALENV_ROOT}/Scripts/activate"
    fi

    eval ${PYTHON_VIRTUALENV_ACTIVATE_OUT}="'${ACTIVATE}'"
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

    local PYTHON_VERSION_STRING
    get_python_version_string PYTHON_VERSION_STRING
    local PYTHON_VIRTUALENV_ROOT="${PYTHON_VIRTUALENV:-"${ZSERIO_BUILD_DIR}/pyenv/${PYTHON_VERSION_STRING}"}"
    local PYTHON_VIRTUALENV_ACTIVATE
    detect_python_virtualenv_activate "${PYTHON_VIRTUALENV_ROOT}" PYTHON_VIRTUALENV_ACTIVATE

    if [ ! -z "${PYTHON_VIRTUALENV}" ] ; then # forced python virtualenv
        if [ -z "${PYTHON_VIRTUALENV_ACTIVATE}" ] ; then
            stderr_echo "Failed to find virtualenv activate script in '${PYTHON_VIRTUALENV_ROOT}'!"
            return 1
        fi
    else
        if [ -z "${PYTHON_VIRTUALENV_ACTIVATE}" ] ; then
            # On Windows, virtualenv option -p does not accept python executable without '.exe' extension
            local PYTHON_EXE
            get_executable "${PYTHON}" PYTHON_EXE
            "${PYTHON}" -m virtualenv -p "${PYTHON_EXE}" "${PYTHON_VIRTUALENV_ROOT}"
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

    local STANDARD_REQUIREMENTS=(
        "setuptools==69.0.3"
        "coverage==6.5.0" "sphinx"
        "astroid==3.0.3" "pylint==3.0.3" "mypy==0.931"
        "pybind11>=2.10.0"
    )
    local APSW_REQUIREMENTS=("apsw")

    if [ ! -z "${PYTHON_VIRTUALENV}" ] ; then  # forced python virtualenv
        local REQUIREMENTS=("${STANDARD_REQUIREMENTS[@]}" "${APSW_REQUIREMENTS[@]}")
        check_python_requirements python REQUIREMENTS[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    else
        check_python_requirements python STANDARD_REQUIREMENTS[@] 2> /dev/null
        if [ $? -ne 0 ] ; then
            # don't use only pip because this does not support spaces in path (https://github.com/pypa/pip/issues/923)
            python -m pip install "${STANDARD_REQUIREMENTS[@]}"
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
        git clone --depth 1 https://github.com/rogerbinns/apsw.git -b 3.45.1.0
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to clone apsw repository!"
            popd > /dev/null
            return 1
        fi
    fi

    cd apsw

    python setup.py fetch --sqlite --version=3.45.1 build --enable=fts4,fts5 install
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
    MAKE_CMAKE_GENERATOR   CMake generator to use for build using Makefiles. Default is
                           "Unix Makefiles".
    MSVC_CMAKE_GENERATOR   CMake generator to use with MSVC compiler. Default is
                           "Visual Studio 17 2022". Note that CMake option "-A x64"
                           is added automatically for windows64-mscv target.
    MSVC_CMAKE_TOOLSET     MSVC toolset specification for CMake generator.
                           Default is "v141". Note that "v141" is for VS 2017,
                           "v142" is for VS 2019.
    GCC_VERSION_SUFFIX     Gcc compiler version suffix. Default is empty.
                           Set e.g. "-11" to use "gcc-11" instead of "gcc".
    CLANG_VERSION_SUFFIX   Clang compiler version suffix. Default is empty.
                           Set e.g. "-8" to use "clang-8" instead of "clang".
    CMAKE_EXTRA_ARGS       Extra arguments to CMake. Default is empty string.
    CMAKE_BUILD_OPTIONS    Arguments to be passed by CMake to a native build tool.
    CTEST                  Ctest executable to use. Default is "ctest".
    DOXYGEN                Doxygen executable to use. Default is 'doxygen".
    JAVAC_BIN              Java compiler executable to use. Default is "javac".
    JAVA_BIN               Java executable to use. Default is "java".
    PYTHON                 Python 3.5+ executable to use. Default is "python".
    PYTHON_VIRTUALENV      Custom python virtualenv to use. Default is empty string.
    DOT                    Dot executable to use. Default is "dot".
    FIND                   Bash command find to use. Default is "/usr/bin/find".
    SPOTBUGS_HOME          Home directory of spotbugs tool where lib is located
                           (e.g. /usr/share/spotbugs). If set, spotbugs will be
                           called. Default is empty string.
    CPPCHECK_HOME          Home directory of cppcheck tool where cppcheck
                           binary is located. If set, cppcheck will be called.
                           Default is empty string.
    CLANG_TIDY_BIN         Name of clang-tidy binary. If not set, clang-tidy tool is not called.
    CLANG_FORMAT_BIN       Name of clang-format binary. If not set, clang-format tool is not called.
    GCOVR_BIN              Gcovr binary to use for coverage report generation (gcc).
                           Default is empty string.
    LLVM_PROFDATA_BIN      llvm-profdata  binary to use for coverage report generation (clang).
                           Default is empty string.
    LLVM_COV_BIN           llvm-cov  binary to use for coverage report generation (clang).
                           Default is empty string.
    SANITIZERS_ENABLED     Defines whether to use sanitizers. Default is 0 (disabled).

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
    # don't use "`pwd`" here because it does not work if path contains spaces
    local ABSOLUTE_PATH="'`pwd`'"
    popd > /dev/null

    if [ -n "${FILE_TO_CONVERT}" ] ; then
        ABSOLUTE_PATH="${ABSOLUTE_PATH}/${FILE_TO_CONVERT}"
    fi

    eval ${ABSOLUTE_PATH_OUT}="${ABSOLUTE_PATH}"

    return 0
}

# Get latest zserio release (even pre-release if it is the latest) number from the GitHub.
get_latest_zserio_version()
{
    exit_if_argc_ne $# 1
    local ZSERIO_VERSION_OUT="$1"; shift

    local CURL_AUTHORIZATION=()
    if [[ ! -z "${GITHUB_TOKEN}" ]] ; then
        local CURL_AUTHORIZATION=(-H "Authorization: Bearer ${GITHUB_TOKEN}") # we need to pass quotes
    fi

    local ZSERIO_VERSION_GET_LATEST_ZSERIO=`curl "${CURL_AUTHORIZATION[@]}" -s \
            https://api.github.com/repos/ndsev/zserio/releases |
            grep -m 1 tag_name | cut -d\" -f4 | cut -c2-`
    if [ $? -ne 0 -o -z "${ZSERIO_VERSION_GET_LATEST_ZSERIO}" ] ; then
        stderr_echo "Failed to grep the latest Zserio version from GitHub!"
        return 1
    fi

    eval ${ZSERIO_VERSION_OUT}="'${ZSERIO_VERSION_GET_LATEST_ZSERIO}'"

    return 0
}

# Get link to release of zserio binaries on the GitHub.
get_zserio_bin_url()
{
    exit_if_argc_ne $# 2
    local ZSERIO_VERSION="${1}"; shift
    local ZSERIO_BIN_URL_OUT="${1}"; shift

    local LINK_PREFIX="https://github.com/ndsev/zserio/releases/download"
    eval ${ZSERIO_BIN_URL_OUT}="${LINK_PREFIX}/v${ZSERIO_VERSION}/zserio-${ZSERIO_VERSION}-bin.zip"
}

# Get link to release of zserio runtime libraries on the GitHub.
get_zserio_runtime_libs_url()
{
    exit_if_argc_ne $# 2
    local ZSERIO_VERSION="${1}"; shift
    local ZSERIO_RUNTIME_LIBS_URL_OUT="${1}"; shift

    local LINK_PREFIX="https://github.com/ndsev/zserio/releases/download"
    eval ${ZSERIO_RUNTIME_LIBS_URL_OUT}="${LINK_PREFIX}/v${ZSERIO_VERSION}/zserio-${ZSERIO_VERSION}-runtime-libs.zip"
}

# Get link to LICENSE matching to the release on the GitHub.
get_zserio_license_url()
{
    exit_if_argc_ne $# 2
    local ZSERIO_VERSION="${1}"; shift
    local ZSERIO_LICENSE_URL_OUT="${1}"; shift
    eval ${ZSERIO_LICENSE_URL_OUT}="https://raw.githubusercontent.com/ndsev/zserio/v${ZSERIO_VERSION}/LICENSE"
}

# Get zserio binaries from the GitHub.
get_zserio_bin()
{
    exit_if_argc_lt $# 2
    local ZSERIO_VERSION="$1"; shift
    local ZSERIO_BIN_OUT_DIR="$1"; shift
    if [ $# -ne 0 ] ; then
        local ZSERIO_BIN_ZIP_NAME="$1"; shift
    else
        local ZSERIO_BIN_ZIP_NAME="zserio-${ZSERIO_VERSION}-bin.zip"
    fi

    local CURL_AUTHORIZATION=()
    if [[ ! -z "${GITHUB_TOKEN}" ]] ; then
        local CURL_AUTHORIZATION=(-H "Authorization: Bearer ${GITHUB_TOKEN}") # we need to pass quotes
    fi

    local URL
    get_zserio_bin_url "${ZSERIO_VERSION}" URL

    curl "${CURL_AUTHORIZATION[@]}" -L -s -f "${URL}" \
            -o "${ZSERIO_BIN_OUT_DIR}/${ZSERIO_BIN_ZIP_NAME}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to download Zserio binaries from GitHub!"
        return 1
    fi

    return 0
}

# Get zserio runtime libraries from the GitHub.
get_zserio_runtime_libs()
{
    exit_if_argc_lt $# 2
    local ZSERIO_VERSION="$1"; shift
    local ZSERIO_RUNTIME_LIBS_OUT_DIR="$1"; shift
    if [ $# -ne 0 ] ; then
        local ZSERIO_RUNTIME_LIBS_ZIP_NAME="$1"; shift
    else
        local ZSERIO_RUNTIME_LIBS_ZIP_NAME="zserio-${ZSERIO_VERSION}-runtime-libs.zip"
    fi

    local CURL_AUTHORIZATION=()
    if [[ ! -z "${GITHUB_TOKEN}" ]] ; then
        local CURL_AUTHORIZATION=(-H "Authorization: Bearer ${GITHUB_TOKEN}") # we need to pass quotes
    fi

    local URL
    get_zserio_runtime_libs_url "${ZSERIO_VERSION}" URL

    curl "${CURL_AUTHORIZATION[@]}" -L -s -f "${URL}" \
            -o "${ZSERIO_RUNTIME_LIBS_OUT_DIR}/${ZSERIO_RUNTIME_LIBS_ZIP_NAME}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to download Zserio runtime libraries from GitHub!"
        return 1
    fi

    return 0
}

# Get Zserio LICENSE mathching to the release
get_zserio_license()
{
    exit_if_argc_lt $# 2
    local ZSERIO_VERSION="$1"; shift
    local ZSERIO_LICENSE_OUT_DIR="$1"; shift
    if [ $# -ne 0 ] ; then
        local ZSERIO_LICENSE_NAME="$1"; shift
    else
        local ZSERIO_LICENSE_NAME="LICENSE"
    fi

    local CURL_AUTHORIZATION=()
    if [[ ! -z "${GITHUB_TOKEN}" ]] ; then
        local CURL_AUTHORIZATION=(-H "Authorization: Bearer ${GITHUB_TOKEN}") # we need to pass quotes
    fi

    local URL
    get_zserio_license_url "${ZSERIO_VERSION}" URL

    curl "${CURL_AUTHORIZATION[@]}" -L -s -f "${URL}" \
            -o "${ZSERIO_LICENSE_OUT_DIR}/${ZSERIO_LICENSE_NAME}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to download Zserio LICENSE from GitHub!"
        return 1
    fi

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
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_OUT_DIR="$1"; shift
    local ZSERIO_RELEASE_DIR_OUT="$1"; shift
    local ZSERIO_VERSION_OUT="$1"; shift

    local ZSERIO_VERSION_GET_RELEASE_DIR
    get_zserio_version "${ZSERIO_PROJECT_ROOT}" ZSERIO_VERSION_GET_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${ZSERIO_RELEASE_DIR_OUT}="'${ZSERIO_OUT_DIR}/release-${ZSERIO_VERSION_GET_RELEASE_DIR}'"
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

    if [ -n "${CLANG_FORMAT_BIN}" ] ; then
        ANT_PROPS+=("-Dclang_format.exec_file=${CLANG_FORMAT_BIN}")
    fi

    if [ -n "${SPOTBUGS_HOME}" ] ; then
        ANT_PROPS+=("-Dspotbugs.home_dir=${SPOTBUGS_HOME}")
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
    exit_if_argc_ne $# 7
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
        compile_cpp_for_target "${ZSERIO_PROJECT_ROOT}" "${BUILD_DIR}/${TARGET}" "${CMAKELISTS_DIR}" \
                               "${TARGET}" CMAKE_ARGS[@] CTEST_ARGS[@] "${MAKE_TARGET}"
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
                "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON"
                "-DCMAKE_TOOLCHAIN_FILE=${TOOLCHAIN_FILE}"
                "-DCMAKE_PREFIX_PATH=${SQLITE_RELEASE_ROOT}/${TARGET}")

    if [ -n "${CPPCHECK_HOME}" ] ; then
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-DCPPCHECK_HOME=${CPPCHECK_HOME}")
    else
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-UCPPCHECK_HOME")
    fi

    if [ ${SANITIZERS_ENABLED} -eq 1 ] ; then
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-DSANITIZERS_ENABLED=ON")
    else
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-DSANITIZERS_ENABLED=OFF")
    fi

    # mingw uses msvc STL which is c++14. Clang-tidy then complains project is compiled as c++11
    if [[ ( ! -z "${CLANG_TIDY_BIN}" ) && ( ${TARGET} != *"-mingw" ) ]] ; then
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-DCLANG_TIDY_BIN=${CLANG_TIDY_BIN}")
    else
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-UCLANG_TIDY_BIN")
    fi

    if [ ! -z "${CLANG_FORMAT_BIN}" ] ; then
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-DCLANG_FORMAT_BIN=${CLANG_FORMAT_BIN}")
    else
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-UCLANG_FORMAT_BIN")
    fi

    # detect build type
    local BUILD_TYPE="Release"
    local BUILD_TYPE_LOWER_CASE="release"
    if [[ "${CMAKE_EXTRA_ARGS}" == *-DCMAKE_BUILD_TYPE=?ebug* ]] ; then
        BUILD_TYPE="Debug";
        BUILD_TYPE_LOWER_CASE="debug"
    fi

    BUILD_DIR="${BUILD_DIR}/${BUILD_TYPE_LOWER_CASE}"

    mkdir -p "${BUILD_DIR}"
    pushd "${BUILD_DIR}" > /dev/null

    if [[ ${MAKE_TARGET} == "install" ]] ; then
        local CMAKE_BUILD_TARGET="all"
    else
        local CMAKE_BUILD_TARGET="${MAKE_TARGET}"
    fi

    # resolve CMake generator
    if [[ ${TARGET} == *"-msvc" ]] ; then
        # configure makefiles first so compile_commands.json gets exported
        mkdir -p "nmake"
        pushd "nmake" > /dev/null
        # eval "$(${SCRIPT_DIR}/vcvarsall.sh x64)" - seems to break python_cpp build later
        echo "ECHO ${SCRIPT_DIR}/vcvarsrun.sh x64 -- ${CMAKE} ${CMAKE_EXTRA_ARGS} -G ${NMAKE_CMAKE_GENERATOR} ${CMAKE_ARGS[@]} ${CMAKELISTS_DIR}"
        "${SCRIPT_DIR}/vcvarsrun.sh" x64 -- "${CMAKE}" ${CMAKE_EXTRA_ARGS} -G "${NMAKE_CMAKE_GENERATOR}" "${CMAKE_ARGS[@]}" "${CMAKELISTS_DIR}"
        local CMAKE_RESULT=$?
        if [ ${CMAKE_RESULT} -ne 0 ] ; then
            stderr_echo "Running CMake failed with return code ${CMAKE_RESULT}!"
            popd > /dev/null
            return 1
        fi
        popd > /dev/null
        cp nmake/compile_commands.json .
        # remove arguments which clang-tidy doesn't understand
        sed -i -E 's#/MP##g' compile_commands.json 
        sed -i -E 's#-external:W0##g' compile_commands.json
        
        local CMAKE_BUILD_CONFIG="--config ${BUILD_TYPE}"
        CTEST_ARGS+=("-C ${BUILD_TYPE}")
        if [[ ${CMAKE_BUILD_TARGET} == "all" ]] ; then
            CMAKE_BUILD_TARGET="ALL_BUILD" # all target doesn't exist in MSVC solution
        fi
        local CMAKE_GENERATOR="${MSVC_CMAKE_GENERATOR}";
        CMAKE_ARGS=("${CMAKE_ARGS[@]}" "-A x64" "-T ${MSVC_CMAKE_TOOLSET}")
    else
        local CMAKE_GENERATOR="${MAKE_CMAKE_GENERATOR}"
        local CMAKE_BUILD_CONFIG=""
    fi

    # generate makefile running cmake
    echo "ECHO ${CMAKE} ${CMAKE_EXTRA_ARGS} -G ${CMAKE_GENERATOR} ${CMAKE_ARGS[@]} ${CMAKELISTS_DIR}"
    "${CMAKE}" ${CMAKE_EXTRA_ARGS} -G "${CMAKE_GENERATOR}" "${CMAKE_ARGS[@]}" "${CMAKELISTS_DIR}"
    local CMAKE_RESULT=$?
    if [ ${CMAKE_RESULT} -ne 0 ] ; then
        stderr_echo "Running CMake failed with return code ${CMAKE_RESULT}!"
        popd > /dev/null
        return 1
    fi
    #echo "ECHO head compile_commands.json"
    #head compile_commands.json
        
    # build it running cmake
    "${CMAKE}" --build . --target ${CMAKE_BUILD_TARGET} ${CMAKE_BUILD_CONFIG} -- ${CMAKE_BUILD_OPTIONS}
    local CMAKE_RESULT=$?
    if [ ${CMAKE_RESULT} -ne 0 ] ; then
        stderr_echo "Running CMake failed with return code ${CMAKE_RESULT}!"
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

        if [[ (! -z "${GCOVR_BIN}" && "${TARGET}" == *"gcc"*) ||
                (! -z "${LLVM_PROFDATA_BIN}" && ! -z "${LLVM_COV_BIN}" && "${TARGET}" == *"clang"*) ]] ; then
            "${CMAKE}" --build . --target coverage
            local COVERAGE_RESULT=$?
            if [ ${COVERAGE_RESULT} -ne 0 ] ; then
                stderr_echo "Generating of coverage report failed with return code ${COVERAGE_RESULT}."
                popd > /dev/null
                return 1
            fi
        fi
    fi

    # install it running cmake
    if [[ ${MAKE_TARGET} == "install" ]] ; then
        "${CMAKE}" --build . --target install ${CMAKE_BUILD_CONFIG} -- ${CMAKE_BUILD_OPTIONS}
        local CMAKE_RESULT=$?
        if [ ${CMAKE_RESULT} -ne 0 ] ; then
            stderr_echo "Running CMake failed with return code ${CMAKE_RESULT}!"
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

# Run mypy on given python sources.
run_mypy()
{
    if [[ ${MYPY_ENABLED} != 1 ]] ; then
        echo "Mypy is disabled."
        echo
        return 0
    fi

    exit_if_argc_lt $# 3
    local BUILD_DIR="$1"; shift
    local MYPY_CONFIG_FILE="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local MYPY_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local SOURCES=("$@")

    python -m mypy ${MYPY_EXTRA_ARGS} "${MYPY_ARGS[@]}" --cache-dir="${BUILD_DIR}/.mypy_cache" \
            --config-file "${MYPY_CONFIG_FILE}" "${SOURCES[@]}"
    local MYPY_RESULT=$?
    if [ ${MYPY_RESULT} -ne 0 ] ; then
        stderr_echo "Running mypy failed with return code ${MYPY_RESULT}!"
        return 1
    fi

    echo "Mypy done."
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
        [[ "${TARGET_PLATFORM}" == "linux32-"* ]]
        ;;
    ubuntu64)
        [[ "${TARGET_PLATFORM}" == "linux32-"* || "${TARGET_PLATFORM}" = "linux64-"* ]]
        ;;
    windows64)
        [[ "${TARGET_PLATFORM}" == "windows64-"* ]]
        ;;
    *)
        stderr_echo "can_run_tests: unknown current platform ${HOST_PLATFORM}!"
        return 1
    esac
}

# Determines the current host platform.
#
# Returns one of the following platforms:
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
    MINGW*|MSYS*)
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

# Returns executable according to the current host.
#
# On Linux the given executable is unchanged, on Windows the executable is appended by extension if it is given
# without any extension.
get_executable()
{
    exit_if_argc_lt $# 2
    local EXECUTABLE="$1"; shift
    local HOST_EXECUTABLE_OUT="$1"; shift

    local HOST_PLATFORM
    get_host_platform HOST_PLATFORM
    if [[ "${HOST_PLATFORM}" == "windows"* ]] ; then
        local HOST_EXECUTABLE="`ls --append-exe "${EXECUTABLE}"`"
    else
        local HOST_EXECUTABLE="${EXECUTABLE}"
    fi

    eval ${HOST_EXECUTABLE_OUT}="'${HOST_EXECUTABLE}'"
}

# Builds C++ runtime binding to Python.
build_cpp_binding_to_python()
{
    exit_if_argc_ne $# 3
    local PYTHON_RUNTIME_DIR="$1"; shift
    local CPP_RUNTIME_DIR="$1"; shift
    local BUILD_DIR="$1"; shift

    local BUILD_ZSERIO_CPP_DIR="${BUILD_DIR}/zserio_cpp"
    local BUILD_SOURCES_DIR="${BUILD_ZSERIO_CPP_DIR}/sources"

    mkdir -p "${BUILD_SOURCES_DIR}"
    cp -r "${PYTHON_RUNTIME_DIR}/zserio_cpp/"* "${BUILD_SOURCES_DIR}"
    cp -r "${CPP_RUNTIME_DIR}/"* "${BUILD_SOURCES_DIR}/"

    python "${BUILD_SOURCES_DIR}/setup.py" build --build-base="${BUILD_ZSERIO_CPP_DIR}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to build C++ runtime binding to Python!"
        return 1
    fi

    return 0
}

