#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_test_tools.sh"

# Generate Ant build.xml file and src/PerformanceTest.java
generate_java_files()
{
    exit_if_argc_ne $# 7
    local ZSERIO_RELEASE="$1"; shift
    local BUILD_DIR="$1"; shift
    local BLOB_FULL_NAME="$1"; shift
    local JSON_PATH="$1"; shift
    local BLOB_PATH="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local TEST_CONFIG="$1"; shift

    local LOG_PATH="${BUILD_DIR}/PerformanceTest.log"
    local TOP_LEVEL_PACKAGE_NAME=${BLOB_FULL_NAME%%.*}

    local INPUT_SWITCH="-j"
    local INPUT_PATH="${JSON_PATH}"
    if [[ "${BLOB_PATH}" != "" ]] ; then
        INPUT_SWITCH="-b"
        INPUT_PATH="${BLOB_PATH}"
    fi

    # use host paths in generated files
    posix_to_host_path "${ZSERIO_RELEASE}" HOST_ZSERIO_RELEASE
    posix_to_host_path "${BUILD_DIR}" HOST_BUILD_DIR
    posix_to_host_path "${LOG_PATH}" HOST_LOG_PATH
    posix_to_host_path "${INPUT_PATH}" HOST_INPUT_PATH

    cat > "${BUILD_DIR}"/build.xml << EOF
<project name="performance_test" basedir="." default="run">
    <property name="zserio.release_dir" location="${HOST_ZSERIO_RELEASE}"/>

    <property name="runtime.jar_dir" location="\${zserio.release_dir}/runtime_libs/java"/>
    <property name="runtime.jar_file_name" value="zserio_runtime.jar"/>
    <property name="runtime.jar_file" location="\${runtime.jar_dir}/\${runtime.jar_file_name}"/>

    <property name="test_perf.build_dir" location="${HOST_BUILD_DIR}/\${ant.java.version}"/>
    <property name="test_perf.classes_dir" location="\${test_perf.build_dir}/classes"/>
    <property name="test_perf.jar_dir" location="\${test_perf.build_dir}/jar"/>
    <property name="test_perf.jar_file" location="\${test_perf.jar_dir}/performance_test.jar"/>
    <property name="test_perf.src_dir" location="${HOST_BUILD_DIR}/src"/>
    <property name="test_perf.gen_dir" location="${HOST_BUILD_DIR}/gen"/>

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
            <arg value="${INPUT_SWITCH}"/>
            <arg file="${HOST_INPUT_PATH}"/>
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
import java.nio.file.Files;
import java.nio.file.Paths;

import zserio.runtime.io.SerializeUtil;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.DebugStringUtil;

public class PerformanceTest
{
    public static void main(String[] args) throws Exception
    {
        System.out.println("Zserio Java Performance Test");

        if (args.length < 3)
        {
            System.err.println("No enough arguments!");
            System.err.println("Usage: PerformanceTest LOG_PATH [-j|-b] INPUT_PATH [NUM_ITERATIONS]");
            System.exit(1);
        }

        final String logPath = args[0];
        final boolean inputIsJson = args[1].equals("-j") ? true : false;
        final String inputPath = args[2];
        final int numIterations = args.length > 3 ? Integer.parseInt(args[3]) : ${NUM_ITERATIONS};

        // prepare byte array
        byte[] blobBuffer = readBlobBuffer(inputIsJson, inputPath);

        // calculate blob memory size
        final ByteArrayBitStreamReader blobReader = new ByteArrayBitStreamReader(blobBuffer);
        System.gc();
        Thread.sleep(1000);
        final long startHeapSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        final ${BLOB_FULL_NAME} memoryBlob = new ${BLOB_FULL_NAME}(blobReader);
        final long endHeapSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        final long blobMemorySize = endHeapSize - startHeapSize;
        final long blobSize = blobReader.getBitPosition();

        // run the test
EOF

    if [[ "${TEST_CONFIG}" == "WRITE" ]] ; then
        cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blobBuffer);
        final ${BLOB_FULL_NAME} blob = new ${BLOB_FULL_NAME}(reader);
EOF
    fi

    cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
        final long startTime = System.nanoTime();
        for (int i = 0; i < numIterations; ++i)
        {
EOF

    case "${TEST_CONFIG}" in
        "READ")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blobBuffer);
            final ${BLOB_FULL_NAME} blob = new ${BLOB_FULL_NAME}(reader);
EOF
            ;;
        "READ_WRITE")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(blobBuffer);
            final ${BLOB_FULL_NAME} blob = new ${BLOB_FULL_NAME}(reader);
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            blob.write(writer);
EOF
            ;;

        "WRITE")
            cat >> "${BUILD_DIR}"/src/PerformanceTest.java << EOF
            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            blob.write(writer);
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
        final double blobKbSize = blobSize/ 8. / 1000.;
        final double blobMemoryKbSize = blobMemorySize / 1000.;
        System.out.println("Total Duration: " + String.format("%.3f", totalDuration) + "ms");
        System.out.println("Iterations:     " + numIterations);
        System.out.println("Step Duration:  " + String.format("%.3f", stepDuration) + "ms");
        System.out.println("Blob Size:      " + blobSize + " bits (" +
                String.format("%.3f", blobKbSize) + " kB)");
        System.out.println("Blob in Memory: " + blobMemorySize + " bytes (" +
                String.format("%.3f", blobMemoryKbSize) + " kB)");

        // write results to file
        PrintStream logFile = new PrintStream(new File(logPath));
        logFile.println(String.format("%.03fms %d %.03fms %.03fkB %.03fkB",
                totalDuration, numIterations, stepDuration, blobKbSize, blobMemoryKbSize));
        logFile.close();
    }

    private static byte[] readBlobBuffer(boolean isInputJson, String inputPath) throws Exception
    {
        try
        {
            if (isInputJson)
            {
                final ${BLOB_FULL_NAME} blob = (${BLOB_FULL_NAME})
                        DebugStringUtil.fromJsonFile(${BLOB_FULL_NAME}.class, inputPath);

                // serialize to binary file for further analysis
                SerializeUtil.serializeToFile(blob, "${TOP_LEVEL_PACKAGE_NAME}.blob");

                final ByteArrayBitStreamWriter bufferWriter = new ByteArrayBitStreamWriter();
                blob.write(bufferWriter);
                return bufferWriter.toByteArray();
            }
            else
            {
                final long blobByteSize = Files.size(Paths.get(inputPath));
                final ${BLOB_FULL_NAME} blobFromFile =
                        SerializeUtil.deserializeFromFile(${BLOB_FULL_NAME}.class, inputPath);
                final ByteArrayBitStreamWriter bufferWriter = new ByteArrayBitStreamWriter();
                blobFromFile.write(bufferWriter);
                byte[] blobBuffer = bufferWriter.toByteArray();

                if (blobByteSize != (long)blobBuffer.length)
                {
                    System.err.println("Read only " + blobBuffer.length + "/" + blobByteSize + " bytes!");
                    System.exit(1);
                }
                return blobBuffer;
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to read blob buffer! (" + e + ")");
            System.exit(1);
        }

        return null;
    }
};
EOF
}

