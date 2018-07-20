#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check release global variables.
#
# $1 - Set to 1 if "-t" argument is present.
set_release_global_variables()
{
    exit_if_argc_ne $# 1
    local SWITCH_TAG_ONLY="$1"

    if [[ ${SWITCH_TAG_ONLY} == 1 ]] ; then
        # GIT to use, defaults to "git" if not set
        GIT="${GIT:-git}"
        if [ ! -f "`which "${GIT}"`" ] ; then
            stderr_echo "Cannot find git! Set GIT environment variable."
            return 1
        fi
    else
        # ZIP to use, defaults to "zip" if not set
        ZIP="${ZIP:-zip}"
        if [ ! -f "`which "${ZIP}"`" ] ; then
            stderr_echo "Cannot find zip! Set ZIP environment variable."
            return 1
        fi
    fi

    return 0
}

# Print help on the environment variables used for this release script.
print_release_help_env()
{
    cat << EOF
Uses the following environment variables for releasing:
    ZIP Zip executable to use. Default is "zip".
    GIT Git executable to use. Default is "git".
EOF
}

# Tag release in Git.
#
# $1 - The release version.
make_git_tag()
{
    exit_if_argc_ne $# 1
    local RELEASE_VERSION="$1"

    echo -ne "Tagging release in Git..."
    "${GIT}" tag -a -m "Release Zserio-${RELEASE_VERSION}." Zserio-${RELEASE_VERSION}
    if [ $? -ne 0 ] ; then
        stderr_echo "Git tagging failure!"
        return 1
    fi
    echo "Done"

    return 0
}

# Assemble Zserio release ZIP file.
#
# This requires the Zserio tool to be already built (by build.sh).
#
# $1 - The name of the release.
# $2 - The release directory.
# $3 - Name of the directory where the release is to be created.
make_release()
{
    exit_if_argc_ne $# 3
    local ZSERIO_VERSION="$1"
    local ZSERIO_RELEASE_SRC_DIR="$2"
    local ZSERIO_RELEASE_ZIP_DIR="$3"

    echo -ne "Creating release ${ZSERIO_VERSION}..."
    rm -rf "${ZSERIO_RELEASE_ZIP_DIR}"
    mkdir -p "${ZSERIO_RELEASE_ZIP_DIR}"

    # create zips
    pushd "${ZSERIO_RELEASE_SRC_DIR}" > /dev/null

    # create zip: jar
    "${ZIP}" -rq "${ZSERIO_RELEASE_ZIP_DIR}/zserio-${ZSERIO_VERSION}-bin.zip" "ant_task" "zserio_libs" "zserio.jar"
    if [ $? -ne 0 ] ; then
        stderr_echo "Can't zip Zserio release (bin)."
        return 1
    fi

    # create zip: runtime-libs
    "${ZIP}" -rq "${ZSERIO_RELEASE_ZIP_DIR}/zserio-${ZSERIO_VERSION}-runtime-libs.zip" "runtime_libs"
    if [ $? -ne 0 ] ; then
        stderr_echo "Can't zip Zserio release (runtime_libs)."
        return 1
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
    $0 [-h] [-t]

Arguments:
    -h, --help     Show this help.
    -t, --tag-only Don't release just tag Zserio trunk in Git.

Examples:
    $0

EOF

    print_release_help_env
}

# Parse all command line arguments.
#
# Parameters:
# -----------
# $1 - The name of variable to set to 1 if "-t" argument is present.
# $@ - The command line arguments to parse.
#
# Usage:
# ------
# local PARAM
# local SWITCH
# parse_arguments PARAM SWITCH $@
# if [[ ${SWITCH} == 1 ]] ; then
#     SWITCH has been present, do something
# fi
#
# Return codes:
# -------------
# 0 - Success. Arguments have been successfully parsed.
# 1 - Failure. Some arguments are wrong or missing.
# 2 - Help switch is present. Arguments after help switch have not been checked.
parse_arguments()
{
    local NUM_OF_ARGS=1
    exit_if_argc_lt $# ${NUM_OF_ARGS}
    local SWITCH_TAG_ONLY_OUT="$1"
    shift ${NUM_OF_ARGS}

    eval ${SWITCH_TAG_ONLY_OUT}=0

    local NUM_PARAMS=0
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-t" | "--tag-only")
                eval ${SWITCH_TAG_ONLY_OUT}=1
                shift
                ;;

            "-"*)
                stderr_echo "Invalid switch ${ARG}!"
                echo
                return 1
                ;;

            *)
                stderr_echo "Invalid parameter ${ARG}!"
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
    echo "Releasing Zserio binaries."
    echo

    # parse command line arguments
    local SWITCH_TAG_ONLY
    parse_arguments SWITCH_TAG_ONLY $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # get the project root (the absolute path is necessary for zip)
    local ZSERIO_PROJECT_ROOT="${SCRIPT_DIR}/.."
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # set global variables
    set_release_global_variables ${SWITCH_TAG_ONLY}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # get Zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [[ ${SWITCH_TAG_ONLY} == 1 ]] ; then
        echo "The release version: ${ZSERIO_VERSION}"
        echo
        make_git_tag "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    else
        local ZSERIO_DISTR_DIR="${ZSERIO_PROJECT_ROOT}/distr"

        # create empty release directory
        rm -rf "${ZSERIO_RELEASE_DIR}"
        mkdir -p "${ZSERIO_RELEASE_DIR}"

        echo "The release source directory: ${ZSERIO_DISTR_DIR}"
        echo "The release target directory: ${ZSERIO_RELEASE_DIR}"
        echo
        make_release "${ZSERIO_VERSION}" "${ZSERIO_DISTR_DIR}" "${ZSERIO_RELEASE_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    return 0
}

main "$@"

