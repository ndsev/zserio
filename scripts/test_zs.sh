#!/bin/bash

SCRIPT_DIR=`dirname $0`
source "${SCRIPT_DIR}/common_tools.sh"

# Set and check test global variables.
set_test_global_variables()
{
    # UNZIP to use, defaults to "unzip" if not set
    UNZIP="${UNZIP:-unzip}"
    if [ ! -f "`which "${UNZIP}"`" ] ; then
        stderr_echo "Cannot find unzip! Set UNZIP environment variable."
        return 1
    fi

    # GRPC setup
    GRPC_ROOT="${GRPC_ROOT:-""}"

    # Zserio extra arguments
    ZSERIO_EXTRA_ARGS="${ZSERIO_EXTRA_ARGS:-""}"

    return 0
}

# Print help on the environment variables used for this release script.
print_test_help_env()
{
    cat << EOF
Uses the following environment variables for testing:
    UNZIP               Unzip executable to use. Default is "unzip".
    GRPC_ROOT           Root path to GRPC repository. GRPC is disabled by default.
    ZSERIO_EXTRA_ARGS   Extra arguments to zserio tool. Default is empty.

EOF
}

# Run zserio tool with specified sources and arguments
run_zserio_tool()
{
    exit_if_argc_ne $# 6
    local ZSERIO_RELEASE_ROOT="$1"; shift
    local BUILD_DIR="$1"; shift # for logging
    local ZSERIO_DIRECTORY="$1"; shift
    local ZSERIO_SOURCE="$1"; shift
    local WERROR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local ZSERIO_ARGS=("${MSYS_WORKAROUND_TEMP[@]}")

    local ZSERIO="${ZSERIO_RELEASE_ROOT}/zserio.jar"
    local ZSERIO_LOG="${BUILD_DIR}/zserio_log.txt"
    local MESSAGE="Compilation of zserio '${ZSERIO_DIRECTORY}/${ZSERIO_SOURCE}'"
    echo "STARTING - ${MESSAGE}"

    "${JAVA_BIN}" -jar "${ZSERIO}" ${ZSERIO_EXTRA_ARGS} "-src" "${ZSERIO_DIRECTORY}" "${ZSERIO_SOURCE}" \
            "${ZSERIO_ARGS[@]}" 2>&1 | tee ${ZSERIO_LOG}

    if [ ${PIPESTATUS[0]} -ne 0 ] ; then
        stderr_echo "${MESSAGE} failed!"
        return 1
    fi

    if [ ${WERROR} -ne 0 ] ; then
        grep -q "\[WARNING\]" ${ZSERIO_LOG}
        if [ $? -eq 0 ] ; then
            stderr_echo "${MESSAGE} failed because warnings are treated as errors! "
            return 1
        fi
    fi

    echo -e "FINISHED - ${MESSAGE}\n"

    return 0
}