# Generate C++ files
generate_cpp_files()
{
    exit_if_argc_ne $# 9
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_RELEASE="$1"; shift
    local BUILD_DIR="$1"; shift
    local BLOB_FULL_NAME="$1"; shift
    local JSON_PATH="$1"; shift
    local BLOB_PATH="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local TEST_CONFIG="$1"; shift
    local PROFILE="$1"; shift

    local INPUT_SWITCH="-j"
    local INPUT_PATH="${JSON_PATH}"
    if [[ "${BLOB_PATH}" != "" ]] ; then
        INPUT_SWITCH="-b"
        INPUT_PATH="${BLOB_PATH}"
    fi

    # use host paths in generated files
    local DISABLE_SLASHES_CONVERSION=1
    posix_to_host_path "${ZSERIO_PROJECT_ROOT}" HOST_ZSERIO_ROOT ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${ZSERIO_RELEASE}" HOST_ZSERIO_RELEASE ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${INPUT_PATH}" HOST_INPUT_PATH ${DISABLE_SLASHES_CONVERSION}

    local BUILD_SRC_DIR="${BUILD_DIR}/src"
    mkdir -p "${BUILD_SRC_DIR}"
    cat > "${BUILD_SRC_DIR}"/CMakeLists.txt << EOF
cmake_minimum_required(VERSION 3.15.0)
project(PerformanceTest)

enable_testing()

set(ZSERIO_ROOT "${HOST_ZSERIO_ROOT}" CACHE PATH "")
set(ZSERIO_RELEASE "${HOST_ZSERIO_RELEASE}" CACHE PATH "")
set(LOG_PATH "PerformanceTest.log")
set(INPUT_SWITCH "${INPUT_SWITCH}")
set(INPUT_PATH "${HOST_INPUT_PATH}")
set(CMAKE_MODULE_PATH "\${ZSERIO_ROOT}/cmake")

EOF

if [[ ${PROFILE} == 1 ]] ; then
    cat >> "${BUILD_SRC_DIR}"/CMakeLists.txt << EOF
string(CONCAT MEMORYCHECK_COMMAND_OPTIONS
    "--tool=callgrind -v --instr-atstart=no --collect-atstart=no --collect-jumps=yes --dump-instr=yes "
    "--callgrind-out-file=callgrind.out"
)
include(CTest)

EOF
fi

cat >> "${BUILD_SRC_DIR}"/CMakeLists.txt << EOF
# cmake helpers
include(cmake_utils)

# setup compiler
include(compiler_utils)
compiler_set_pthread()
compiler_set_static_clibs()
if (MSVC)
    set(CMAKE_CXX_FLAGS "\${CMAKE_CXX_FLAGS} /bigobj")
endif ()
compiler_set_warnings()

# add zserio runtime library
add_subdirectory("\${ZSERIO_RELEASE}/runtime_libs/cpp" ZserioCppRuntime)

file(GLOB_RECURSE SOURCES RELATIVE "\${CMAKE_CURRENT_SOURCE_DIR}" "gen/*.cpp" "gen/*.h")

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(\${CMAKE_CURRENT_SOURCE_DIR}/../../../../..)

add_executable(\${PROJECT_NAME} PerformanceTest.cpp \${SOURCES})
# CXX_EXTENSIONS are necessary for old MinGW32 to support clock_gettime method
set_target_properties(\${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)

target_include_directories(\${PROJECT_NAME} PUBLIC "\${CMAKE_CURRENT_SOURCE_DIR}/gen")
target_include_directories(\${PROJECT_NAME} SYSTEM PRIVATE \${SQLITE_INCDIR})
target_link_libraries(\${PROJECT_NAME} ZserioCppRuntime \${SQLITE_LIBRARY})

add_test(NAME PerformanceTest COMMAND \${PROJECT_NAME} \${LOG_PATH} \${INPUT_SWITCH} \${INPUT_PATH})
EOF

    local BLOB_INCLUDE_PATH=${BLOB_FULL_NAME//.//}.h
    local BLOB_CLASS_FULL_NAME=${BLOB_FULL_NAME//./::}
    local TOP_LEVEL_PACKAGE_NAME=${BLOB_FULL_NAME%%.*}

    cat > "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
#include <fstream>
#include <iostream>
#include <iomanip>

#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>
#include <zserio/DebugStringUtil.h>
#include <zserio/SerializeUtil.h>
#include <zserio/UniquePtr.h>

#include <${BLOB_INCLUDE_PATH}>

EOF

if [[ ${PROFILE} == 1 ]] ; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
#include <valgrind/callgrind.h>

EOF
fi

cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
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
EOF

if [[ "${ZSERIO_EXTRA_ARGS}" == *"polymorphic"* ]]; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF

class TrackerMemoryResource : public zserio::pmr::MemoryResource
{
public:
    size_t getAllocatedSize() const
    {
        return allocatedSize;
    }

    size_t getDeallocatedSize() const
    {
        return deallocatedSize;
    }

private:
    void* doAllocate(size_t bytes, size_t) override
    {
        allocatedSize += bytes;
        return ::operator new(bytes);
    }

    void doDeallocate(void* p, size_t bytes, size_t) override
    {
        deallocatedSize += bytes;
        ::operator delete(p);
    }

    bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        return this == &other;
    }

    size_t allocatedSize = 0;
    size_t deallocatedSize = 0;
};
EOF
fi

    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF

using allocator_type = ${BLOB_CLASS_FULL_NAME}::allocator_type;
using BitBuffer = zserio::BasicBitBuffer<zserio::RebindAlloc<allocator_type, uint8_t>>;

static BitBuffer readBlobBuffer(bool inputIsJson, const char* inputPath)
{
    if (inputIsJson)
    {
        // read json file
        auto blob = zserio::fromJsonFile<${BLOB_CLASS_FULL_NAME}>(inputPath);

        // serialize to binary file for further analysis
        zserio::serializeToFile(blob, "${TOP_LEVEL_PACKAGE_NAME}.blob");

        return zserio::serialize<${BLOB_CLASS_FULL_NAME}, allocator_type>(blob);
    }
    else
    {
        // read blob file
        std::ifstream is(inputPath, std::ifstream::binary);
        if (!is)
            throw zserio::CppRuntimeException("Cannot open '") << inputPath << "' for reading!";
        is.seekg(0, is.end);
        const size_t blobByteSize = static_cast<size_t>(is.tellg());
        is.close();

        auto blob = zserio::deserializeFromFile<${BLOB_CLASS_FULL_NAME}>(inputPath);
        auto bitBuffer = zserio::serialize<${BLOB_CLASS_FULL_NAME}, allocator_type>(blob);
        if (bitBuffer.getByteSize() != blobByteSize)
        {
            throw zserio::CppRuntimeException("Read only ") << bitBuffer.getByteSize()
                    << "/" << blobByteSize << " bytes!";
        }

        return bitBuffer;
    }
}

int main(int argc, char* argv[])
{
    std::cout << "Zserio C++ Performance Test" << std::endl;

    if (argc < 4)
    {
        std::cerr << "No enough arguments!" << std::endl;
        std::cerr << "Usage: PerformanceTest LOG_PATH (-j|-b) INPUT_PATH [NUM_ITERATIONS]" << std::endl;
        return 1;
    }

    const char* logPath = argv[1];
    const bool inputIsJson = strcmp("-j", argv[2]) == 0 ? true : false;
    const char* inputPath = argv[3];
    int numIterations = ${NUM_ITERATIONS};
    if (argc > 4)
        numIterations = atoi(argv[4]);

    if (numIterations <= 0)
    {
        std::cerr << "Num iterations must be a positive integer (" << numIterations << ")!" << std::endl;
        return 1;
    }

    BitBuffer bitBuffer;
    try
    {
        bitBuffer = readBlobBuffer(inputIsJson, inputPath);
    }
    catch (const std::exception& e)
    {
        std::cerr << e.what() << std::endl;
        return 1;
    }
EOF

if [[ "${ZSERIO_EXTRA_ARGS}" == *"polymorphic"* ]]; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF

    // calculate blob memory size
    TrackerMemoryResource memoryResource;
    const allocator_type allocator(&memoryResource);
    zserio::BitStreamReader blobReader(bitBuffer);
    auto memoryBlob = zserio::allocate_unique<${BLOB_CLASS_FULL_NAME}>(allocator, blobReader, allocator);
    const size_t blobMemorySize = memoryResource.getAllocatedSize();
    const size_t blobDeallocMemorySize = memoryResource.getDeallocatedSize();
    if (blobDeallocMemorySize != 0)
    {
        std::cerr << "Memory deallocation during blob parsing occurred (" << blobDeallocMemorySize << ")!"
                << std::endl;
        return 1;
    }

    // run the test
EOF
fi

if [[ "${TEST_CONFIG}" != "WRITE" ]] ; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    std::vector<${BLOB_CLASS_FULL_NAME}> readBlobs;
    readBlobs.reserve(static_cast<size_t>(numIterations));
EOF
else
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    zserio::BitStreamReader reader(bitBuffer);
    auto readBlob = ${BLOB_CLASS_FULL_NAME}(reader);
EOF
fi

if [[ ${PROFILE} == 1 ]] ; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF

    CALLGRIND_START_INSTRUMENTATION;
    CALLGRIND_TOGGLE_COLLECT;

EOF
fi

cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    const uint64_t start = PerfTimer::getMicroTime();
    for (int i = 0; i < numIterations; ++i)
    {
EOF

    case "${TEST_CONFIG}" in
        "READ")
            cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
        zserio::BitStreamReader reader(bitBuffer);
        readBlobs.emplace_back(reader);
EOF
            ;;
        "READ_WRITE")
            cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
        zserio::BitStreamReader reader(bitBuffer);
        readBlobs.emplace_back(reader);
        zserio::BitStreamWriter writer(bitBuffer);
        readBlobs.back().write(writer);
EOF
            ;;

        "WRITE")
            cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
        zserio::BitStreamWriter writer(bitBuffer);
        readBlob.write(writer);
EOF
            ;;
    esac

cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    }
    const uint64_t stop = PerfTimer::getMicroTime();

EOF

if [[ ${PROFILE} == 1 ]] ; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    CALLGRIND_STOP_INSTRUMENTATION;
    CALLGRIND_TOGGLE_COLLECT;

EOF
fi

cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    // process results
    double totalDuration = static_cast<double>(stop - start) / 1000.;
    double stepDuration = totalDuration / numIterations;
    double blobkBSize = static_cast<double>(bitBuffer.getByteSize()) / 1000.;
    std::cout << std::fixed << std::setprecision(3);
    std::cout << "Total Duration: " << totalDuration << "ms" << std::endl;
    std::cout << "Iterations:     " << numIterations << std::endl;
    std::cout << "Step Duration:  " << stepDuration << "ms" << std::endl;
    std::cout << "Blob Size:      " << bitBuffer.getBitSize() << " bits" << "(" << blobkBSize << " kB)"
              << std::endl;
EOF

if [[ "${ZSERIO_EXTRA_ARGS}" == *"polymorphic"* ]]; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
    double blobMemorykBSize = static_cast<double>(blobMemorySize) / 1000.;
    std::cout << "Blob in Memory: " << blobMemorySize << " bytes" << "(" << blobMemorykBSize << " kB)"
              << std::endl;
EOF
fi

cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF

    // write results to file
    std::ofstream logFile(logPath);
    logFile << std::fixed << std::setprecision(3);
    logFile << totalDuration << "ms "
            << numIterations << " "
            << stepDuration << "ms "
            << blobkBSize << "kB "
EOF

if [[ "${ZSERIO_EXTRA_ARGS}" == *"polymorphic"* ]]; then
    cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
            << blobMemorykBSize << "kB"
EOF
fi

cat >> "${BUILD_SRC_DIR}"/PerformanceTest.cpp << EOF
            << std::endl;

    return 0;
}
EOF
}

