#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check global variables.
set_update_version_global_variables()
{
    # bash command find to use, defaults to "/usr/bin/find" if not set
    # (bash command find makes trouble under MinGW because it clashes with Windows find command)
    FIND="${FIND:-/usr/bin/find}"
    if [ ! -f "`which "${FIND}"`" ] ; then
        stderr_echo "Cannot find bash command find! Set FIND environment variable."
        return 1
    fi

    # GIT to use, defaults to "git" if not set
    GIT="${GIT:-git}"
    if [ ! -f "`which "${GIT}"`" ] ; then
        stderr_echo "Cannot find git! Set GIT environment variable."
        return 1
    fi

    return 0
}

# Print help on the environment variables used for this script.
print_update_version_help_env()
{
    cat << EOF
Uses the following environment variables:
    GIT      Git executable to use. Default is "git".

    Either set these directly, or create 'scripts/build-env.sh' that sets these.
    It's sourced automatically if it exists.

EOF
}

# Update version number in a single file.
update_version_number_in_file()
{
    exit_if_argc_ne $# 4
    local VERSION_FILE="$1"; shift
    local VERSION_DESC="$1"; shift
    local OLD_VERSION_STRING="$1"; shift
    local NEW_VERSION_STRING="$1"; shift

    # calculate version numbers from string
    local OLD_VERSION_ARRAY=(${OLD_VERSION_STRING//./ })
    local OLD_VERSION_NUMBER=$(printf "%d%03d%03d" ${OLD_VERSION_ARRAY[0]} ${OLD_VERSION_ARRAY[1]} \
            ${OLD_VERSION_ARRAY[2]})
    local NEW_VERSION_ARRAY=(${NEW_VERSION_STRING//./ })
    local NEW_VERSION_NUMBER=$(printf "%d%03d%03d" ${NEW_VERSION_ARRAY[0]} ${NEW_VERSION_ARRAY[1]} \
            ${NEW_VERSION_ARRAY[2]})

    local SED_COMMAND="s|${VERSION_DESC} \(= \)\{0,1\}${OLD_VERSION_NUMBER}|${VERSION_DESC} \1${NEW_VERSION_NUMBER}|"
    update_version_string_in_file "${VERSION_FILE}" "${SED_COMMAND}" "${OLD_VERSION_STRING}" \
            "${NEW_VERSION_STRING}"
}

# Update version in a single document file.
update_version_string_in_doc_file()
{
    exit_if_argc_ne $# 4
    local VERSION_FILE="$1"; shift
    local VERSION_DESC="$1"; shift
    local OLD_VERSION_STRING="$1"; shift
    local NEW_VERSION_STRING="$1"; shift

    local SED_COMMAND="s|${VERSION_DESC} ${OLD_VERSION_STRING}|${VERSION_DESC} ${NEW_VERSION_STRING}|"
    update_version_string_in_file "${VERSION_FILE}" "${SED_COMMAND}" "${OLD_VERSION_STRING}" \
            "${NEW_VERSION_STRING}"
}

# Update version in a single source file.
update_version_string_in_src_file()
{
    exit_if_argc_ne $# 4
    local VERSION_FILE="$1"; shift
    local VERSION_DESC="$1"; shift
    local OLD_VERSION_STRING="$1"; shift
    local NEW_VERSION_STRING="$1"; shift

    local SED_COMMAND="s|${VERSION_DESC} = \"${OLD_VERSION_STRING}\"|${VERSION_DESC} = \"${NEW_VERSION_STRING}\"|"
    update_version_string_in_file "${VERSION_FILE}" "${SED_COMMAND}" "${OLD_VERSION_STRING}" \
            "${NEW_VERSION_STRING}"
}

# Update version in a single file.
update_version_string_in_file()
{
    exit_if_argc_ne $# 4
    local VERSION_FILE="$1"; shift
    local SED_COMMAND="$1"; shift
    local OLD_VERSION_STRING="$1"; shift
    local NEW_VERSION_STRING="$1"; shift

    echo -ne "Updating version from ${OLD_VERSION_STRING} to ${NEW_VERSION_STRING} in '${VERSION_FILE}'..."
    sed -i -e "${SED_COMMAND}" "${VERSION_FILE}"
    local SED_RESULT=$?
    if [ ${SED_RESULT} -ne 0 ] ; then
        stderr_echo "Failed with return code ${SED_RESULT}!"
        return 1
    fi
    echo "Done"

    return 0
}

# Commit updated files into the Git repository.
commit_updated_files()
{
    exit_if_argc_ne $# 4
    local VERSION_FILES="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local VERSION_DESC="$1"; shift

    if [[ ${SWITCH_COMMIT} == 1 ]] ; then
        "${GIT}" diff --exit-code ${VERSION_FILES} > /dev/null
        if [ $? -ne 0 ] ; then
            echo -ne "Committing updated version files..."
            "${GIT}" commit -m "Update ${VERSION_DESC} version to ${NEW_VERSION_STRING}" -q ${VERSION_FILES}
            local GIT_RESULT=$?
            if [ ${GIT_RESULT} -ne 0 ] ; then
                stderr_echo "Git failed with return code ${GIT_RESULT}!"
                return 1
            fi
            echo "Done"
        fi
    fi

    return 0
}

# Update Zserio core version in local copy of Git repository.
update_core_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local ZSERIO_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/core/src/zserio/tools/ZserioVersion.java"
    local FOUND_EXTENSION_VERSION_FILES=`${FIND} ${ZSERIO_PROJECT_ROOT}/compiler -iname "*ExtensionVersion.*"`
    local VERSION_FILES="${ZSERIO_VERSION_FILE}
                         ${FOUND_EXTENSION_VERSION_FILES[@]}"

    # get old version string
    local OLD_VERSION_STRING
    get_zserio_version "${ZSERIO_PROJECT_ROOT}" OLD_VERSION_STRING
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # update version in files
    local NUM_FILES=0
    for VERSION_FILE in ${VERSION_FILES}
    do
        update_version_string_in_src_file "${VERSION_FILE}" "VERSION_STRING" "${OLD_VERSION_STRING}" \
                "${NEW_VERSION_STRING}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        NUM_FILES=$((NUM_FILES+1))
    done

    # commit updated version files if it is requested
    commit_updated_files "${VERSION_FILES}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} "core"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="'${NUM_FILES}'"

    return 0
}

# Update Zserio BIN version in local copy of Git repository.
update_bin_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local ENCODING_GUIDE_DOC="${ZSERIO_PROJECT_ROOT}/doc/ZserioEncodingGuide.md"
    local CPP_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/src/zserio/extension/cpp/CppExtensionVersion.java"
    local JAVA_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/src/zserio/extension/java/JavaExtensionVersion.java"
    local PYTHON_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/src/zserio/extension/python/PythonExtensionVersion.java"
    local VERSION_FILES="${CPP_EXTENSION_VERSION_FILE}
                         ${JAVA_EXTENSION_VERSION_FILE}
                         ${PYTHON_EXTENSION_VERSION_FILE}"

    # get old version string
    local OLD_VERSION_STRING=`grep -r '# Zserio Encoding Guide' "${ENCODING_GUIDE_DOC}" | cut -d' ' -f5`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep Zserio BIN version!"
        return 1
    fi

    # update version in files
    update_version_string_in_doc_file "${ENCODING_GUIDE_DOC}" "Zserio Encoding Guide" "${OLD_VERSION_STRING}" \
            "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    local NUM_FILES=1
    for VERSION_FILE in ${VERSION_FILES}
    do
        update_version_string_in_src_file "${VERSION_FILE}" "BIN_VERSION_STRING" "${OLD_VERSION_STRING}" \
                "${NEW_VERSION_STRING}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        NUM_FILES=$((NUM_FILES+1))
    done

    # commit updated version files if it is requested
    commit_updated_files "${VERSION_FILES}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} "BIN"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="'${NUM_FILES}'"

    return 0
}

# Update Zserio JSON version in local copy of Git repository.
update_json_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local JSON_GUIDE_DOC="${ZSERIO_PROJECT_ROOT}/doc/ZserioJsonGuide.md"
    local CPP_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/src/zserio/extension/cpp/CppExtensionVersion.java"
    local JAVA_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/src/zserio/extension/java/JavaExtensionVersion.java"
    local PYTHON_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/src/zserio/extension/python/PythonExtensionVersion.java"
    local VERSION_FILES="${CPP_EXTENSION_VERSION_FILE}
                         ${JAVA_EXTENSION_VERSION_FILE}
                         ${PYTHON_EXTENSION_VERSION_FILE}"

    # get old version string
    local OLD_VERSION_STRING=`grep -r '# Zserio JSON Guide' "${JSON_GUIDE_DOC}" | cut -d' ' -f5`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep Zserio JSON version!"
        return 1
    fi

    # update version in files
    update_version_string_in_doc_file "${JSON_GUIDE_DOC}" "Zserio JSON Guide" "${OLD_VERSION_STRING}" \
            "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    local NUM_FILES=1
    for VERSION_FILE in ${VERSION_FILES}
    do
        update_version_string_in_src_file "${VERSION_FILE}" "JSON_VERSION_STRING" "${OLD_VERSION_STRING}" \
                "${NEW_VERSION_STRING}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        NUM_FILES=$((NUM_FILES+1))
    done

    # commit updated version files if it is requested
    commit_updated_files "${VERSION_FILES}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} "JSON"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="'${NUM_FILES}'"

    return 0
}

# Update C++ extension version in local copy of Git repository.
update_cpp_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local CPP_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/src/zserio/extension/cpp/CppExtensionVersion.java"
    local CPP_RUNTIME_VERSION="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/runtime/src/zserio/CppRuntimeVersion.h"

    # get old version string
    local OLD_VERSION_STRING=`
            grep -r CPP_EXTENSION_VERSION_STRING "${CPP_EXTENSION_VERSION_FILE}" | cut -d'"' -f2`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep C++ extension version!"
        return 1
    fi

    # update version in files
    update_version_string_in_src_file "${CPP_EXTENSION_VERSION_FILE}" "CPP_EXTENSION_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    update_version_number_in_file "${CPP_EXTENSION_VERSION_FILE}" "CPP_EXTENSION_VERSION_NUMBER" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    update_version_string_in_src_file "${CPP_RUNTIME_VERSION}" "CPP_EXTENSION_RUNTIME_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    update_version_number_in_file "${CPP_RUNTIME_VERSION}" "CPP_EXTENSION_RUNTIME_VERSION_NUMBER" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # commit updated version files if it is requested
    commit_updated_files "${CPP_EXTENSION_VERSION_FILE} ${CPP_RUNTIME_VERSION}" "${NEW_VERSION_STRING}" \
            ${SWITCH_COMMIT} "C++ extension"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="2"

    return 0
}