# Generate Ant build.xml file
generate_ant_file()
{
    exit_if_argc_ne $# 6
    local ZSERIO_RELEASE="$1"; shift
    local ZSERIO_ROOT="$1"; shift
    local BUILD_DIR="$1"; shift
    local TEST_NAME="$1"; shift
    local NEEDS_SQLITE="$1"; shift
    local NEEDS_GRPC="$1"; shift

    # use host paths in generated files
    posix_to_host_path "${ZSERIO_RELEASE}" HOST_ZSERIO_RELEASE
    posix_to_host_path "${ZSERIO_ROOT}" HOST_ZSERIO_ROOT
    posix_to_host_path "${BUILD_DIR}" HOST_BUILD_DIR

    local FINDBUGS_FILTER_SQLITE
    if [ ${NEEDS_SQLITE} -ne 0 ] ; then
        FINDBUGS_FILTER_SQLITE="
        <!-- A prepared statement is generated from a nonconstant String. -->
        <Match>
            <Bug code=\"SQL\"/>
            <Or>
                <Method name=\"createTable\"/>
                <Method name=\"createOrdinaryRowIdTable\"/>
                <Method name=\"deleteTable\"/>
                <Method name=\"read\"/>
                <Method name=\"update\"/>
                <Method name=\"validate\"/>
                <Method name=\"executeUpdate\"/>
                <Method name=\"attachDatabase\"/>
            </Or>
        </Match>"
    fi

    local GRPC_JAR_DIR
    local GRPC_CLASSPATH
    if [ ${NEEDS_GRPC} -ne 0 ] ; then
        GRPC_JAR_DIR="

    <property name=\"3rdparty.grpc.jar_dir\" location=\"${HOST_ZSERIO_ROOT}/3rdparty/java/grpc\"/>"
        GRPC_CLASSPATH="
                <fileset dir=\"\${3rdparty.grpc.jar_dir}\">
                    <include name=\"*.jar\"/>
                </fileset>"
    fi

    cat > ${BUILD_DIR}/build.xml << EOF
<project name="${TEST_NAME}" basedir="." default="run">
    <property name="zserio.release_dir" location="${HOST_ZSERIO_RELEASE}"/>

    <property name="runtime.jar_dir" location="\${zserio.release_dir}/runtime_libs/java"/>
    <property name="runtime.jar_file" location="\${runtime.jar_dir}/zserio_runtime.jar"/>

    <property name="test_zs.build_dir" location="${HOST_BUILD_DIR}"/>
    <property name="test_zs.classes_dir" location="\${test_zs.build_dir}/classes"/>
    <property name="test_zs.jar_dir" location="\${test_zs.build_dir}/jar"/>
    <property name="test_zs.jar_file" location="\${test_zs.jar_dir}/${TEST_NAME}.jar"/>
    <property name="test_zs.src_dir" location="\${test_zs.build_dir}/gen"/>${GRPC_JAR_DIR}

    <condition property="findbugs.classpath" value="\${findbugs.home_dir}/lib/findbugs-ant.jar">
        <isset property="findbugs.home_dir"/>
    </condition>
    <condition property="findbugs.classname" value="edu.umd.cs.findbugs.anttask.FindBugsTask">
        <isset property="findbugs.home_dir"/>
    </condition>

    <target name="prepare">
        <mkdir dir="\${test_zs.classes_dir}"/>
    </target>

    <target name="compile" depends="prepare">
        <depend srcDir="\${test_zs.src_dir}"
            destDir="\${test_zs.classes_dir}"
            cache="\${test_zs.build_dir}/depend-cache"/>
        <javac destdir="\${test_zs.classes_dir}" debug="on" encoding="utf8" includeAntRuntime="false">
            <compilerarg value="-Xlint:all"/>
            <compilerarg value="-Xlint:-cast"/>
            <compilerarg value="-Werror"/>
            <classpath>
                <pathelement location="\${runtime.jar_file}"/>${GRPC_CLASSPATH}
            </classpath>
            <src path="\${test_zs.src_dir}"/>
        </javac>
    </target>

    <target name="jar" depends="compile">
        <jar destfile="\${test_zs.jar_file}" basedir="\${test_zs.classes_dir}"/>
    </target>

    <target name="findbugs" depends="jar" if="findbugs.home_dir">
        <taskdef name="findbugs" classpath="\${findbugs.classpath}" classname="\${findbugs.classname}"/>
        <findbugs home="\${findbugs.home_dir}"
            output="html"
            outputFile="\${test_zs.build_dir}/findbugs.html"
            reportLevel="low"
            excludeFilter="\${test_zs.build_dir}/findbugs_filter.xml"
            errorProperty="test_zs.findbugs.is_failed"
            warningsProperty="test_zs.findbugs.is_failed">
            <sourcePath path="\${test_zs.src_dir}"/>
            <class location="\${test_zs.jar_file}"/>
            <auxClasspath>
                <pathelement location="\${runtime.jar_file}"/>${GRPC_CLASSPATH}
            </auxClasspath>
        </findbugs>
        <fail message="FindBugs found some issues!" if="test_zs.findbugs.is_failed"/>
    </target>

    <target name="run" depends="findbugs">
    </target>

    <target name="clean">
        <delete dir="\${test_zs.classes_dir}"/>
        <delete dir="\${test_zs.jar_dir}"/>
        <delete dir="\${test_zs.build_dir}/depend-cache"/>
        <delete file="\${test_zs.build_dir}/findbugs.html"/>
    </target>
</project>
EOF

    cat > ${BUILD_DIR}/findbugs_filter.xml << EOF
<FindBugsFilter>
    <Match>
    <Match>
        <!-- Same code in different switch clauses. -->
        <Bug code="DB"/>
        <Or>
            <Method name="initializeOffsets"/>
            <Method name="bitSizeOf"/>
            <Method name="read"/>
        </Or>
    </Match>
    </Match>${FINDBUGS_FILTER_SQLITE}
</FindBugsFilter>
EOF
}

