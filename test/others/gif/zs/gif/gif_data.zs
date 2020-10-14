package gif.gif_data;

import gif.gif_version.GifVersion;
import gif.rgb_color.RgbColor;

struct GifData(GifVersion version)
{
    BlockType                   tag;
    BlockTypes(version, tag)    block     if tag != BlockType.TERMINATOR_BLOCK;
    GifData(version)            nextBlock if tag != BlockType.TERMINATOR_BLOCK;
};

enum uint8 BlockType
{
    EXTENSION_BLOCK     = 0x21, // "!"
    IMAGE_BLOCK         = 0x2C, // ","
    TERMINATOR_BLOCK    = 0x3B  // ";"
};


choice BlockTypes(GifVersion version, BlockType tag) on tag
{
    case BlockType.EXTENSION_BLOCK:
        ExtensionBlock      extension;
    case BlockType.IMAGE_BLOCK:
        ImageBlock(version) images;
    default:
        BlockData           unknownData;
};

struct ImageBlock(GifVersion version)
{
    ImageDescriptor(version) image;
    RasterData               data;
};

enum uint8 ExtensionType
{
    PLAINTEXT_EXTENSION      = 0x0001,
    GRAPHICCONTROL_EXTENSION = 0x00F9,
    COMMENT_EXTENSION        = 0x00FE,
    APPLICATIONEXTENSION     = 0x00FF
};

struct ExtensionBlock
{
    ExtensionType                         extensionFunctionCode;
    ExtensionTypes(extensionFunctionCode) extension;
};

choice ExtensionTypes(ExtensionType extensionFunctionCode) on extensionFunctionCode
{
    case ExtensionType.PLAINTEXT_EXTENSION:
        PlainTextExtension      plainTextData;
    case ExtensionType.GRAPHICCONTROL_EXTENSION:
        GraphicControlExtension controlData;
    case ExtensionType.COMMENT_EXTENSION:
        CommentExtension        commentData;
    case ExtensionType.APPLICATIONEXTENSION:
        ApplicationExtension    applicationData;
    default:
        BlockData               data;
};

struct PlainTextExtension
{
    uint8   byteCount : byteCount == 12;

    uint16  left;
    uint16  top;
    uint16  width;
    uint16  height;

    uint8   cellWidth;
    uint8   cellHeight;

    uint8   FGColorIndex;
    uint8   BGColorIndex;

    uint8   textSize;
    SubBlock(textSize)  plainTextData if textSize > 0;
};

enum bit:3 DisposalMethod
{
    NotSpecified    = 0,
    NoDispose       = 1,
    RestoreBGColor  = 2,
    RestorePrevious = 3
};

/*
 *  available in 89a
 */
struct GraphicControlExtension
{
    uint8           byteCount : byteCount == 4;

    bit:3           _null_  : _null_ == 0;
    DisposalMethod  disposalMethod;
    bit:1           userInput;
    bit:1           transparentColor;

    uint16          delayTime;
    uint8           transparentColorIndex;

    uint8           blockTerminator : blockTerminator == 0;
};

struct CommentExtension
{
    uint8   byteCount;
    SubBlock(byteCount) commentData if byteCount > 0;
};

struct ApplicationExtension
{
    uint8   byteCount : byteCount == 11;

    uint8   applicationIdentifier[8];
    uint8   authenticationCode[3];

    uint8   applDataSize;
    SubBlock(applDataSize)  applicationData if applDataSize > 0;
};

struct BlockData
{
    uint8   byteCount;
    SubBlock(byteCount) dataBytes if byteCount > 0;
};

struct ZippedBlockData
{
    uint8   byteCount;
    ZippedSubBlock(byteCount) dataBytes if byteCount > 0;
};

struct SubBlock(uint8 byteCount)
{
    uint8   dataBytes[byteCount] : byteCount > 0;
    uint8   blockTerminator;

    SubBlock(blockTerminator) nextData if blockTerminator > 0;
};

struct ZippedSubBlock(uint8 byteCount)
{
    uint8   dataBytes[byteCount] : byteCount > 0;
    uint8   blockTerminator;

    ZippedSubBlock(blockTerminator) nextData if blockTerminator > 0;
};

struct ImageDescriptor(GifVersion version)
{
    uint16      left;
    uint16      top;
    uint16      width;
    uint16      height;

    bit:1       localColorMapFollows;
    bit:1       interlacedFormatted;
    bit:1       _null1_  if version == GifVersion.V87A : _null1_ == 0;
    bit:1       sortFlag if version == GifVersion.V89A;
    bit:2       _null2_ : _null2_ == 0;
    bit:3       bitsPerPixel;

    RgbColor    localColorMap[1 << (bitsPerPixel+1)] if localColorMapFollows == 1;
};

struct RasterData
{
    uint8           codeSize;
    ZippedBlockData data;
};
