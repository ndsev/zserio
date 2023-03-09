#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_test_tools.sh"
source "${SCRIPT_DIR}/test_perf.sh"

# Set and check benchmark global variables.
set_benchmark_global_variables()
{
    # ZIP to use, defaults to "zip" if not set
    ZIP="${ZIP:-zip}"
    if [ ! -f "`which "${ZIP}"`" ] ; then
        stderr_echo "Cannot find zip! Set ZIP environment variable."
        return 1
    fi

    return 0
}

# Print help on the environment variables used for this script.
print_benchmark_help_env()
{
    cat << EOF
Uses the following environment variables for benchmarking:
    ZIP                 Zip executable to use. Default is "zip".

EOF
}

# Run a single benchmark with a single dataset.
run_benchmark()
{
    exit_if_argc_ne $# 15
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local BENCHMARKS_SRC_DIR="$1"; shift
    local BENCHMARKS_OUT_DIR="$1"; shift
    local BENCHMARK="$1"; shift
    local DATASET="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local PARAM_PYTHON_CPP="$1"; shift
    local TEST_CONFIG="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local SWITCH_RUN_ONLY="$1"; shift
    local LOG_FILE="$1"; shift

    local BLOB_FILE=""
    local PROFILE=0

    local BENCHMARK_DIR="${BENCHMARK%/*}"
    local BENCHMARK_ZS="${BENCHMARK##*/}"
    local ZSERIO_PACKAGE_NAME="${BENCHMARK_ZS%.*}"
    local DATASET_FILENAME="${DATASET##*/}"
    local DATASET_NAME="${DATASET_FILENAME%.*}"

    local FIRST_STRUCT
    FIRST_STRUCT=$(grep -m 1 -w "struct" "${BENCHMARKS_SRC_DIR}/${BENCHMARK}" | cut -d\  -f2)
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to get the root object (first struct)!"
        return 1
    fi

    local TEST_OUT_DIR="${BENCHMARKS_OUT_DIR}/${ZSERIO_PACKAGE_NAME}_${DATASET_NAME}"

    test_perf "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
              "${TEST_OUT_DIR}" CPP_TARGETS[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_PYTHON_CPP} \
              "${BENCHMARKS_SRC_DIR}/${BENCHMARK_DIR}" "${BENCHMARK_ZS}" \
              "${ZSERIO_PACKAGE_NAME}.${FIRST_STRUCT}" \
              "${DATASET}" "${BLOB_FILE}" ${NUM_ITERATIONS} ${TEST_CONFIG} ${SWITCH_RUN_ONLY} ${PROFILE}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    local BLOBS=($("${FIND}" "${TEST_OUT_DIR}" -iname "${ZSERIO_PACKAGE_NAME}.blob"))
    if [ ${#BLOBS[@]} -eq 0 ] ; then
        stderr_echo "Failed to find blobs created by performance test!"
        return 1
    fi
    local BLOB=${BLOBS[0]} # all blobs are same
    local ZIP_FILE=${BLOB/%blob/zip}
    "${ZIP}" "${ZIP_FILE}" "${BLOB}" > /dev/null
    if [ $? -ne 0 ] ; then
        stderr_echo "Failed to zip blob created by performance test!"
        return 1
    fi
    local ZIP_SIZE="$(du --block-size=1000 ${ZIP_FILE} | cut -f1)kB"

    local LOGS=($(${FIND} "${TEST_OUT_DIR}" -iname "PerformanceTest*.log"))
    local TARGET
    local RESULTS
    for LOG in "${LOGS[@]}" ; do
        TARGET="${LOG#"${TEST_OUT_DIR}/"}"
        if [[ "${TARGET}" == "cpp/"* ]] ; then
            TARGET="${TARGET#cpp/}"
            TARGET="${TARGET%%/*}"
            TARGET="C++ (${TARGET})"
        elif [[ "${TARGET}" == "java/"* ]] ; then
            TARGET="Java"
        elif [[ "${TARGET}" == "python/python-pure"* ]] ; then
            TARGET="Python"
        elif [[ "${TARGET}" == "python/python-cpp"* ]] ; then
            TARGET="Python (C++)"
        fi

        RESULTS=($(cat ${LOG}))
        printf "| %-22s | %-22s | %-22s | %10s | %10s | %10s | %10s | %10s |\n" \
               "${BENCHMARK_ZS}" "${DATASET_FILENAME}" "${TARGET}" \
               ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]} ${RESULTS[3]} "${ZIP_SIZE}" >> "${LOG_FILE}"
    done

    return 0
}

