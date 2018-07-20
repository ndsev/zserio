set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/gif/ApplicationExtension.cpp
    ${ZSERIO_GEN_DIR}/gif/ApplicationExtension.h
    ${ZSERIO_GEN_DIR}/gif/BlockData.cpp
    ${ZSERIO_GEN_DIR}/gif/BlockData.h
    ${ZSERIO_GEN_DIR}/gif/BLOCK_TYPE.cpp
    ${ZSERIO_GEN_DIR}/gif/BLOCK_TYPE.h
    ${ZSERIO_GEN_DIR}/gif/BlockTypes.cpp
    ${ZSERIO_GEN_DIR}/gif/BlockTypes.h
    ${ZSERIO_GEN_DIR}/gif/CommentExtension.cpp
    ${ZSERIO_GEN_DIR}/gif/CommentExtension.h
    ${ZSERIO_GEN_DIR}/gif/DisposalMethod.cpp
    ${ZSERIO_GEN_DIR}/gif/DisposalMethod.h
    ${ZSERIO_GEN_DIR}/gif/ExtensionBlock.cpp
    ${ZSERIO_GEN_DIR}/gif/ExtensionBlock.h
    ${ZSERIO_GEN_DIR}/gif/EXTENSION_TYPE.cpp
    ${ZSERIO_GEN_DIR}/gif/EXTENSION_TYPE.h
    ${ZSERIO_GEN_DIR}/gif/ExtensionTypes.cpp
    ${ZSERIO_GEN_DIR}/gif/ExtensionTypes.h
    ${ZSERIO_GEN_DIR}/gif/GIFData.cpp
    ${ZSERIO_GEN_DIR}/gif/GIFData.h
    ${ZSERIO_GEN_DIR}/gif/GifFile.cpp
    ${ZSERIO_GEN_DIR}/gif/GifFile.h
    ${ZSERIO_GEN_DIR}/gif/GIFVersion.cpp
    ${ZSERIO_GEN_DIR}/gif/GIFVersion.h
    ${ZSERIO_GEN_DIR}/gif/GraphicControlExtension.cpp
    ${ZSERIO_GEN_DIR}/gif/GraphicControlExtension.h
    ${ZSERIO_GEN_DIR}/gif/ImageBlock.cpp
    ${ZSERIO_GEN_DIR}/gif/ImageBlock.h
    ${ZSERIO_GEN_DIR}/gif/ImageDescriptor.cpp
    ${ZSERIO_GEN_DIR}/gif/ImageDescriptor.h
    ${ZSERIO_GEN_DIR}/gif/PlainTextExtension.cpp
    ${ZSERIO_GEN_DIR}/gif/PlainTextExtension.h
    ${ZSERIO_GEN_DIR}/gif/RasterData.cpp
    ${ZSERIO_GEN_DIR}/gif/RasterData.h
    ${ZSERIO_GEN_DIR}/gif/RGBColor.cpp
    ${ZSERIO_GEN_DIR}/gif/RGBColor.h
    ${ZSERIO_GEN_DIR}/gif/ScreenDescriptor.cpp
    ${ZSERIO_GEN_DIR}/gif/ScreenDescriptor.h
    ${ZSERIO_GEN_DIR}/gif/Signature.cpp
    ${ZSERIO_GEN_DIR}/gif/Signature.h
    ${ZSERIO_GEN_DIR}/gif/SubBlock.cpp
    ${ZSERIO_GEN_DIR}/gif/SubBlock.h
    ${ZSERIO_GEN_DIR}/gif/ZippedBlockData.cpp
    ${ZSERIO_GEN_DIR}/gif/ZippedBlockData.h
    ${ZSERIO_GEN_DIR}/gif/ZippedSubBlock.cpp
    ${ZSERIO_GEN_DIR}/gif/ZippedSubBlock.h
)

zserio_add_library(TARGET gif_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE gif.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(cpp/gifTest.cpp)
add_test_dependencies(gif_zs)

add_custom_command(TARGET gif_zs POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E copy_directory ${CMAKE_CURRENT_SOURCE_DIR}/data
                           ${CMAKE_CURRENT_BINARY_DIR}/data
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/data
                   COMMENT "Copying data directory for gif test")
