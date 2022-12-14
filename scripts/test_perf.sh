#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_test_tools.sh"

# Generate Ant build.xml file and src/PerformanceTest.java
generate_java_files()
{
    exit_if_argc_ne $# 6
    local ZSERIO_RELEASE="$1"; shift
    local BUILD_DIR="$1"; shift
    local BLOB_FULL_NAME="$1"; shift
    local BLOB_PATH="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local TEST_CONFIG="$1"; shift

    local LOG_PATH="${BUILD_DIR}/PerformanceTest.log"

    # use host paths in generated files
    posix_to_host_path "${ZSERIO_RELEASE}" HOST_ZSERIO_RELEASE
    posix_to_host_path "${BUILD_DIR}" HOST_BUILD_DIR
    posix_to_host_path "${LOG_PATH}" HOST_LOG_PATH
    posix_to_host_path "${BLOB_PATH}" HOST_BLOB_PATH

    cat > "${BUILD_DIR}"/build.xml << EOF
<project name="performance_test" basedir="." default="run">
    <property name="zserio.release_dir" location="${HOST_ZSERIO_RELEASE}"/>

    <property name="runtime.jar_dir" location="\${zserio.release_dir}/runtime_libs/java"/>
    <property name="runtime.jar_file_name" value="zserio_runtime.jar"/>
    <property name="runtime.jar_file" location="\${runtime.jar_dir}/\${runtime.jar_file_name}"/>

    <property name="test_perf.build_dir" location="${HOST_BUILD_DIR}"/>
    <property name="test_perf.classes_dir" location="\${test_perf.build_dir}/classes"/>
    <property name="test_perf.jar_dir" location="\${test_perf.build_dir}/jar"/>
    <property name="test_perf.jar_file" location="\${test_perf.jar_dir}/performance_test.jar"/>
    <property name="test_perf.src_dir" location="\${test_perf.build_dir}/src"/>
    <property name="test_perf.gen_dir" location="\${test_perf.build_dir}/gen"/>

    <target name="prepare">
        <mkdir dir="\${test_perf.classes_dir}"/>
    </target>

    <target name="compile" depends="prepare">
        <depend srcDir="\${test_perf.src_dir}:\${test_perf.gen_dir}"
            destDir="\${test_perf.classes_dir}"
            cache="\${test_perf.build_dir}/depend-cache"/>
        <javac destdir="\${test_perf.classes_dir}" debug="on" encoding="utf8" includeAntRuntime="false">
            <compilerarg value="-Xlint:all"/>
            <compilerarg value="-Xlint:-cast"/>
            <compilerarg value="-Werror"/>
            <classpath>
                <pathelement location="\${runtime.jar_file}"/>
            </classpath>
            <src path="\${test_perf.src_dir}"/>
            <src path="\${test_perf.gen_dir}"/>
        </javac>
    </target>

    <target name="jar" depends="compile">
        <copy file="\${runtime.jar_file}" todir="\${test_perf.jar_dir}"/>
        <jar destfile="\${test_perf.jar_file}" basedir="\${test_perf.classes_dir}">
            <manifest>
                <attribute name="Main-Class" value="PerformanceTest"/>
                <attribute name="Class-Path" value="\${runtime.jar_file_name}"/>
            </manifest>
        </jar>
    </target>

    <target name="run" depends="jar">
        <java jar="\${test_perf.jar_file}" fork="true" failonerror="true">
            <arg file="${HOST_LOG_PATH}"/>
            <arg file="${HOST_BLOB_PATH}"/>
            <arg value="${NUM_ITERATIONS}"/>
        </java>
    </target>

    <target name="clean">
        <delete dir="\${test_perf.classes_dir}"/>
        <delete dir="\${test_perf.jar_dir}"/>
        <delete dir="\${test_perf.build_dir}/depend-cache"/>
    </target>
</project>
EOF

    mkdir -p "${BUILD_DIR}/src"
    cat > "${BUILD_DIR}"/src/PerformanceTest.java << EOF
import java.io.File;
import java.io.PrintStream;

import zserio.runtime.io.SerializeUtil;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class PerformanceTest
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Zserio Java Performance Test");

        if (args.length < 2)
        {
            System.err.println("No enough arguments!");
            System.err.println("Usage: PerformanceTest LOG_PATH BLOB_PATH [NUM_ITERATIONS]");
            System.exit(1);
        }

        final String logPath = args[0];
        final String blobPath = args[1];
        final int numIterations = args.length > 2 ? Integer.parseInt(args[2]) : ${NUM_ITERATIONS};

        // prepare byte array
        final ${BLOB_FULL_NAME} blobFromFile = SerializeUtil.deserializeFromFile(
                ${BLOB_FULL_NAME}.class, blobPath);
        final ByteArrayBitStreamWriter bufferWriter = new ByteArrayBitStreamWriter();
        blobFromFile.write(bufferWriter);
        final byte[] byteArray = bufferWriter.toByteArray();

        // run the test
        final long startTime = System.nanoTime();
        for (int i = 0; i < numIterations; ++i)
        {
EOF

    case "${TEST_CONFIG}" in
        "READ")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(byteArray);
            final ${BLOB_FULL_NAME} blob = new ${BLOB_FULL_NAME}(reader);
EOF
            ;;
        "READ_WRITE")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(byteArray);
            final ${BLOB_FULL_NAME} blob = new ${BLOB_FULL_NAME}(reader);
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            blob.write(writer);
EOF
            ;;

        "WRITE")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            blobFromFile.write(writer);
