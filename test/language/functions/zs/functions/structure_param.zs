package functions.structure_param;

struct MetresConverter(uint16 metres)
{
    uint16  a;

    function uint16 toCentimetres()
    {
        return 100*metres;
    }
};

struct MetresConverterCaller
{
    MetresConverter(2)  metresConverter;
    uint16              cm : cm == metresConverter.toCentimetres();
};
