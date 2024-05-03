#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check release global variables.
set_release_global_variables()
{
    # ZIP to use, defaults to "zip" if not set
    ZIP="${ZIP:-zip}"
    if [ ! -f "`which "${ZIP}"`" ] ; then
        stderr_echo "Cannot find zip! Set ZIP environment variable."
        return 1
    fi

    return 0
}

# Print help on the environment variables used for this release script.
print_release_help_env()
{
    cat << EOF
Uses the following environment variables for releasing:
    ZIP      Zip executable to use. Default is "zip".

    Either set these directly, or create 'scripts/build-env.sh' that sets these.
    It's sourced automatically if it exists.

EOF
}

# Assemble Zserio release ZIP file.
#
# This requires the Zserio tool to be already built (by build.sh).
make_release()
{
    exit_if_argc_ne $# 4
    local ZSERIO_VERSION="$1"; shift
    local ZSERIO_RELEASE_SRC_DIR="$1"; shift
    local ZSERIO_RELEASE_ZIP_DIR="$1"; shift
    local PACKAGE_NAME="$1"; shift

    echo "The release source directory: ${ZSERIO_RELEASE_SRC_DIR}"
    echo "The release target directory: ${ZSERIO_RELEASE_ZIP_DIR}"
    echo

    echo -ne "Creating release ${ZSERIO_VERSION}..."

    # create zips
    pushd "${ZSERIO_RELEASE_SRC_DIR}" > /dev/null

    # create zip: jar
    if [[ "${PACKAGE_NAME}" == "" || "${PACKAGE_NAME}" == "zserio" ]] ; then
        rm -f "zserio-${ZSERIO_VERSION}-bin.zip"
        "${ZIP}" -rq "${ZSERIO_RELEASE_ZIP_DIR}/zserio-${ZSERIO_VERSION}-bin.zip" "ant_task" "cmake" \
                "zserio_libs" "zserio.jar" "zserio_javadocs.jar" "zserio_sources.jar"
        if [ $? -ne 0 ] ; then
            stderr_echo "Can't zip Zserio release (bin)."
            return 1
        fi
    fi

    # create zip: runtime-libs
    if [[ "${PACKAGE_NAME}" == "" || "${PACKAGE_NAME}" == "runtime_libs" ]] ; then
        rm -f "zserio-${ZSERIO_VERSION}-runtime-libs.zip"
        "${ZIP}" -rq "${ZSERIO_RELEASE_ZIP_DIR}/zserio-${ZSERIO_VERSION}-runtime-libs.zip" "runtime_libs"
        if [ $? -ne 0 ] ; then
            stderr_echo "Can't zip Zserio release (runtime_libs)."
            return 1
        fi
    fi

    popd > /dev/null

    echo "Done"

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Releases Zserio to release-ver directory.

Usage:
    $0 [-h] [-e] [-o <dir>] [-p package]

Arguments:
    -h, --help     Show this help.
    -e, --help-env Show help for enviroment variables.
    -o <dir>, --output-directory <dir>
                   Output directory where build and distr are located.
    -p <name>, --package-name <name>
                   Package name to pack. Can be 'zserio', 'runtime_libs' or remain empty for both.

Examples:
    $0

EOF
}

# Parse all command line arguments.
#
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
# 3 - Environment help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    local NUM_OF_ARGS=2
    exit_if_argc_lt $# ${NUM_OF_ARGS}
    local PARAM_OUT_DIR_OUT="$1"; shift
    local PARAM_PACKAGE_NAME_OUT="$1"; shift

    local ARG="$1"
    while [ $# -ne 0 ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
                ;;

            "-o" | "--output-directory")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing output directory!"
                    echo
                    return 1
                fi
                eval ${PARAM_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-p" | "--package")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing package name!"
                    echo
                    return 1
                fi
                if [[ "$2" != "zserio" && "$2" != "runtime_libs" ]] ; then
                    stderr_echo "Invalid package name!"
                    echo
                    return 1
                fi
                eval ${PARAM_PACKAGE_NAME_OUT}="$2"
                shift 2
                ;;

            "-"*)
                stderr_echo "Invalid switch '${ARG}'!"
                echo
                return 1
                ;;

            *)
                stderr_echo "Invalid parameter '${ARG}'!"
                echo
                return 1
                ;;
        esac
        ARG="$1"
    done

    return 0
}

# Main entry of the script to make Zserio release.
main()
{
    # get the project root
    local ZSERIO_PROJECT_ROOT="${SCRIPT_DIR}/.."

    # parse command line arguments
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local PARAM_PACKAGE_NAME=""
    parse_arguments PARAM_OUT_DIR PARAM_PACKAGE_NAME "$@"
    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_release_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        return 1
    fi

    # get the output directory (the absolute path is necessary for zip)
    convert_to_absolute_path "${PARAM_OUT_DIR}" PARAM_OUT_DIR

    # set global variables
    set_release_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    echo "Releasing Zserio binaries."
    echo

    # get Zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" "${PARAM_OUT_DIR}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # make a release
    mkdir -p "${ZSERIO_RELEASE_DIR}"
    local ZSERIO_DISTR_DIR="${PARAM_OUT_DIR}/distr"
    make_release "${ZSERIO_VERSION}" "${ZSERIO_DISTR_DIR}" "${ZSERIO_RELEASE_DIR}" "${PARAM_PACKAGE_NAME}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

main "$@"