EOF
            ;;
    esac

    cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
        }
        final long stopTime = System.nanoTime();

        // process results
        final long duration = stopTime - startTime;
        final double totalDuration = duration / 1000000.;
        final double stepDuration = totalDuration / numIterations;
        System.out.println("Total Duration: " + String.format("%.3f", totalDuration) + "ms");
        System.out.println("Iterations:     " + numIterations);
        System.out.println("Step Duration:  " + String.format("%.3f", stepDuration) + "ms");

        // write results to file
        PrintStream logFile = new PrintStream(new File(logPath));
        logFile.println(String.format("%.03fms %d %.03fms", totalDuration, numIterations, stepDuration));
        logFile.close();
    }
};
EOF
}

# Generate C++ files
generate_cpp_files()
{
    exit_if_argc_ne $# 8
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_RELEASE="$1"; shift
    local BUILD_DIR="$1"; shift
    local BLOB_FULL_NAME="$1"; shift
    local BLOB_PATH="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local TEST_CONFIG="$1"; shift
    local GENERATOR="$1"; shift

    local LOG_PATH="${BUILD_DIR}/PerformanceTest.log"

    # use host paths in generated files
    local DISABLE_SLASHES_CONVERSION=1
    posix_to_host_path "${ZSERIO_PROJECT_ROOT}" HOST_ZSERIO_ROOT ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${ZSERIO_RELEASE}" HOST_ZSERIO_RELEASE ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${LOG_PATH}" HOST_LOG_PATH ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${BLOB_PATH}" HOST_BLOB_PATH ${DISABLE_SLASHES_CONVERSION}

    cat > "${BUILD_DIR}"/CMakeLists.txt << EOF
cmake_minimum_required(VERSION 3.1.0)
project(PerformanceTest)

enable_testing()

set(ZSERIO_ROOT "${HOST_ZSERIO_ROOT}" CACHE PATH "")
set(ZSERIO_RELEASE "${HOST_ZSERIO_RELEASE}" CACHE PATH "")
set(LOG_PATH "${HOST_LOG_PATH}")
set(BLOB_PATH "${HOST_BLOB_PATH}")
set(CMAKE_MODULE_PATH "\${ZSERIO_ROOT}/cmake")

# cmake helpers
include(cmake_utils)

# setup compiler
include(compiler_utils)
compiler_set_pthread()
compiler_set_static_clibs()
compiler_set_warnings()

# add zserio runtime library
include(zserio_utils)
set(ZSERIO_RUNTIME_LIBRARY_DIR "\${ZSERIO_RELEASE}/runtime_libs/${GENERATOR}")
zserio_add_runtime_library(RUNTIME_LIBRARY_DIR "\${ZSERIO_RUNTIME_LIBRARY_DIR}")

file(GLOB_RECURSE SOURCES RELATIVE "\${CMAKE_CURRENT_SOURCE_DIR}" "gen/*.cpp" "gen/*.h")

add_executable(\${PROJECT_NAME} src/PerformanceTest.cpp \${SOURCES})
# CXX_EXTENSIONS are necessary for old MinGW32 to support clock_gettime method
set_target_properties(\${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS YES)

target_include_directories(\${PROJECT_NAME} PUBLIC "\${CMAKE_CURRENT_SOURCE_DIR}/gen")
target_link_libraries(\${PROJECT_NAME} ZserioCppRuntime)

add_test(NAME PerformanceTest COMMAND \${PROJECT_NAME} \${LOG_PATH} \${BLOB_PATH})
EOF

    local BLOB_INCLUDE_PATH=${BLOB_FULL_NAME//.//}.h
    local BLOB_CLASS_FULL_NAME=${BLOB_FULL_NAME//./::}

    mkdir -p "${BUILD_DIR}/src"
    cat > "${BUILD_DIR}"/src/PerformanceTest.cpp << EOF
#include <fstream>
#include <iostream>
#include <iomanip>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>

#include <${BLOB_INCLUDE_PATH}>

#if defined(_WIN32) || defined(_WIN64)
#   include <windows.h>
#else
#   include <time.h>
#endif

class PerfTimer
{
public:
    static uint64_t getMicroTime()
    {
#if defined(_WIN32) || defined(_WIN64)
        FILETIME creation, exit, kernelTime, userTime;
        GetThreadTimes(GetCurrentThread(), &creation, &exit, &kernelTime, &userTime);
        return fileTimeToMicro(kernelTime) + fileTimeToMicro(userTime);
#else
        struct timespec ts;
        clock_gettime(CLOCK_THREAD_CPUTIME_ID, &ts);
        return static_cast<uint64_t>(ts.tv_sec) * 1000000 + static_cast<uint64_t>(ts.tv_nsec) / 1000;
#endif
    }

private:
#if defined(_WIN32) || defined(_WIN64)
    static uint64_t fileTimeToMicro(const FILETIME& time)
    {
        uint64_t value = time.dwHighDateTime;
        value <<= 8 * sizeof(time.dwHighDateTime);
        value |= static_cast<uint64_t>(time.dwLowDateTime);
        value /= 10;

        return value;
    }
#endif
};

int main(int argc, char* argv[])
{
    std::cout << "Zserio C++ Performance Test" << std::endl;

    if (argc < 3)
    {
        std::cerr << "No enough arguments!" << std::endl;
        std::cerr << "Usage: PerformanceTest LOG_PATH BLOB_PATH [NUM_ITERATIONS]" << std::endl;
        return 1;
    }

    const char* logPath = argv[1];
    const char* blobPath = argv[2];
    int numIterations = ${NUM_ITERATIONS};
    if (argc > 3)
        numIterations = atoi(argv[3]);

    // read file
    std::ifstream is(blobPath, std::ifstream::binary);
    if (!is)
    {
        std::cerr << std::string("Cannot open '") + blobPath + "' for reading!" << std::endl;
        return 1;
    }
    is.seekg(0, is.end);
    const size_t blobByteSize = static_cast<size_t>(is.tellg());
    is.seekg(0);
    std::vector<uint8_t> blobBuffer(blobByteSize);
    is.read(reinterpret_cast<char*>(&blobBuffer[0]), static_cast<std::streamsize>(blobByteSize));
    if (!is)
    {
        std::cerr << std::string("Failed to read '") + blobPath + "'!" << std::endl;
        return 1;
    }

    // prepare test buffer
    zserio::BitStreamReader blobReader(blobBuffer);
    ${BLOB_CLASS_FULL_NAME} blobFromFile(blobReader);

    uint8_t* buffer = &blobBuffer[0];
    size_t bufferBitSize = blobReader.getBitPosition();

    // run the test
    const uint64_t start = PerfTimer::getMicroTime();
    for (int i = 0; i < numIterations; ++i)
    {
EOF

    case "${TEST_CONFIG}" in
        "READ")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.cpp << EOF
        zserio::BitStreamReader reader(buffer, bufferBitSize, zserio::BitsTag());
        ${BLOB_CLASS_FULL_NAME} blob(reader);
EOF
            ;;
        "READ_WRITE")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.cpp << EOF
        zserio::BitStreamReader reader(buffer, bufferBitSize, zserio::BitsTag());
        ${BLOB_CLASS_FULL_NAME} blob(reader);
        zserio::BitStreamWriter writer(buffer, bufferBitSize, zserio::BitsTag());
        blob.write(writer);
EOF
            ;;

        "WRITE")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.cpp << EOF
        zserio::BitStreamWriter writer(buffer, bufferBitSize, zserio::BitsTag());
        blobFromFile.write(writer);
EOF
            ;;
    esac

cat >> "${BUILD_DIR}"/src/PerformanceTest.cpp << EOF
    }
    const uint64_t stop = PerfTimer::getMicroTime();

    // process results
    double totalDuration = static_cast<double>(stop - start) / 1000.;
    double stepDuration = totalDuration / numIterations;
    std::cout << std::fixed << std::setprecision(3);
    std::cout << "Total Duration: " << totalDuration << "ms" << std::endl;
    std::cout << "Iterations:     " << numIterations << std::endl;
    std::cout << "Step Duration:  " << stepDuration << "ms" << std::endl;

    // write results to file
    std::ofstream logFile(logPath);
    logFile << std::fixed << std::setprecision(3);
    logFile << totalDuration << "ms " << numIterations << " " << stepDuration << "ms" << std::endl;

    return 0;
}
EOF
}

