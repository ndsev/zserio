set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/gif/GifFile.cpp
    ${ZSERIO_GEN_DIR}/gif/GifFile.h

    ${ZSERIO_GEN_DIR}/gif_data/ApplicationExtension.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ApplicationExtension.h
    ${ZSERIO_GEN_DIR}/gif_data/BlockData.cpp
    ${ZSERIO_GEN_DIR}/gif_data/BlockData.h
    ${ZSERIO_GEN_DIR}/gif_data/BlockType.cpp
    ${ZSERIO_GEN_DIR}/gif_data/BlockType.h
    ${ZSERIO_GEN_DIR}/gif_data/BlockTypes.cpp
    ${ZSERIO_GEN_DIR}/gif_data/BlockTypes.h
    ${ZSERIO_GEN_DIR}/gif_data/CommentExtension.cpp
    ${ZSERIO_GEN_DIR}/gif_data/CommentExtension.h
    ${ZSERIO_GEN_DIR}/gif_data/DisposalMethod.cpp
    ${ZSERIO_GEN_DIR}/gif_data/DisposalMethod.h
    ${ZSERIO_GEN_DIR}/gif_data/ExtensionBlock.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ExtensionBlock.h
    ${ZSERIO_GEN_DIR}/gif_data/ExtensionType.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ExtensionType.h
    ${ZSERIO_GEN_DIR}/gif_data/ExtensionTypes.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ExtensionTypes.h
    ${ZSERIO_GEN_DIR}/gif_data/GifData.cpp
    ${ZSERIO_GEN_DIR}/gif_data/GifData.h
    ${ZSERIO_GEN_DIR}/gif_data/GraphicControlExtension.cpp
    ${ZSERIO_GEN_DIR}/gif_data/GraphicControlExtension.h
    ${ZSERIO_GEN_DIR}/gif_data/ImageBlock.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ImageBlock.h
    ${ZSERIO_GEN_DIR}/gif_data/ImageDescriptor.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ImageDescriptor.h
    ${ZSERIO_GEN_DIR}/gif_data/PlainTextExtension.cpp
    ${ZSERIO_GEN_DIR}/gif_data/PlainTextExtension.h
    ${ZSERIO_GEN_DIR}/gif_data/RasterData.cpp
    ${ZSERIO_GEN_DIR}/gif_data/RasterData.h
    ${ZSERIO_GEN_DIR}/gif_data/SubBlock.cpp
    ${ZSERIO_GEN_DIR}/gif_data/SubBlock.h
    ${ZSERIO_GEN_DIR}/gif_data/ZippedBlockData.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ZippedBlockData.h
    ${ZSERIO_GEN_DIR}/gif_data/ZippedSubBlock.cpp
    ${ZSERIO_GEN_DIR}/gif_data/ZippedSubBlock.h

    ${ZSERIO_GEN_DIR}/gif_version/GifVersion.cpp
    ${ZSERIO_GEN_DIR}/gif_version/GifVersion.h

    ${ZSERIO_GEN_DIR}/rgb_color/RgbColor.cpp
    ${ZSERIO_GEN_DIR}/rgb_color/RgbColor.h

    ${ZSERIO_GEN_DIR}/screen_descriptor/ScreenDescriptor.cpp
    ${ZSERIO_GEN_DIR}/screen_descriptor/ScreenDescriptor.h

    ${ZSERIO_GEN_DIR}/signature/Signature.cpp
    ${ZSERIO_GEN_DIR}/signature/Signature.h
)

zserio_add_library(TARGET gif_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE gif.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(cpp/GifTest.cpp)
add_test_dependencies(gif_zs)

add_custom_command(TARGET gif_zs POST_BUILD
                   COMMAND ${CMAKE_COMMAND} -E copy_directory ${CMAKE_CURRENT_SOURCE_DIR}/data
                           ${CMAKE_CURRENT_BINARY_DIR}/data
                   DEPENDS ${CMAKE_CURRENT_SOURCE_DIR}/data
                   COMMENT "Copying data directory for gif test")
