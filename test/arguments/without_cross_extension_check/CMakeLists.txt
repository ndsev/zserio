set(ZSERIO_GEN_DIR_JAVA ${CMAKE_CURRENT_BINARY_DIR}/gen_java)
set(ZSERIO_GENERATED_SOURCES_JAVA
    ${ZSERIO_GEN_DIR_JAVA}/invalid_in_java/Test.cpp
    ${ZSERIO_GEN_DIR_JAVA}/invalid_in_java/Test.h
)

set(ZSERIO_GEN_DIR_PYTHON ${CMAKE_CURRENT_BINARY_DIR}/gen_python)
set(ZSERIO_GENERATED_SOURCES_PYTHON
    ${ZSERIO_GEN_DIR_PYTHON}/invalid_in_python/Test.cpp
    ${ZSERIO_GEN_DIR_PYTHON}/invalid_in_python/Test.h
)

zserio_add_library(invalid_in_java_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE invalid_in_java.zs
    OUT_DIR ${ZSERIO_GEN_DIR_JAVA}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_JAVA}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withoutCrossExtensionCheck
)

zserio_add_library(invalid_in_python_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE invalid_in_python.zs
    OUT_DIR ${ZSERIO_GEN_DIR_PYTHON}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_PYTHON}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withoutCrossExtensionCheck
)

add_custom_test(without_cross_extension_check
    DEPENDS
        invalid_in_java_zs
        invalid_in_python_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutCrossExtensionCheckTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES_JAVA}
        ${ZSERIO_GENERATED_SOURCES_PYTHON}
)