# Generate python peftest.py
generate_python_perftest()
{
    exit_if_argc_ne $# 4
    local BUILD_DIR="$1"; shift
    local BLOB_FULL_NAME="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local TEST_CONFIG="$1"; shift

    local API_MODULE=${BLOB_FULL_NAME%%.*}
    local BLOB_API_PATH=${BLOB_FULL_NAME#*.}

    mkdir -p "${BUILD_DIR}/src"
    cat > "${BUILD_DIR}/src/perftest.py" << EOF
import sys
import argparse
from timeit import default_timer as timer
import cProfile
import re
import os

import zserio
import ${API_MODULE}.api as api

def performance_test(log_path, blob_path, num_iterations):
    print("Zserio Python Performance Test")

    # prepare byte array

    with open(blob_path, 'rb') as file:
        reader_from_file = zserio.BitStreamReader(file.read())
    blob_from_file = api.${BLOB_API_PATH}.from_reader(reader_from_file)
    buffer_writer = zserio.BitStreamWriter()
    blob_from_file.write(buffer_writer)
    byte_array = buffer_writer.byte_array

    # run the test
    pr = cProfile.Profile()
    start = timer()
    pr.enable()
    for i in range(num_iterations):
EOF

    case "${TEST_CONFIG}" in
        "READ")
            cat >> "${BUILD_DIR}/src/perftest.py" << EOF
        reader = zserio.BitStreamReader(byte_array)
        blob = api.${BLOB_API_PATH}.from_reader(reader)
EOF
            ;;
        "READ_WRITE")
            cat >> "${BUILD_DIR}/src/perftest.py" << EOF
        reader = zserio.BitStreamReader(byte_array)
        blob = api.${BLOB_API_PATH}.from_reader(reader)
        writer = zserio.BitStreamWriter()
        blob.write(writer)
