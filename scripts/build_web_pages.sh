#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check global variables.
set_build_web_pages_global_variables()
{
    # SED to use, defaults to "sed" if not set; GNU version is required
    # (BSD sed silently does something else with the options used here)
    SED="${SED:-sed}"
    if ! "${SED}" --version 2>/dev/null | grep -q "GNU" ; then
        stderr_echo "GNU sed is required, '${SED}' is not GNU sed! Set SED environment variable" \
                "(e.g. SED=gsed on macOS)."
        return 1
    fi

    # UNZIP to use, defaults to "unzip" if not set
    UNZIP="${UNZIP:-unzip}"
    if [ ! -f "`which "${UNZIP}"`" ] ; then
        stderr_echo "Cannot find unzip! Set UNZIP environment variable."
        return 1
    fi

    return 0
}

# Print help on the environment variables used for this script.
print_build_web_pages_help_env()
{
    cat << EOF
Uses the following environment variables for building of Zserio Web Pages:
    SED      GNU sed executable to use. Default is "sed" (on macOS e.g. "gsed").
    UNZIP    Unzip executable to use. Default is "unzip".

    Either set these directly, or create 'scripts/build-env.sh' that sets these.
    It's sourced automatically if it exists.

EOF
}

# Add a new version <option> to the version select of all already published runtime documentations.
patch_old_runtime_doc()
{
    exit_if_argc_ne $# 2
    local ZSERIO_DOC_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local PATTERN="<select id=\"zserio-version-select\""

    local HTML_FILES=($(grep "${PATTERN}" "${ZSERIO_DOC_DIR}" -R -l))
    for HTML_FILE  in "${HTML_FILES[@]}" ; do
        "${SED}" -i '/'"${PATTERN}"'/a <option value="'"${ZSERIO_VERSION}"'">'"${ZSERIO_VERSION}"'</option>' ${HTML_FILE}
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to append the new version <option>!"
            return 1
        fi
    done

    return 0
}

# Replace the version in the new runtime documentation by the select of all published versions.
patch_new_runtime_doc()
{
    exit_if_argc_ne $# 3
    local ZSERIO_OLD_DOC_DIR="$1"; shift
    local ZSERIO_NEW_DOC_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local ZSERIO_VERSION_SELECT="\n\
<select id=\"zserio-version-select\" style=\"font-size: 100%; margin-bottom: 1px; padding: 2px;\"\
 onChange=\"(function(value){ var url = top.document.URL.split('\/'); url[url.length-3] = \`\${value}\`;\
 top.location.href=url.join('\/'); \
})(value)\">\n\
<option value=\"${ZSERIO_VERSION}\" selected>${ZSERIO_VERSION}<\/option>\n\
"
    local OLD_VERSIONS=($(ls -1 "${ZSERIO_OLD_DOC_DIR}" | sort -rV))
    for OLD_VERSION in ${OLD_VERSIONS[@]}; do
        ZSERIO_VERSION_SELECT+="<option value=\"${OLD_VERSION}\">${OLD_VERSION}<\/option>\n"
    done
    ZSERIO_VERSION_SELECT+="<\/select>\n"

    local GREP_INCLUDE=(--include "index.html" --include "zserio.html" --include "overview-summary.html")
    local HTML_FILES=($(grep "Built for Zserio" "${ZSERIO_NEW_DOC_DIR}" -R -l ${GREP_INCLUDE[@]}))
    for HTML_FILE  in "${HTML_FILES[@]}" ; do
        "${SED}" -i 's/\(Built for Zserio\)\s*[a-zA-Z0-9.-]*/\1'"${ZSERIO_VERSION_SELECT}"'/' "${HTML_FILE}"
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to apply zserio-version-select!"
            return 1
        fi
    done

    return 0
}

