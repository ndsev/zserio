#!/usr/bin/env bash

# Tests select_legs.sh: each case feeds changed paths and compares the selected legs.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SELECT_LEGS="${SCRIPT_DIR}/select_legs.sh"

NUM_PASSED=0
NUM_FAILED=0

# check <name> <expected legs separated by space or "none"> <changed paths...>
check()
{
    local NAME="$1"; shift
    local EXPECTED_LEGS="$1"; shift

    local EXPECTED=""
    local LEG
    for LEG in zserio cpp java python doc xml ; do
        local VALUE=false
        if [[ " ${EXPECTED_LEGS} " == *" ${LEG} "* ]] ; then
            VALUE=true
        fi
        EXPECTED+="${LEG}=${VALUE}"$'\n'
    done

    local ACTUAL
    ACTUAL="$(printf "%s\n" "$@" | "${SELECT_LEGS}")"$'\n'

    if [[ "${ACTUAL}" == "${EXPECTED}" ]] ; then
        echo "PASSED: ${NAME}"
        NUM_PASSED=$((NUM_PASSED + 1))
    else
        echo "FAILED: ${NAME}"
        echo "  expected: $(echo "${EXPECTED}" | tr "\n" " ")"
        echo "  actual:   $(echo "${ACTUAL}" | tr "\n" " ")"
        NUM_FAILED=$((NUM_FAILED + 1))
    fi
}

ALL="zserio cpp java python doc xml"

check "python extension only runs python legs" "zserio python" \
        compiler/extensions/python/runtime/src/zserio/__init__.py \
        compiler/extensions/python/freemarker/api.py.ftl
check "docs only runs no legs" "none" \
        doc/ZserioUserGuide.md README.md compiler/extensions/python/README.md LICENSE CNAME
check "compiler core runs all legs" "${ALL}" compiler/core/src/zserio/tools/ZserioTool.java
check "scripts run all legs" "${ALL}" scripts/build.sh
check "cmake runs all legs" "${ALL}" cmake/compiler_utils.cmake
check "github runs all legs" "${ALL}" .github/workflows/build_linux_python.yml
check "github markdown runs all legs" "${ALL}" .github/pull_request_template.md
check "unknown path runs all legs" "${ALL}" build.xml
check "shared test schema runs all legs" "${ALL}" test/extensions/language/alignment/build.xml
check "cpp extension runs cpp and python legs" "zserio cpp python" \
        compiler/extensions/cpp/runtime/src/zserio/AnyHolder.h
check "java extension runs java legs" "zserio java" compiler/extensions/java/build.xml
check "doc extension runs doc legs" "zserio doc" compiler/extensions/doc/build.xml
check "xml extension runs xml legs" "zserio xml" compiler/extensions/xml/build.xml
check "python test runs python legs" "zserio python" \
        test/extensions/language/alignment/python/Alignment.py
check "cpp test runs cpp legs" "zserio cpp" test/extensions/language/alignment/cpp/BitAlignmentTest.cpp
check "java test runs java legs" "zserio java" \
        test/extensions/language/alignment/java/alignment/BitAlignmentTest.java
check "test data submodule runs all legs" "${ALL}" test/data
check "java and xml changes run both" "zserio java xml" \
        compiler/extensions/java/build.xml compiler/extensions/xml/build.xml
check "docs with core change runs all legs" "${ALL}" \
        doc/ZserioUserGuide.md compiler/core/build.xml
check "no changes runs no legs" "none"

echo
echo "${NUM_PASSED} passed, ${NUM_FAILED} failed"
[[ ${NUM_FAILED} -eq 0 ]]