EOF
            ;;

        "WRITE")
            cat >> "${BUILD_DIR}/src/perftest.py" << EOF
        writer = zserio.BitStreamWriter()
        blob_from_file.write(writer)
EOF
            ;;
    esac

    cat >> "${BUILD_DIR}/src/perftest.py" << EOF
    pr.disable()
    stop = timer()
    prof_path = re.sub("\..*$", ".prof", log_path)
    if pr.getstats():
        pr.dump_stats(prof_path)
    elif os.path.exists(prof_path):
        os.remove(prof_path)

    # process results
    total_duration = (stop - start) * 1000
    step_duration = total_duration / num_iterations
    print("Total Duration: %.03fms" % total_duration)
    print("Iterations:     %d" % num_iterations)
    print("Step Duration   %.03fms" % step_duration)

    # write results to file
    log_file = open(log_path, "w")
    log_file.write("%.03fms %d %.03fms" % (total_duration, num_iterations, step_duration))

if __name__ == "__main__":
    sys.setrecursionlimit(5000) # empiric constant to prevent failing during testing on recursive blobs

    arg_parser = argparse.ArgumentParser(description="Zserio Python Performance Test")
    arg_parser.add_argument("--log-path", required=True, help="Path to the log file to create")
    arg_parser.add_argument('--blob-path', required=True, help="Path to the blob file")
    arg_parser.add_argument('--num-iterations', default=${NUM_ITERATIONS}, type=int, help="Number of iterations")
    args = arg_parser.parse_args()

    performance_test(args.log_path, args.blob_path, args.num_iterations)