# Create JSON configuration files for all GitHub badges
create_github_badge_jsons()
{
    exit_if_argc_ne $# 2
    local ZSERIO_RUNTIME_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    local CLANG_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/cpp/coverage/clang
    local CLANG_LINES_COVERAGE=`cat "${CLANG_COVERAGE_DIR}"/coverage_report.txt | grep TOTAL | \
            tr -s ' ' | cut -d' ' -f 10`
    create_github_badge_json "${CLANG_COVERAGE_DIR}"/coverage_github_badge.json \
            "C++ clang runtime ${ZSERIO_VERSION} coverage" "${CLANG_LINES_COVERAGE}"

    local JAVA_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/java/coverage
    local JAVA_COVERAGE_REPORT=`cat "${JAVA_COVERAGE_DIR}"/jacoco_report.xml`
    local JAVA_LINES_MISSED=`echo ${JAVA_COVERAGE_REPORT##*INSTRUCTION} | cut -d'"' -f3`
    local JAVA_LINES_COVERED=`echo ${JAVA_COVERAGE_REPORT##*INSTRUCTION} | cut -d'"' -f5`
    local JAVA_LINES_VALID=$((${JAVA_LINES_COVERED} - ${JAVA_LINES_MISSED}))
    local JAVA_LINES_COVERAGE=$((10000 * ${JAVA_LINES_VALID} / ${JAVA_LINES_COVERED}))
    create_github_badge_json "${JAVA_COVERAGE_DIR}"/coverage_github_badge.json \
            "Java runtime ${ZSERIO_VERSION} coverage" \
            "${JAVA_LINES_COVERAGE:0:-2}.${JAVA_LINES_COVERAGE: -2}%"

    local PYTHON_COVERAGE_DIR="${ZSERIO_RUNTIME_DIR}"/python/coverage
    local PYTHON_LINES_VALID=`cat "${PYTHON_COVERAGE_DIR}"/coverage_report.xml | grep lines-covered | \
            cut -d' ' -f 4 | cut -d= -f2 | tr -d \"`
    local PYTHON_LINES_COVERED=`cat "${PYTHON_COVERAGE_DIR}"/coverage_report.xml | grep lines-covered | \
            cut -d' ' -f 5 | cut -d= -f2 | tr -d \"`
    local PYTHON_LINES_COVERAGE=$((10000 * ${PYTHON_LINES_VALID} / ${PYTHON_LINES_COVERED}))
    create_github_badge_json "${PYTHON_COVERAGE_DIR}"/coverage_github_badge.json \
            "Python runtime ${ZSERIO_VERSION} coverage" \
            "${PYTHON_LINES_COVERAGE:0:-2}.${PYTHON_LINES_COVERAGE: -2}%"
}

# Create JSON configuration file for one GitHub badge
create_github_badge_json()
{
    exit_if_argc_ne $# 3
    local BADGE_JSON_FILE="$1"; shift
    local BADGE_LABEL="$1"; shift
    local BADGE_MESSAGE="$1"; shift

    cat > "${BADGE_JSON_FILE}" << EOF
{
    "schemaVersion": 1,
    "label": "${BADGE_LABEL}",
    "message": "${BADGE_MESSAGE}",
    "color": "green"
}
EOF
}

