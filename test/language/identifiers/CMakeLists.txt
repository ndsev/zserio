set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/identifiers/bitmask_name_clashing_with_java/BitmaskNameClashingWithJava.cpp
    ${ZSERIO_GEN_DIR}/identifiers/bitmask_name_clashing_with_java/BitmaskNameClashingWithJava.h
    ${ZSERIO_GEN_DIR}/identifiers/bitmask_name_clashing_with_java/String.cpp
    ${ZSERIO_GEN_DIR}/identifiers/bitmask_name_clashing_with_java/String.h

    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Byte.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Byte.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Short.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Short.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Integer.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Integer.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Long.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Long.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/BigInteger.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/BigInteger.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Float.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Float.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Double.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/Double.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/String.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/String.h
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/StructureNameClashingWithJava.cpp
    ${ZSERIO_GEN_DIR}/identifiers/structure_name_clashing_with_java/StructureNameClashingWithJava.h
)

zserio_add_library(TARGET identifiers_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE identifiers.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/BitmaskNameClashingWithJavaTest.cpp
    cpp/StructureNameClashingWithJavaTest.cpp
)

add_test_dependencies(identifiers_zs)
