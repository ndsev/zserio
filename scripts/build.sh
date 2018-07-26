#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Print help message.
print_help()
{
    cat << EOF
Description:
    Builds Zserio into the distr directory.

Usage:
    $0 [-h] [-c] [-p] [-C <dir>] package...

Arguments:
    -h, --help       Show this help.
    -c, --clean      Clean package instead of build.
    -p, --purge      Purge build and distr directories before build.
    -C <dir>, --directory <dir>
                     Output directory for build and artifacts
    package          Specify the package to build or clean.

Package can be the combination of:
    ant_task         Zserio Ant task.
    core             Zserio Core.
    cpp              Zserio C++ extension.
    cpp_rt-linux32   Zserio C++ extension runtime library for native linux32 (gcc).
    cpp_rt-linux64   Zserio C++ extension runtime library for native linux64 (gcc).
    cpp_rt-windows32 Zserio C++ extension runtime library for windows32 target (MinGW 4.5.4).
    cpp_rt-windows64 Zserio C++ extension runtime library for windows64 target (MinGW64 4.5.4).
    java             Zserio Java extension.
    java_rt          Zserio Java extension runtime library.
    xml              Zserio XML extension.
    doc              Zserio Documentation extension.
    zserio           Zserio bundle (Zserio Core packed together with all already built extensions).
    all-linux32      All available packages for linux32.
    all-linux64      All available packages for linux64.
    all-windows32    All available packages for windows32.
    all-windows64    All available packages for windows64.
    all              All available packages.

Examples:
    $0 ant_task core cpp cpp_rt-linux64 java java_rt xml doc
    $0 zserio

EOF

    print_help_env
}

