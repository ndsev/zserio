package map;

template <TYPE, VALUE>
struct Map
{
    TYPE type;
    Element<TYPE, VALUE>(type) valueList[];
};

template <TYPE, VALUE>
struct Element(TYPE type)
{
    VALUE(type) value;
};

enum uint32 ConcreteType
{
    U32,
    STRING,
    DOUBLE
};

choice ConcreteValue(ConcreteType type) on type
{
    case U32:
        uint32 valueU32;
    case STRING:
        string valueString;
    case DOUBLE:
        float64 valueDouble;
};

subtype Map<ConcreteType, ConcreteValue> ConcreteMap;
