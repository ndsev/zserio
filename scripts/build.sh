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
    $0 [-h] [-c] [-p] [-o <dir>] package...

Arguments:
    -h, --help              Show this help.
    -c, --clean             Clean package instead of build.
    -p, --purge             Purge build and distr directories before build.
    -o <dir>, --output-directory <dir>
                            Output directory where build and distr will be located.
    package                 Specify the package to build or clean.

Package can be the combination of:
    ant_task                Zserio Ant task.
    core                    Zserio Core.
    cpp                     Zserio C++ extension.
    cpp_rt-linux32          Zserio C++ extension runtime library for native linux32 (gcc).
    cpp_rt-linux64          Zserio C++ extension runtime library for native linux64 (gcc).
    cpp_rt-windows32-mingw  Zserio C++ extension runtime library for windows32 target (MinGW).
    cpp_rt-windows64-mingw  Zserio C++ extension runtime library for windows64 target (MinGW64).
    cpp_rt-windows32-msvc   Zserio C++ extension runtime library for windows32 target (MSVC).
    cpp_rt-windows64-msvc   Zserio C++ extension runtime library for windows64 target (MSVC).
    java                    Zserio Java extension.
    java_rt                 Zserio Java extension runtime library.
    python                  Zserio Python extension.
    python_rt               Zserio Python extensions runtime library.
    xml                     Zserio XML extension.
    doc                     Zserio Documentation extension.
    zserio                  Zserio bundle (Zserio Core packed together with all already built extensions).
    all-linux32             All available packages for linux32.
    all-linux64             All available packages for linux64.
    all-windows32-mingw     All available packages for windows32 target (MinGW).
    all-windows64-mingw     All available packages for windows64 target (MinGW64).
    all-windows32-msvc      All available packages for windows32 target (MSVC).
    all-windows64-msvc      All available packages for windows32 target (MSVC).

Examples:
    $0 ant_task core cpp cpp_rt-linux64 java java_rt python python_rt xml doc
    $0 zserio

EOF

    print_help_env
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
    local NUM_OF_ARGS=12
    exit_if_argc_lt $# ${NUM_OF_ARGS}
    local PARAM_ANT_TASK_OUT="$1"; shift
    local PARAM_CORE_OUT="$1"; shift
    local PARAM_CPP_OUT="$1"; shift
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_JAVA_RUNTIME_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_PYTHON_RUNTIME_OUT="$1"; shift
    local PARAM_XML_OUT="$1"; shift
    local PARAM_DOC_OUT="$1"; shift
    local PARAM_ZSERIO_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${PARAM_ANT_TASK_OUT}=0
    eval ${PARAM_CORE_OUT}=0
    eval ${PARAM_CPP_OUT}=0
    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_JAVA_RUNTIME_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${PARAM_PYTHON_RUNTIME_OUT}=0
    eval ${PARAM_XML_OUT}=0
    eval ${PARAM_DOC_OUT}=0
    eval ${PARAM_ZSERIO_OUT}=0
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

            "cpp_rt-linux32" | "cpp_rt-linux64" | "cpp_rt-windows32-"* | "cpp_rt-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#cpp_rt-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
                ;;

            "java")
                eval ${PARAM_JAVA_OUT}=1
                ;;

            "java_rt")
                eval ${PARAM_JAVA_RUNTIME_OUT}=1
                ;;

            "python")
                eval ${PARAM_PYTHON_OUT}=1
                ;;

            "python_rt")
                eval ${PARAM_PYTHON_RUNTIME_OUT}=1
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

            "all-linux32" | "all-linux64" | "all-windows32-"* | "all-windows64-"*)
                eval ${PARAM_ANT_TASK_OUT}=1
                eval ${PARAM_CORE_OUT}=1
                eval ${PARAM_CPP_OUT}=1
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#all-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_JAVA_RUNTIME_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                eval ${PARAM_PYTHON_RUNTIME_OUT}=1
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
          ${!PARAM_PYTHON_OUT} == 0 &&
          ${!PARAM_PYTHON_RUNTIME_OUT} == 0 &&
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

