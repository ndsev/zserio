package functions.structure_param;

struct MetresConverter(uint16 metres)
{
    uint16  valueA;

    function uint16 toCentimetres()
    {
        return 100*metres;
    }
};

struct MetresConverterCaller
{
    MetresConverter(2)  metresConverter;
    uint16              centimeters : centimeters == metresConverter.toCentimetres();
};