EOF
}

# Run zserio performance tests.
test_perf()
{
    exit_if_argc_ne $# 14
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local SWITCH_DIRECTORY="$1"; shift
    local SWITCH_SOURCE="$1"; shift
    local SWITCH_TEST_NAME="$1"; shift
    local SWITCH_BLOB_NAME="$1"; shift
    local SWITCH_BLOB_PATH="$1"; shift
    local SWITCH_NUM_ITERATIONS="$1"; shift
    local SWITCH_TEST_CONFIG="$1"; shift

    convert_to_absolute_path "${SWITCH_BLOB_PATH}" SWITCH_BLOB_PATH

    # generate sources using zserio
    local ZSERIO_ARGS=()
    if [[ ${#CPP_TARGETS[@]} -ne 0 ]] ; then
        rm -rf "${TEST_OUT_DIR}/cpp"
        ZSERIO_ARGS+=("-cpp" "${TEST_OUT_DIR}/cpp/gen")
    fi
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        rm -rf "${TEST_OUT_DIR}/java"
        ZSERIO_ARGS+=("-java" "${TEST_OUT_DIR}/java/gen")
    fi
    if [[ ${PARAM_PYTHON} == 1 ]] ; then
        rm -rf "${TEST_OUT_DIR}/python"
        ZSERIO_ARGS+=("-python" "${TEST_OUT_DIR}/python/gen")
    fi

    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}" \
        "${SWITCH_DIRECTORY}" "${SWITCH_SOURCE}" 0 ZSERIO_ARGS[@]
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # run java performance test
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        generate_java_files "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}/java" "${SWITCH_BLOB_NAME}" \
                            "${SWITCH_BLOB_PATH}" ${SWITCH_NUM_ITERATIONS} ${SWITCH_TEST_CONFIG}
        ANT_ARGS=()
        compile_java "${TEST_OUT_DIR}/java/build.xml" ANT_ARGS[@] "run"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run C++ performance test
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        generate_cpp_files "${ZSERIO_PROJECT_ROOT}" "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}/cpp" \
                           "${SWITCH_BLOB_NAME}" "${SWITCH_BLOB_PATH}" ${SWITCH_NUM_ITERATIONS} \
                           ${SWITCH_TEST_CONFIG} "cpp"
        local CMAKE_ARGS=()
        local CTEST_ARGS=()
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/cpp" "${TEST_OUT_DIR}/cpp" \
                    CPP_TARGETS[@] CMAKE_ARGS[@] CTEST_ARGS[@] all
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run Python performance test
    if [[ ${PARAM_PYTHON} == 1 ]] ; then
        activate_python_virtualenv "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi

        generate_python_perftest "${TEST_OUT_DIR}/python" "${SWITCH_BLOB_NAME}" ${SWITCH_NUM_ITERATIONS} \
                                 ${SWITCH_TEST_CONFIG}

        PYTHONPATH="${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/python:${TEST_OUT_DIR}/python/gen" \
        python ${TEST_OUT_DIR}/python/src/perftest.py \
               --log-path="${TEST_OUT_DIR}/python/perftest.log" \
               --blob-path "${SWITCH_BLOB_PATH}"
        if [ -f ${TEST_OUT_DIR}/python/perftest.prof ] ; then
            #python -m snakeviz ${TEST_OUT_DIR}/python/perftest.prof
            python -m pyprof2calltree -k -i ${TEST_OUT_DIR}/python/perftest.prof
        fi
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # collect results
    echo
    echo "Performance Tests Results - ${SWITCH_TEST_CONFIG}"
    for i in {1..72} ; do echo -n "=" ; done ; echo
    printf "| %-20s | %14s | %10s | %15s |\n" "Generator" "Total Duration" "Iterations" "Step Duration"
    echo -n "|" ; for i in {1..70} ; do echo -n "-" ; done ; echo "|"
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        RESULTS=($(cat ${TEST_OUT_DIR}/java/PerformanceTest.log))
        printf "| %-20s | %14s | %10s | %15s |\n" Java ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]}
    fi
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        for CPP_TARGET in ${CPP_TARGETS[@]} ; do
        RESULTS=($(cat ${TEST_OUT_DIR}/cpp/PerformanceTest.log))
        printf "| %-20s | %14s | %10s | %15s |\n" "C++ (${CPP_TARGET})" ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]}
        done
    fi
    if [[ ${PARAM_PYTHON} == 1 ]] ; then
        RESULTS=($(cat ${TEST_OUT_DIR}/python/perftest.log))
        printf "| %-20s | %14s | %10s | %15s |\n" Python ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]}
    fi
    for i in {1..72} ; do echo -n "=" ; done ; echo
    echo
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runds performance tests on given zserio sources with zserio release compiled in release-ver directory.

