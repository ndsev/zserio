#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Update Zserio version in a single file.
update_version_in_file()
{
    exit_if_argc_ne $# 2
    local VERSION_FILE="${1}"; shift
    local NEW_VERSION_STRING="${1}"; shift

    echo -ne "Updating version to ${NEW_VERSION_STRING} in '${VERSION_FILE}'..."
    sed -i -e 's/[0-9]\+\.[0-9]\+\.[0-9]\+\(\-[A-Za-z0-9]\+\)\?/'"${NEW_VERSION_STRING}"'/' "${VERSION_FILE}"
    local SED_RESULT=$?
    if [ ${SED_RESULT} -ne 0 ] ; then
        stderr_echo "Failed with return code ${SED_RESULT}!"
        return 1
    fi
    echo "Done"

    return 0
}

# Update Zserio version in local copy of Git repository.
update_version()
{
    exit_if_argc_ne $# 2
    local ZSERIO_SOURCE_DIR="${1}"; shift
    local NEW_VERSION_STRING="${1}"; shift

    # find all files with version
    local PYTHON_RUNTIME_VERSION_FILE="${ZSERIO_SOURCE_DIR}/extensions/python/runtime/src/zserio/__init__.py"
    local CPP_RUNTIME_DOXYFILE="${ZSERIO_SOURCE_DIR}/extensions/cpp/runtime/doc/doxyfile"
    local FOUND_VERSION_FILES=`${FIND} ${ZSERIO_SOURCE_DIR} -iname "*Version.*"`
    local VERSION_FILES="${FOUND_VERSION_FILES[@]}
                         ${PYTHON_RUNTIME_VERSION_FILE}
                         ${CPP_RUNTIME_DOXYFILE}"
    local COMPATIBILITY_VERSION_FILE="${ZSERIO_SOURCE_DIR}/core/src/zserio/ast/CompatibilityVersion.java"
    local NUM_UPDATED_FILES=0
    for VERSION_FILE in ${VERSION_FILES}
    do
        if [[ "${VERSION_FILE}" != "${COMPATIBILITY_VERSION_FILE}" ]] ; then 
            update_version_in_file "${VERSION_FILE}" "${NEW_VERSION_STRING}"
            if [ $? -ne 0 ] ; then
                return 1
            fi
            NUM_UPDATED_FILES=$((NUM_UPDATED_FILES+1))
        fi
    done
    echo
    echo "Total number of updated files: ${NUM_UPDATED_FILES}"

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Updates Zserio version files in local copy of Git repository.

Usage:
    $0 [-h] new_version

Arguments:
    -h, --help  Show this help.
    new_version New version to use for updating.

Examples:
    $0 1.1.0

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
    exit_if_argc_lt $# 1
    local NEW_VERSION_STRING_OUT="${1}"; shift

    local NUM_PARAMS=0
    local PARAM_ARRAY=();
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-"*)
                stderr_echo "Invalid switch ${ARG}!"
                echo
                return 1
                ;;

            *)
                PARAM_ARRAY[NUM_PARAMS]=${ARG}
                NUM_PARAMS=$((NUM_PARAMS + 1))
                shift
                ;;
        esac
        ARG="$1"
    done

    if [ ${NUM_PARAMS} -ne 1 ] ; then
        stderr_echo "Bad number of specified parameters!"
        echo
        return 1
    fi

    eval ${NEW_VERSION_STRING_OUT}="${PARAM_ARRAY[0]}"

    return 0
}

# Main entry of the script to update zserio version.
main()
{
    echo "Updating Zserio version in local copy of Git repository."
    echo

    # parse command line arguments
    local NEW_VERSION_STRING
    parse_arguments NEW_VERSION_STRING "$@"
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # set global variables if needed
    set_global_common_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # update version
    update_version "${SCRIPT_DIR}/../compiler" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

# call main function
main $@
