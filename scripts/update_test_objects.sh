#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"
source "${SCRIPT_DIR}/common_test_tools.sh"

# Update C++ test objects.
update_cpp_test_objects()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local SWITCH_PURGE="$1"; shift
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local UPDATE_OUT_DIR="$1"; shift

    local CPP_RUNTIME_TEST_DIR="${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/runtime/test"
    local CPP_RUNTIME_TEST_OBJECT_DIR="${CPP_RUNTIME_TEST_DIR}/test_object"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test objects."
        echo
        rm -rf "${CPP_RUNTIME_TEST_OBJECT_DIR}/polymorphic_allocator"
        rm -rf "${CPP_RUNTIME_TEST_OBJECT_DIR}/std_allocator"
    fi
    local ZSERIO_ARGS_BASE=("-cpp" "${CPP_RUNTIME_TEST_DIR}"
            "-withTypeInfoCode" "-withReflectionCode" "-withoutSourcesAmalgamation" "-withBitPositionCode")
    local SWITCH_WERROR=1

    local ZSERIO_ARGS_STD=(${ZSERIO_ARGS_BASE[@]} "-setCppAllocator" "std"
            "-setTopLevelPackage" "test_object.std_allocator")
    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${UPDATE_OUT_DIR}/cpp/std" \
            "${CPP_RUNTIME_TEST_OBJECT_DIR}" "test_object.zs" ${SWITCH_WERROR} ZSERIO_ARGS_STD[@]
    if [ $? -ne 0 ] ; then
        stderr_echo "Zserio tool failed!"
        return 1
    fi

    local ZSERIO_ARGS_POLYMORPHIC=(${ZSERIO_ARGS_BASE[@]} "-setCppAllocator" "polymorphic"
            "-setTopLevelPackage" "test_object.polymorphic_allocator")
    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${UPDATE_OUT_DIR}/cpp/polymorhic" \
            "${CPP_RUNTIME_TEST_OBJECT_DIR}" "test_object.zs" ${SWITCH_WERROR} \
            ZSERIO_ARGS_POLYMORPHIC[@]
    if [ $? -ne 0 ] ; then
        stderr_echo "Zserio tool failed!"
        return 1
    fi

    # remove runtime version check from generated headers
    echo "Removing runtime version check from generated test objects."
    for GENERATED_HEADER in "${CPP_RUNTIME_TEST_OBJECT_DIR}/"**/*.h ; do
        sed -i '/#include <zserio\/CppRuntimeVersion.h>/,+5d' "${GENERATED_HEADER}"
        if [ $? -ne 0 ] ; then
            stderr_echo "Sed tool failed!"
            return 1
        fi
    done
    echo

    return 0
}

# Update Java test objects.
update_java_test_objects()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local SWITCH_PURGE="$1"; shift
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local UPDATE_OUT_DIR="$1"; shift

    local JAVA_RUNTIME_TEST_DIR="${ZSERIO_PROJECT_ROOT}/compiler/extensions/java/runtime/test"
    local JAVA_RUNTIME_TEST_OBJECT_DIR="${JAVA_RUNTIME_TEST_DIR}/test_object"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test objects."
        echo
        rm -rf "${JAVA_RUNTIME_TEST_OBJECT_DIR}"/*.java
    fi
    local ZSERIO_ARGS=("-java" "${JAVA_RUNTIME_TEST_DIR}" "-withTypeInfoCode")
    local SWITCH_WERROR=1
    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${UPDATE_OUT_DIR}/java" \
            "${JAVA_RUNTIME_TEST_OBJECT_DIR}" "test_object.zs" ${SWITCH_WERROR} ZSERIO_ARGS[@]
    if [ $? -ne 0 ] ; then
        stderr_echo "Zserio tool failed!"
        return 1
    fi

    return 0
}

# Update Python test objects.
update_python_test_objects()
{
    exit_if_argc_ne $# 4
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local SWITCH_PURGE="$1"; shift
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local UPDATE_OUT_DIR="$1"; shift

    local PYTHON_RUNTIME_TEST_DIR="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/runtime/tests"
    local PYTHON_RUNTIME_TEST_OBJECT_DIR="${PYTHON_RUNTIME_TEST_DIR}/test_object"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test objects."
        echo
        rm -rf "${PYTHON_RUNTIME_TEST_OBJECT_DIR}"/*.py
    fi
    local ZSERIO_ARGS=("-python" "${PYTHON_RUNTIME_TEST_DIR}" "-withTypeInfoCode")
    local SWITCH_WERROR=1
    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${UPDATE_OUT_DIR}/python" \
            "${PYTHON_RUNTIME_TEST_OBJECT_DIR}" "test_object.zs" ${SWITCH_WERROR} ZSERIO_ARGS[@]
    if [ $? -ne 0 ] ; then
        stderr_echo "Zserio tool failed!"
        return 1
    fi

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Update test objects in the runtime libraries.

Usage:
    $0 [-h] [-e] [-p] [-o <dir>] generator...

Arguments:
    -h, --help          Show this help.
    -e, --help-env      Show help for enviroment variables.
    -p, --purge         Purge test objects together with output directory before update.
    -o <dir>, --output-directory <dir>
                        Output directory for updating.
    generator           Specify the generator to test.

Generator can be:
    cpp                 Update test objects in C++ runtime library.
    java                Update test objects in Java runtime library.
    python              Update test objects in Python runtime library.
    all                 Update test objects in all runtime libraries.

Examples:
    $0 cpp java python
    $0 all
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
    exit_if_argc_lt $# 5
    local PARAM_CPP_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${PARAM_CPP_OUT}=0
    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=()
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
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing output directory!"
                    echo
                    return 1
                fi
                eval ${PARAM_OUT_DIR_OUT}="$2"
                shift 2
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

    local PARAM
    for PARAM in "${PARAM_ARRAY[@]}" ; do
        case "${PARAM}" in
            "cpp")
                eval ${PARAM_CPP_OUT}=1
                ;;

            "java")
                eval ${PARAM_JAVA_OUT}=1
                ;;

            "python")
                eval ${PARAM_PYTHON_OUT}=1
                ;;

            "all")
                eval ${PARAM_CPP_OUT}=1
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                ;;

            *)
                stderr_echo "Invalid argument '${PARAM}'!"
                echo
                return 1
        esac
    done

    if [[ ${!PARAM_CPP_OUT} == 0 && ${!PARAM_JAVA_OUT} == 0 && ${!PARAM_PYTHON_OUT} == 0 ]] ; then
        stderr_echo "Generator is not specified!"
        echo
        return 1
    fi

    return 0
}

main()
{
    # get the project root, absolute path is necessary only for CMake !@#
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # parse command line arguments
    local PARAM_CPP
    local PARAM_JAVA
    local PARAM_PYTHON
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_PURGE
    parse_arguments PARAM_CPP PARAM_JAVA PARAM_PYTHON PARAM_OUT_DIR SWITCH_PURGE "$@"
    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_test_help_env
        print_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        return 1
    fi

    echo "Updating test objects in runtime libraries."
    echo

    # set global variables
    set_global_common_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    set_test_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [[ ${PARAM_CPP} != 0 ]] ; then
        set_global_cpp_variables
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_JAVA} != 0 ]] ; then
        set_global_java_variables
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_PYTHON} != 0 ]] ; then
        set_global_python_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # extensions need absolute paths
    convert_to_absolute_path "${PARAM_OUT_DIR}" PARAM_OUT_DIR

    # purge if requested and then create output directory
    local UPDATE_OUT_DIR="${PARAM_OUT_DIR}/build/update_objects"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging output directory."
        echo
        rm -rf "${UPDATE_OUT_DIR}/"
    fi
    mkdir -p "${UPDATE_OUT_DIR}"

    # get zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" "${PARAM_OUT_DIR}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # print information
    echo "Used release: ${ZSERIO_RELEASE_DIR}"
    echo "Output directory: ${UPDATE_OUT_DIR}"
    echo

    # unpack release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${UPDATE_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # update C++ test objects
    if [[ ${PARAM_CPP} != 0 ]] ; then
        local MESSAGE="Update of C++ test objects in runtime library"
        echo "STARTING - ${MESSAGE}"
        echo
        update_cpp_test_objects "${ZSERIO_PROJECT_ROOT}" ${SWITCH_PURGE} "${UNPACKED_ZSERIO_RELEASE_DIR}" \
                "${UPDATE_OUT_DIR}"
        echo "FINISHED - ${MESSAGE}"
        echo
    fi

    # update Java test objects
    if [[ ${PARAM_JAVA} != 0 ]] ; then
        local MESSAGE="Update of Java test objects in runtime library"
        echo "STARTING - ${MESSAGE}"
        echo
        update_java_test_objects "${ZSERIO_PROJECT_ROOT}" ${SWITCH_PURGE} "${UNPACKED_ZSERIO_RELEASE_DIR}" \
                "${UPDATE_OUT_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo "FINISHED - ${MESSAGE}"
        echo
    fi

    # update Python test objects
    if [[ ${PARAM_PYTHON} != 0 ]] ; then
        local MESSAGE="Update of Python test objects in runtime library"
        echo "STARTING - ${MESSAGE}"
        echo
        update_python_test_objects "${ZSERIO_PROJECT_ROOT}" ${SWITCH_PURGE} "${UNPACKED_ZSERIO_RELEASE_DIR}" \
                "${UPDATE_OUT_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        echo "FINISHED - ${MESSAGE}"
        echo
    fi

    return 0
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
