package gif;

enum bit:2 GIFVersion
{
    V87A,
    V89A,
    VUnknown
};

/**
 * This defines the GDF 87a file format. Exceptions for the GDF 89a file format are given locally.
 */
struct GifFile
{
    Signature           signature;

    ScreenDescriptor( (signature.version[0]==0x38 && signature.version[1]==0x37 &&
                       signature.version[2]==0x61) ? GIFVersion.V87A :
                      (signature.version[0]==0x38 && signature.version[1]==0x39 &&
                       signature.version[2]==0x61) ? GIFVersion.V89A : GIFVersion.VUnknown ) screen;

    GIFData( (signature.version[0]==0x38 && signature.version[1]==0x37 &&
              signature.version[2]==0x61) ? GIFVersion.V87A :
             (signature.version[0]==0x38 && signature.version[1]==0x39 &&
              signature.version[2]==0x61) ? GIFVersion.V89A : GIFVersion.VUnknown) blocks;
};

struct Signature
{
    uint8   format[3];  // only contains "GIF"
    uint8   version[3]; // i.e. "87a"
};

enum uint8 BLOCK_TYPE
{
    EXTENSION_BLOCK     = 0x21, // "!"
    IMAGE_BLOCK         = 0x2C, // ","
    TERMINATOR_BLOCK    = 0x3B  // ";"
};

struct GIFData(GIFVersion version)
{
    BLOCK_TYPE                  tag;
    BlockTypes(version, tag)    block     if tag != BLOCK_TYPE.TERMINATOR_BLOCK;
    GIFData(version)            nextBlock if tag != BLOCK_TYPE.TERMINATOR_BLOCK;
};

choice BlockTypes(GIFVersion version, BLOCK_TYPE tag) on tag
{
    case BLOCK_TYPE.EXTENSION_BLOCK:
        ExtensionBlock      extension;
    case BLOCK_TYPE.IMAGE_BLOCK:
        ImageBlock(version) images;
    default:
        BlockData           unknownData;
};

struct ImageBlock(GIFVersion version)
{
    ImageDescriptor(version)    image;
    RasterData      data;
};

enum uint8 EXTENSION_TYPE
{
    PLAINTEXT_EXTENSION      = 0x0001,
    GRAPHICCONTROL_EXTENSION = 0x00F9,
    COMMENT_EXTENSION        = 0x00FE,
    APPLICATIONEXTENSION     = 0x00FF
};

struct ExtensionBlock
{
    EXTENSION_TYPE  extensionFunctionCode;
    ExtensionTypes(extensionFunctionCode)   extension;
};

choice ExtensionTypes(EXTENSION_TYPE extensionFunctionCode) on extensionFunctionCode
{
    case EXTENSION_TYPE.PLAINTEXT_EXTENSION:
        PlainTextExtension      plainTextData;
    case EXTENSION_TYPE.GRAPHICCONTROL_EXTENSION:
        GraphicControlExtension controlData;
    case EXTENSION_TYPE.COMMENT_EXTENSION:
        CommentExtension        commentData;
    case EXTENSION_TYPE.APPLICATIONEXTENSION:
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

struct ScreenDescriptor(GIFVersion version)
{
    uint16      width;
    uint16      height;

    bit:1       globalColorMapFollows;
    bit:3       bitsOfColorResulution;
    bit:1       _null1_          if version == GIFVersion.V87A : _null1_ == 0;
    bit:1       sortFlag         if version == GIFVersion.V89A;
    bit:3       bitsPerPixel;

    uint8       bgColor;
    uint8       _null2_          if version == GIFVersion.V87A : _null2_ == 0;
    uint8       pixelAspectRatio if version == GIFVersion.V89A;

    RGBColor    globalColorMap[1 << (bitsPerPixel+1)] if globalColorMapFollows == 1;
};

struct ImageDescriptor(GIFVersion version)
{
    // TODO: add constraints for the top left corner
    uint16      left;
    uint16      top;
    uint16      width;
    uint16      height;

    bit:1       localColorMapFollows;
    bit:1       interlacedFormatted;
    bit:1       _null1_  if version == GIFVersion.V87A : _null1_ == 0;
    bit:1       sortFlag if version == GIFVersion.V89A;
    bit:2       _null2_ : _null2_ == 0;
    bit:3       bitsPerPixel;

    RGBColor    localColorMap[1 << (bitsPerPixel+1)] if localColorMapFollows == 1;
};

struct RasterData
{
    uint8           codeSize;
    ZippedBlockData data;
};

struct RGBColor
{
    uint8   red;
    uint8   green;
    uint8   blue;
};