Usage:
    $0 [-h] [-e] [-p] [-o <dir>] [-d <dir>] [-t <name>] -[n <num>] [-c <config>]
       generator... -s test.zs -b test.Blob -f blob.bin

Arguments:
    -h, --help              Show this help.
    -e, --help-env          Show help for enviroment variables.
    -p, --purge             Purge test build directory.
    -o <dir>, --output-directory <dir>
                            Output directory where tests will be run.
    -d <dir>, --source-dir <dir>
                            Directory with zserio sources. Default is ".".
    -t <name>, --test-name <name>
                            Test name. Optional.
    -n <num>, --num-iterations <num>
                            Number of iterations. Optional, default is 100.
    -c <config>, --test-config <config>
                            Test configuration: READ (default), WRITE, READ_WRITE.
    -s <source>, --source <source>
                            Main zserio source.
    -b <blob>, --blob-name <blob>
                            Full name of blob to run performance tests on.
    -f <filename>, --blob-file <filename>
                            Path to the blobfile.
    generator               Specify the generator to test.

Generator can be:
    cpp-linux32-gcc         Generate C++ sources and compile them for linux32 target (gcc).
    cpp-linux64-gcc         Generate C++ sources and compile them for for linux64 target (gcc).
    cpp-linux32-clang       Generate C++ sources and compile them for linux32 target (Clang).
    cpp-linux64-clang       Generate C++ sources and compile them for for linux64 target (Clang).
    cpp-windows64-mingw     Generate C++ sources and compile them for for windows64 target (MinGW64).
    cpp-windows64-msvc      Generate C++ sources and compile them for for windows64 target (MSVC).
    java                    Generate Java sources and compile them.
    python                  Generate python sources.
    all-linux32-gcc         Test all generators and compile all possible linux32 sources (gcc).
    all-linux64-gcc         Test all generators and compile all possible linux64 sources (gcc).
    all-linux32-clang       Test all generators and compile all possible linux32 sources (Clang).
    all-linux64-clang       Test all generators and compile all possible linux64 sources (Clang).
    all-windows64-mingw     Test all generators and compile all possible windows64 sources (MinGW64).
    all-windows64-msvc      Test all generators and compile all possible windows64 sources (MSVC).