# Add the runtime documentation of one new Zserio version to the runtime documentation directory.
add_runtime_doc()
{
    exit_if_argc_ne $# 4
    local RUNTIME_DOC_DIR="$1"; shift
    local WORK_DIR="$1"; shift
    local RUNTIME_LIBS_ZIP="$1"; shift
    local ZSERIO_VERSION="$1"; shift

    echo "Adding Zserio runtime libraries documentation version ${ZSERIO_VERSION}."
    local UNZIP_DIR="${WORK_DIR}/${ZSERIO_VERSION}"
    rm -rf "${UNZIP_DIR}"
    mkdir -p "${UNZIP_DIR}"

    echo -ne "Removing Zserio runtime libraries latest version..."
    local DEST_LATEST_DIR="${RUNTIME_DOC_DIR}/latest"
    rm -rf "${DEST_LATEST_DIR}"
    echo "Done"

    echo -ne "Adding cross references between runtime libraries versions to old documentations..."
    patch_old_runtime_doc "${RUNTIME_DOC_DIR}" "${ZSERIO_VERSION}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    echo "Done"

    echo -ne "Unzipping Zserio runtime libraries..."
    "${UNZIP}" -q "${RUNTIME_LIBS_ZIP}" -d "${UNZIP_DIR}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip zserio runtime libraries to ${UNZIP_DIR}!"
        return 1
    fi
    mkdir -p "${UNZIP_DIR}"/runtime_libs/java/zserio_doc
    "${UNZIP}" -q "${UNZIP_DIR}"/runtime_libs/java/zserio_runtime_javadocs.jar \
            -d "${UNZIP_DIR}"/runtime_libs/java/zserio_doc -x META-INF/*
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip zserio runtime javadocs jar!"
        return 1
    fi
    echo "Done"

    echo -ne "Adding cross references between runtime libraries versions to new documentations..."
    patch_new_runtime_doc "${RUNTIME_DOC_DIR}" "${UNZIP_DIR}/runtime_libs" "${ZSERIO_VERSION}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    echo "Done"

    echo -ne "Copying Zserio runtime libraries version ${ZSERIO_VERSION}..."
    local DEST_RUNTIME_DIR="${RUNTIME_DOC_DIR}/${ZSERIO_VERSION}"
    local LANGUAGE
    for LANGUAGE in cpp java python ; do
        mkdir -p "${DEST_RUNTIME_DIR}"/${LANGUAGE}
        cp -r "${UNZIP_DIR}"/runtime_libs/${LANGUAGE}/zserio_doc/* "${DEST_RUNTIME_DIR}"/${LANGUAGE}
        if [ $? -ne 0 ] ; then
            stderr_echo "Cannot copy ${LANGUAGE} runtime library documentation!"
            return 1
        fi
    done
    echo "Done"

    echo -ne "Creating Zserio runtime library GitHub badges..."
    create_github_badge_jsons "${DEST_RUNTIME_DIR}" "${ZSERIO_VERSION}"
    echo "Done"

    echo -ne "Copying Zserio runtime libraries latest version..."
    mkdir -p "${DEST_LATEST_DIR}"
    cp -r "${DEST_RUNTIME_DIR}"/* "${DEST_LATEST_DIR}"
    echo "Done"
    echo

    return 0
}

# Assemble the Zserio Web Pages sources which are built by Jekyll as zserio.org.
build_web_pages()
{
    exit_if_argc_lt $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local SOURCE_DIR="$1"; shift
    local ARCHIVE_DIR="$1"; shift
    local SITE_DIR="$1"; shift
    local RUNTIME_LIBS_ZIPS=("$@")

    echo "Copying Zserio sources from ${SOURCE_DIR}."
    rm -rf "${SITE_DIR}"
    mkdir -p "${SITE_DIR}"
    tar -C "${SOURCE_DIR}" --exclude=.git -cf - . | tar -C "${SITE_DIR}" -xf -
    if [ ${PIPESTATUS[0]} -ne 0 -o ${PIPESTATUS[1]} -ne 0 ] ; then
        stderr_echo "Cannot copy Zserio sources to ${SITE_DIR}!"
        return 1
    fi
    cp "${ZSERIO_PROJECT_ROOT}/.github/pages/_config.yml" "${SITE_DIR}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot copy Jekyll configuration to ${SITE_DIR}!"
        return 1
    fi

    echo "Copying Zserio runtime libraries documentation archive from ${ARCHIVE_DIR}."
    local RUNTIME_DOC_DIR="${SITE_DIR}/doc/runtime"
    if [ -e "${RUNTIME_DOC_DIR}" ] ; then
        stderr_echo "Zserio sources must not contain ${RUNTIME_DOC_DIR#${SITE_DIR}/}!"
        return 1
    fi
    mkdir -p "${RUNTIME_DOC_DIR}"
    cp -r "${ARCHIVE_DIR}"/* "${RUNTIME_DOC_DIR}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot copy runtime libraries documentation archive to ${RUNTIME_DOC_DIR}!"
        return 1
    fi
    echo

    local WORK_DIR="${SITE_DIR}.work"
    rm -rf "${WORK_DIR}"
    local RUNTIME_LIBS_ZIP
    for RUNTIME_LIBS_ZIP in "${RUNTIME_LIBS_ZIPS[@]}" ; do
        local ZIP_NAME="${RUNTIME_LIBS_ZIP##*/}"
        if [[ ! "${ZIP_NAME}" =~ ^zserio-([0-9]+\.[0-9]+\.[0-9]+)-runtime-libs\.zip$ ]] ; then
            stderr_echo "Invalid runtime libraries zip name '${ZIP_NAME}'!"
            return 1
        fi
        local ZSERIO_VERSION="${BASH_REMATCH[1]}"
        local NEWEST_VERSION=`ls -1 "${RUNTIME_DOC_DIR}" | grep -v "^latest$" | sort -V | tail -n 1`
        if [[ "${NEWEST_VERSION}" == "${ZSERIO_VERSION}" ||
              "`printf "%s\n%s\n" "${NEWEST_VERSION}" "${ZSERIO_VERSION}" | sort -V | tail -n 1`" != \
                    "${ZSERIO_VERSION}" ]] ; then
            stderr_echo "Version ${ZSERIO_VERSION} is not newer than the published version ${NEWEST_VERSION}!"
            return 1
        fi

        add_runtime_doc "${RUNTIME_DOC_DIR}" "${WORK_DIR}" "${RUNTIME_LIBS_ZIP}" "${ZSERIO_VERSION}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    done
    rm -rf "${WORK_DIR}"

    echo "Zserio Web Pages sources are in ${SITE_DIR}."

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Assemble the sources of Zserio Web Pages (zserio.org) which are then built by Jekyll.

    The site consists of the Zserio sources, the Jekyll configuration .github/pages/_config.yml,
    the archived runtime libraries documentation of all already published versions and the runtime
    libraries documentation of the given new versions.

Usage:
    $0 [-h] [-e] -s <dir> -a <dir> -o <dir> [runtime_libs_zip...]

