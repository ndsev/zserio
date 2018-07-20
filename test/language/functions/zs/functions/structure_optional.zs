package functions.structure_optional;

/* Important thing here is that defaultValue is set to 0 if this type is constructed using default (empty)
 * constructor. However, externalValue is optional, so it must be initialized by default constructor as well.
 * Otherwise calling of function value() will throw if it is called immediatelly after construction. Please
 * note that this is not necessary for BitStreamReader constructor which can leave all optional fields
 * uninitialized. */
struct ValueCalculator
{
    bit:4   defaultValue;
    bit:4   externalValue   if defaultValue == 0;

    function bit:4 value()
    {
        return ((defaultValue == 0) ? externalValue : defaultValue);
    }
};

struct ValueConsumer(bit:4 value)
{
    bool isSmall : value < 8;
};

/* valueCalculator.value() will be called during construction. */
struct ValueConsumerCreator
{
    ValueCalculator                         valueCalculator;
    ValueConsumer(valueCalculator.value())  valueConsumer;
};
