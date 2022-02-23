#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_test_tools.sh"

# Gets test suites matching the provided patterns.
get_test_suites()
{
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PATTERNS=("${MSYS_WORKAROUND_TEMP[@]}")
    local TEST_SUITES_OUT="$1"; shift

    local STARTING_POINT="${ZSERIO_PROJECT_ROOT}/test"
    local FIND_EXPRESSION=("!" "-ipath" "${STARTING_POINT}/utils/*")
    for i in ${!PATTERNS[@]} ; do
        FIND_EXPRESSION+=("(")
    done
    for i in ${!PATTERNS[@]} ; do
        local PATTERN="${PATTERNS[$i]}"
        if [[ $PATTERN == "i:"* ]] ; then
            PATTERN="${PATTERN#i:}"
            if [ $i -gt 0 ] ; then
                FIND_EXPRESSION+=("-o")
            fi
            FIND_EXPRESSION+=("-ipath" "${STARTING_POINT}/${PATTERN}")
        elif [[ $PATTERN == "x:"* ]] ; then
            PATTERN="${PATTERN#x:}"
            if [ $i -gt 0 ] ; then
                FIND_EXPRESSION+=("-a")
            fi
            FIND_EXPRESSION+=("!" "-ipath" "${STARTING_POINT}/${PATTERN}")
        else
            stderr_echo "Unexpected test pattern!"
            return 1
        fi
        FIND_EXPRESSION+=(")")
    done

    local TEST_SUITES_ARR=(
        $(${FIND} ${STARTING_POINT} -mindepth 2 -maxdepth 2 -type d "${FIND_EXPRESSION[@]}" | sort)
    )

    for i in ${!TEST_SUITES_ARR[@]} ; do
        eval ${TEST_SUITES_OUT}[$i]="${TEST_SUITES_ARR[$i]#${STARTING_POINT}/}"
    done

    return 0
}