# Parse all command line arguments.
#
# Parameters:
# -----------
# $1..$9 - The name of variable to set to 1 if an appropriate argument is present.
# ${10}..${11} - The name of variable to set to 1 if an appropriate switch is present.
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
    local NUM_OF_ARGS=12
    exit_if_argc_lt $# ${NUM_OF_ARGS}
    local PARAM_ANT_TASK_OUT="$1"; shift
    local PARAM_CORE_OUT="$1"; shift
    local PARAM_CPP_OUT="$1"; shift
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_JAVA_RUNTIME_OUT="$1"; shift
    local PARAM_XML_OUT="$1"; shift
    local PARAM_DOC_OUT="$1"; shift
    local PARAM_ZSERIO_OUT="$1"; shift
    local PARAM_OUT_DIR="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${PARAM_ANT_TASK_OUT}=0
    eval ${PARAM_CORE_OUT}=0
    eval ${PARAM_CPP_OUT}=0
    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_JAVA_RUNTIME_OUT}=0
    eval ${PARAM_XML_OUT}=0
    eval ${PARAM_DOC_OUT}=0
    eval ${PARAM_ZSERIO_OUT}=0
    eval ${PARAM_OUT_DIR}="${SCRIPT_DIR}/.."
    eval ${SWITCH_CLEAN_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=();
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-c" | "--clean")
                eval ${SWITCH_CLEAN_OUT}=1
                shift
                ;;

            "-p" | "--purge")
                eval ${SWITCH_PURGE_OUT}=1
                shift
                ;;

            "-C" | "--directory")
                eval ${PARAM_OUT_DIR}="$2"
                shift
                shift
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

    local NUM_TARGETS=0
    local PARAM
    for PARAM in "${PARAM_ARRAY[@]}" ; do
        case "${PARAM}" in
            "ant_task")
                eval ${PARAM_ANT_TASK_OUT}=1
                ;;

            "core")
                eval ${PARAM_CORE_OUT}=1
                ;;

            "cpp")
                eval ${PARAM_CPP_OUT}=1
                ;;

            "cpp_rt-linux32" | "cpp_rt-linux64" | "cpp_rt-windows32" | "cpp_rt-windows64")
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#cpp_rt-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
                ;;

            "java")
                eval ${PARAM_JAVA_OUT}=1
                ;;

            "java_rt")
                eval ${PARAM_JAVA_RUNTIME_OUT}=1
                ;;

            "xml")
                eval ${PARAM_XML_OUT}=1
                ;;

            "doc")
                eval ${PARAM_DOC_OUT}=1
                ;;

            "zserio")
                eval ${PARAM_CORE_OUT}=1
                eval ${PARAM_ZSERIO_OUT}=1
                ;;

            "all"*)
                eval ${PARAM_ANT_TASK_OUT}=1
                eval ${PARAM_CORE_OUT}=1
                eval ${PARAM_CPP_OUT}=1
                if [[ ${PARAM:3:1} == "-" ]] ; then
                    eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#all-}"
                    NUM_TARGETS=$((NUM_TARGETS + 1))
                else
                    eval ${PARAM_CPP_TARGET_ARRAY_OUT}[0]="linux32"
                    eval ${PARAM_CPP_TARGET_ARRAY_OUT}[1]="linux64"
                    eval ${PARAM_CPP_TARGET_ARRAY_OUT}[2]="windows32"
                    eval ${PARAM_CPP_TARGET_ARRAY_OUT}[3]="windows64"
                    NUM_TARGETS=4
                fi
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_JAVA_RUNTIME_OUT}=1
                eval ${PARAM_XML_OUT}=1
                eval ${PARAM_DOC_OUT}=1
                eval ${PARAM_ZSERIO_OUT}=1
                ;;

            *)
                stderr_echo "Invalid argument ${PARAM}!"
                echo
                return 1
        esac
    done

    if [[ ${!PARAM_ANT_TASK_OUT} == 0 &&
          ${!PARAM_CORE_OUT} == 0 &&
          ${!PARAM_CPP_OUT} == 0 &&
          ${NUM_TARGETS} == 0 &&
          ${!PARAM_JAVA_OUT} == 0 &&
          ${!PARAM_JAVA_RUNTIME_OUT} == 0 &&
          ${!PARAM_XML_OUT} == 0 &&
          ${!PARAM_DOC_OUT} == 0 &&
          ${!PARAM_ZSERIO_OUT} == 0 &&
          ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Package to build is not specified!"
        echo
        return 1
    fi

    return 0
}

main()
{
    # parse command line arguments
    local PARAM_ANT_TASK
    local PARAM_CORE
    local PARAM_CPP
    local PARAM_CPP_TARGET_ARRAY=()
    local PARAM_JAVA
    local PARAM_JAVA_RUNTIME
    local PARAM_XML
    local PARAM_DOC
    local PARAM_ZSERIO
    local SWITCH_CLEAN
    local SWITCH_PURGE
    parse_arguments PARAM_ANT_TASK PARAM_CORE \
                    PARAM_CPP PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_JAVA_RUNTIME PARAM_XML PARAM_DOC PARAM_ZSERIO \
                    PARAM_OUTPUT_DIR SWITCH_CLEAN SWITCH_PURGE $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # set global java variables
    set_global_java_variables "${ZSERIO_PROJECT_ROOT}"
    if [ $? -ne 0 ] ; then
        return 1
    fi
    if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ]] ; then
        set_global_cpp_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # purge if requested and then create build and distr directories
    local BUILD_DIR="${PARAM_OUTPUT_DIR}/build"
    local ZSERIO_DISTR_DIR="${PARAM_OUTPUT_DIR}/distr"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging build and distr directories."
        echo
        rm -rf "${BUILD_DIR}/"
        rm -rf "${ZSERIO_DISTR_DIR}/"
    fi
    mkdir -p "${BUILD_DIR}"
    mkdir -p "${ZSERIO_DISTR_DIR}"

    # get action name and description
    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        local CPP_TARGET="clean"
        local JAVA_TARGET="clean"
        local ACTION_DESCRIPTION="Cleaning"
    else
        local CPP_TARGET="install"
        local JAVA_TARGET="test"
        local ACTION_DESCRIPTION="Building"
    fi

    # build Zserio Ant task if requested
    if [[ ${PARAM_ANT_TASK} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Ant task."
        echo
        local ANT_PROPS=()
        compile_java "${ZSERIO_PROJECT_ROOT}/build.xml" ANT_PROPS[@] zserio_ant_task.${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio core if requested
    if [[ ${PARAM_CORE} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio compiler with all extensions."
        echo
        local ANT_PROPS=()
        compile_java "${ZSERIO_PROJECT_ROOT}/build.xml" ANT_PROPS[@] zserio_core.${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio C++ extension
    if [[ ${PARAM_CPP} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio C++ extension."
        echo
        local ANT_PROPS=()
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio C++ extension runtime
    if [ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ] ; then
        # build Zserio C++ runtime library
        echo "${ACTION_DESCRIPTION} Zserio C++ runtime library."
        echo
        local CMAKELISTS_DIR="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/runtime"
        local CPP_BUILD_DIR="${BUILD_DIR}/compiler/extensions/cpp/runtime"
        local CMAKE_ARGS=("-DCMAKE_INSTALL_PREFIX=${ZSERIO_DISTR_DIR}"
                          "-DZSERIO_RUNTIME_INCLUDE_INSPECTOR=ON"
                          "-DZSERIO_RUNTIME_INCLUDE_RELATIONAL=ON")
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${CPP_BUILD_DIR}" "${CMAKELISTS_DIR}" PARAM_CPP_TARGET_ARRAY[@] \
                    CMAKE_ARGS[@] ${CPP_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio Java extension
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Java extension."
        echo
        local ANT_PROPS=()
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio Java runtime library if requested
    if [[ ${PARAM_JAVA_RUNTIME} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Java runtime library."
        echo
        local ANT_PROPS=("-Drelational.enable=yes")
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/runtime/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio XML extension
    if [[ ${PARAM_XML} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio XML extension."
        echo
        local ANT_PROPS=()
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/xml/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio Documentation extension
    if [[ ${PARAM_DOC} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Documentation extension."
        echo
        local ANT_PROPS=()
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/doc/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # bundle Zserio if requested
    if [[ ${PARAM_ZSERIO} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio bundle."
        echo
        local ANT_PROPS=()
        compile_java ${ZSERIO_PROJECT_ROOT}/build.xml ANT_PROPS[@] zserio.${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    return 0
}

# call main function
main "$@"

