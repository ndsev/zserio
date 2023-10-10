#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Get name of the branch with old Zerio version.
get_old_version_branch()
{
    exit_if_argc_ne $# 2
    local PARAM_OLD_VERSION_BRANCH="$1"; shift
    local OLD_VERSION_BRANCH_OUT="$1"; shift

    if [[ "${PARAM_OLD_VERSION_BRANCH}" != "" ]] ; then
        local BRANCH_NAME="${PARAM_OLD_VERSION_BRANCH}"
    else
        local DEFAULT_OLD_VERSION_BRANCH="compat-api"
        git ls-remote --exit-code --heads origin refs/heads/${DEFAULT_OLD_VERSION_BRANCH} >/dev/null
        if [ $? -eq 0 ] ; then
           local BRANCH_NAME="${DEFAULT_OLD_VERSION_BRANCH}"
        else
           local LATEST_VERSION
            get_latest_zserio_version LATEST_VERSION
            if [ $? -ne 0 ] ; then
                return 1
            fi
            local BRANCH_NAME="v${LATEST_VERSION}"
        fi
    fi

    eval ${OLD_VERSION_BRANCH_OUT}="'${BRANCH_NAME}'"

    return 0
}

# Get sources of the old version.
get_old_sources()
{
    exit_if_argc_ne $# 2
    local OLD_VERSION_DIR="$1"; shift
    local OLD_VERSION_BRANCH="$1"; shift

    rm -rf "${OLD_VERSION_DIR}"
    mkdir "${OLD_VERSION_DIR}"
    git clone --depth=1 --branch="${OLD_VERSION_BRANCH}" --recurse-submodules \
            https://github.com/ndsev/zserio "${OLD_VERSION_DIR}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to clone zserio tag \"${TAG_NAME}\"!"
        return 1
    fi
    echo

    return 0
}

# Copy new release into directory with old sources.
copy_new_release()
{
    exit_if_argc_ne $# 2
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local OLD_VERSION_DIR="$1"; shift

    local OLD_VERSION_NUMBER
    get_zserio_version "${OLD_VERSION_DIR}" OLD_VERSION_NUMBER
    if [ $? -ne 0 ] ; then
        return 1
    fi

    local NEW_VERSION_NUMBER
    get_zserio_version "${ZSERIO_PROJECT_ROOT}" NEW_VERSION_NUMBER
    if [ $? -ne 0 ] ; then
        return 1
    fi

    local NEW_VERSION_RELEASE_DIR="${ZSERIO_PROJECT_ROOT}/release-${NEW_VERSION_NUMBER}"
    local OLD_VERSION_RELEASE_DIR="${OLD_VERSION_DIR}/release-${OLD_VERSION_NUMBER}"
    mkdir "${OLD_VERSION_RELEASE_DIR}"
    cp "${NEW_VERSION_RELEASE_DIR}/zserio-${NEW_VERSION_NUMBER}-bin.zip" \
            "${OLD_VERSION_RELEASE_DIR}/zserio-${OLD_VERSION_NUMBER}-bin.zip"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    cp "${NEW_VERSION_RELEASE_DIR}/zserio-${NEW_VERSION_NUMBER}-runtime-libs.zip" \
            "${OLD_VERSION_RELEASE_DIR}/zserio-${OLD_VERSION_NUMBER}-runtime-libs.zip"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

# Run test from older version with release from newer version to check generated API compatibility.
compatibility_check()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local OLD_VERSION_DIR="$1"; shift
    local OLD_VERSION_BRANCH="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PACKAGES_ARRAY=("${MSYS_WORKAROUND_TEMP[@]}")

    local MESSAGE="Run API compatibility check using branch ${OLD_VERSION_BRANCH} with working release"
    echo "STARTING - ${MESSAGE}"

    rm -rf "${OLD_VERSION_DIR}/build"
    "${OLD_VERSION_DIR}/scripts/test.sh" "${PACKAGES_ARRAY[@]}"
    TEST_RESULT=$?
    if [ ${TEST_RESULT} -ne 0 ] ; then
        stderr_echo "FAILED - ${MESSAGE}"
        return 1
    fi

    echo "FINISHED - ${MESSAGE}"

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs Zserio tests from older version with release from newer version to check generated API compatibility.

Usage:
    $0 [-h] [-e] [-p] [-o <dir>] [-b <branch>] package...

Arguments:
    -h, --help            Show this help.
    -e, --help-env        Show help for enviroment variables.
    -p, --purge           Purge testing directory.
    -o <dir>, --output-directory <dir>
                          Output directory where tests will be run.
    -b <branch>, --old-version-branch <branch>
                          Specify git branch with the old version to test. If not specified, branch 'compat-api'
                          is used. If not specified and branch 'compat-api' does not exist,
                          the latest release from GitHub is used.

Packages:
    All packages available in zserio tests.

Examples:
    $0 all-linux64-clang
    $0 java --old-version-branch test-compat-api

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
    local PARAM_OLD_VERSION_BRANCH_OUT="$1"; shift
    local PARAM_PACKAGES_ARRAY_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

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

            "-b" | "--old-version-branch")
                eval ${PARAM_OLD_VERSION_BRANCH_OUT}="$2"
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

    # parse command line arguments
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local PARAM_OLD_VERSION_BRANCH=""
    local PARAM_PACKAGES_ARRAY=()
    local SWITCH_PURGE
    parse_arguments PARAM_OUT_DIR PARAM_OLD_VERSION_BRANCH PARAM_PACKAGES_ARRAY SWITCH_PURGE "$@"
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

    # detect old version branch
    local OLD_VERSION_BRANCH
    get_old_version_branch "${PARAM_OLD_VERSION_BRANCH}" OLD_VERSION_BRANCH
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # get old version sources
    local OLD_VERSION_DIR="${TEST_OUT_DIR}/${OLD_VERSION_BRANCH}"
    get_old_sources "${OLD_VERSION_DIR}" "${OLD_VERSION_BRANCH}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # copy new release
    copy_new_release "${ZSERIO_PROJECT_ROOT}" "${OLD_VERSION_DIR}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # run tests
    compatibility_check "${ZSERIO_PROJECT_ROOT}" "${OLD_VERSION_DIR}" "${OLD_VERSION_BRANCH}" \
            PARAM_PACKAGES_ARRAY[@]

    return $?
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
