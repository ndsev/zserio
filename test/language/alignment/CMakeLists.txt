set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/alignment/auto_optional_member_alignment/AutoOptionalMemberAlignment.cpp
    ${ZSERIO_GEN_DIR}/alignment/auto_optional_member_alignment/AutoOptionalMemberAlignment.h

    ${ZSERIO_GEN_DIR}/alignment/bit_alignment/BitAlignment.cpp
    ${ZSERIO_GEN_DIR}/alignment/bit_alignment/BitAlignment.h

    ${ZSERIO_GEN_DIR}/alignment/optional_member_alignment/OptionalMemberAlignment.cpp
    ${ZSERIO_GEN_DIR}/alignment/optional_member_alignment/OptionalMemberAlignment.h
)

zserio_add_library(alignment_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE alignment.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(alignment
    DEPENDS
        alignment_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoOptionalMemberAlignmentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitAlignmentTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalMemberAlignmentTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