Arguments:
    -h, --help       Show this help.
    -e, --help-env   Show help for enviroment variables.
    -s <dir>, --source-directory <dir>
                     Zserio sources to publish, e.g. a checkout of the release tag with submodules. Required.
    -a <dir>, --archive-directory <dir>
                     Archived runtime libraries documentation (doc/runtime of the published site). Required.
    -o <dir>, --output-directory <dir>
                     Directory where the site sources are assembled. It is removed first. Required.

    runtime_libs_zip Zserio runtime libraries zip zserio-<version>-runtime-libs.zip of a version newer
                     than all archived versions. Several zips are added in the given order.

Examples:
    $0 -s . -a ../web-pages-archive/doc/runtime -o build/web_pages/site \\
            release/zserio-2.20.0-runtime-libs.zip

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
    exit_if_argc_lt $# 4
    local PARAM_SOURCE_DIR_OUT="$1"; shift
    local PARAM_ARCHIVE_DIR_OUT="$1"; shift
    local PARAM_SITE_DIR_OUT="$1"; shift
    local PARAM_RUNTIME_LIBS_ZIPS_OUT="$1"; shift

    eval ${PARAM_RUNTIME_LIBS_ZIPS_OUT}="()"
    local NUM_ZIPS=0
    while [ $# -ne 0 ] ; do
        local ARG="$1"
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
                ;;

            "-s" | "--source-directory" | "-a" | "--archive-directory" | "-o" | "--output-directory")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing directory for '${ARG}'!"
                    echo
                    return 1
                fi
                case "${ARG}" in
                    "-s" | "--source-directory") eval ${PARAM_SOURCE_DIR_OUT}="\"$2\"" ;;
                    "-a" | "--archive-directory") eval ${PARAM_ARCHIVE_DIR_OUT}="\"$2\"" ;;
                    *) eval ${PARAM_SITE_DIR_OUT}="\"$2\"" ;;
                esac
                shift 2
                ;;

            "-"*)
                stderr_echo "Invalid switch '${ARG}'!"
                echo
                return 1
                ;;

            *)
                eval ${PARAM_RUNTIME_LIBS_ZIPS_OUT}[${NUM_ZIPS}]="\"${ARG}\""
                NUM_ZIPS=$((NUM_ZIPS + 1))
                shift
                ;;
        esac
    done

    if [[ -z "${!PARAM_SOURCE_DIR_OUT}" || -z "${!PARAM_ARCHIVE_DIR_OUT}" || -z "${!PARAM_SITE_DIR_OUT}" ]] ; then
        stderr_echo "Source, archive and output directories are required!"
        echo
        return 1
    fi

    return 0
}

# Main entry of the script to build Zserio Web Pages.
main()
{
    # get the project root
    local ZSERIO_PROJECT_ROOT="${SCRIPT_DIR}/.."

    # parse command line arguments
    local PARAM_SOURCE_DIR=""
    local PARAM_ARCHIVE_DIR=""
    local PARAM_SITE_DIR=""
    local PARAM_RUNTIME_LIBS_ZIPS
    parse_arguments PARAM_SOURCE_DIR PARAM_ARCHIVE_DIR PARAM_SITE_DIR PARAM_RUNTIME_LIBS_ZIPS "$@"
    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_build_web_pages_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        print_help
        return 1
    fi

    # set global variables
    set_build_web_pages_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # absolute paths are necessary, the zips are checked before anything is removed
    convert_to_absolute_path "${ZSERIO_PROJECT_ROOT}" ZSERIO_PROJECT_ROOT
    local DIR_PARAM
    for DIR_PARAM in PARAM_SOURCE_DIR PARAM_ARCHIVE_DIR ; do
        if [ ! -d "${!DIR_PARAM}" ] ; then
            stderr_echo "Directory '${!DIR_PARAM}' does not exist!"
            return 1
        fi
        convert_to_absolute_path "${!DIR_PARAM}" ${DIR_PARAM}
    done
    mkdir -p "${PARAM_SITE_DIR}"
    convert_to_absolute_path "${PARAM_SITE_DIR}" PARAM_SITE_DIR
    local I
    for I in "${!PARAM_RUNTIME_LIBS_ZIPS[@]}" ; do
        if [ ! -f "${PARAM_RUNTIME_LIBS_ZIPS[${I}]}" ] ; then
            stderr_echo "Runtime libraries zip '${PARAM_RUNTIME_LIBS_ZIPS[${I}]}' does not exist!"
            return 1
        fi
        convert_to_absolute_path "${PARAM_RUNTIME_LIBS_ZIPS[${I}]}" PARAM_RUNTIME_LIBS_ZIPS[${I}]
    done

    build_web_pages "${ZSERIO_PROJECT_ROOT}" "${PARAM_SOURCE_DIR}" "${PARAM_ARCHIVE_DIR}" "${PARAM_SITE_DIR}" \
            "${PARAM_RUNTIME_LIBS_ZIPS[@]}"
}

main "$@"
