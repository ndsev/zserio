set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_reflection_code/Bitmask.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Bitmask.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/BitmaskLen.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Child.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Child.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Choice.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Choice.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Empty.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Empty.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/EmptyString.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Extended.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Extended.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Original.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Original.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Parameterized.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Parameterized.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Selector.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/SelectorEnum.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/SelectorEnum.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Struct.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Struct.h
    ${ZSERIO_GEN_DIR}/with_reflection_code/Union.cpp
    ${ZSERIO_GEN_DIR}/with_reflection_code/Union.h
)

zserio_add_library(with_reflection_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_reflection_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withTypeInfoCode -withReflectionCode -withoutSourcesAmalgamation
)

add_custom_test(with_reflection_code
    DEPENDS
        with_reflection_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithReflectionCodeTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
