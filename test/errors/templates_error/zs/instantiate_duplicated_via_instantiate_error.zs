package instantiate_duplicated_via_instantiate_error;

struct Data<T>
{
    T field;
};

instantiate Data<uint8> Data8;

struct Other<T>
{
    T field;
};

instantiate Other<Data8> OtherFirst;
instantiate Other<Data<uint8>> OtherSecond;