Examples:
    $0 cpp-linux64-gcc java python -d /tmp/zs -s test.zs -b test.Blob -f blob.bin
    $0 all-linux64-gcc -d /tmp/zs -s test.zs -b test.Blob -f blob.bin

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
    exit_if_argc_lt $# 12
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_DIRECTORY_OUT="$1"; shift
    local SWITCH_SOURCE_OUT="$1"; shift
    local SWITCH_TEST_NAME_OUT="$1"; shift
    local SWITCH_BLOB_NAME_OUT="$1"; shift
    local SWITCH_BLOB_FILE_OUT="$1"; shift
    local SWITCH_NUM_ITERATIONS_OUT="$1"; shift
    local SWITCH_TEST_CONFIG_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${SWITCH_DIRECTORY_OUT}="."
    eval ${SWITCH_SOURCE_OUT}=""
    eval ${SWITCH_TEST_NAME_OUT}=""
    eval ${SWITCH_BLOB_NAME_OUT}=""
    eval ${SWITCH_BLOB_FILE_OUT}=""
    eval ${SWITCH_NUM_ITERATIONS_OUT}=100 # default
    eval ${SWITCH_TEST_CONFIG_OUT}="READ"
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=();
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
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
                eval ${PARAM_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-d" | "--directory")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Directory with zserio sources is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_DIRECTORY_OUT}="${ARG}"
                shift
                ;;

            "-s" | "--source")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Main zserio source is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_SOURCE_OUT}="${ARG}"
                shift
                ;;

            "-t" | "--test-name")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "Test name is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_NAME_OUT}="${ARG}"
                shift
                ;;

            "-b" | "--blob-name")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "BLOB name is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_BLOB_NAME_OUT}="${ARG}"
                shift
                ;;

            "-f" | "--blob-file")
                shift
                local ARG="$1"
                if [ -z "${ARG}" ] ; then
                    stderr_echo "BLOB filename is not set!"
                    echo
                    return 1
                fi
                eval ${SWITCH_BLOB_FILE_OUT}="${ARG}"
                shift
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

            "all-linux32-"* | "all-linux64-"* | "all-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_CPP_TARGETS}]="${PARAM#all-}"
                NUM_CPP_TARGETS=$((NUM_CPP_TARGETS + 1))
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                ;;

            *)
                stderr_echo "Invalid argument ${PARAM}!"
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
        if [[ ${NUM_CPP_TARGETS} == 0 &&
            ${!PARAM_JAVA_OUT} == 0 &&
            ${!PARAM_PYTHON_OUT} == 0 ]] ; then
            stderr_echo "Generator to test is not specified!"
            echo
            return 1
        fi

        if [[ "${!SWITCH_SOURCE_OUT}" == "" ]] ; then
            stderr_echo "Main zserio source is not set!"
            echo
            return 1
        fi

        if [[ "${!SWITCH_BLOB_NAME_OUT}" == "" ]] ; then
            stderr_echo "Blob name is not set!"
            echo
            return 1
        fi

        if [[ "${!SWITCH_BLOB_FILE_OUT}" == "" ]] ; then
            stderr_echo "Blob filename is not set!"
            echo
            return 1
        fi
    fi

    # default test name
    if [[ "${!SWITCH_TEST_NAME_OUT}" == "" ]] ; then
        local DEFAULT_TEST_NAME=${!SWITCH_SOURCE_OUT%.*} # strip extension
        DEFAULT_TEST_NAME=${DEFAULT_TEST_NAME//\//_} # all slashes to underscores
        eval ${SWITCH_TEST_NAME_OUT}=${DEFAULT_TEST_NAME}
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
    local PARAM_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_DIRECTORY
    local SWITCH_SOURCE
    local SWITCH_TEST_NAME
    local SWITCH_BLOB_NAME
    local SWITCH_BLOB_FILE
    local SWITCH_NUM_ITERATIONS
    local SWITCH_TEST_CONFIG
    local SWITCH_PURGE
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_OUT_DIR \
            SWITCH_DIRECTORY SWITCH_SOURCE SWITCH_TEST_NAME SWITCH_BLOB_NAME SWITCH_BLOB_FILE \
            SWITCH_NUM_ITERATIONS SWITCH_TEST_CONFIG SWITCH_PURGE "$@"
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

    echo "Zserio Performance Tests"
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

    if [[ ${PARAM_PYTHON} != 0 ]] ; then
        set_global_python_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # extensions need absolute paths
    convert_to_absolute_path "${PARAM_OUT_DIR}" PARAM_OUT_DIR

    # purge if requested and then create test output directory
    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"
    local TEST_OUT_DIR="${ZSERIO_BUILD_DIR}/test_perf/${SWITCH_TEST_NAME}"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test directory." # purges all tests in test_perf directory
        echo
        rm -rf "${TEST_OUT_DIR}/"

        if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} == 0 &&
              ${PARAM_JAVA} == 0 &&
              ${PARAM_PYTHON} == 0 ]] ; then
            return 0  # purge only
        fi
    fi
    mkdir -p "${TEST_OUT_DIR}"

    # get zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" "${PARAM_OUT_DIR}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # print information
    echo "Testing release: ${ZSERIO_RELEASE_DIR}"
    echo "Test output directory: ${TEST_OUT_DIR}"
    echo "Test config: ${SWITCH_TEST_CONFIG}"
    echo

    # unpack testing release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${TEST_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # run test
    test_perf "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
              "${TEST_OUT_DIR}" PARAM_CPP_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} \
              "${SWITCH_DIRECTORY}" "${SWITCH_SOURCE}" "${SWITCH_TEST_NAME}" "${SWITCH_BLOB_NAME}" \
              "${SWITCH_BLOB_FILE}" ${SWITCH_NUM_ITERATIONS} ${SWITCH_TEST_CONFIG}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
