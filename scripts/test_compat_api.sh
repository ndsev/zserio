#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Get sources for the requested version.
get_sources()
{
    exit_if_argc_ne $# 3
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local VERSION="$1"; shift

    if [[ "${VERSION}" == "working-"* ]] ; then
        rm -rf "${TEST_OUT_DIR}/${VERSION}" # always use fresh working copy
        mkdir "${TEST_OUT_DIR}/${VERSION}"
        cp -r `ls -A "${ZSERIO_PROJECT_ROOT}" | grep -v ".git*" | grep -v "build"` "${TEST_OUT_DIR}/${VERSION}/."
    else
        if [ -d "${TEST_OUT_DIR}/${VERSION}" ] ; then
            echo "Version \"${TEST_OUT_DIR}/${VERSION}\" already present."
        else
            if [[ "${VERSION}" == "latest-"* ]] ; then
                local TAG_NAME="v${VERSION#*-}"
            else
                local TAG_NAME="v${VERSION}"
            fi
            git clone --depth=1 --branch="${TAG_NAME}" --recurse-submodules \
                    https://github.com/ndsev/zserio "${TEST_OUT_DIR}/${VERSION}"
            if [ $? -ne 0 ] ; then
                stderr_echo "Failed to clone zserio tag \"${TAG_NAME}\"."
                return 1
            fi
        fi
    fi

    return 0
}

# Get release for the requested version.
get_release()
{
    exit_if_argc_ne $# 3
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local VERSION="$1"; shift

    if [[ "${VERSION}" == "latest-"* || "${VERSION}" == "working-"* ]] ; then
        local VERSION_NUMBER=${VERSION#*-}
    else
        local VERSION_NUMBER=${VERSION}
    fi

    if [[ "${VERSION}" == "working"-* ]] ; then
        cp "${ZSERIO_PROJECT_ROOT}/release-${VERSION_NUMBER}/zserio-${VERSION_NUMBER}-bin.zip" \
                "${TEST_OUT_DIR}/zserio-${VERSION}-bin.zip"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        cp "${ZSERIO_PROJECT_ROOT}/release-${VERSION_NUMBER}/zserio-${VERSION_NUMBER}-runtime-libs.zip" \
                "${TEST_OUT_DIR}/zserio-${VERSION}-runtime-libs.zip"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    else
        get_zserio_bin ${VERSION_NUMBER} "${TEST_OUT_DIR}" "zserio-${VERSION}-bin.zip"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        get_zserio_runtime_libs ${VERSION_NUMBER} "${TEST_OUT_DIR}" "zserio-${VERSION}-runtime-libs.zip"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    return 0
}

# Copy release to tested version directory and rename it appropriately if needed.
copy_release()
{
    exit_if_argc_ne $# 4
    local TEST_OUT_DIR="$1"; shift
    local VERSION_DIR="$1"; shift
    local RELEASE="$1"; shift
    local AS_RELEASE="$1"; shift

    rm -rf "${TEST_OUT_DIR}/${VERSION_DIR}/release-${AS_RELEASE}"
    mkdir "${TEST_OUT_DIR}/${VERSION_DIR}/release-${AS_RELEASE}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    cp "${TEST_OUT_DIR}/zserio-${RELEASE}-bin.zip" \
            "${TEST_OUT_DIR}/${VERSION_DIR}/release-${AS_RELEASE}/zserio-${AS_RELEASE}-bin.zip"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    cp "${TEST_OUT_DIR}/zserio-${RELEASE}-runtime-libs.zip" \
            "${TEST_OUT_DIR}/${VERSION_DIR}/release-${AS_RELEASE}/zserio-${AS_RELEASE}-runtime-libs.zip"
    if [ $? -ne 0 ] ; then
        return 1
    fi
}

# Runs test from older version with release from newer version to check generated API compatibility.
#
# Return codes:
# -------------
# 0 - Success.
# 1 - Failure - unknown reason.
# 2 - Failure - test.sh failed.
compatibility_check()
{
    exit_if_argc_ne $# 5
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local OLD_VERSION="$1"; shift
    local NEW_VERSION="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PACKAGES_ARRAY=("${MSYS_WORKAROUND_TEMP[@]}")

    local MESSAGE="Run API compatibiliy check for ${OLD_VERSION} with ${NEW_VERSION} release"
    echo "STARTING - ${MESSAGE}"

    if [[ "${OLD_VERSION}" == "latest-"* || "${OLD_VERSION}" == "working-"* ]] ; then
        local OLD_VERSION_NUMBER=${OLD_VERSION#*-}
    else
        local OLD_VERSION_NUMBER=${OLD_VERSION}
    fi

    rm -rf "${TEST_OUT_DIR}/${OLD_VERSION}/build"

    # test OLD_VERSION with NEW_VERSION release
    copy_release "${TEST_OUT_DIR}" "${OLD_VERSION}" "${NEW_VERSION}" "${OLD_VERSION_NUMBER}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    "${TEST_OUT_DIR}/${OLD_VERSION}/scripts/test.sh" "${PACKAGES_ARRAY[@]}" \
                                                     -x errors/* \
                                                     -x warnings/array_types_warning \
                                                     -x arguments/without_writer_code
    TEST_RESULT=$?
    if [ ${TEST_RESULT} -ne 0 ] ; then
        stderr_echo "FAILED - ${MESSAGE}"
        return 2
    fi

    echo "FINISHED - ${MESSAGE}"
}

# Append version numbers to 'working' and 'latest'
append_version_number()
{
    exit_if_argc_ne $# 2
    local VERSION_IN="$1"; shift
    local VERSION_OUT="$1"; shift

    if [[ "${VERSION_IN}" == "working" ]] ; then
        local WORKING_VERSION
        get_zserio_version "${ZSERIO_PROJECT_ROOT}" WORKING_VERSION
        if [ $? -ne 0 ] ; then
            return 1
        fi
        eval ${VERSION_OUT}="working-${WORKING_VERSION}"
    elif [[ "${VERSION_IN}" == "latest" ]] ; then
        local LATEST_VERSION
        get_latest_zserio_version LATEST_VERSION
        if [ $? -ne 0 ] ; then
            return 1
        fi
        eval ${VERSION_OUT}="latest-${LATEST_VERSION}"
    else
        eval ${VERSION_OUT}="${VERSION_IN}"
    fi

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs Zserio tests on requested Zserio releases and compares produced BLOBs to check binary compatibility.

Usage:
    $0 [-h] [-e] [-p] [-o <dir>] [-v <ver>] [-n <ver>] package...

Arguments:
    -h, --help            Show this help.
    -e, --help-env        Show help for enviroment variables.
    -p, --purge           Purge testing directory.
    -o <dir>, --output-directory <dir>
                          Output directory where tests will be run.
    -v <ver>, --old-version <ver>
                          Specify the old version to test. Defaults to 'latest'.
    -n <ver>, --new-version <ver>
                          Specify the new version to test. Defaults to 'working'.

Versions:
    Use 'latest' for latest release on GitHub.
    Use 'working' for release in working directory.
    Or use exact version number for specific release.

Packages:
    All packages available in zserio tests.

Examples:
    $0 all-linux64-clang
    $0 --old-version latest --new-version working java

Uses the following environment variable:
    GITHUB_TOKEN          GitHub token authentication to use during looking for the latest release on GitHub.
                          Default is without authentication.
EOF
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
    exit_if_argc_lt $# 2
    local PARAM_OUT_DIR_OUT="$1"; shift
    local PARAM_OLD_VERSION_OUT="$1"; shift
    local PARAM_NEW_VERSION_OUT="$1"; shift
    local PARAM_PACKAGES_ARRAY_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${PARAM_OLD_VERSION_OUT}="latest"
    eval ${PARAM_NEW_VERSION_OUT}="working"
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PACKAGES=0
    local ARG="$1"
    while [ $# -ne 0 ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
                ;;

            "-p" | "--purge")
                eval ${SWITCH_PURGE_OUT}=1
                shift
                ;;

            "-o" | "--output-directory")
                eval ${PARAM_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-v" | "--old-version")
                eval ${PARAM_OLD_VERSION}="$2"
                shift 2
                ;;

            "-n" | "--new-version")
                eval ${PARAM_NEW_VERSION}="$2"
                shift 2
                ;;

            "-"*)
                stderr_echo "Invalid switch '${ARG}'!"
                echo
                return 1
                ;;

            *)
                eval ${PARAM_PACKAGES_ARRAY_OUT}[NUM_PACKAGES]="${ARG}"
                NUM_PACKAGES=$((NUM_PACKAGES + 1))
                shift
                ;;
        esac
        ARG="$1"
    done

    if [ ${NUM_PACKAGES} -lt 1 ] ; then
        stderr_echo "Missing packages to test!"
        echo
        return 1
    fi
}

