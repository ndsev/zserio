#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_test_tools.sh"

capitalize_test_name()
{
    exit_if_argc_ne $# 2
    local STR="$1"; shift
    local OUT_VAR="$1"; shift
    
    local WORDS=(${STR//_/ }) # split at _
    local OUT="${WORDS[@]^}" # capitalize and join
    OUT="${OUT/Uint/UInt}"
    OUT="${OUT/Varint/VarInt}"
    OUT="${OUT/Varuint/VarUInt}"
    OUT="${OUT// /}" # remove spaces
    eval "${OUT_VAR}=${OUT}"
}

# Compares zs and test files and prints missing tests
print_missing_tests()
{
    exit_if_argc_ne $# 2
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local PLATFORM="$1"; shift

    local EXT=".${PLATFORM}"
    if [[ ${EXT} == ".python" ]] ; then
        EXT=".py"
    fi
    local STARTING_POINT="${ZSERIO_PROJECT_ROOT}/test"    
    local TEST_SUITES_ARR=(
        $(${FIND} "${STARTING_POINT}/data/" -mindepth 2 -maxdepth 2 -type d -printf "%P\\n" | sort)
    )
    local MISSING_TS_ARR=()
    local COMMON_TS_ARR=()
    local MISSING_COUNT=0
    for TS in ${TEST_SUITES_ARR[@]} ; do
        local TS1="${TS##*/}" # remove prefix
        #echo "-> ${TS}" # de
        capitalize_test_name ${TS1} TEST_NAME
        local TEST_NAME="${TEST_NAME}Test${EXT}"
        local ZS_STARTING_POINT="${STARTING_POINT}/data/${TS}/zs"
        local TS_STARTING_POINT="${STARTING_POINT}/extensions/${TS}/${PLATFORM}"
        if [ ! -d "${TS_STARTING_POINT}" ] || [ -z "$( ls -A ${TS_STARTING_POINT} )" ] ; then
            MISSING_TS_ARR+=(${TS})
        elif [ -f "${TS_STARTING_POINT}/${TEST_NAME}" ] ||
             [ -f "${TS_STARTING_POINT}/${TS1}/${TEST_NAME}" ] ; then
            COMMON_TS_ARR+=(${TS})
        else
            local MISSING_ARR=()
            # iterate subfolders
            local DIR_NAME_ARR=(
                $(${FIND} "${ZS_STARTING_POINT}" -mindepth 1 -maxdepth 1 -type d -printf "%f\\n")
            )
            for DIR_NAME in ${DIR_NAME_ARR[@]} ; do
                capitalize_test_name ${DIR_NAME} TEST_NAME
                TEST_NAME="${TEST_NAME}Test${EXT}"
                #echo "DIR_NAME ${DIR_NAME}"
                if [ ! -f "${TS_STARTING_POINT}/${TEST_NAME}" ] &&
                   [ ! -f "${TS_STARTING_POINT}/${TS1}/${TEST_NAME}" ] &&
                   [ ! -f "${TS_STARTING_POINT}/${TS1}/${DIR_NAME}/${TEST_NAME}" ] ;
                then
                    # iterate zs files
                    local ZS_NAME_ARR=(
                        $(${FIND} "${ZS_STARTING_POINT}/${DIR_NAME}" -mindepth 1 -maxdepth 1 -name "*.zs" -printf "%f\\n")
                    )
                    #echo " ${TEST_NAME} not found, recurse"
                    for ZS_NAME in ${ZS_NAME_ARR[@]} ; do
                        #echo "  ${ZS_NAME}"
                        local BASE="${ZS_NAME%.zs}"
                        capitalize_test_name ${BASE} TEST_NAME
                        TEST_NAME="${TEST_NAME}Test${EXT}"
                        if [ ! -f "${TS_STARTING_POINT}/${TEST_NAME}" ] &&
                           [ ! -f "${TS_STARTING_POINT}/${DIR_NAME}/${TEST_NAME}" ] &&
                           [ ! -f "${TS_STARTING_POINT}/${DIR_NAME}/${BASE}/${TEST_NAME}" ] &&
                           [ ! -f "${TS_STARTING_POINT}/${TS1}/${DIR_NAME}/${TEST_NAME}" ] ;
                        then
                            #echo "   ${TEST_NAME} not found"
                            MISSING_ARR+=("${TEST_NAME}")  
                            MISSING_COUNT=$((MISSING_COUNT+1))                            
                        fi
                    done
                fi
            done
            # iterate zs files except those having folder
            ZS_NAME_ARR=(
                $(${FIND} "${ZS_STARTING_POINT}" -mindepth 1 -maxdepth 1 -name "*.zs" -printf "%f\\n")
            )
            for ZS_NAME in ${ZS_NAME_ARR[@]} ; do
                #echo "FNAME ${ZS_NAME}"
                local BASE="${ZS_NAME%.zs}"
                capitalize_test_name ${BASE} TEST_NAME
                TEST_NAME="${TEST_NAME}Test${EXT}"
                if [ ! -d "${ZS_STARTING_POINT}/${BASE}" ] &&
                   [ ! -f "${TS_STARTING_POINT}/${TEST_NAME}" ] &&
                   [ ! -f "${TS_STARTING_POINT}/${TS1}/${TEST_NAME}" ] &&
                   [ ! -f "${TS_STARTING_POINT}/${BASE}/${TEST_NAME}" ];
                then
                    #echo " ${TEST_NAME} not found"
                    MISSING_ARR+=(${TEST_NAME})  
                    MISSING_COUNT=$((MISSING_COUNT+1))
                fi
            done
            # print missing tests
            if [ ${#MISSING_ARR[@]} -gt 0 ] ; then
                echo "Missing tests in ${TS}:"
                for MISS in ${MISSING_ARR[@]} ; do
                    echo "  ${MISS}"
                done
                echo
            fi
        fi
    done
    if [ ${#COMMON_TS_ARR[@]} -gt 0 ] ; then
        echo "Ignored test suites with shared implementation:"
        for COM in ${COMMON_TS_ARR[@]} ; do
            echo "  ${COM}"
        done
        echo
    fi
    if [ ${#MISSING_TS_ARR[@]} -gt 0 ] ; then
        echo "Missing test suites:"
        for MISS in ${MISSING_TS_ARR[@]} ; do
            echo "  ${MISS}"
        done
        echo
    fi

    echo "Summary:"
    echo "${MISSING_COUNT} tests missing" 
    echo "${#MISSING_TS_ARR[@]} test suites missing"
    return 0
}

# Gets test suites matching the provided patterns.
get_test_suites()
{
    exit_if_argc_ne $# 3
    local TEST_DATA_ROOT="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PATTERNS=("${MSYS_WORKAROUND_TEMP[@]}")
    local TEST_SUITES_OUT="$1"; shift

    local STARTING_POINT="${TEST_DATA_ROOT}"
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

# Compare BLOBs and JSONs created by tests.
compare_test_data()
{
    exit_if_argc_ne $# 4
    local TEST_SRC_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local TEST_SUITES=("${MSYS_WORKAROUND_TEMP[@]}")
    local PLATFORM_NAME="$1"; shift

    echo
    echo "Comparing data created by ${PLATFORM_NAME} tests"
    echo

    local TOTAL_BLOBS=0
    local TOTAL_JSONS=0
    for TEST_SUITE in "${TEST_SUITES[@]}"; do
        local TEST_DATA_DIR="${TEST_SRC_DIR}/../data/${TEST_SUITE}/data"
        local TEST_SUITE_DIR="${TEST_OUT_DIR}/${TEST_SUITE}"

        # check if test suite exists for this platform
        if [ -d "${TEST_SUITE_DIR}" ] ; then
            local FIND_PARAMS="-name *.blob -o -name *.json -not -path *.mypy_cache* -not -name compile_commands.json"
            local TEST_SUITE_FILES=($("${FIND}" "${TEST_SUITE_DIR}" ${FIND_PARAMS} | sort))

            echo -n "${TEST_SUITE} ... "
            local NUM_BLOBS=0
            local NUM_JSONS=0
            local CMP_RESULT=0
            for TEST_SUITE_FILE in ${TEST_SUITE_FILES[@]} ; do
                local FILE_NAME="${TEST_SUITE_FILE##*/}"
                local TEST_DATA_FILE="${TEST_DATA_DIR}/${FILE_NAME}"

                if [ ! -e "${TEST_DATA_FILE}" ] ; then
                    stderr_echo "Data file '${TEST_DATA_FILE}' doesn't exist!"
                    echo
                    return 1
                fi

                if [[ "${FILE_NAME}" == *.blob ]] ; then
                    NUM_BLOBS=$((NUM_BLOBS+1))
                    cmp "${TEST_SUITE_FILE}" "${TEST_DATA_FILE}"
                    if [ $? -ne 0 ] ; then
                        CMP_RESULT=1
                    fi
                else
                    NUM_JSONS=$((NUM_JSONS+1))
                    diff --strip-trailing-cr "${TEST_SUITE_FILE}" "${TEST_DATA_FILE}"
                    if [ $? -ne 0 ] ; then
                        CMP_RESULT=1
                    fi
                fi
            done

            if [ ${CMP_RESULT} -ne 0 ] ; then
                stderr_echo "Comparison failed!"
                echo
                return 1
            fi

            if [ ${NUM_BLOBS} -eq 0 -a ${NUM_JSONS} -eq 0 ] ; then
                echo "N/A"
            else
                echo "${NUM_BLOBS} BLOBs, ${NUM_JSONS} JSONs"
            fi

            TOTAL_BLOBS=$((TOTAL_BLOBS+NUM_BLOBS))
            TOTAL_JSONS=$((TOTAL_JSONS+NUM_JSONS))
        fi
    done

    if [[ $((TOTAL_BLOBS+TOTAL_JSONS)) -gt 0 ]] ; then
        echo "Successfully compared ${TOTAL_BLOBS} BLOBs and ${TOTAL_JSONS} JSONs."
    else
        echo "Nothing to compare."
    fi
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

    if [[ ${SWITCH_CLEAN} != 1 ]] ; then
        for TARGET in "${CPP_TARGETS[@]}"; do
            local BUILD_TYPE="release"
            if [[ "${CMAKE_EXTRA_ARGS}" == *-DCMAKE_BUILD_TYPE=?ebug* ]] ; then
                BUILD_TYPE="debug"
            fi

            compare_test_data "${TEST_SRC_DIR}" "${TEST_OUT_DIR}/cpp/${TARGET}/${BUILD_TYPE}" TEST_SUITES[@] \
                    "C++ ${TARGET}"
            if [ $? -ne 0 ] ; then
                stderr_echo "${MESSAGE} failed!"
                return 1
            fi
        done
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
    local IS_FIRST=1
    for TEST_SUITE in "${TEST_SUITES[@]}" ; do
        if [ ${IS_FIRST} -eq 0 ] ; then
            TEST_FILTER+=","
        else
            IS_FIRST=0
        fi
        TEST_FILTER+="${TEST_SUITE}"
    done

    local MESSAGE="Zserio Java tests"
    echo "STARTING - ${MESSAGE}"
    local ANT_ARGS=("-Dzserio.release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                    "-Dzserio_java_test.build_root_dir=${TEST_OUT_DIR}/java"
                    "-Dzserio_java_test.test_suites=${TEST_FILTER}")
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

    # calculate number of all run tests from ant output log file
    local JAVA_VERSION=`${ANT} -diagnostics | grep ant.java.version | cut -d' ' -f2`
    local ANT_LOG_FILE="${TEST_OUT_DIR}/java/${JAVA_VERSION}/test_log.txt"
    if [ ! -f "${ANT_LOG_FILE}" ] ; then
        stderr_echo "Ant output file '${ANT_LOG_FILE}' does not exist!"
        return 1
    fi
    local TEST_COUNTS=(`grep '.*tests found.*' "${ANT_LOG_FILE}" | tr -s ' ' | cut -d' ' -f4`)
    local INDEX
    for INDEX in ${!TEST_COUNTS[@]} ; do
        TOTAL_TEST_COUNT=$((${TOTAL_TEST_COUNT} + ${TEST_COUNTS[INDEX]}))
    done
    echo "Total number of testcases: ${TOTAL_TEST_COUNT}"

    if [[ ${SWITCH_CLEAN} != 1 ]] ; then
        compare_test_data "${TEST_SRC_DIR}" "${TEST_OUT_DIR}/java/${JAVA_VERSION}" TEST_SUITES[@] "Java"
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
            return 1
        fi
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

    local PYTHON_VERSION_STRING
    get_python_version_string PYTHON_VERSION_STRING
    local TEST_PYTHON_OUT_DIR="${TEST_OUT_DIR}/python/${PYTHON_VERSION_STRING}"

    if [[ ${SWITCH_CLEAN} == 1 ]] ; then
        rm -rf "${TEST_PYTHON_OUT_DIR}"
    else
        activate_python_virtualenv "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi

        build_cpp_binding_to_python "${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/python" \
                "${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/cpp" "${TEST_PYTHON_OUT_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
        local ZSERIO_CPP_DIR
        ZSERIO_CPP_DIR=$(ls -d1 "${TEST_PYTHON_OUT_DIR}/zserio_cpp/lib"*)
        if [ $? -ne 0 ] ; then
            stderr_echo "Failed to locate C++ runtime binding to Python!"
            return 1
        fi

        local TEST_FILTER=""
        for i in ${!TEST_SUITES[@]} ; do
            if [ $i -gt 0 ] ; then
                TEST_FILTER+=","
            fi
            TEST_FILTER+="${TEST_SUITES[$i]}"
        done

        local TEST_ARGS=("--release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                         "--build_dir=${TEST_PYTHON_OUT_DIR}"
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
                --pylint_rcfile_test="${PYLINT_RCFILE_FOR_TESTS}" --mypy_config_file="${MYPY_CONFIG_FILE}" \
                --zserio_cpp_dir="${ZSERIO_CPP_DIR}"
        local PYTHON_RESULT=$?
        if [ ${PYTHON_RESULT} -ne 0 ] ; then
            stderr_echo "Running python failed with return code ${PYTHON_RESULT}!"
            return 1
        fi

        echo "Running pylint on python test utilities."

        local PYLINT_ARGS=("--disable=missing-docstring,import-outside-toplevel,c-extension-no-member")
        PYTHONPATH="${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/python" \
                run_pylint "${PYLINT_RCFILE_FOR_TESTS}" PYLINT_ARGS[@] "${TEST_FILE}" "${TEST_SRC_DIR}/utils/python"/*
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${SWITCH_CLEAN} != 1 ]] ; then
        compare_test_data "${TEST_SRC_DIR}" "${TEST_PYTHON_OUT_DIR}" TEST_SUITES[@] "Python"
        if [ $? -ne 0 ] ; then
            stderr_echo "${MESSAGE} failed!"
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
        local TEST_DATA_ROOT_DIR="${TEST_SRC_DIR}/../data"
        local TOTAL_NUMBER_OF_TESTS=0
        for TEST_SUITE in "${TEST_SUITES[@]}" ; do
            local TEST_ZS_DIRS=`"${FIND}" "${TEST_DATA_ROOT_DIR}/${TEST_SUITE}" \
                -path '*/zs' ! -path '*errors*'`
            for TEST_ZS_DIR in ${TEST_ZS_DIRS} ; do
                local MAIN_ZS_FILES=`"${FIND}" "${TEST_ZS_DIR}" -maxdepth 1 -type f`
                for MAIN_ZS_FILE in ${MAIN_ZS_FILES} ; do
                    local MAIN_ZS_FILE_NAME="${MAIN_ZS_FILE#${TEST_ZS_DIR}/}"
                    local TEST_ZS_RELDIR="${TEST_ZS_DIR#${TEST_DATA_ROOT_DIR}/}"
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
        local TEST_DATA_ROOT_DIR="${TEST_SRC_DIR}/../data"
        local TOTAL_NUMBER_OF_TESTS=0
        for TEST_SUITE in "${TEST_SUITES[@]}" ; do
            local TEST_ZS_DIRS=`"${FIND}" "${TEST_DATA_ROOT_DIR}/${TEST_SUITE}" \
                -path '*/zs' ! -path '*errors*'`
            for TEST_ZS_DIR in ${TEST_ZS_DIRS} ; do
                local MAIN_ZS_FILES=`"${FIND}" "${TEST_ZS_DIR}" -maxdepth 1 -type f`
                for MAIN_ZS_FILE in ${MAIN_ZS_FILES} ; do
                    local MAIN_ZS_FILE_NAME="${MAIN_ZS_FILE#${TEST_ZS_DIR}/}"
                    local TEST_ZS_RELDIR="${TEST_ZS_DIR#${TEST_DATA_ROOT_DIR}/}"
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

# Run Zserio extensions tests.
test_extensions()
{
    exit_if_argc_ne $# 10
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

    local TEST_SRC_DIR="${ZSERIO_PROJECT_ROOT}/test/extensions"

    # get test suites to run
    local TEST_SUITES=()
    get_test_suites "${ZSERIO_PROJECT_ROOT}/test/data" SWITCH_TEST_PATTERN_ARRAY[@] TEST_SUITES
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [ ${#TEST_SUITES[@]} -eq 0 ] ; then
        echo "No test suites for extensions found."
        return 0
    fi

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

# Run Zserio core tests.
test_core()
{
    exit_if_argc_ne $# 5
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local PARAM_CORE="$1"; shift
    local SWITCH_CLEAN="$1"; shift

    local TEST_SRC_DIR="${ZSERIO_PROJECT_ROOT}/test/core"

    # get test suites to run
    local TEST_SUITES=()
    get_test_suites "${TEST_SRC_DIR}" SWITCH_TEST_PATTERN_ARRAY[@] TEST_SUITES
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [ ${#TEST_SUITES[@]} -eq 0 ] ; then
        echo "No test suites for core found."
        return 0
    fi

    # run Zserio core tests
    if [[ ${PARAM_CORE} != 0 ]] ; then
        local TEST_FILTER=""
        local IS_FIRST=1
        for TEST_SUITE in "${TEST_SUITES[@]}" ; do
            if [ ${IS_FIRST} -eq 0 ] ; then
                TEST_FILTER+=","
            else
                IS_FIRST=0
            fi
            TEST_FILTER+="${TEST_SUITE}"
        done

        local MESSAGE="Zserio Core tests"
        echo "STARTING - ${MESSAGE}"
        local ANT_ARGS=("-Dzserio.release_dir=${UNPACKED_ZSERIO_RELEASE_DIR}"
                        "-Dzserio_core_test.build_root_dir=${TEST_OUT_DIR}/java"
                        "-Dzserio_core_test.test_suites=${TEST_FILTER}")
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

        # calculate number of all run tests from ant output log file
        local JAVA_VERSION=`${ANT} -diagnostics | grep ant.java.version | cut -d' ' -f2`
        local ANT_LOG_FILE="${TEST_OUT_DIR}/java/${JAVA_VERSION}/test_log.txt"
        if [ ! -f "${ANT_LOG_FILE}" ] ; then
            stderr_echo "Ant output file '${ANT_LOG_FILE}' does not exist!"
            return 1
        fi
        local TEST_COUNTS=(`grep '.*tests found.*' "${ANT_LOG_FILE}" | tr -s ' ' | cut -d' ' -f4`)
        local INDEX
        for INDEX in ${!TEST_COUNTS[@]} ; do
            TOTAL_TEST_COUNT=$((${TOTAL_TEST_COUNT} + ${TEST_COUNTS[INDEX]}))
        done
        echo "Total number of testcases: ${TOTAL_TEST_COUNT}"

        echo -e "FINISHED - ${MESSAGE}\n"
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
    cpp-linux32-gcc       Zserio C++ tests for linux32 target (gcc).
    cpp-linux64-gcc       Zserio C++ tests for linux64 target (gcc).
    cpp-linux32-clang     Zserio C++ tests for linux32 target (Clang).
    cpp-linux64-clang     Zserio C++ tests for linux64 target (Clang).
    cpp-windows64-mingw   Zserio C++ tests for windows64 target (MinGW64).
    cpp-windows64-msvc    Zserio C++ tests for windows64 target (MSVC).
    java                  Zserio Java tests.
    python                Zserio Python tests.
    xml                   Zserio XML tests.
    doc                   Zserio documentation tests.
    core                  Zserio core tests.
    all-linux32-gcc       Zserio all tests with C++ for linux32 target (gcc).
    all-linux64-gcc       Zserio all tests with C++ for linux64 target (gcc).
    all-linux32-clang     Zserio all tests with C++ for linux32 target (Clang).
    all-linux64-clang     Zserio all tests with C++ for linux64 target (Clang).
    all-windows64-mingw   Zserio all tests with C++ for windows64 target (MinGW64).
    all-windows64-msvc    Zserio all tests with C++ for windows64 target (MSVC).

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
    exit_if_argc_lt $# 10
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_XML_OUT="$1"; shift
    local PARAM_DOC_OUT="$1"; shift
    local PARAM_CORE_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_CLEAN_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift
    local SWITCH_MISSING_OUT="$1"; shift
    local SWITCH_TEST_SUITES_ARRAY_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${PARAM_XML_OUT}=0
    eval ${PARAM_DOC_OUT}=0
    eval ${PARAM_CORE_OUT}=0
    eval ${SWITCH_CLEAN_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0
    eval ${SWITCH_MISSING_OUT}=0

    local NUM_PARAMS=0
    local NUM_PATTERNS=0
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

            "-c" | "--clean")
                eval ${SWITCH_CLEAN_OUT}=1
                shift
                ;;

            "-p" | "--purge")
                eval ${SWITCH_PURGE_OUT}=1
                shift
                ;;

            "-m" | "--missing")
                eval ${SWITCH_MISSING_OUT}=1
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

            "-i" | "--include")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing test include pattern!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_SUITES_ARRAY_OUT}[${NUM_PATTERNS}]="i:$2"
                NUM_PATTERNS=$((NUM_PATTERNS + 1))
                shift 2
                ;;

            "-x" | "--exclude")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing test exclude pattern!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_SUITES_ARRAY_OUT}[${NUM_PATTERNS}]="x:$2"
                NUM_PATTERNS=$((NUM_PATTERNS + 1))
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

    local NUM_CPP_TARGETS=0
    local NUM_TEST_TARGETS=0
    local PARAM
    for PARAM in "${PARAM_ARRAY[@]}" ; do
        case "${PARAM}" in
            "cpp-linux32-"* | "cpp-linux64-"* | "cpp-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#cpp-}"
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                NUM_TEST_TARGETS=$((NUM_TEST_TARGETS + 1))
                ;;

            "java")
                eval ${PARAM_JAVA_OUT}=1
                NUM_TEST_TARGETS=$((NUM_TEST_TARGETS + 1))
                ;;

            "python")
                eval ${PARAM_PYTHON_OUT}=1
                NUM_TEST_TARGETS=$((NUM_TEST_TARGETS + 1))
                ;;

            "xml")
                eval ${PARAM_XML_OUT}=1
                ;;

            "doc")
                eval ${PARAM_DOC_OUT}=1
                ;;

            "core")
                eval ${PARAM_CORE_OUT}=1
                ;;

            "all-linux32-"* | "all-linux64-"* | "all-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#all-}"
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                eval ${PARAM_XML_OUT}=1
                eval ${PARAM_DOC_OUT}=1
                eval ${PARAM_CORE_OUT}=1
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                ;;

            *)
                stderr_echo "Invalid argument '${PARAM}'!"
                echo
                return 1
        esac
    done

    if [[ ${NUM_CPP_TARGETS} -eq 0 &&
          ${!PARAM_JAVA_OUT} == 0 &&
          ${!PARAM_PYTHON_OUT} == 0 &&
          ${!PARAM_XML_OUT} == 0 &&
          ${!PARAM_DOC_OUT} == 0 &&
          ${!PARAM_CORE_OUT} == 0 &&
          ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Package to test is not specified!"
        echo
        return 1
    fi

    if [[ ${!SWITCH_MISSING_OUT} != 0 &&
          ${NUM_TEST_TARGETS} != 1 ]] ; then
        stderr_echo "For -m specify exactly one target (cpp-* / java / python)"
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
    local PARAM_CORE
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_CLEAN
    local SWITCH_PURGE
    local SWITCH_MISSING
    local SWITCH_TEST_PATTERN_ARRAY=()
    # note that "$@" must have qoutes to prevent expansion of include/exclude patterns
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_XML PARAM_DOC PARAM_CORE \
        PARAM_OUT_DIR SWITCH_CLEAN SWITCH_PURGE SWITCH_MISSING SWITCH_TEST_PATTERN_ARRAY "$@"
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

    if [[ ${PARAM_JAVA} != 0 || ${PARAM_CORE} != 0 ]] ; then
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

    # print missing tests
    if [[ ${SWITCH_MISSING} == 1 ]] ; then
        local PLATFORM="cpp"
        if [[ ${PARAM_JAVA} != 0 ]] ; then
            PLATFORM="java"
        elif [[ ${PARAM_PYTHON} != 0 ]] ; then
            PLATFORM="python"
        fi
        print_missing_tests "${ZSERIO_PROJECT_ROOT}" ${PLATFORM}
        return 0
    fi

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
              ${PARAM_DOC} == 0 &&
              ${PARAM_CORE} == 0 ]] ; then
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

    # run extensions tests
    test_extensions "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
        "${TEST_OUT_DIR}/extensions" PARAM_CPP_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_XML} \
        ${PARAM_DOC} ${SWITCH_CLEAN}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # run core tests
    test_core "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/core" \
        ${PARAM_CORE} ${SWITCH_CLEAN}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