# Generate CMakeList.txt
generate_cmake_lists()
{
    exit_if_argc_ne $# 7
    local ZSERIO_RELEASE="$1"; shift
    local ZSERIO_ROOT="$1"; shift
    local BUILD_DIR="$1"; shift
    local TEST_NAME="$1"; shift
    local NEEDS_SQLITE="$1"; shift
    local NEEDS_INSPECTOR="$1"; shift
    local NEEDS_GRPC="$1"; shift

    # use host paths in generated files
    local DISABLE_SLASHES_CONVERSION=1
    posix_to_host_path "${ZSERIO_RELEASE}" HOST_ZSERIO_RELEASE ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${ZSERIO_ROOT}" HOST_ZSERIO_ROOT ${DISABLE_SLASHES_CONVERSION}
    posix_to_host_path "${BUILD_DIR}" HOST_BUILD_DIR ${DISABLE_SLASHES_CONVERSION}

    local SQLITE_SETUP
    local SQLITE_USE
    if [ ${NEEDS_SQLITE} -ne 0 ] ; then
        SQLITE_SETUP="

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(\"\${ZSERIO_ROOT}\")"
        SQLITE_USE="
target_include_directories(\${PROJECT_NAME} SYSTEM PRIVATE \${SQLITE_INCDIR})
target_link_libraries(\${PROJECT_NAME} \${SQLITE_LIBRARY})"
    fi

    local INSPECTOR_SETUP
    local INSPECTOR_USE="OFF"
    if [ ${NEEDS_INSPECTOR} -ne 0 ]; then
        INSPECTOR_SETUP="
add_definitions(-DZSERIO_RUNTIME_INCLUDE_INSPECTOR)"
        INSPECTOR_USE="ON"
    fi

    local GRPC_SETUP
    local GRPC_USE
    if [ ${NEEDS_GRPC} -ne 0 ] ; then
        GRPC_SETUP="

# setup GRPC
include(grpc_utils)
find_grpc_libraries()
set(CMAKE_CXX_STANDARD 11) # needed due to GRPC"
        GRPC_USE="
target_include_directories(\${PROJECT_NAME} SYSTEM PRIVATE \${GRPC_INCDIR})
target_link_libraries(\${PROJECT_NAME} \${GRPC_LIBRARIES})"
    fi

    cat > ${BUILD_DIR}/CMakeLists.txt << EOF
cmake_minimum_required(VERSION 2.8.12.2)
project(test_zs_${TEST_NAME})

enable_testing()

set(ZSERIO_ROOT "${HOST_ZSERIO_ROOT}" CACHE PATH "")
set(ZSERIO_RELEASE "${HOST_ZSERIO_RELEASE}" CACHE PATH "")
set(CMAKE_MODULE_PATH "\${ZSERIO_ROOT}/cmake")

# cmake helpers
include(cmake_utils)

# setup compiler
include(compiler_utils)
compiler_set_pthread()
compiler_set_static_clibs()
compiler_set_warnings()
compiler_set_warnings_as_errors()${SQLITE_SETUP}${GRPC_SETUP}

# add zserio runtime library
include(zserio_utils)
set(ZSERIO_RUNTIME_LIBRARY_DIR "\${ZSERIO_RELEASE}/runtime_libs/cpp")
zserio_add_runtime_library(RUNTIME_LIBRARY_DIR "\${ZSERIO_RUNTIME_LIBRARY_DIR}"
                           INCLUDE_INSPECTOR ${INSPECTOR_USE})${INSPECTOR_SETUP}

file(GLOB_RECURSE SOURCES RELATIVE "\${CMAKE_CURRENT_SOURCE_DIR}" "gen/*.cpp" "gen/*.h")
add_library(\${PROJECT_NAME} \${SOURCES})
target_include_directories(\${PROJECT_NAME} PUBLIC "\${CMAKE_CURRENT_SOURCE_DIR}/gen")
target_link_libraries(\${PROJECT_NAME} ZserioCppRuntime)${SQLITE_USE}${GRPC_USE}

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET \${PROJECT_NAME} SOURCE_DIR \${CMAKE_CURRENT_SOURCE_DIR})

add_test(compile_generated_cpp \${CMAKE_COMMAND} -E echo "Generated sources were successfully compiled!")
EOF
}

