package expressions.cast_uint64_to_uint8;

struct CastUInt64ToUInt8Expression
{
    uint64  uint64Value;
    bool    useConstant;

    function uint8 uint8Value()
    {
        return (useConstant) ? 1 : uint64Value;
    }
};
