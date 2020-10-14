package gif.screen_descriptor;

import gif.gif_version.GifVersion;
import gif.rgb_color.RgbColor;

struct ScreenDescriptor(GifVersion version)
{
    uint16      width;
    uint16      height;

    bit:1       globalColorMapFollows;
    bit:3       bitsOfColorResulution;
    bit:1       _null1_          if version == GifVersion.V87A : _null1_ == 0;
    bit:1       sortFlag         if version == GifVersion.V89A;
    bit:3       bitsPerPixel;

    uint8       bgColor;
    uint8       _null2_          if version == GifVersion.V87A : _null2_ == 0;
    uint8       pixelAspectRatio if version == GifVersion.V89A;

    RgbColor    globalColorMap[1 << (bitsPerPixel+1)] if globalColorMapFollows == 1;
};