# Generate python peftest.py
generate_python_perftest()
{
    exit_if_argc_ne $# 5
    local BUILD_DIR="$1"; shift
    local BLOB_FULL_NAME="$1"; shift
    local NUM_ITERATIONS="$1"; shift
    local TEST_CONFIG="$1"; shift
    local PROFILE="$1"; shift

    local API_MODULE=${BLOB_FULL_NAME%%.*}
    local BLOB_API_PATH=${BLOB_FULL_NAME#*.}

    mkdir -p "${BUILD_DIR}/src"
    cat > "${BUILD_DIR}/src/perftest.py" << EOF
import os
import sys
import argparse
import tracemalloc
from timeit import default_timer as timer
EOF

    if [[ ${PROFILE} == 1 ]] ; then
        cat >> "${BUILD_DIR}/src/perftest.py" << EOF
import cProfile
import re

EOF
    fi

    cat >> "${BUILD_DIR}/src/perftest.py" << EOF
import zserio
import ${API_MODULE}.api as api

def read_blob_buffer(input_is_json, input_path):
    if input_is_json:
        blob = zserio.from_json_file(api.${BLOB_API_PATH}, input_path)

        # serialize to binary file for further analysis
        zserio.serialize_to_file(blob, "${API_MODULE}.blob");

        return zserio.serialize_to_bytes(blob)
    else:
        blob_byte_size = os.path.getsize(input_path)

        blob = zserio.deserialize_from_file(api.${BLOB_API_PATH}, input_path)
        blob_buffer = zserio.serialize_to_bytes(blob)

        assert len(blob_buffer) == blob_byte_size

        return blob_buffer

def performance_test(log_path, input_is_json, input_path, num_iterations):
    print("Zserio Python Performance Test")

    # prepare byte array
    blob_buffer = read_blob_buffer(input_is_json, input_path)

    # calculate blob memory size
    blob_reader = zserio.BitStreamReader(blob_buffer)
    tracemalloc.start()
    memory_blob = api.${BLOB_API_PATH}.from_reader(blob_reader)
    blob_memory_size = tracemalloc.get_traced_memory()[1]
    tracemalloc.stop()
    blob_size = blob_reader.bitposition

    # run the test
EOF

    if [[ "${TEST_CONFIG}" == "WRITE" ]] ; then
        cat >> "${BUILD_DIR}/src/perftest.py" << EOF
    reader_from_file = zserio.BitStreamReader(blob_buffer)
    blob_from_file = api.${BLOB_API_PATH}.from_reader(reader_from_file)
EOF
    fi

    if [[ ${PROFILE} == 1 ]] ; then
        cat >> "${BUILD_DIR}/src/perftest.py" << EOF
    pr = cProfile.Profile()
    pr.enable()
    start = timer()
EOF
    else
        cat >> "${BUILD_DIR}/src/perftest.py" << EOF
    start = timer()
EOF
    fi

    cat >> "${BUILD_DIR}/src/perftest.py" << EOF

    for i in range(num_iterations):
EOF

    case "${TEST_CONFIG}" in
        "READ")
            cat >> "${BUILD_DIR}/src/perftest.py" << EOF
        reader = zserio.BitStreamReader(blob_buffer)
        blob = api.${BLOB_API_PATH}.from_reader(reader)
EOF
            ;;
        "READ_WRITE")
            cat >> "${BUILD_DIR}/src/perftest.py" << EOF
        reader = zserio.BitStreamReader(blob_buffer)
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

    stop = timer()