main()
{
    # get the project root
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local PARAM_OLD_VERSION
    local PARAM_NEW_VERSION
    local PARAM_PACKAGES_ARRAY=()
    local SWITCH_PURGE
    parse_arguments PARAM_OUT_DIR PARAM_OLD_VERSION PARAM_NEW_VERSION PARAM_PACKAGES_ARRAY SWITCH_PURGE "$@"
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

    set_global_common_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # rather use absolute paths
    convert_to_absolute_path "${PARAM_OUT_DIR}" PARAM_OUT_DIR

    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"
    local TEST_OUT_DIR="${ZSERIO_BUILD_DIR}/test_compat"

    if [ ${SWITCH_PURGE} -ne 0 ] ; then
        echo "Purging test directory: ${TEST_OUT_DIR}"
        echo
        rm -rf "${TEST_OUT_DIR}/"
        return 0
    fi

    mkdir -p "${TEST_OUT_DIR}"

    # append version numbers to 'working' and 'latest'
    local OLD_VERSION
    append_version_number "${PARAM_OLD_VERSION}" OLD_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi
    local NEW_VERSION
    append_version_number "${PARAM_NEW_VERSION}" NEW_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # get old version sources
    get_sources "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" "${OLD_VERSION}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # get new version release
    get_release "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" "${NEW_VERSION}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    local SUMMARY=()
    compatibility_check "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" \
                        "${OLD_VERSION}" "${NEW_VERSION}" PARAM_PACKAGES_ARRAY[@]
    return $?
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
