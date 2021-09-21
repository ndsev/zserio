package allow_implicit_arrays.lengthof_with_implicit_array;

struct LengthOfWithImplicitArray
{
    implicit uint8  implicitArray[];

    function uint8 getLengthOfImplicitArray()
    {
        return lengthof(implicitArray);
    }
};
