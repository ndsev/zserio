package service_types.complex_types_service;

subtype ColorModelChoice Color;

struct RequestData(ColorModel model)
{
    uint32          offsets[];
offsets[@index]:
    Color(model)    data[];
};

enum bit:8 ColorModel
{
    RGB = 1,
    CMYK = 2
};

struct RGBModel
{
    bit:8 red;
    bit:8 green;
    bit:8 blue;
};

struct CMYKModel
{
    bit:8 cyan;
    bit:8 magneta;
    bit:8 yellow;
    bit:8 key; // (blacK)
};

choice ColorModelChoice(ColorModel model) on model
{
case RGB:
    RGBModel rgb;
case CMYK:
    CMYKModel cmyk;
};

struct Request
{
    ColorModel model;
    RequestData(model) data;
};

union ResponseData(uint32 length)
{
    RGBModel rgbData[length];
    CMYKModel cmykData[length];
};

struct Response
{
    uint32 length;
    ResponseData(length) data;
};

struct LengthResponse
{
    uint32 length;
};

service ComplexTypesService
{
    rpc Response swapModels(Request);
    rpc LengthResponse getLength(Request);
};