# Run requested benchmarks with all available datasets.
run_benchmarks()
{
    exit_if_argc_ne $# 14
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local BENCHMARKS_SRC_DIR="$1"; shift
    local BENCHMARKS_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local BENCHMARKS=("${MSYS_WORKAROUND_TEMP[@]}")
    local DATASETS_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local PARAM_PYTHON_CPP="$1"; shift
    local TEST_CONFIG="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local SWITCH_RUN_ONLY="$1"; shift

    local LOG_FILE="${BENCHMARKS_OUT_DIR}/benchmarks.log"
    rm -f ${LOG_FILE}
    printf "| %-22s | %-22s | %-22s | %10s | %10s | %10s | %10s | %10s |\n" \
           "Benchmark" "Dataset" "Target" "Total Time" "Iterations" "Step Time" "Blob Size" "Zip Size" >> \
           "${LOG_FILE}"
    printf "| %-22s | %-22s | %-22s | %10s | %10s | %10s | %10s | %10s |\n" \
            "$(for i in {1..22} ; do echo -n "-" ; done)" \
            "$(for i in {1..22} ; do echo -n "-" ; done)" \
            "$(for i in {1..22} ; do echo -n "-" ; done)" \
            "$(for i in {1..10} ; do echo -n "-" ; done)" \
            "$(for i in {1..10} ; do echo -n "-" ; done)" \
            "$(for i in {1..10} ; do echo -n "-" ; done)" \
            "$(for i in {1..10} ; do echo -n "-" ; done)" \
            "$(for i in {1..10} ; do echo -n "-" ; done)" >> "${LOG_FILE}"

    if [[ ! -d "${DATASETS_DIR}" ]] ; then
        stderr_echo "Datasets dir '${DATASETS_DIR}' does not exists!"
        return 1
    fi

    local DATASETS
    for BENCHMARK in "${BENCHMARKS[@]}" ; do
        local DATASETS=($(${FIND} "${DATASETS_DIR}/${BENCHMARK%/*}" -iname "*.json" ! -iname "*.schema.*"))

        for DATASET in "${DATASETS[@]}" ; do
            run_benchmark "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
                          "${BENCHMARKS_SRC_DIR}" "${BENCHMARKS_OUT_DIR}" "${BENCHMARK}" "${DATASET}" \
                          CPP_TARGETS[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_PYTHON_CPP} \
                          ${TEST_CONFIG} ${NUM_ITERATIONS} ${SWITCH_RUN_ONLY} ${LOG_FILE}
            if [[ $? -ne 0 ]] ; then
                stderr_echo "Benchmark ${BENCHMARK} failed!"
                return 1
            fi
        done
    done

    cat "${LOG_FILE}"
}

