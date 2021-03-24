set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/invalid_in_java/Test.cpp
    ${ZSERIO_GEN_DIR}/invalid_in_java/Test.h
    ${ZSERIO_GEN_DIR}/invalid_in_python/Test.cpp
    ${ZSERIO_GEN_DIR}/invalid_in_python/Test.h
)

zserio_add_library(TARGET invalid_in_java_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE invalid_in_java.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withoutCrossExtensionCheck
)

zserio_add_library(TARGET invalid_in_python_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE invalid_in_python.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withoutCrossExtensionCheck
)

add_test_sources(
    cpp/WithoutCrossExtensionCheckTest.cpp
)

add_test_dependencies(invalid_in_java_zs)
add_test_dependencies(invalid_in_python_zs)
