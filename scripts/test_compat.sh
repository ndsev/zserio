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
                    git@github.com:ndsev/zserio.git "${TEST_OUT_DIR}/${VERSION}"
            if [ $? -ne 0 ] ; then
                stderr_echo "Failed to clone zserio tag \"${TAG_NAME}\"."
                return 1
            fi
        fi
    fi

    return 0
}

# Download asset from zserio GitHub.
download_zserio_asset()
{
    exit_if_argc_lt $# 2
    local OUT_DIR="$1"; shift
    local ASSET_PATH="$1"; shift
    if [ $# -ne 0 ] ; then
        local NAME="$1"; shift
    else
        local NAME="${ASSET_PATH##*/}"
    fi

    if [ -f ${OUT_DIR}/${NAME} ] ; then
        echo "Asset \"${OUT_DIR}/${NAME}\" already present."
    else
        echo "Downloading https://github.com/ndsev/zserio/releases/download/${ASSET_PATH} as ${NAME}"

        curl -L -s -f "https://github.com/ndsev/zserio/releases/download/${ASSET_PATH}" -o "${OUT_DIR}/${NAME}"
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to download asset \"${ASSET_PATH}\"!"
            return 1
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
        download_zserio_asset "${TEST_OUT_DIR}" "v${VERSION_NUMBER}/zserio-${VERSION_NUMBER}-bin.zip" \
                "zserio-${VERSION}-bin.zip"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        download_zserio_asset "${TEST_OUT_DIR}" "v${VERSION_NUMBER}/zserio-${VERSION_NUMBER}-runtime-libs.zip" \
                "zserio-${VERSION}-runtime-libs.zip"
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

# Compare two versions using test.sh script from the left-hand side version. Compare also produced BLOBs.
#
# Return codes:
# -------------
# 0 - Success.
# 1 - Failure - unknown reason.
# 2 - Failure - test.sh failed.
# 3 - Failure - compare_test_blobs.sh failed.
compare_versions()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local LHS_VERSION="$1"; shift
    local RHS_VERSION="$1"; shift

    local MESSAGE="Compare ${LHS_VERSION} <-> ${RHS_VERSION}"
    echo "STARTING - ${MESSAGE}"

    if [[ "${LHS_VERSION}" == "latest-"* || "${LHS_VERSION}" == "working-"* ]] ; then
        local LHS_VERSION_NUMBER=${LHS_VERSION#*-}
    else
        local LHS_VERSION_NUMBER=${LHS_VERSION}
    fi

    rm -rf "${TEST_OUT_DIR}/${LHS_VERSION}/build"*

    # test LHS_VERSION with its own release
    copy_release "${TEST_OUT_DIR}" ${LHS_VERSION} ${LHS_VERSION} ${LHS_VERSION_NUMBER}
    if [ $? -ne 0 ] ; then
        return 1
    fi
    "${TEST_OUT_DIR}/${LHS_VERSION}/scripts/test.sh" java -t language/*
    local TEST_RESULT=$?
    mv "${TEST_OUT_DIR}/${LHS_VERSION}/build" "${TEST_OUT_DIR}/${LHS_VERSION}/build-${LHS_VERSION}"
    if [ ${TEST_RESULT} -ne 0 ] ; then
        return 2
    fi

    # test LHS_VERSION with RHS_VERSION release
    copy_release "${TEST_OUT_DIR}" ${LHS_VERSION} ${RHS_VERSION} ${LHS_VERSION_NUMBER}
    if [ $? -ne 0 ] ; then
        return 1
    fi
    "${TEST_OUT_DIR}/${LHS_VERSION}/scripts/test.sh" java -t language/*
    TEST_RESULT=$?
    mv "${TEST_OUT_DIR}/${LHS_VERSION}/build" "${TEST_OUT_DIR}/${LHS_VERSION}/build-${RHS_VERSION}"
    if [ ${TEST_RESULT} -ne 0 ] ; then
        return 2
    fi

    # compare BLOBs generated by each version
    "${ZSERIO_PROJECT_ROOT}/scripts/compare_test_blobs.sh" \
            "${TEST_OUT_DIR}/${LHS_VERSION}/build-${LHS_VERSION}" \
            "${TEST_OUT_DIR}/${LHS_VERSION}/build-${RHS_VERSION}"
    if [ $? -ne 0 ] ; then
        return 3
    fi

    echo "FINISHED - ${MESSAGE}"
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs Zserio tests on requested Zserio releases and compares produced BLOBs to check binary compatibility.

Usage:
    $0 [-h] [-e] [-p] [-o <dir>] version...

Arguments:
    -h, --help            Show this help.
    -e, --help-env        Show help for enviroment variables.
    -p, --purge           Purge testing directory.
    -o <dir>, --output-directory <dir>
                          Output directory where tests will be run.
    version               Specify the version(s) to test.
                          Use 'working' for release in working directory.
                          Use 'latest' for latest release on GitHub.
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
    local PARAM_VERSIONS_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${SWITCH_PURGE_OUT}=0

    local NUM_VERSIONS=0
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
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

            "-"*)
                stderr_echo "Invalid switch ${ARG}!"
                echo
                return 1
                ;;

            *)
                eval ${PARAM_VERSIONS_OUT}[NUM_VERSIONS]="${ARG}"
                NUM_VERSIONS=$((NUM_VERSIONS + 1))
                shift
                ;;
        esac
        ARG="$1"
    done

    if [ ${NUM_VERSIONS} -eq 0 ] ; then
        eval ${PARAM_VERSIONS_OUT}[NUM_VERSIONS]="latest"
        NUM_VERSIONS=$((NUM_VERSIONS + 1))
        echo "Adding 'latest' release to comparison."
    fi
    if [ ${NUM_VERSIONS} -eq 1 ] ; then
        eval ${PARAM_VERSIONS_OUT}[NUM_VERSIONS]="working"
        NUM_VERSIONS=$((NUM_VERSIONS + 1))
        echo "Adding 'working' release to comparison."
    fi
}

main()
{
    # get the project root
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local PARAM_VERSIONS=()
    local SWITCH_PURGE
    parse_arguments PARAM_OUT_DIR PARAM_VERSIONS SWITCH_PURGE "$@"
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
    for i in ${!PARAM_VERSIONS[@]} ; do
        if [[ ${PARAM_VERSIONS[$i]} == "working" ]] ; then
            local WORKING_VERSION
            get_zserio_version "${ZSERIO_PROJECT_ROOT}" WORKING_VERSION
            PARAM_VERSIONS[$i]="working-${WORKING_VERSION}"
        elif [[ ${PARAM_VERSIONS[$i]} == "latest" ]] ; then
            local LATEST_VERSION
            get_latest_zserio_version LATEST_VERSION
            if [ $? -ne 0 ] ; then
                return 1
            fi
            PARAM_VERSIONS[$i]="latest-${LATEST_VERSION}"
        fi
    done

    # get sources
    for VERSION in ${PARAM_VERSIONS[@]:0:$((${#PARAM_VERSIONS[@]}-1))} ; do # all except the last
        get_sources "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" "${VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    done

    # get releases
    for VERSION in ${PARAM_VERSIONS[@]} ; do
        get_release "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" "${VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    done

    local i=0
    local j=1
    local SUMMARY=()
    local RESULT=0
    while [ $j -lt ${#PARAM_VERSIONS[@]} ] ; do
        compare_versions "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}" \
                "${PARAM_VERSIONS[$i]}" "${PARAM_VERSIONS[$j]}"
        local COMPARE_RESULT=$?
        if [ ${COMPARE_RESULT} -ne 0  ] ; then
            if [ ${COMPARE_RESULT} -eq 2 ] ; then
                local ERROR="tests"
            elif [ ${COMPARE_RESULT} -eq 3 ] ; then
                local ERROR="blob comparison"
            else
                local ERROR="unknown error"
            fi
            SUMMARY[$i]="${PARAM_VERSIONS[$i]} <-> ${PARAM_VERSIONS[$j]}:\033[40GFAILED (${ERROR})!"
            RESULT=1
        else
            SUMMARY[$i]="${PARAM_VERSIONS[$i]} <-> ${PARAM_VERSIONS[$j]}:\033[40GOK"
        fi
        i=$((i+1))
        j=$((j+1))
    done

    echo "Compatibility check overview:"
    for SUM in "${SUMMARY[@]}" ; do
        echo -e "$SUM"
    done

    if [ ${RESULT} -ne 0 ] ; then
        stderr_echo "Compatibility check failed!"
        return 1
    fi

    return 0
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
