set(ZSERIO_GEN_DIR_COMPLEX_ALLOCATION ${CMAKE_CURRENT_BINARY_DIR}/gen_complex_allocation)
set(ZSERIO_GENERATED_SOURCES_COMPLEX_ALLOCATION
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/MainStructure.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/MainStructure.h
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_choice/AllocationChoice.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_choice/AllocationChoice.h
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_choice/ChoiceCompound.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_choice/ChoiceCompound.h
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_struct/AllocationStruct.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_struct/AllocationStruct.h
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_struct_optional/AllocationStructOptional.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_struct_optional/AllocationStructOptional.h
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_union/AllocationUnion.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_union/AllocationUnion.h
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_union/UnionCompound.cpp
    ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}/complex_allocation/allocation_union/UnionCompound.h
)

set(ZSERIO_GEN_DIR_PUBSUB_ALLOCATION ${CMAKE_CURRENT_BINARY_DIR}/gen_pubsub_allocation)
set(ZSERIO_GENERATED_SOURCES_PUBSUB_ALLOCATION
    ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}/pubsub_allocation/Greeting.cpp
    ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}/pubsub_allocation/Greeting.h
    ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}/pubsub_allocation/GreetingPubsub.cpp
    ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}/pubsub_allocation/GreetingPubsub.h
    ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}/pubsub_allocation/Name.cpp
    ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}/pubsub_allocation/Name.h
)

set(ZSERIO_GEN_DIR_SERVICE_ALLOCATION ${CMAKE_CURRENT_BINARY_DIR}/gen_service_allocation)
set(ZSERIO_GENERATED_SOURCES_SERVICE_ALLOCATION
    ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}/service_allocation/Greeting.cpp
    ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}/service_allocation/Greeting.h
    ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}/service_allocation/GreetingService.cpp
    ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}/service_allocation/GreetingService.h
    ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}/service_allocation/Name.cpp
    ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}/service_allocation/Name.h
)

set(ZSERIO_GEN_DIR_SQL_ALLOCATION ${CMAKE_CURRENT_BINARY_DIR}/gen_sql_allocation)
set(ZSERIO_GENERATED_SOURCES_SQL_ALLOCATION
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/Color.cpp
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/Color.h
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/DataBlob.cpp
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/DataBlob.h
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/ParameterizedBlob.cpp
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/ParameterizedBlob.h
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/Role.cpp
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/Role.h
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/SqlAllocationDb.cpp
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/SqlAllocationDb.h
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/SqlAllocationTable.cpp
    ${ZSERIO_GEN_DIR_SQL_ALLOCATION}/sql_allocation/SqlAllocationTable.h
)

zserio_add_library(TARGET complex_allocation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE complex_allocation.zs
    OUT_DIR ${ZSERIO_GEN_DIR_COMPLEX_ALLOCATION}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_COMPLEX_ALLOCATION}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -setCppAllocator polymorphic -withoutSourcesAmalgamation
)

zserio_add_library(TARGET pubsub_allocation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE pubsub_allocation.zs
    OUT_DIR ${ZSERIO_GEN_DIR_PUBSUB_ALLOCATION}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_PUBSUB_ALLOCATION}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -setCppAllocator polymorphic -withoutSourcesAmalgamation
)

zserio_add_library(TARGET service_allocation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE service_allocation.zs
    OUT_DIR ${ZSERIO_GEN_DIR_SERVICE_ALLOCATION}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_SERVICE_ALLOCATION}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -setCppAllocator polymorphic -withoutSourcesAmalgamation
)

zserio_add_library(TARGET sql_allocation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_allocation.zs
    OUT_DIR ${ZSERIO_GEN_DIR_SQL_ALLOCATION}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_SQL_ALLOCATION}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -setCppAllocator polymorphic -withoutSourcesAmalgamation -withValidationCode
)

target_include_directories(sql_allocation_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/ComplexAllocationTest.cpp
    cpp/PubsubAllocationTest.cpp
    cpp/ServiceAllocationTest.cpp
    cpp/SqlAllocationTest.cpp
)

add_test_dependencies(complex_allocation_zs)
add_test_dependencies(pubsub_allocation_zs)
add_test_dependencies(service_allocation_zs)
add_test_dependencies(sql_allocation_zs)