EOF

    if [[ ${PROFILE} == 1 ]] ; then
        cat >> "${BUILD_DIR}/src/perftest.py" << EOF
    pr.disable()
    prof_path = re.sub("\..*$", ".prof", log_path)
    pr.dump_stats(prof_path)
EOF
    fi

    cat >> "${BUILD_DIR}/src/perftest.py" << EOF

    # process results
    total_duration = (stop - start) * 1000
    step_duration = total_duration / num_iterations
    blob_kb_size = blob_size / 8. / 1000.
    blob_memory_kb_size = blob_memory_size / 1000.
    print("Total Duration: %.03fms" % total_duration)
    print("Iterations:     %d" % num_iterations)
    print("Step Duration:  %.03fms" % step_duration)
    print("Blob Size:      %d bits (%.03f kB)" % (blob_size, blob_kb_size))
    print("Blob in Memory: %d bytes (%.03f kB)" % (blob_memory_size, blob_memory_kb_size))

    # write results to file
    log_file = open(log_path, "w")
    log_file.write("%.03fms %d %.03fms %.03fkB %.03fkB" % (total_duration, num_iterations, step_duration,
                   blob_kb_size, blob_memory_kb_size))

if __name__ == "__main__":
    sys.setrecursionlimit(5000) # empiric constant to prevent failing during testing on recursive blobs

    arg_parser = argparse.ArgumentParser(description="Zserio Python Performance Test")
    arg_parser.add_argument("--log-path", required=True, help="Path to the log file to create")
    arg_parser.add_argument('--is-json', default=False, action="store_true",
                            help="Use when the input is a JSON file")
    arg_parser.add_argument('--input-path', required=True, help="Path to the input file")
    arg_parser.add_argument('--num-iterations', default=${NUM_ITERATIONS}, type=int, help="Number of iterations")
    args = arg_parser.parse_args()

    performance_test(args.log_path, args.is_json, args.input_path, args.num_iterations)
