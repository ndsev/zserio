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

    # Zserio extra arguments
    ZSERIO_EXTRA_ARGS="${ZSERIO_EXTRA_ARGS:-""}"

    # vnu.jar HTML Validator (disabled by default)
    NU_HTML_VALIDATOR="${NU_HTML_VALIDATOR:-""}"
    if [[ -n "${NU_HTML_VALIDATOR}" && ! -f "${NU_HTML_VALIDATOR}" ]] ; then
        stderr_echo "Invalid NU HTML Validator! Set NU_HTML_VALIDATOR environment variable properly."
        return 1
    fi

    # NU HTML Validator extra arguments
    VNU_FILTER_FILE="${VNU_FILTER_FILE:-""}"

    # Configuration of xmllint (disabled by default)
    XMLLINT_ENABLED="${XMLLINT_ENABLED:-0}"
    if [[ ${XMLLINT_ENABLED} == 1 ]] ; then
        XMLLINT="${XMLLINT:-xmllint}"
        if [ ! -f "`which "${XMLLINT}"`" ] ; then
            stderr_echo "Cannot find xmllint. Set XMLLINT environment variable or disable xmllint."
            return 1
        fi
    fi

    return 0
}

# Print help on the environment variables used for the tests scripts.
print_test_help_env()
{
    cat << EOF
Uses the following environment variables for testing:
    UNZIP               Unzip executable to use. Default is "unzip".
    ZSERIO_EXTRA_ARGS   Extra arguments to zserio tool. Default is empty.
    NU_HTML_VALIDATOR   Path to NU HTML Validator jar file (vnu.jar).
    VNU_FILTER_FILE     Filter file path to be used by NU HTML Validator. Default is empty.
    XMLLINT_ENABLED     Defines whether to run xmllint in xml tests. Default is 0 (disabled).
    XMLLINT             Executable of xmllint to use. Default is "xmllint".

EOF
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

# Run zserio tool with specified sources and arguments
run_zserio_tool()
{
    exit_if_argc_ne $# 6
    local ZSERIO_RELEASE_ROOT="$1"; shift
    local BUILD_DIR="$1"; shift # for logging
    local ZSERIO_SOURCE_DIRECTORY="$1"; shift
    local ZSERIO_SOURCE="$1"; shift
    local SWITCH_WERROR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local ZSERIO_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")

    local ZSERIO="${ZSERIO_RELEASE_ROOT}/zserio.jar"
    local ZSERIO_LOG="${BUILD_DIR}/zserio_log.txt"
    local MESSAGE="Compilation of zserio '${ZSERIO_SOURCE_DIRECTORY}/${ZSERIO_SOURCE}'"
    echo "STARTING - ${MESSAGE}"

    # build directory must be created in advance because tee will fail to create a new zserio log file
    mkdir -p "${BUILD_DIR}"
    "${JAVA_BIN}" -jar "${ZSERIO}" ${ZSERIO_EXTRA_ARGS} "-src" "${ZSERIO_SOURCE_DIRECTORY}" "${ZSERIO_SOURCE}" \
            "${ZSERIO_ARGS[@]}" 2>&1 | tee ${ZSERIO_LOG}

    if [ ${PIPESTATUS[0]} -ne 0 ] ; then
        stderr_echo "${MESSAGE} failed!"
        return 1
    fi

    if [ ${SWITCH_WERROR} -ne 0 ] ; then
        grep -q "\[WARNING\]" ${ZSERIO_LOG}
        if [ $? -eq 0 ] ; then
            stderr_echo "${MESSAGE} failed because warnings are treated as errors! "
            return 1
        fi
    fi

    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Run xmllint if enabled
run_xmllint()
{
    exit_if_argc_ne $# 1
    local XML_FILE="$1"; shift

    if [ ${XMLLINT_ENABLED} -ne 0 ] ; then
        local MESSAGE="XML Validation of '${XML_FILE}'"
        echo "STARTING - ${MESSAGE}"
        ${XMLLINT} --format "${XML_FILE}" > /dev/null
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    else
        echo -e "XML Validation is disabled.\n"
    fi

    return 0
}

# Run NU HTML Validator if available
run_vnu()
{
    exit_if_argc_ne $# 2
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local VNU_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")
    local DOC_ROOT_DIR="$1"; shift

    if [ -f "${VNU_FILTER_FILE}" ] ; then
        VNU_ARGS+=(--filterfile "${VNU_FILTER_FILE}")
    fi

    if [ -n "${NU_HTML_VALIDATOR}" ] ; then
        local MESSAGE="Validation of generated HTML in '${DOC_ROOT_DIR}'"
        echo "STARTING - ${MESSAGE}"
        # NU validator needs at least 512k of stack size
        "${JAVA_BIN}" -Xss1024k -jar "${NU_HTML_VALIDATOR}" --Werror --skip-non-html \
                      "${VNU_ARGS[@]}" "${DOC_ROOT_DIR}"
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
        echo -e "FINISHED - ${MESSAGE}\n"
    else
        echo -e "HTML Validation is disabled.\n"
    fi

    return 0
}
