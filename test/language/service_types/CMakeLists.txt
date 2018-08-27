if (GRPC_ENABLED)
    set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
    set(ZSERIO_GENERATED_SOURCES
        ${ZSERIO_GEN_DIR}/service_types/simple_service/SimpleService.h
        ${ZSERIO_GEN_DIR}/service_types/simple_service/SimpleService.cpp
        ${ZSERIO_GEN_DIR}/service_types/simple_service/Response.h
        ${ZSERIO_GEN_DIR}/service_types/simple_service/Response.cpp
        ${ZSERIO_GEN_DIR}/service_types/simple_service/Request.h
        ${ZSERIO_GEN_DIR}/service_types/simple_service/Request.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/CMYKModel.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/CMYKModel.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/Color.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ColorModelChoice.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ColorModelChoice.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ColorModel.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ColorModel.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ComplexTypesService.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ComplexTypesService.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/LengthResponse.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/LengthResponse.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/Request.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/RequestData.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/RequestData.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/Request.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/Response.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ResponseData.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/ResponseData.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/Response.h
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/RGBModel.cpp
        ${ZSERIO_GEN_DIR}/service_types/complex_types_service/RGBModel.h
    )

    zserio_add_library(TARGET service_types_zs
        SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
        MAIN_SOURCE service_types.zs
        OUT_DIR ${ZSERIO_GEN_DIR}
        OUT_FILES ${ZSERIO_GENERATED_SOURCES}
        ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
        ZSERIO_OPTIONS -withoutSourcesAmalgamation
    )

    target_include_directories(service_types_zs SYSTEM PRIVATE ${GRPC_INCDIR})

    add_test_sources(
        cpp/SimpleServiceTest.cpp
        cpp/ComplexTypesServiceTest.cpp
    )

    add_test_dependencies(service_types_zs)
endif ()
