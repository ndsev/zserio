package gif.signature;

struct Signature
{
    uint8   format[3];  // only contains "GIF"
    uint8   version[3]; // i.e. "87a"
};