EOF
}

# Run zserio performance tests.
test_perf()
{
    exit_if_argc_ne $# 17
    local UNPACKED_ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local PARAM_PYTHON_CPP="$1"; shift
    local SWITCH_DIRECTORY="$1"; shift
    local SWITCH_SOURCE="$1"; shift
    local SWITCH_BLOB_NAME="$1"; shift
    local SWITCH_JSON_PATH="$1"; shift
    local SWITCH_BLOB_PATH="$1"; shift
    local SWITCH_NUM_ITERATIONS="$1"; shift
    local SWITCH_TEST_CONFIG="$1"; shift
    local SWITCH_RUN_ONLY="$1"; shift
    local SWITCH_PROFILE="$1"; shift

    if [[ "${SWITCH_JSON_PATH}" != "" ]] ; then
        convert_to_absolute_path "${SWITCH_JSON_PATH}" SWITCH_JSON_PATH
    fi
    if [[ "${SWITCH_BLOB_PATH}" != "" ]] ; then
        convert_to_absolute_path "${SWITCH_BLOB_PATH}" SWITCH_BLOB_PATH
    fi

    if [[ ${SWITCH_PROFILE} == 1 && ( ${PARAM_JAVA} == 1 ) ]] ; then
        stderr_echo "Profiling not available for Java!"
        return 1
    fi

    # generate sources using zserio
    local PYTHON_VERSION_STRING
    get_python_version_string PYTHON_VERSION_STRING
    local TEST_PYTHON_OUT_DIR="${TEST_OUT_DIR}/python/${PYTHON_VERSION_STRING}"
    if [[ ${SWITCH_RUN_ONLY} == 0 ]] ; then
        local ZSERIO_ARGS=("-withTypeInfoCode")
        if [[ ${#CPP_TARGETS[@]} -ne 0 ]] ; then
            rm -rf "${TEST_OUT_DIR}/cpp"
            ZSERIO_ARGS+=("-cpp" "${TEST_OUT_DIR}/cpp/src/gen")
        fi
        if [[ ${PARAM_JAVA} == 1 ]] ; then
            rm -rf "${TEST_OUT_DIR}/java"
            ZSERIO_ARGS+=("-java" "${TEST_OUT_DIR}/java/gen")
        fi
        if [[ ${PARAM_PYTHON} == 1 || ${PARAM_PYTHON_CPP} == 1 ]] ; then
            rm -rf "${PYTHON_TEST_OUT_DIR}"
            ZSERIO_ARGS+=("-python" "${TEST_PYTHON_OUT_DIR}/gen")
        fi

        run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}" \
            "${SWITCH_DIRECTORY}" "${SWITCH_SOURCE}" 0 ZSERIO_ARGS[@]
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run java performance test
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        if [[ ${SWITCH_RUN_ONLY} == 0 ]] ; then
            generate_java_files "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}/java" \
                                "${SWITCH_BLOB_NAME}" "${SWITCH_JSON_PATH}" "${SWITCH_BLOB_PATH}" \
                                ${SWITCH_NUM_ITERATIONS} ${SWITCH_TEST_CONFIG}
        fi
        ANT_ARGS=()
        compile_java "${TEST_OUT_DIR}/java/build.xml" ANT_ARGS[@] "run"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run C++ performance test
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        if [[ ${SWITCH_RUN_ONLY} == 0 ]] ; then
            generate_cpp_files "${ZSERIO_PROJECT_ROOT}" "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}/cpp" \
                               "${SWITCH_BLOB_NAME}" "${SWITCH_JSON_PATH}" "${SWITCH_BLOB_PATH}" \
                               ${SWITCH_NUM_ITERATIONS} ${SWITCH_TEST_CONFIG} ${SWITCH_PROFILE}
        fi
        local CMAKE_ARGS=()
        local CTEST_ARGS=("--verbose")
        if [[ ${SWITCH_PROFILE} == 1 ]] ; then
            CMAKE_ARGS=("-DCMAKE_BUILD_TYPE=RelWithDebInfo")
            CTEST_ARGS+=("-T memcheck")
        fi
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/cpp" "${TEST_OUT_DIR}/cpp/src" \
                    CPP_TARGETS[@] CMAKE_ARGS[@] CTEST_ARGS[@] all
        if [ $? -ne 0 ] ; then
            return 1
        fi

        if [[ ${SWITCH_PROFILE} == 1 ]] ; then
            echo ""
            echo "C++ profiling finished, use one of the following commands for analysis:"
            for CPP_TARGET in "${CPP_TARGETS[@]}" ; do
                local CALLGRIND_FILE=$(${FIND} "${TEST_OUT_DIR}/cpp/${CPP_TARGET}" -name "callgrind.out")
                echo "    kcachegrind ${CALLGRIND_FILE}"
            done
        fi
    fi

    # run Python performance test
    if [[ ${PARAM_PYTHON} == 1 || ${PARAM_PYTHON_CPP} == 1 ]] ; then
        activate_python_virtualenv "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi

        if [[ ${SWITCH_RUN_ONLY} == 0 ]] ; then
            generate_python_perftest "${TEST_PYTHON_OUT_DIR}" "${SWITCH_BLOB_NAME}" ${SWITCH_NUM_ITERATIONS} \
                                     ${SWITCH_TEST_CONFIG} ${SWITCH_PROFILE}
        fi

        local IS_JSON="--is-json"
        local INPUT_PATH="${SWITCH_JSON_PATH}"
        if [[ "${SWITCH_BLOB_FILE}" != "" ]] ; then
            IS_JSON=""
            INPUT_PATH="${SWITCH_BLOB_PATH}"
        fi
        local PYTHON_RUNTIME_DIR="${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/python"

        if [[ ${PARAM_PYTHON} == 1 ]] ; then
            mkdir -p "${TEST_PYTHON_OUT_DIR}/python-pure"
            pushd "${TEST_PYTHON_OUT_DIR}/python-pure" > /dev/null
            ZSERIO_PYTHOM_IMPLEMENTATION="python" \
            PYTHONPATH="${PYTHON_RUNTIME_DIR}:${TEST_PYTHON_OUT_DIR}/gen" \
            python ${TEST_PYTHON_OUT_DIR}/src/perftest.py \
                   --log-path="PerformanceTest.log" ${IS_JSON} --input-path "${INPUT_PATH}"
            if [ $? -ne 0 ] ; then
                popd > /dev/null
                return 1
            fi
            popd > /dev/null
        fi

        if [[ ${PARAM_PYTHON_CPP} == 1 ]] ; then
            if [[ ${SWITCH_RUN_ONLY} == 0 ]] ; then
                build_cpp_binding_to_python "${PYTHON_RUNTIME_DIR}" \
                        "${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/cpp" "${TEST_PYTHON_OUT_DIR}"
                if [ $? -ne 0 ] ; then
                    stderr_echo "Failed to build C++ runtime binding to Python!"
                    return 1
                fi
            fi

            local ZSERIO_CPP_DIR
            ZSERIO_CPP_DIR=$(ls -d1 "${TEST_PYTHON_OUT_DIR}/zserio_cpp/lib"*)
            if [ $? -ne 0 ] ; then
                stderr_echo "Failed to locate C++ runtime binding to Python!"
                return 1
            fi

            mkdir -p "${TEST_PYTHON_OUT_DIR}/python-cpp"
            pushd "${TEST_PYTHON_OUT_DIR}/python-cpp" > /dev/null
            ZSERIO_PYTHOM_IMPLEMENTATION="cpp" \
            PYTHONPATH="${PYTHON_RUNTIME_DIR}:${TEST_PYTHON_OUT_DIR}/gen:${ZSERIO_CPP_DIR}" \
            python ${TEST_PYTHON_OUT_DIR}/src/perftest.py \
                   --log-path="PerformanceTest.log" ${IS_JSON} --input-path "${INPUT_PATH}"
            if [ $? -ne 0 ] ; then
                popd > /dev/null
                return 1
            fi
            popd > /dev/null
        fi

        if [[ ${SWITCH_PROFILE} == 1 ]] ; then
            local PROFDATA_FILE="PerformanceTest.prof"
            echo ""
            echo "Python profiling finished, use one of the following commands for analysis:"
            echo "    python3 -m pstats ${TEST_PYTHON_OUT_DIR}/${PROFDATA_FILE}"
            echo "    python3 -m snakeviz ${TEST_PYTHON_OUT_DIR}/${PROFDATA_FILE}"
            echo "    python3 -m pyprof2calltree -k -i ${TEST_PYTHON_OUT_DIR}/${PROFDATA_FILE}"
        fi
    fi

    # collect results
    echo
    echo "Performance Tests Results - ${SWITCH_TEST_CONFIG}"
    echo "Blob name: ${SWITCH_BLOB_NAME}"
    if [[ "${SWITCH_JSON_PATH}" != "" ]] ; then
        echo "JSON file: ${SWITCH_JSON_PATH##*/}"
    else
        echo "Blob file: ${SWITCH_BLOB_PATH##*/}"
    fi
    for i in {1..103} ; do echo -n "=" ; done ; echo
    printf "| %-21s | %14s | %10s | %15s | %10s | %10s |\n" \
           "Generator" "Total Duration" "Iterations" "Step Duration" "Blob Size" "Blob in Memory"
    echo -n "|" ; for i in {1..101} ; do echo -n "-" ; done ; echo "|"
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        local RESULTS=($(cat ${TEST_OUT_DIR}/java/PerformanceTest.log))
        printf "| %-21s | %14s | %10s | %15s | %10s | %14s |\n" \
               "Java" ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]} ${RESULTS[3]} ${RESULTS[4]}
    fi
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        for CPP_TARGET in "${CPP_TARGETS[@]}" ; do
            local PERF_TEST_FILE=$(${FIND} "${TEST_OUT_DIR}/cpp/${CPP_TARGET}" -name "PerformanceTest.log")
            local RESULTS=($(cat ${PERF_TEST_FILE}))
            printf "| %-21s | %14s | %10s | %15s | %10s | %14s |\n" \
                   "C++ (${CPP_TARGET})" ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]} ${RESULTS[3]} ${RESULTS[4]}
        done
    fi
    if [[ ${PARAM_PYTHON} == 1 ]] ; then
        local RESULTS=($(cat ${TEST_PYTHON_OUT_DIR}/python-pure/PerformanceTest.log))
        printf "| %-21s | %14s | %10s | %15s | %10s | %14s |\n" \
               "Python" ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]} ${RESULTS[3]} ${RESULTS[4]}
    fi
    if [[ ${PARAM_PYTHON_CPP} == 1 ]] ; then
        local RESULTS=($(cat ${TEST_PYTHON_OUT_DIR}/python-cpp/PerformanceTest.log))
        printf "| %-21s | %14s | %10s | %15s | %10s | %14s |\n" \
               "Python (C++)" ${RESULTS[0]} ${RESULTS[1]} ${RESULTS[2]} ${RESULTS[3]} ${RESULTS[4]}
    fi
    for i in {1..103} ; do echo -n "=" ; done ; echo
    echo

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Runs performance tests on given zserio sources with zserio release compiled in release-ver directory.