# Update Java extension version in local copy of Git repository.
update_java_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local JAVA_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/src/zserio/extension/java/JavaExtensionVersion.java"
    local JAVA_RUNTIME_VERSION="${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/runtime/src/zserio/runtime/JavaRuntimeVersion.java"

    # get old version string
    local OLD_VERSION_STRING=`
            grep -r JAVA_EXTENSION_VERSION_STRING "${JAVA_EXTENSION_VERSION_FILE}" | cut -d'"' -f2`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep Java extension version!"
        return 1
    fi

    # update version in files
    update_version_string_in_src_file "${JAVA_EXTENSION_VERSION_FILE}" "JAVA_EXTENSION_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    update_version_string_in_src_file "${JAVA_RUNTIME_VERSION}" "JAVA_EXTENSION_RUNTIME_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # commit updated version files if it is requested
    commit_updated_files "${JAVA_EXTENSION_VERSION_FILE} ${JAVA_RUNTIME_VERSION}" "${NEW_VERSION_STRING}" \
            ${SWITCH_COMMIT} "Java extension"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="2"

    return 0
}

# Update Python extension version in local copy of Git repository.
update_python_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local PYTHON_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/src/zserio/extension/python/PythonExtensionVersion.java"
    local PYTHON_RUNTIME_VERSION="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/runtime/src/zserio/__init__.py"

    # get old version string
    local OLD_VERSION_STRING=`
            grep -r PYTHON_EXTENSION_VERSION_STRING "${PYTHON_EXTENSION_VERSION_FILE}" | cut -d'"' -f2`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep Python extension version!"
        return 1
    fi

    # update version in files
    update_version_string_in_src_file "${PYTHON_EXTENSION_VERSION_FILE}" "PYTHON_EXTENSION_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    update_version_string_in_src_file "${PYTHON_RUNTIME_VERSION}" "PYTHON_EXTENSION_RUNTIME_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # commit updated version files if it is requested
    commit_updated_files "${PYTHON_EXTENSION_VERSION_FILE} ${PYTHON_RUNTIME_VERSION}" "${NEW_VERSION_STRING}" \
            ${SWITCH_COMMIT} "Python extension"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="2"

    return 0
}