# Run Zserio C++ tests
test_cpp()
{
    exit_if_argc_ne $# 7
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local TEST_SRC_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local SWITCH_CLEAN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")

    local MESSAGE="Zserio C++ tests"
    echo "STARTING - ${MESSAGE}"

    local HOST_PLATFORM
    get_host_platform HOST_PLATFORM
    if [ $? -ne 0 ] ; then
        return 1
    fi

    local TEST_SUITES_LIST=""
    for i in ${!TEST_SUITES[@]} ; do
        if [ ${i} -gt 0 ] ; then
            TEST_SUITES_LIST+=";"
        fi
        TEST_SUITES_LIST+="${TEST_SUITES[i]}"
    done

    local CMAKE_ARGS=("-DZSERIO_RELEASE_ROOT=${UNPACKED_ZSERIO_RELEASE_DIR}"
                      "-DZSERIO_TEST_SUITES=${TEST_SUITES_LIST}")
    local CTEST_ARGS=()
    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        local CPP_TARGET="clean"
    else
        local CPP_TARGET="all"
    fi
    compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/cpp" "${TEST_SRC_DIR}" CPP_TARGETS[@] \
                CMAKE_ARGS[@] CTEST_ARGS[@] ${CPP_TARGET}
    if [ $? -ne 0 ] ; then
        stderr_echo "${MESSAGE} failed!"
        return 1
    fi
    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Run Zserio Java tests
test_java()
{
    exit_if_argc_ne $# 5
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local TEST_SRC_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local SWITCH_CLEAN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")

    local TEST_FILTER=""
    for i in ${!TEST_SUITES[@]} ; do
        if [ $i -gt 0 ] ; then
            TEST_FILTER+=","
        fi
        TEST_FILTER+="${TEST_SUITES[$i]}"
    done

    local MESSAGE="Zserio Java tests"
    echo "STARTING - ${MESSAGE}"
    local ANT_ARGS=("-Dzserio.release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                    "-Dzserio_java_test.build_dir=${TEST_OUT_DIR}/java")
    ANT_ARGS+=("-Dzserio_java_test.test_suites=${TEST_FILTER}")
    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        local JAVA_TARGET="clean"
    else
        local JAVA_TARGET="run"
    fi
    compile_java "${TEST_SRC_DIR}/build.xml" ANT_ARGS[@] "${JAVA_TARGET}"
    if [ $? -ne 0 ] ; then
        stderr_echo "${MESSAGE} failed!"
        return 1
    fi
    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Run Zserio Python tests
test_python()
{
    exit_if_argc_ne $# 7
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local TEST_SRC_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local SWITCH_CLEAN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")

    local MESSAGE="Zserio Python tests"
    echo "STARTING - ${MESSAGE}"

    activate_python_virtualenv "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}"
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        rm -rf "${TEST_OUT_DIR}/python"
    else
        local TEST_FILTER=""
        for i in ${!TEST_SUITES[@]} ; do
            if [ $i -gt 0 ] ; then
                TEST_FILTER+=","
            fi
            TEST_FILTER+="${TEST_SUITES[$i]}"
        done

        local TEST_ARGS=("--release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                         "--build_dir=${TEST_OUT_DIR}/python"
                         "--java=${JAVA_BIN}")
        TEST_ARGS+=("--filter=${TEST_FILTER}")
        local TEST_FILE="${TEST_SRC_DIR}/tests.py"
        local PYLINT_RCFILE="${TEST_SRC_DIR}/pylintrc.txt"
        local PYLINT_RCFILE_FOR_TESTS="${TEST_SRC_DIR}/pylintrc_test.txt"
        local MYPY_CONFIG_FILE="${TEST_SRC_DIR}/mypy.ini"

        echo
        echo "Running python tests."
        echo

        python "${TEST_FILE}" "${TEST_ARGS[@]}" --pylint_rcfile="${PYLINT_RCFILE}" \
                --pylint_rcfile_test="${PYLINT_RCFILE_FOR_TESTS}" --mypy_config_file="${MYPY_CONFIG_FILE}"
        local PYTHON_RESULT=$?
        if [ ${PYTHON_RESULT} -ne 0 ] ; then
            stderr_echo "Running python failed with return code ${PYTHON_RESULT}!"
            return 1
        fi

        echo "Running pylint on python test utilities."

        local PYLINT_ARGS=("--disable=missing-docstring,import-outside-toplevel,c-extension-no-member")
        run_pylint "${PYLINT_RCFILE_FOR_TESTS}" PYLINT_ARGS[@] "${TEST_FILE}" "${TEST_SRC_DIR}/utils/python"/*
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi
    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Run Zserio XML tests.
test_xml()
{
    exit_if_argc_ne $# 5
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local TEST_SRC_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local SWITCH_CLEAN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")

    local MESSAGE="Zserio XML tests"
    echo "STARTING - ${MESSAGE}"
    echo

    local TEST_XML_OUT_DIR="${TEST_OUT_DIR}/xml"
    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        rm -rf "${TEST_XML_OUT_DIR}"
    else
        local TOTAL_NUMBER_OF_TESTS=0
        for TEST_SUITE in ${TEST_SUITES[@]} ; do
            local TEST_ZS_DIRS=`"${FIND}" "${TEST_SRC_DIR}/${TEST_SUITE}" -path '*/zs' ! -path '*errors*'`
            for TEST_ZS_DIR in ${TEST_ZS_DIRS} ; do
                local MAIN_ZS_FILES=`"${FIND}" "${TEST_ZS_DIR}" -maxdepth 1 -type f`
                for MAIN_ZS_FILE in ${MAIN_ZS_FILES} ; do
                    local MAIN_ZS_FILE_NAME="${MAIN_ZS_FILE#${TEST_ZS_DIR}/}"
                    local TEST_ZS_RELDIR="${TEST_ZS_DIR#${TEST_SRC_DIR}/}"
                    local TEST_SUBDIR="${TEST_ZS_RELDIR%/zs}"
                    local TEST_XML_OUT_ZS_DIR="${TEST_XML_OUT_DIR}/${TEST_SUBDIR}/${MAIN_ZS_FILE_NAME%.zs}"

                    local OPTIONS_FILE="${TEST_SRC_DIR}/${TEST_SUBDIR}/xml_options.txt"
                    local SWITCH_WERROR=1
                    local SWITCH_XMLLINT=1
                    local ZSERIO_ARGS=("-xml" "${TEST_XML_OUT_ZS_DIR}")
                    if [[ -f "${OPTIONS_FILE}" ]] ; then
                        local OPTION_WERROR=`grep 'WERROR' "${OPTIONS_FILE}" | cut -d= -f 2`
                        if [ -n "${OPTION_WERROR}" ] ; then
                            SWITCH_WERROR=${OPTION_WERROR}
                        fi
                        local OPTION_XMLLINT=`grep 'XMLLINT' "${OPTIONS_FILE}" | cut -d= -f 2`
                        if [ -n "${OPTION_XMLLINT}" ] ; then
                            SWITCH_XMLLINT=${OPTION_XMLLINT}
                        fi
                        local OPTION_ZSERIO_ARGS=`grep 'ZSERIO_ARGS' "${OPTIONS_FILE}" | cut -d= -f 2`
                        if [ -n "${OPTION_ZSERIO_ARGS}" ] ; then
                            ZSERIO_ARGS+=(${OPTION_ZSERIO_ARGS})
                        fi
                    fi
                    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_XML_OUT_ZS_DIR}" "${TEST_ZS_DIR}" \
                        "${MAIN_ZS_FILE_NAME}" ${SWITCH_WERROR} ZSERIO_ARGS[@]
                    if [ $? -ne 0 ] ; then
                        stderr_echo "${MESSAGE} failed!"
                        return 1
                    fi

                    if [ ${SWITCH_XMLLINT} -ne 0 ] ; then
                        run_xmllint "${TEST_XML_OUT_ZS_DIR}/abstract_syntax_tree.xml"
                        if [ $? -ne 0 ] ; then
                            return 1
                        fi
                    fi

                    TOTAL_NUMBER_OF_TESTS=$((TOTAL_NUMBER_OF_TESTS+1))
                done
            done
        done
        echo "Total number of tests: ${TOTAL_NUMBER_OF_TESTS}"
        echo
    fi
    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Run Zserio documentation tests.
test_doc()
{
    exit_if_argc_ne $# 5
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local TEST_SRC_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local SWITCH_CLEAN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")

    local MESSAGE="Zserio documentation tests"
    echo "STARTING - ${MESSAGE}"
    echo

    local TEST_DOC_OUT_DIR="${TEST_OUT_DIR}/doc"
    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        rm -rf "${TEST_DOC_OUT_DIR}"
    else
        local TOTAL_NUMBER_OF_TESTS=0
        for TEST_SUITE in ${TEST_SUITES[@]} ; do
            local TEST_ZS_DIRS=`"${FIND}" "${TEST_SRC_DIR}/${TEST_SUITE}" -path '*/zs' ! -path '*errors*'`
            for TEST_ZS_DIR in ${TEST_ZS_DIRS} ; do
                local MAIN_ZS_FILES=`"${FIND}" "${TEST_ZS_DIR}" -maxdepth 1 -type f`
                for MAIN_ZS_FILE in ${MAIN_ZS_FILES} ; do
                    local MAIN_ZS_FILE_NAME="${MAIN_ZS_FILE#${TEST_ZS_DIR}/}"
                    local TEST_ZS_RELDIR="${TEST_ZS_DIR#${TEST_SRC_DIR}/}"
                    local TEST_SUBDIR="${TEST_ZS_RELDIR%/zs}"
                    local TEST_DOC_OUT_ZS_DIR="${TEST_DOC_OUT_DIR}/${TEST_SUBDIR}/${MAIN_ZS_FILE_NAME%.zs}"

                    local OPTIONS_FILE="${TEST_SRC_DIR}/${TEST_SUBDIR}/doc_options.txt"
                    local SWITCH_WERROR=1
                    local ZSERIO_ARGS=("-doc" "${TEST_DOC_OUT_ZS_DIR}"
                                       "-withSvgDiagrams"
                                       "-setDotExecutable" "${DOT}")
                    if [[ -f "${OPTIONS_FILE}" ]] ; then
                        local OPTION_WERROR=`grep 'WERROR' "${OPTIONS_FILE}" | cut -d= -f 2`
                        if [ -n "${OPTION_WERROR}" ] ; then
                            SWITCH_WERROR=${OPTION_WERROR}
                        fi
                        local OPTION_ZSERIO_ARGS=`grep 'ZSERIO_ARGS' "${OPTIONS_FILE}" | cut -d= -f 2`
                        if [ -n "${OPTION_ZSERIO_ARGS}" ] ; then
                            ZSERIO_ARGS+=(${OPTION_ZSERIO_ARGS})
                        fi
                    fi
                    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_DOC_OUT_ZS_DIR}" "${TEST_ZS_DIR}" \
                        "${MAIN_ZS_FILE_NAME}" ${SWITCH_WERROR} ZSERIO_ARGS[@]
                    if [ $? -ne 0 ] ; then
                        stderr_echo "${MESSAGE} failed!"
                        return 1
                    fi

                    local FILTER_FILE="${TEST_SRC_DIR}/${TEST_SUBDIR}/vnu_filter.txt"
                    local VNU_ARGS=()
                    if [[ ! -f "${VNU_FILTER_FILE}" && -f "${FILTER_FILE}" ]] ; then
                        # apply only if global VNU_FILTER_FILE is not set
                        VNU_ARGS+=(--filterfile "${FILTER_FILE}")
                    fi
                    run_vnu VNU_ARGS[@] "${TEST_DOC_OUT_ZS_DIR}"
                    if [ $? -ne 0 ] ; then
                        return 1
                    fi

                    TOTAL_NUMBER_OF_TESTS=$((TOTAL_NUMBER_OF_TESTS+1))
                done
            done
        done
        echo "Total number of tests: ${TOTAL_NUMBER_OF_TESTS}"
        echo
    fi
    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Run Zserio tests.
test()
{
    exit_if_argc_ne $# 11
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local PARAM_XML="$1"; shift
    local PARAM_DOC="$1"; shift
    local SWITCH_CLEAN="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")

    local TEST_SRC_DIR="${ZSERIO_PROJECT_ROOT}/test"

    # run Zserio C++ tests
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        test_cpp "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${TEST_SRC_DIR}" "${TEST_OUT_DIR}" \
            CPP_TARGETS[@] ${SWITCH_CLEAN} TEST_SUITES[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run Zserio Java tests
    if [[ ${PARAM_JAVA} != 0 ]] ; then
        test_java "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_SRC_DIR}" "${TEST_OUT_DIR}" ${SWITCH_CLEAN} \
            TEST_SUITES[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run Zserio Python tests
    if [[ ${PARAM_PYTHON} != 0 ]]; then
        test_python "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
            "${TEST_SRC_DIR}" "${TEST_OUT_DIR}" ${SWITCH_CLEAN} TEST_SUITES[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run Zserio XML tests
    if [[ ${PARAM_XML} != 0 ]]; then
        test_xml "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_SRC_DIR}" "${TEST_OUT_DIR}" ${SWITCH_CLEAN} \
            TEST_SUITES[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run Zserio documentation tests
    if [[ ${PARAM_DOC} != 0 ]]; then
        test_doc "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_SRC_DIR}" "${TEST_OUT_DIR}" ${SWITCH_CLEAN} \
            TEST_SUITES[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs Zserio tests on Zserio release compiled in release-ver directory.

Usage:
    $0 [-h] [-e] [-c] [-p] [-o <dir>] [-i <pattern>]... [-x <pattern>]... package...

Arguments:
    -h, --help            Show this help.
    -e, --help-env        Show help for enviroment variables.
    -c, --clean           Clean package instead of build.
    -p, --purge           Purge test build directory.
    -o <dir>, --output-directory <dir>
                          Output directory where tests will be run.
    -i <pattern>, --include <pattern>
                          Include tests matching the specified pattern. Can be specified multiple times.
    -x <pattern>, --exclude <pattern>
                          Exclude tests matching the specified pattern. Can be specified multiple times.
    package               Specify the package to test.

Package can be a combination of:
    cpp-linux32-gcc       Zserio C++ tests for linux32 target using gcc compiler.
    cpp-linux64-gcc       Zserio C++ tests for linux64 target using gcc compiler.
    cpp-linux32-clang     Zserio C++ tests for linux32 target using using Clang compiler.
    cpp-linux64-clang     Zserio C++ tests for linux64 target using Clang compiler.
    cpp-windows64-mingw   Zserio C++ tests for windows64 target (MinGW64).
    cpp-windows64-msvc    Zserio C++ tests for windows64 target (MSVC).
    java                  Zserio Java tests.
    python                Zserio Python tests.
    xml                   Zserio XML tests.
    doc                   Zserio documentation tests.
    all-linux32-gcc       Zserio tests - all available linux32 packages (gcc).
    all-linux64-gcc       Zserio tests - all available linux64 packages (gcc).
    all-linux32-clang     Zserio tests - all available linux32 packages (Clang).
    all-linux64-clang     Zserio tests - all available linux64 packages (Clang).
    all-windows64-mingw   Zserio tests - all available windows64 packages (MinGW64).
    all-windows64-msvc    Zserio tests - all available windows64 packages (MSVC).

Examples:
    $0 java cpp-linux64-gcc
    $0 cpp-linux64-gcc -i language/sql_tables
    $0 python -i lan* -x *sql_tables*
    $0 all-linux64-gcc

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
    exit_if_argc_lt $# 9
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_XML_OUT="$1"; shift
    local PARAM_DOC_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift
    local SWITCH_TEST_SUITES_ARRAY_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${PARAM_XML_OUT}=0
    eval ${PARAM_DOC_OUT}=0
    eval ${SWITCH_CLEAN_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PARAMS=0
    local NUM_PATTERNS=0
    local PARAM_ARRAY=()
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
                ;;

            "-e" | "--help-env")
                return 3
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

            "-i" | "--include")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Test include pattern is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_SUITES_ARRAY_OUT}[${NUM_PATTERNS}]="i:${ARG}"
                NUM_PATTERNS=$((NUM_PATTERNS + 1))
                shift
                ;;

            "-x" | "--exclude")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Test exclude pattern is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_SUITES_ARRAY_OUT}[${NUM_PATTERNS}]="x:${ARG}"
                NUM_PATTERNS=$((NUM_PATTERNS + 1))
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

    local NUM_CPP_TARGETS=0
    local PARAM
    for PARAM in "${PARAM_ARRAY[@]}" ; do
        case "${PARAM}" in
            "cpp-linux32-"* | "cpp-linux64-"* | "cpp-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#cpp-}"
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                ;;

            "java")
                eval ${PARAM_JAVA_OUT}=1
                ;;

            "python")
                eval ${PARAM_PYTHON_OUT}=1
                ;;

            "xml")
                eval ${PARAM_XML_OUT}=1
                ;;

            "doc")
                eval ${PARAM_DOC_OUT}=1
                ;;

            "all-linux32-"* | "all-linux64-"* | "all-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#all-}"
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                eval ${PARAM_XML_OUT}=1
                eval ${PARAM_DOC_OUT}=1
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                ;;

            *)
                stderr_echo "Invalid argument ${PARAM}!"
                echo
                return 1
        esac
    done

    if [[ ${NUM_CPP_TARGETS} -eq 0 &&
          ${!PARAM_JAVA_OUT} == 0 &&
          ${!PARAM_PYTHON_OUT} == 0 &&
          ${!PARAM_XML_OUT} == 0 &&
          ${!PARAM_DOC_OUT} == 0 &&
          ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Package to test is not specified!"
        echo
        return 1
    fi

    return 0
}

main()
{
    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    # parse command line arguments
    local PARAM_CPP_TARGET_ARRAY=()
    local PARAM_JAVA
    local PARAM_PYTHON
    local PARAM_XML
    local PARAM_DOC
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_CLEAN
    local SWITCH_PURGE
    local SWITCH_TEST_PATTERN_ARRAY=()
    # note that "$@" must have qoutes to prevent expansion of include/exclude patterns
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_XML PARAM_DOC PARAM_OUT_DIR \
                    SWITCH_CLEAN SWITCH_PURGE SWITCH_TEST_PATTERN_ARRAY "$@"
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

    echo "Compilation and testing of Zserio sources."
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

    if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ]] ; then
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

    if [[ ${PARAM_DOC} != 0 ]] ; then
        set_global_doc_variables
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # extensions need absolute paths
    convert_to_absolute_path "${PARAM_OUT_DIR}" PARAM_OUT_DIR

    # purge if requested and then create test output directory
    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"
    local TEST_OUT_DIR="${ZSERIO_BUILD_DIR}/test"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test directory."
        echo
        rm -rf "${TEST_OUT_DIR}/"

        if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} == 0 &&
              ${PARAM_JAVA} == 0 &&
              ${PARAM_PYTHON} == 0 &&
              ${PARAM_XML} == 0 &&
              ${PARAM_DOC} == 0 ]] ; then
            return 0 # purge only
        fi
    fi
    mkdir -p "${TEST_OUT_DIR}"

    # get Zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" "${PARAM_OUT_DIR}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # print information
    echo "Testing release: ${ZSERIO_RELEASE_DIR}"
    echo "Test output directory: ${TEST_OUT_DIR}"
    echo

    # unpack testing release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${TEST_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # get test suites to run
    local TEST_SUITES=()
    get_test_suites "${ZSERIO_PROJECT_ROOT}" SWITCH_TEST_PATTERN_ARRAY[@] TEST_SUITES
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [ ${#TEST_SUITES[@]} -eq 0 ] ; then
        echo "No test suites found."
        return 0
    fi

    # run test
    test "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" "${TEST_OUT_DIR}" \
         PARAM_CPP_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_XML} ${PARAM_DOC} \
         ${SWITCH_CLEAN} TEST_SUITES[@]
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