Usage:
    $0 [-h] [-e] [-p] [-o <dir>] [-d <dir>] [-t <name>] -[n <num>] [-c <config>]
        generator... -s test.zs -b test.Blob -f blob.bin

Arguments:
    -h, --help              Show this help.
    -e, --help-env          Show help for enviroment variables.
    -p, --purge             Purge test build directory.
    -r, --run-only          Run already compiled PerformanceTests again.
    --profile               Run the test in profiling mode and produce profiling data.
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
    python                  Generate python sources and use pure python runtime.
    python-cpp              Generate python sources and use C++ optimized python runtime.
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
    exit_if_argc_lt $# 16
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_PYTHON_CPP_OUT="$1"; shift
    local SWITCH_OUT_DIR_OUT="$1"; shift
    local SWITCH_DIRECTORY_OUT="$1"; shift
    local SWITCH_SOURCE_OUT="$1"; shift
    local SWITCH_TEST_NAME_OUT="$1"; shift
    local SWITCH_BLOB_NAME_OUT="$1"; shift
    local SWITCH_JSON_FILE_OUT="$1"; shift
    local SWITCH_BLOB_FILE_OUT="$1"; shift
    local SWITCH_NUM_ITERATIONS_OUT="$1"; shift
    local SWITCH_TEST_CONFIG_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift
    local SWITCH_RUN_ONLY_OUT="$1"; shift
    local SWITCH_PROFILE_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${PARAM_PYTHON_CPP_OUT}=0
    eval ${SWITCH_DIRECTORY_OUT}="."
    eval ${SWITCH_SOURCE_OUT}=""
    eval ${SWITCH_TEST_NAME_OUT}=""
    eval ${SWITCH_BLOB_NAME_OUT}=""
    eval ${SWITCH_JSON_FILE_OUT}=""
    eval ${SWITCH_BLOB_FILE_OUT}=""
    eval ${SWITCH_NUM_ITERATIONS_OUT}=100 # default
    eval ${SWITCH_TEST_CONFIG_OUT}="READ"
    eval ${SWITCH_PURGE_OUT}=0
    eval ${SWITCH_RUN_ONLY_OUT}=0
    eval ${SWITCH_PROFILE_OUT}=0

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
                eval ${SWITCH_OUT_DIR_OUT}="$2"
                shift 2
                ;;

            "-d" | "--source-dir")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing directory with zserio sources!"
                    echo
                    return 1
                fi
                eval ${SWITCH_DIRECTORY_OUT}="$2"
                shift 2
                ;;

            "-s" | "--source")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing main zserio source!"
                    echo
                    return 1
                fi
                eval ${SWITCH_SOURCE_OUT}="$2"
                shift 2
                ;;

            "-t" | "--test-name")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing test name!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_NAME_OUT}="$2"
                shift 2
                ;;

            "-b" | "--blob-name")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing blob name!"
                    echo
                    return 1
                fi
                eval ${SWITCH_BLOB_NAME_OUT}="$2"
                shift 2
                ;;

            "-j" | "--json-file")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing JSON file name!"
                    echo
                    return 1
                fi
                eval ${SWITCH_JSON_FILE_OUT}="$2"
                shift 2
                ;;

            "-f" | "--blob-file")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing BLOB file name!"
                    echo
                    return 1
                fi
                eval ${SWITCH_BLOB_FILE_OUT}="$2"
                shift 2
                ;;

            "-n" | "--num-iterations")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing number of iterations!"
                    echo
                    return 1
                fi
                eval ${SWITCH_NUM_ITERATIONS_OUT}="$2"
                shift 2
                ;;

            "-c" | "--test-config")
                if [ $# -eq 1 ] ; then
                    stderr_echo "Missing test configuration!"
                    echo
                    return 1
                fi
                eval ${SWITCH_TEST_CONFIG_OUT}="$2"
                shift 2
                ;;

            "-r" | "--run-only")
                eval ${SWITCH_RUN_ONLY_OUT}=1
                shift
                ;;

            "--profile")
                eval ${SWITCH_PROFILE_OUT}=1
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
        if [[ ${NUM_CPP_TARGETS} == 0 &&
            ${!PARAM_JAVA_OUT} == 0 &&
            ${!PARAM_PYTHON_OUT} == 0 &&
            ${!PARAM_PYTHON_CPP_OUT} == 0 ]] ; then
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

        if [[ "${!SWITCH_BLOB_FILE_OUT}" == "" && "${!SWITCH_JSON_FILE_OUT}" == "" ]] ; then
            stderr_echo "Neither blob nor JSON filename is set!"
            echo
            return 1
        fi

        if [[ "${!SWITCH_BLOB_FILE_OUT}" != "" && "${!SWITCH_JSON_FILE_OUT}" != "" ]] ; then
            stderr_echo "Set either blob or JSON filename, not both!"
            echo
            return 1
        fi
    else
        if [[ ${!SWITCH_RUN_ONLY_OUT} == 1 ]] ; then
            stderr_echo "Cannot run-only tests when purge is required!"
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
    local PARAM_PYTHON_CPP
    local SWITCH_OUT_DIR="${ZSERIO_PROJECT_ROOT}"
    local SWITCH_DIRECTORY
    local SWITCH_SOURCE
    local SWITCH_TEST_NAME
    local SWITCH_BLOB_NAME
    local SWITCH_JSON_FILE
    local SWITCH_BLOB_FILE
    local SWITCH_NUM_ITERATIONS
    local SWITCH_TEST_CONFIG
    local SWITCH_PURGE
    local SWITCH_RUN_ONLY
    local SWITCH_PROFILE
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_PYTHON_CPP SWITCH_OUT_DIR \
            SWITCH_DIRECTORY SWITCH_SOURCE SWITCH_TEST_NAME SWITCH_BLOB_NAME SWITCH_JSON_FILE SWITCH_BLOB_FILE \
            SWITCH_NUM_ITERATIONS SWITCH_TEST_CONFIG SWITCH_PURGE SWITCH_RUN_ONLY SWITCH_PROFILE "$@"
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

    if [[ ${PARAM_PYTHON} != 0 || ${PARAM_PYTHON_CPP} != 0 ]] ; then
        set_global_python_variables "${ZSERIO_PROJECT_ROOT}"
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # extensions need absolute paths
    convert_to_absolute_path "${SWITCH_OUT_DIR}" SWITCH_OUT_DIR

    # purge if requested and then create test output directory
    local ZSERIO_BUILD_DIR="${SWITCH_OUT_DIR}/build"
    local TEST_OUT_DIR="${ZSERIO_BUILD_DIR}/test_perf/${SWITCH_TEST_NAME}"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test directory." # purges all tests in test_perf directory
        echo
        rm -rf "${TEST_OUT_DIR}/"

        if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} == 0 &&
              ${PARAM_JAVA} == 0 &&
              ${PARAM_PYTHON} == 0 &&
              ${PARAM_PYTHON_CPP} == 0 ]] ; then
            return 0  # purge only
        fi
    fi
    mkdir -p "${TEST_OUT_DIR}"

    # get zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" "${SWITCH_OUT_DIR}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
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
              "${TEST_OUT_DIR}" PARAM_CPP_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_PYTHON_CPP} \
              "${SWITCH_DIRECTORY}" "${SWITCH_SOURCE}" "${SWITCH_BLOB_NAME}" \
              "${SWITCH_JSON_FILE}" "${SWITCH_BLOB_FILE}" ${SWITCH_NUM_ITERATIONS} ${SWITCH_TEST_CONFIG} \
              ${SWITCH_RUN_ONLY} ${SWITCH_PROFILE}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

if [[ "${BASH_SOURCE[0]}" == "${0}" ]] ; then
    main "$@"
fi