# Install python runtime source to distribution directory.
install_python_runtime()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="${1}"; shift
    local PYTHON_RUNTIME_ROOT="${1}"; shift
    local PYTHON_RUNTIME_BUILD_DIR="${1}"; shift
    local PYTHON_RUNTIME_DISTR_DIR="${1}"; shift

    local PYTHON_RUNTIME_SOURCES="${PYTHON_RUNTIME_ROOT}/src"
    local PYTHON_RUNTIME_DOC_BUILD_DIR="${PYTHON_RUNTIME_BUILD_DIR}/doc"
    local ZSERIO_LOGO="${ZSERIO_PROJECT_ROOT}/doc/Zserio.png"

    # build doc
    echo "Building documentation for Zserio Python runtime library."
    echo
    rm -rf ${PYTHON_RUNTIME_DOC_BUILD_DIR}
    mkdir -p ${PYTHON_RUNTIME_DOC_BUILD_DIR}
    pushd ${PYTHON_RUNTIME_DOC_BUILD_DIR} > /dev/null

    cp ${PYTHON_RUNTIME_ROOT}/doc/* .
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to get python doc configuration!"
        popd > /dev/null
        return 1
    fi

    PYTHONDONTWRITEBYTECODE=1 PYTHONPATH="${PYTHON_RUNTIME_SOURCES}" \
    sphinx-build -Wa -b html -d . -Dhtml_logo="${ZSERIO_LOGO}" . zserio_apidoc
    if [ $? -ne 0 ] ; then
        popd > /dev/null
        return 1
    fi

    popd > /dev/null

    echo
    echo "Installing Zserio Python runtime library."
    echo

    echo "Purging python distr dir: ${PYTHON_RUNTIME_DISTR_DIR}"
    rm -rf "${PYTHON_RUNTIME_DISTR_DIR}"
    mkdir -p "${PYTHON_RUNTIME_DISTR_DIR}"

    # install sources and doc
    pushd "${PYTHON_RUNTIME_SOURCES}" > /dev/null

    find . -name "*.py" | while read -r SOURCE ; do
        echo "Installing ${SOURCE}"
        cp --parents "${SOURCE}" "${PYTHON_RUNTIME_DISTR_DIR}"
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to install ${SOURCE}!"
            popd > /dev/null
            return 1
        fi
    done

    echo "Installing API documentation"
    cp -r "${PYTHON_RUNTIME_DOC_BUILD_DIR}/zserio_apidoc" "${PYTHON_RUNTIME_DISTR_DIR}/"
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to install documentation!"
        popd > /dev/null
        return 1
    fi

    popd > /dev/null
    return 0
}

main()
{
    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # parse command line arguments
    local PARAM_ANT_TASK
    local PARAM_CORE
    local PARAM_CPP
    local PARAM_CPP_TARGET_ARRAY=()
    local PARAM_JAVA
    local PARAM_JAVA_RUNTIME
    local PARAM_PYTHON
    local PARAM_PYTHON_RUNTIME
    local PARAM_XML
    local PARAM_DOC
    local PARAM_ZSERIO
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_CLEAN
    local SWITCH_PURGE
    parse_arguments PARAM_ANT_TASK PARAM_CORE \
                    PARAM_CPP PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_JAVA_RUNTIME \
                    PARAM_PYTHON PARAM_PYTHON_RUNTIME PARAM_XML PARAM_DOC PARAM_ZSERIO \
                    PARAM_OUT_DIR SWITCH_CLEAN SWITCH_PURGE $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

    # set global variables if needed
    if [[ ${PARAM_ANT_TASK} != 0 ||
          ${PARAM_CORE} != 0 ||
          ${PARAM_CPP} != 0 ||
          ${PARAM_JAVA} != 0 ||
          ${PARAM_JAVA_RUNTIME} != 0 ||
          ${PARAM_PYTHON} != 0 ||
          ${PARAM_XML} != 0 ||
          ${PARAM_DOC} != 0 ]] ; then
        set_global_java_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ]] ; then
        set_global_cpp_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_PYTHON_RUNTIME} != 0 ]] ; then
        set_global_python_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # purge if requested and then create build and distr directories
    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"
    local ZSERIO_DISTR_DIR="${PARAM_OUT_DIR}/distr"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging build and distr directories."
        echo
        rm -rf "${ZSERIO_BUILD_DIR}/"
        rm -rf "${ZSERIO_DISTR_DIR}/"
    fi
    mkdir -p "${ZSERIO_BUILD_DIR}"
    mkdir -p "${ZSERIO_DISTR_DIR}"

    # extensions need absolute paths
    convert_to_absolute_path "${ZSERIO_BUILD_DIR}" ZSERIO_BUILD_DIR
    convert_to_absolute_path "${ZSERIO_DISTR_DIR}" ZSERIO_DISTR_DIR

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

    local ANT_PROPS=(-Dzserio.build_dir="${ZSERIO_BUILD_DIR}"
                     -Dzserio.install_dir="${ZSERIO_DISTR_DIR}"
                     -Dzserio_extensions.build_dir="${ZSERIO_BUILD_DIR}/compiler/extensions"
                     -Dzserio_extensions.install_dir="${ZSERIO_DISTR_DIR}/zserio_libs"
                     -Dzserio_runtimes.build_dir="${ZSERIO_BUILD_DIR}/runtime_libs"
                     -Dzserio_runtimes.install_dir="${ZSERIO_DISTR_DIR}/runtime_libs"
                     -Dzserio_core.jar_file="${ZSERIO_BUILD_DIR}/compiler/core/jar/zserio_core.jar")

    # build Zserio Ant task
    if [[ ${PARAM_ANT_TASK} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Ant task."
        echo
        compile_java "${ZSERIO_PROJECT_ROOT}/build.xml" ANT_PROPS[@] zserio_ant_task.${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio core
    if [[ ${PARAM_CORE} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio compiler with all extensions."
        echo
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
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio C++ runtime library
    if [ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ] ; then
        echo "${ACTION_DESCRIPTION} Zserio C++ runtime library."
        echo
        local CMAKELISTS_DIR="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/runtime"
        local CPP_BUILD_DIR="${ZSERIO_BUILD_DIR}/runtime_libs"
        local CMAKE_ARGS=("-DCMAKE_INSTALL_PREFIX=${ZSERIO_DISTR_DIR}/runtime_libs"
                          "-DZSERIO_RUNTIME_INCLUDE_INSPECTOR=ON"
                          "-DZSERIO_RUNTIME_INCLUDE_RELATIONAL=ON")
        local CTEST_ARGS=()
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${CPP_BUILD_DIR}" "${CMAKELISTS_DIR}" PARAM_CPP_TARGET_ARRAY[@] \
                    CMAKE_ARGS[@] CTEST_ARGS[@] ${CPP_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio Java extension
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Java extension."
        echo
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio Java runtime library
    if [[ ${PARAM_JAVA_RUNTIME} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Java runtime library."
        local JAVA_RUNTIME_ANT_PROPS=("${ANT_PROPS[@]}"
                                      -Drelational.enable=yes)
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/runtime/build.xml" \
                     JAVA_RUNTIME_ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # build Zserio Python extension
    if [[ ${PARAM_PYTHON} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Python extension."
        echo
        echo "TODO"
    fi

    # build Zserio Python runtime library
    if [[ ${PARAM_PYTHON_RUNTIME} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio Python runtime library."
        echo

        local PYTHON_RUNTIME_BUILD_DIR=${ZSERIO_BUILD_DIR}/runtime_libs/python
        if [[ ${SWITCH_CLEAN} == 1 ]] ; then
            rm -rf ${PYTHON_RUNTIME_BUILD_DIR}
        else
            local PYTHON_RUNTIME_ROOT=${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/runtime
            test_python "${PYTHON_RUNTIME_BUILD_DIR}" "${PYTHON_RUNTIME_ROOT}" "${PYTHON_RUNTIME_ROOT}/src" \
                        "${PYTHON_RUNTIME_ROOT}/tests"
            if [ $? -ne 0 ] ; then
                return 1
            fi
            install_python_runtime "${ZSERIO_PROJECT_ROOT}" "${PYTHON_RUNTIME_ROOT}" \
                                   "${PYTHON_RUNTIME_BUILD_DIR}" "${ZSERIO_DISTR_DIR}/runtime_libs/python"
            if [ $? -ne 0 ] ; then
                return 1
            fi
        fi
    fi

    # build Zserio XML extension
    if [[ ${PARAM_XML} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio XML extension."
        echo
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
        compile_java "${ZSERIO_PROJECT_ROOT}/compiler/extensions/doc/build.xml" ANT_PROPS[@] ${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    # bundle Zserio
    if [[ ${PARAM_ZSERIO} == 1 ]] ; then
        echo "${ACTION_DESCRIPTION} Zserio bundle."
        echo
        compile_java "${ZSERIO_PROJECT_ROOT}/build.xml" ANT_PROPS[@] zserio_bundle.${JAVA_TARGET}
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo
    fi

    return 0
}

# call main function
main "$@"
