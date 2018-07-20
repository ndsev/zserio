package expressions.cast_uint8_to_uint64;

struct CastUInt8ToUInt64Expression
{
    uint8   uint8Value;
    bool    useConstant;

    function uint64 uint64Value()
    {
        return (useConstant) ? 1 : uint8Value;
    }
};