# Run zserio tests.
test()
{
    exit_if_argc_ne $# 14
    local ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift
    local ZSERIO_PROJECT_ROOT="$1"; shift
    local ZSERIO_BUILD_DIR="$1"; shift
    local TEST_OUT_DIR="$1"; shift
    local MSYS_WORKAROUND_TEMP=("${!1}"); shift
    local CPP_TARGETS=("${MSYS_WORKAROUND_TEMP[@]}")
    local PARAM_JAVA="$1"; shift
    local PARAM_PYTHON="$1"; shift
    local PARAM_XML="$1"; shift
    local PARAM_DOC="$1"; shift
    local SWITCH_DIRECTORY="$1"; shift
    local SWITCH_SOURCE="$1"; shift
    local SWITCH_TEST_NAME="$1"; shift
    local SWITCH_WERROR="$1"; shift

    # unpack testing release
    local UNPACKED_ZSERIO_RELEASE_DIR
    unpack_release "${TEST_OUT_DIR}" "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" UNPACKED_ZSERIO_RELEASE_DIR
    if [ $? -ne 0 ] ; then
        return 1
    fi

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
    if [[ ${PARAM_XML} == 1 ]] ; then
        rm -rf "${TEST_OUT_DIR}/xml"
        ZSERIO_ARGS+=("-xml" "${TEST_OUT_DIR}/xml")
    fi
    if [[ ${PARAM_DOC} == 1 ]] ; then
        rm -rf "${TEST_OUT_DIR}/doc"
        ZSERIO_ARGS+=("-doc" "${TEST_OUT_DIR}/doc")
    fi

    run_zserio_tool "${UNPACKED_ZSERIO_RELEASE_DIR}" "${TEST_OUT_DIR}" \
        "${SWITCH_DIRECTORY}" "${SWITCH_SOURCE}" ${SWITCH_WERROR} ZSERIO_ARGS[@]
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # compile generated Java sources
    if [[ ${PARAM_JAVA} == 1 ]] ; then
        echo "Compile generated Java sources"
        ! grep "import static io.grpc" -qr ${TEST_OUT_DIR}/java/gen
        local JAVA_NEEDS_GRPC=$?
        ! grep "import java.sql.Connection" -qr ${TEST_OUT_DIR}/java/gen
        local JAVA_NEEDS_SQLITE=$?
        generate_ant_file "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" \
            "${TEST_OUT_DIR}/java" "${SWITCH_TEST_NAME}" ${JAVA_NEEDS_SQLITE} ${JAVA_NEEDS_GRPC}
        local ANT_PROPS=()
        compile_java ${TEST_OUT_DIR}/java/build.xml ANT_PROPS[@] run
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # run generated C++ sources
    if [[ ${#CPP_TARGETS[@]} != 0 ]] ; then
        echo "Compile generated C++ sources"
        ! grep "#include <grpcpp/impl/codegen" -qr ${TEST_OUT_DIR}/cpp/gen
        local CPP_NEEDS_GRPC=$?
        ! grep "#include <sqlite3.h>" -qr ${TEST_OUT_DIR}/cpp/gen
        local CPP_NEEDS_SQLITE=$?
        ! grep "#include <zserio/inspector/BlobInspectorTree.h>" -qr ${TEST_OUT_DIR}/cpp/gen
        local CPP_NEEDS_INSPECTOR=$?
        generate_cmake_lists "${UNPACKED_ZSERIO_RELEASE_DIR}" "${ZSERIO_PROJECT_ROOT}" \
            "${TEST_OUT_DIR}/cpp" "${SWITCH_TEST_NAME}" \
            ${CPP_NEEDS_SQLITE} ${CPP_NEEDS_INSPECTOR} ${CPP_NEEDS_GRPC}
        local CTEST_ARGS=()
        if [ ${CPP_NEEDS_GRPC} -ne 0 ] ; then
            CMAKE_ARGS+=("-DGRPC_ROOT=${GRPC_ROOT}" "-DGRPC_ENABLED=ON")
        fi
        compile_cpp "${ZSERIO_PROJECT_ROOT}" "${TEST_OUT_DIR}/cpp" "${TEST_OUT_DIR}/cpp" CPP_TARGETS[@] \
                    CMAKE_ARGS[@] CTEST_ARGS[@] all
        if [ $? -ne 0 ] ; then
            return 1
        fi
    fi

    # pylint generated Python sources
    if [[ ${PARAM_PYTHON} == 1 ]] ; then
        activate_python_virtualenv "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}"
        if [ $? -ne 0 ] ; then
            return 1
        fi

        local PYLINT_RCFILE="${ZSERIO_PROJECT_ROOT}/compiler/extensions/python/runtime/pylintrc.txt"
        local GEN_PYTHON_DIR="${TEST_OUT_DIR}/python/gen"
        local PYTHON_RUNTIME_ROOT="${UNPACKED_ZSERIO_RELEASE_DIR}/runtime_libs/python"

        echo
        echo "Running pylint on Python generated files."

        local GEN_DISABLE_OPTION=""
        GEN_DISABLE_OPTION+="missing-docstring,invalid-name,no-self-use,duplicate-code,line-too-long,"
        GEN_DISABLE_OPTION+="singleton-comparison,too-many-instance-attributes,too-many-arguments,"
        GEN_DISABLE_OPTION+="too-many-public-methods,too-many-locals,too-many-branches,too-many-statements,"
        GEN_DISABLE_OPTION+="too-many-lines,unneeded-not,superfluous-parens,len-as-condition,"
        GEN_DISABLE_OPTION+="import-self,too-few-public-methods,too-many-function-args,c-extension-no-member,"
        GEN_DISABLE_OPTION+="simplifiable-if-expression"
        local PYLINT_ARGS=("--disable=${GEN_DISABLE_OPTION}" "--ignore=api.py")
        PYTHONPATH="${GEN_PYTHON_DIR}:${PYTHON_RUNTIME_ROOT}" \
        run_pylint "${PYLINT_RCFILE}" PYLINT_ARGS[@] "${GEN_PYTHON_DIR}"/*
        if [ $? -ne 0 ]; then
            return 1
        fi

        echo "Running pylint on Python generated api.py files."

        local API_DISABLE_OPTION="missing-docstring,unused-import,line-too-long,redefined-builtin"
        # ignore all files that are not api.py, but don't ignore directories
        local PYLINT_ARGS=("--disable=${API_DISABLE_OPTION}" "--ignore-patterns=^.*\.py(?<!^api\.py)$")
        PYTHONPATH="${GEN_PYTHON_DIR}" \
        run_pylint "${PYLINT_RCFILE}" PYLINT_ARGS[@] "${GEN_PYTHON_DIR}"/*
        if [ $? -ne 0 ]; then
            return 1
        fi
    fi

    return 0
}

# Unpack zserio release zips.
unpack_release()
{
    exit_if_argc_ne $# 4
    local TEST_OUT_DIR="$1"; shift
    local ZSERIO_RELEASE_DIR="$1"; shift
    local ZSERIO_VERSION="$1"; shift
    local UNPACKED_ZSERIO_RELEASE_DIR_OUT="$1"; shift

    local UNPACKED_ZSERIO_RELEASE_DIR_LOC="${TEST_OUT_DIR}/tested_release"
    rm -rf "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}" # always use fresh release
    mkdir -p "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"

    # bin
    "${UNZIP}" -q "${ZSERIO_RELEASE_DIR}/zserio-${ZSERIO_VERSION}-bin.zip" \
        -d "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip zserio binaries to ${UNPACKED_ZSERIO_RELEASE_DIR_LOC}!"
        return 1
    fi

    # runtime-libs
    "${UNZIP}" -q "${ZSERIO_RELEASE_DIR}/zserio-${ZSERIO_VERSION}-runtime-libs.zip" \
        -d "${UNPACKED_ZSERIO_RELEASE_DIR_LOC}"
    if [ $? -ne 0 ] ; then
        stderr_echo "Cannot unzip zserio runtime libraries to ${UNPACKED_ZSERIO_RELEASE_DIR_LOC}!"
        return 1
    fi

    eval ${UNPACKED_ZSERIO_RELEASE_DIR_OUT}="'${UNPACKED_ZSERIO_RELEASE_DIR_LOC}'"

    return 0
}

# Print help message.
print_help()
{
    cat << EOF
Description:
    Tests given zserio sources with zserio release compiled in release-ver directory.

Usage:
    $0 [-h] generator... -s test.zs

Arguments:
    -h, --help                Show this help.
    -p, --purge               Purge test build directory.
    -o <dir>, --output-directory <dir>
                              Output directory where tests will be run.
    -d, --source-dir DIR      Directory with zserio sources. Default is ".".
    -s, --source SOURCE       Main zserio source.
    -t, --test-name NAME      Test name. Optional.
    -w, --werror              Treat zserio warnings as errors.
    generator                 Specify the generator to test.

Generator can be:
    cpp-linux32         Generate C++ sources and compile them for linux32 target (GCC).
    cpp-linux64         Generate C++ sources and compile them for for linux64 target (GCC).
    cpp-windows32-mingw Generate C++ sources and compile them for for windows32 target (MinGW).
    cpp-windows64-mingw Generate C++ sources and compile them for for windows64 target (MinGW64).
    cpp-windows32-msvc  Generate C++ sources and compile them for for windows32 target (MSVC).
    cpp-windows64-msvc  Generate C++ sources and compile them for for windows64 target (MSVC).
    java                Generate Java sources and compile them.
    python              Generate python sources.
    xml                 Generate XML.
    doc                 Generate HTML documentation.
    all-linux32         Test all generators and compile all possible linux32 sources (GCC).
    all-linux64         Test all generators and compile all possible linux64 sources (GCC).
    all-windows32-mingw Test all generators and compile all possible windows32 sources (MinGW).
    all-windows64-mingw Test all generators and compile all possible windows64 sources (MinGW64).
    all-windows32-msvc  Test all generators and compile all possible windows32 sources (MSVC).
    all-windows64-msvc  Test all generators and compile all possible windows64 sources (MSVC).

Examples:
    $0 cpp-linux64 java python xml doc -d /tmp/zs -s test.zs
    $0 all-linux64 -d /tmp/zs -s test.zs

EOF

    print_test_help_env
    echo
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
    exit_if_argc_lt $# 10
    local PARAM_CPP_TARGET_ARRAY_OUT="$1"; shift
    local PARAM_JAVA_OUT="$1"; shift
    local PARAM_PYTHON_OUT="$1"; shift
    local PARAM_XML_OUT="$1"; shift
    local PARAM_DOC_OUT="$1"; shift
    local PARAM_OUT_DIR_OUT="$1"; shift
    local SWITCH_DIRECTORY_OUT="$1"; shift
    local SWITCH_SOURCE_OUT="$1"; shift
    local SWITCH_TEST_NAME_OUT="$1"; shift
    local SWITCH_WERROR_OUT="$1"; shift
    local SWITCH_PURGE_OUT="$1"; shift

    eval ${PARAM_JAVA_OUT}=0
    eval ${PARAM_PYTHON_OUT}=0
    eval ${PARAM_XML_OUT}=0
    eval ${PARAM_DOC_OUT}=0
    eval ${SWITCH_DIRECTORY_OUT}="."
    eval ${SWITCH_SOURCE_OUT}=""
    eval ${SWITCH_TEST_NAME_OUT}=""
    eval ${SWITCH_WERROR_OUT}=0
    eval ${SWITCH_PURGE_OUT}=0

    local NUM_PARAMS=0
    local PARAM_ARRAY=();
    local ARG="$1"
    while [ -n "${ARG}" ] ; do
        case "${ARG}" in
            "-h" | "--help")
                return 2
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

            "-w" | "--werror")
                eval ${SWITCH_WERROR_OUT}=1
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
            "cpp-linux32" | "cpp-linux64" | "cpp-windows32-"* | "cpp-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#cpp-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
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

            "all-linux32" | "all-linux64" | "all-windows32-"* | "all-windows64-"*)
                eval ${PARAM_CPP_TARGET_ARRAY_OUT}[${NUM_TARGETS}]="${PARAM#all-}"
                NUM_TARGETS=$((NUM_TARGETS + 1))
                eval ${PARAM_JAVA_OUT}=1
                eval ${PARAM_PYTHON_OUT}=1
                eval ${PARAM_XML_OUT}=1
                eval ${PARAM_DOC_OUT}=1
                ;;

            *)
                stderr_echo "Invalid argument ${PARAM}!"
                echo
                return 1
        esac
    done

    if [[ ${NUM_TARGETS} == 0 &&
          ${!PARAM_JAVA_OUT} == 0 &&
          ${!PARAM_PYTHON_OUT} == 0 &&
          ${!PARAM_DOC_OUT} == 0 &&
          ${!PARAM_XML_OUT} == 0 &&
          ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Generator to test is not specified!"
        echo
        return 1
    fi

    if [[ "${!SWITCH_SOURCE_OUT}" == "" && ${!SWITCH_PURGE_OUT} == 0 ]] ; then
        stderr_echo "Main zserio source is not set!"
        echo
        return 1
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
    echo "Testing generators on given zserio sources."
    echo

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
    local SWITCH_DIRECTORY
    local SWITCH_SOURCE
    local SWITCH_TEST_NAME
    local SWITCH_WERROR
    local SWITCH_PURGE
    parse_arguments PARAM_CPP_TARGET_ARRAY PARAM_JAVA PARAM_PYTHON PARAM_XML PARAM_DOC PARAM_OUT_DIR \
            SWITCH_DIRECTORY SWITCH_SOURCE SWITCH_TEST_NAME SWITCH_WERROR SWITCH_PURGE $@
    if [ $? -ne 0 ] ; then
        print_help
        return 1
    fi

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

    # purge if requested and then create test output directory
    local ZSERIO_BUILD_DIR="${PARAM_OUT_DIR}/build"
    local TEST_OUT_DIR="${ZSERIO_BUILD_DIR}/test_zs/${SWITCH_TEST_NAME}"
    if [[ ${SWITCH_PURGE} == 1 ]] ; then
        echo "Purging test directory." # purges all tests in test_zs directory
        echo
        rm -rf "${TEST_OUT_DIR}/"

        if [[ ${#PARAM_CPP_TARGET_ARRAY[@]} == 0 &&
              ${PARAM_JAVA} == 0 &&
              ${PARAM_PYTHON} == 0 &&
              ${PARAM_XML} == 0 &&
              ${PARAM_DOC} == 0 ]] ; then
            return 0; # purge only
        fi
    fi
    mkdir -p "${TEST_OUT_DIR}"

    # get zserio release directory
    local ZSERIO_RELEASE_DIR
    local ZSERIO_VERSION
    get_release_dir "${ZSERIO_PROJECT_ROOT}" ZSERIO_RELEASE_DIR ZSERIO_VERSION
    if [ $? -ne 0 ] ; then
        return 1
    fi

    # print information
    echo "Testing release: ${ZSERIO_RELEASE_DIR}"
    echo "Test output directory: ${TEST_OUT_DIR}"
    echo

    # run test
    test "${ZSERIO_RELEASE_DIR}" "${ZSERIO_VERSION}" "${ZSERIO_PROJECT_ROOT}" "${ZSERIO_BUILD_DIR}" \
         "${TEST_OUT_DIR}" PARAM_CPP_TARGET_ARRAY[@] ${PARAM_JAVA} ${PARAM_PYTHON} ${PARAM_XML} ${PARAM_DOC} \
         ${SWITCH_DIRECTORY} ${SWITCH_SOURCE} ${SWITCH_TEST_NAME} ${SWITCH_WERROR}
    if [ $? -ne 0 ] ; then
        return 1
    fi

    return 0
}

main "$@"