# Update documentation extension version in local copy of Git repository.
update_doc_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local DOC_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/doc/src/zserio/extension/doc/DocExtensionVersion.java"

    # get old version string
    local OLD_VERSION_STRING=`
            grep -r DOC_EXTENSION_VERSION_STRING "${DOC_EXTENSION_VERSION_FILE}" | cut -d'"' -f2`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep documentation extension version!"
        return 1
    fi

    # update version
    update_version_string_in_src_file "${DOC_EXTENSION_VERSION_FILE}" "DOC_EXTENSION_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # commit updated version files if it is requested
    commit_updated_files "${DOC_EXTENSION_VERSION_FILE}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
            "Doc extension"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="1"

    return 0
}

# Update documentation extension version in local copy of Git repository.
update_xml_version()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local NEW_VERSION_STRING="$1"; shift
    local SWITCH_COMMIT="$1" ; shift
    local NUM_UPDATED_FILES_OUT="$1"; shift

    # find all files with version
    local XML_EXTENSION_VERSION_FILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/xml/src/zserio/extension/xml/XmlExtensionVersion.java"

    # get old version string
    local OLD_VERSION_STRING=`
            grep -r XML_EXTENSION_VERSION_STRING "${XML_EXTENSION_VERSION_FILE}" | cut -d'"' -f2`
    if [ $? -ne 0 -o -z "${OLD_VERSION_STRING}" ] ; then
        stderr_echo "Failed to grep XML extension version!"
        return 1
    fi

    # update version
    update_version_string_in_src_file "${XML_EXTENSION_VERSION_FILE}" "XML_EXTENSION_VERSION_STRING" \
            "${OLD_VERSION_STRING}" "${NEW_VERSION_STRING}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # commit updated version files if it is requested
    commit_updated_files "${XML_EXTENSION_VERSION_FILE}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
            "Xml extension"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    eval ${NUM_UPDATED_FILES_OUT}="1"

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Updates Zserio version files in local copy of Git repository.