# Get benchmarks to run based on the include / exclude patterns.
get_benchmarks()
{
    exit_if_argc_ne $# 3
    local BENCHMARKS_SRC_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local PATTERNS=("${MSYS_WORKAROUND_TEMP[@]}")
    local BENCHMARKS_OUT="$1"; shift

    local FIND_EXPRESSION=("!" "-ipath" "${BENCHMARKS_SRC_DIR}/datasets/*")
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
            FIND_EXPRESSION+=("-ipath" "${BENCHMARKS_SRC_DIR}/${PATTERN}")
        elif [[ $PATTERN == "x:"* ]] ; then
            PATTERN="${PATTERN#x:}"
            if [ $i -gt 0 ] ; then
                FIND_EXPRESSION+=("-a")
            fi
            FIND_EXPRESSION+=("!" "-ipath" "${BENCHMARKS_SRC_DIR}/${PATTERN}")
        else
            stderr_echo "Unexpected pattern!"
            return 1
        fi
        FIND_EXPRESSION+=(")")
    done

    local BENCHMARKS_ARR=(
        $(${FIND} "${BENCHMARKS_SRC_DIR}" -mindepth 2 -maxdepth 2 -type f "${FIND_EXPRESSION[@]}" | sort)
    )

    for i in ${!BENCHMARKS_ARR[@]} ; do
        eval ${BENCHMARKS_OUT}[$i]="${BENCHMARKS_ARR[$i]#${BENCHMARKS_SRC_DIR}/}"
    done
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs performance tests on given zserio sources with zserio release compiled in release-ver directory.

Usage:
    $0 [-h] [-e] [-p] [-r] [-o <dir>] [-d <dir>] [-c <config>] [-i <pattern>]... [-x <pattern>]...
            generator...

Arguments:
    -h, --help              Show this help.
    -e, --help-env          Show help for enviroment variables.
    -p, --purge             Purge test build directory.
    -o <dir>, --output-directory <dir>
                            Output directory where tests will be run.
    -d <dir>, --datasets-directory <dir>
                            Directory containing datasets. Optional, default is "benchmarks/datasets" folder
                            in project root.
    -i <pattern>, --include <pattern>
                            Include benchmarks matching the specified pattern. Can be specified multiple times.
    -x <pattern>, --exclude <pattern>
                            Exclude benchmarks matching the specified pattern. Can be specified multiple times.

Argumnets passed to test_perf.sh:
    -r, --run-only          Run already compiled PerformanceTests again.
    -c <config>, --test-config <config>
                            Test configuration, default is READ.
    -n <num>, --num-iterations <num>
                            Number of iterations. Optional, default is 100.
    generator               Specify the generator to test.

Example:
    $0 cpp-linux64-gcc

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
    local PARAM_PYTHON_CPP_OUT="$1"; shift
    local SWITCH_OUT_DIR_OUT="$1"; shift
    local SWITCH_DATASETS_DIR_OUT="$1"; shift
    local SWITCH_TEST_CONFIG_OUT="$1"; shift
    local SWITCH_NUM_ITERATIONS_OUT="$1"; shift
    local SWITCH_BENCHMARKS_PATTERN_ARRAY_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift
    local SWITCH_RUN_ONLY_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${PARAM_PYTHON_CPP_OUT}=0
    eval ${SWITCH_TEST_CONFIG_OUT}="READ"
    eval ${SWITCH_NUM_ITERATIONS_OUT}=100
    eval ${SWITCH_PURGE_OUT}=0
    eval ${SWITCH_RUN_ONLY_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=()
    local NUM_PATTERNS=0
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

            "-r" | "--run-only")
                eval ${SWITCH_RUN_ONLY_OUT}=1
                shift
                ;;

            "-o" | "--output-directory")
                eval ${SWITCH_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-c" | "--test-config")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Test configuration is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_CONFIG_OUT}="${ARG}"
                shift
                ;;

            "-d" | "--datasets-directory")
                eval ${SWITCH_DATASETS_DIR_OUT}="$2"
                shift 2
                ;;

            "-n" | "--num-iterations")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Number of iterations is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_NUM_ITERATIONS_OUT}="${ARG}"
                shift
                ;;

            "-i" | "--include")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Include pattern is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_BENCHMARKS_PATTERN_ARRAY_OUT}[${NUM_PATTERNS}]="i:${ARG}"
                NUM_PATTERNS=$((NUM_PATTERNS + 1))
                shift
                ;;

            "-x" | "--exclude")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Exclude pattern is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_BENCHMARKS_PATTERN_ARRAY_OUT}[${NUM_PATTERNS}]="x:${ARG}"
                NUM_PATTERNS=$((NUM_PATTERNS + 1))
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

    local NUM_CPP_TARGETS=0
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

            "python-cpp")
                eval ${PARAM_PYTHON_CPP_OUT}=1
                ;;

            "all-linux32-"* | "all-linux64-"* | "all-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#all-}"
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                eval ${PARAM_PYTHON_CPP_OUT}=1
                ;;

            *)
                stderr_echo "Invalid argument '${PARAM}'!"
                echo
                return 1
        esac
    done

    # validate test configuration
    case "${!SWITCH_TEST_CONFIG_OUT}" in
        "READ" | "WRITE" | "READ_WRITE")
            ;;
        *)
            stderr_echo "Invalid test configuration, use one of READ, WRITE, READ_WRITE"
            return 1
            ;;
    esac

    if [[ ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        if [[ "${!SWITCH_DATASETS_DIR_OUT}" == "" ]] ; then
            stderr_echo "Datasets directory is not set!"
            echo
            return 1
        fi

        if [[ ${NUM_PARAMS} == 0 ]] ; then
            stderr_echo "No generator set!"
            echo
            return 1
        fi
    fi

    return 0
}

main()
{
    # get the project root, absolute path is necessary only for CMake
    local ZSERIO_PROJECT_ROOT
    convert_to_absolute_path "${SCRIPT_DIR}/.." ZSERIO_PROJECT_ROOT

    local PARAM_CPP_TARGET_ARRAY=()
    local PARAM_JAVA
    local PARAM_PYTHON
    local PARAM_PYTHON_CPP
    local SWITCH_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_DATASETS_DIR="${ZSERIO_PROJECT_ROOT}"/benchmarks/datasets
    local SWITCH_TEST_CONFIG
    local SWITCH_NUM_ITERATIONS
    local SWITCH_BENCHMARKS_PATTERN_ARRAY=()
    local SWITCH_PURGE
    local SWITCH_RUN_ONLY
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_PYTHON_CPP \
                    SWITCH_OUT_DIR SWITCH_DATASETS_DIR SWITCH_TEST_CONFIG SWITCH_NUM_ITERATIONS \
                    SWITCH_BENCHMARKS_PATTERN_ARRAY SWITCH_PURGE SWITCH_RUN_ONLY "$@"
    local PARSE_RESULT=$?
    if [ ${PARSE_RESULT} -eq 2 ] ; then
        print_help
        return 0
    elif [ ${PARSE_RESULT} -eq 3 ] ; then
        print_benchmark_help_env
        print_test_help_env
        print_help_env
        return 0
    elif [ ${PARSE_RESULT} -ne 0 ] ; then
        return 1
    fi

    echo "Zserio Benchmarks"
    echo

    convert_to_absolute_path "${SWITCH_OUT_DIR}" SWITCH_OUT_DIR

    local ZSERIO_BUILD_DIR="${SWITCH_OUT_DIR}/build"
    local BENCHMARKS_SRC_DIR="${ZSERIO_PROJECT_ROOT}"/benchmarks
    local BENCHMARKS_OUT_DIR="${ZSERIO_BUILD_DIR}"/benchmarks

    if [[ ${SWITCH_PURGE} != 0 ]] ; then
        echo "Purging benchmark directory."
        echo
        rm -rf "${BENCHMARKS_OUT_DIR}/"

        if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} == 0 &&
              ${PARAM_JAVA} == 0 &&
              ${PARAM_PYTHON} == 0 &&
              ${PARAM_PYTHON_CPP} == 0 ]] ; then
            return 0  # purge only
        fi
    fi
    mkdir -p "${BENCHMARKS_OUT_DIR}"

    set_global_common_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    set_test_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    set_benchmark_global_variables
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} -ne 0 ]] ; then
        set_global_cpp_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_JAVA} -ne 0 ]] ; then
        set_global_java_variables
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    if [[ ${PARAM_PYTHON} != 0 || ${PARAM_PYTHON_CPP} != 0 ]] ; then
        set_global_python_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # get benchmarks to run
    local BENCHMARKS=()
    get_benchmarks "${BENCHMARKS_SRC_DIR}" SWITCH_BENCHMARKS_PATTERN_ARRAY[@] BENCHMARKS
    if [ $? -ne 0 ] ; then
        return 1
    fi

    if [[ ${#BENCHMARKS[@]} == 0 ]] ; then
        echo "No benchmarks to run!"
        return 1
    fi

    # get zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" "${SWITCH_OUT_DIR}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # print information
    echo "Testing release: ${ZSERIO_RELEASE_DIR}"
    echo "Benchmarks output directory: ${BENCHMARKS_OUT_DIR}"
    echo "Datasets directory: ${SWITCH_DATASETS_DIR}"
    echo

    # unpack testing release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${BENCHMARKS_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" \
                   UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    run_benchmarks "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
                   "${BENCHMARKS_SRC_DIR}" "${BENCHMARKS_OUT_DIR}" BENCHMARKS[@] "${SWITCH_DATASETS_DIR}" \
                   PARAM_CPP_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_PYTHON_CPP} \
                   ${SWITCH_TEST_CONFIG} ${SWITCH_NUM_ITERATIONS} ${SWITCH_RUN_ONLY}
    if [[ $? -ne 0 ]] ; then
        return 1
    fi
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
