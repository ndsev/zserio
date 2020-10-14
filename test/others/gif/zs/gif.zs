package gif;

import gif.signature.Signature;
import gif.gif_version.GifVersion;
import gif.screen_descriptor.ScreenDescriptor;
import gif.gif_data.GifData;

/**
 * This defines the GDF 87a file format. Exceptions for the GDF 89a file format are given locally.
 */
struct GifFile
{
    Signature           signature;

    ScreenDescriptor( (signature.version[0]==0x38 && signature.version[1]==0x37 &&
                       signature.version[2]==0x61) ? GifVersion.V87A :
                      (signature.version[0]==0x38 && signature.version[1]==0x39 &&
                       signature.version[2]==0x61) ? GifVersion.V89A : GifVersion.VUnknown ) screen;

    GifData( (signature.version[0]==0x38 && signature.version[1]==0x37 &&
              signature.version[2]==0x61) ? GifVersion.V87A :
             (signature.version[0]==0x38 && signature.version[1]==0x39 &&
              signature.version[2]==0x61) ? GifVersion.V89A : GifVersion.VUnknown) blocks;
};
