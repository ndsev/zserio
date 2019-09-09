import zserio.runtime.ObjectGenericParameter;

public class MapTest
{
    public static void main(String[] args)
    {
        final ConcreteType concreteType = ConcreteType.STRING;
        final ConcreteValue concreteValue = new ConcreteValue(concreteType);
        concreteValue.setValueString("TEXT");

        final ObjectGenericParameter<ConcreteType> typeGenericParameter = new ObjectGenericParameter<ConcreteType>();
        typeGenericParameter.set(concreteType);
        final ObjectGenericParameter<ConcreteValue> valueGenericParameter = new ObjectGenericParameter<ConcreteValue>();
        valueGenericParameter.set(concreteValue);

        final Element<ObjectGenericParameter<ConcreteType>, ObjectGenericParameter<ConcreteValue> >
            element = new Element<ObjectGenericParameter<ConcreteType>, ObjectGenericParameter<ConcreteValue> >(typeGenericParameter, valueGenericParameter);
        System.out.println("bitSizeOf = " + element.bitSizeOf());
        System.out.println("OUTPUT = " + element.getValue().get().getValueString());
    }
}