set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/member_access/access_within_type/Header.cpp
    ${ZSERIO_GEN_DIR}/member_access/access_within_type/Header.h
    ${ZSERIO_GEN_DIR}/member_access/access_within_type/Message.cpp
    ${ZSERIO_GEN_DIR}/member_access/access_within_type/Message.h
)

zserio_add_library(TARGET member_access_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE member_access.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/AccessWithinTypeTest.cpp
)

add_test_dependencies(member_access_zs)