Usage:
    $0 [-h] [-e] [-m] version_type new_version

Arguments:
    -h, --help     Show this help.
    -e, --help-env Show help for enviroment variables.
    -m, --commit   Commit updated version into the Git repository. 
    version_type   Type of the version to update.
    new_version    New version to use for updating.

Type of the version can be one of:
    core           Zserio core version.
    bin            Zserio BIN version.
    json           Zserio JSON version.
    cpp            Zserio C++ generator version.
    java           Zserio Java generator version.
    python         Zserio Python generator version.
    doc            Zserio documentation generator version.
    xml            Zserio XML generator version.

Examples:
    $0 core 2.1.0

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
    exit_if_argc_lt $# 3
    local VERSION_TYPE_OUT="$1"; shift
    local NEW_VERSION_STRING_OUT="$1"; shift
    local SWITCH_COMMIT_OUT="$1"; shift

    eval ${SWITCH_COMMIT_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=();
    local ARG="$1"
    while [ $# -ne 0 ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
                ;;

            "-m" | "--commit")
                eval ${SWITCH_COMMIT_OUT}=1
                shift
                ;;

            "-"*)
                stderr_echo "Invalid switch '${ARG}'!"
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

    if [ ${NUM_PARAMS} -ne 2 ] ; then
        stderr_echo "Bad number of specified parameters!"
        echo
        return 1
    fi

    eval ${VERSION_TYPE_OUT}="${PARAM_ARRAY[0]}"
    eval ${NEW_VERSION_STRING_OUT}="${PARAM_ARRAY[1]}"

    return 0
}

# Main entry of the script to update zserio version.
main()
{
    # parse command line arguments
    local VERSION_TYPE
    local NEW_VERSION_STRING
    local SWITCH_COMMIT
    parse_arguments VERSION_TYPE NEW_VERSION_STRING SWITCH_COMMIT "$@"
    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_update_version_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        return 1
    fi

    # set global variables if needed
    set_update_version_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # update version
    echo "Updating Zserio version files in local copy of Git repository."
    echo
    local ZSERIO_PROJECT_ROOT="${SCRIPT_DIR}/.."
    local NUM_UPDATED_FILES=0
    case "${VERSION_TYPE}" in
        "core")
            update_core_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "bin")
            update_bin_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "json")
            update_json_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "cpp")
            update_cpp_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "java")
            update_java_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "python")
            update_python_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "doc")
            update_doc_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        "xml")
            update_xml_version "${ZSERIO_PROJECT_ROOT}" "${NEW_VERSION_STRING}" ${SWITCH_COMMIT} \
                    NUM_UPDATED_FILES
            ;;

        *)
            stderr_echo "Unknown version type!"
            return 1
            ;;
    esac
    if [ $? -ne 0 ] ; then
        return 1
    fi
    echo
    echo "Total number of updated files: ${NUM_UPDATED_FILES}"

    return 0
}

# call main function
main $@
