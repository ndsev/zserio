package zserio.runtime.creator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.BiFunction;

import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.typeinfo.TypeInfoUtil;
import zserio.runtime.ZserioError;
import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.ParameterInfo;

/**
 * Creator for zserio objects.
 *
 * It allows to build zserio object tree defined by the given type info (see -withTypeInfoCode).
 */
public class ZserioTreeCreator
{
    /**
     * Constructor.
     *
     * @param typeInfo Type info defining the tree.
     */
    public ZserioTreeCreator(TypeInfo typeInfo)
    {
        this.typeInfo = typeInfo;
        fieldInfoStack = new Stack<FieldInfo>();
        valueStack = new Stack<Object>();
        this.state = State.BEFORE_ROOT;
    }

    /**
     * Creates the top level compound element and move to state of building its children.
     */
    public void beginRoot()
    {
        if (state != State.BEFORE_ROOT)
            throw new ZserioError("ZserioTreeCreator: Cannot begin root in state '" + state + "'!");

        final Object root = createRoot(typeInfo);
        valueStack.push(root);
        state = State.IN_COMPOUND;
    }

    /**
     * Finishes building and returns the created tree.
     *
     * @return Zserio object tree.
     */
    public Object endRoot()
    {
        if (state != State.IN_COMPOUND || valueStack.size() != 1)
            throw new ZserioError("ZserioTreeCreator: Cannot end root in state '" + state + "'!");

        state = State.BEFORE_ROOT;
        return valueStack.pop();
    }

    /**
     * Creates an array field within the current compound.
     *
     * @param name Name of the array field.
     */
    public void beginArray(String name)
    {
        if (state != State.IN_COMPOUND)
            throw new ZserioError("ZserioTreeCreator: Cannot begin array in state '" + state + "'!");

        final TypeInfo parentTypeInfo = getTypeInfo();
        final FieldInfo fieldInfo = findFieldInfo(parentTypeInfo, name);
        if (!fieldInfo.isArray())
        {
            throw new ZserioError("ZserioTreeCreator: Member '" + fieldInfo.getSchemaName() +
                    "' is not an array!");
        }

        fieldInfoStack.push(fieldInfo);
        valueStack.push(new ArrayList<Object>());
        state = State.IN_ARRAY;
    }

    /**
     * Finishes the array field.
     */
    public void endArray()
    {
        if (state != State.IN_ARRAY)
            throw new ZserioError("ZserioTreeCreator: Cannot end array in state '" + state + "'!");

        final FieldInfo fieldInfo = fieldInfoStack.pop();
        final List<?> list = (List<?>)valueStack.pop();
        final Object array = createArray(fieldInfo, list);

        final TypeInfo parentTypeInfo = getTypeInfo();
        final Object parent = valueStack.peek();
        setField(parentTypeInfo, parent, fieldInfo, array);
        state = State.IN_COMPOUND;
    }

    /**
     * Creates a compound field within the current compound.
     *
     * @param name Name of the compound field.
     */
    public void beginCompound(String name)
    {
        if (state != State.IN_COMPOUND)
            throw new ZserioError("ZserioTreeCreator: Cannot begin compound in state '" + state + "'!");

        final TypeInfo parentTypeInfo = getTypeInfo();
        final FieldInfo fieldInfo = findFieldInfo(parentTypeInfo, name);
        if (fieldInfo.isArray())
            throw new ZserioError("ZserioTreeCreator: Member '" + fieldInfo.getSchemaName() + "' is an array!");

        if (!TypeInfoUtil.isCompound(fieldInfo.getTypeInfo().getSchemaType()))
        {
            throw new ZserioError("ZserioTreeCreator: Member '" + fieldInfo.getSchemaName() +
                    "' is not a compound!");
        }

        final Object compound = createObject(fieldInfo, valueStack.peek());
        fieldInfoStack.push(fieldInfo);
        valueStack.push(compound);
        state = State.IN_COMPOUND;
    }

    /**
     * Finishes the compound.
     */
    public void endCompound()
    {
        if (state != State.IN_COMPOUND || fieldInfoStack.isEmpty())
            throw new ZserioError("ZserioTreeCreator: Cannot end compound in state '" + state + "'" +
                    (fieldInfoStack.isEmpty() ? " (expecting endRoot)!" : "!"));

        final FieldInfo fieldInfo = fieldInfoStack.pop();
        if (fieldInfo.isArray())
            throw new ZserioError("ZserioTreeCreator: Cannot end compound, it's an array element!");

        final Object compound = valueStack.pop();
        final TypeInfo parentTypeInfo = getTypeInfo();
        final Object parent = valueStack.peek();
        setField(parentTypeInfo, parent, fieldInfo, compound);
    }

    /**
     * Sets field value within the current compound.
     *
     * @param name Name of the field.
     * @param value Value to set.
     */
    void setValue(String name, Object value)
    {
        if (state != State.IN_COMPOUND)
            throw new ZserioError("ZserioTreeCreator: Cannot set value in state '" + state + "'!");

        final FieldInfo fieldInfo = findFieldInfo(getTypeInfo(), name);
        if (value != null)
        {
            if (fieldInfo.isArray())
            {
                throw new ZserioError("ZserioTreeCreator: Expecting array in member '" +
                        fieldInfo.getSchemaName() + "!");
            }

            final TypeInfo typeInfo = fieldInfo.getTypeInfo();
            final Class<?> boxedFieldClass = toBoxedClass(typeInfo.getJavaClass());
            if (!boxedFieldClass.isInstance(value))
                throw new ZserioError("ZserioTreeCreator: Unexpected value type '" + value.getClass() + "', " +
                        "expecting '" + typeInfo.getJavaClass() + "'!");
        }

        final TypeInfo parentTypeInfo = getTypeInfo();
        final Object parent = valueStack.peek();
        setField(parentTypeInfo, parent, fieldInfo, value);
    }

    /**
     * Gets type info of the expected member.
     *
     * @param name Member name.
     *
     * @return Type info of the expected member.
     */
    public TypeInfo getMemberType(String name)
    {
        if (state != State.IN_COMPOUND)
            throw new ZserioError("ZserioTreeCreator: Cannot get member type in state '" + state + "'!");

        final FieldInfo fieldInfo = findFieldInfo(getTypeInfo(), name);
        return fieldInfo.getTypeInfo();
    }

    /**
     * Creates compound array element within the current array.
     */
    public void beginCompoundElement()
    {
        if (state != State.IN_ARRAY)
            throw new ZserioError("ZserioTreeCreator: Cannot begin compound element in state '" + state + "'!");

        final FieldInfo fieldInfo = fieldInfoStack.peek();
        if (!TypeInfoUtil.isCompound(fieldInfo.getTypeInfo().getSchemaType()))
        {
            throw new ZserioError("ZserioTreeCreator: Member '" + fieldInfo.getSchemaName() +
                    "' is not a compound!");
        }

        final List<?> list = (List<?>)valueStack.peek();
        final Object parent = valueStack.get(valueStack.size() - 2);
        final Object compound = createObject(fieldInfo, parent, list.size());
        valueStack.push(compound);
        state = State.IN_COMPOUND;
    }

    /**
     * Finishes the compound element.
     */
    public void endCompoundElement()
    {
        if (state != State.IN_COMPOUND || fieldInfoStack.empty())
        {
            throw new ZserioError("ZserioTreeCreator: Cannot end compound element in state '" + state + "'" +
                    (fieldInfoStack.empty() ?  "(expecting endRoot)!" : "!"));
        }

        final FieldInfo fieldInfo = fieldInfoStack.peek();
        if (!fieldInfo.isArray())
            throw new ZserioError("ZserioTreeCreator: Cannot end compound element, not in array!");

        final Object compound = valueStack.pop();
        @SuppressWarnings("unchecked")
        final List<Object> list = (List<Object>)valueStack.peek();
        list.add(compound);
        state = State.IN_ARRAY;
    }

    /**
     * Adds the value to the array.
     *
     * @param value Value to add.
     */
    public void addValueElement(Object value)
    {
        if (state != State.IN_ARRAY)
            throw new ZserioError("ZserioTreeCreator: Cannot add value element in state '" + state + "'!");

        if (value != null)
        {
            final TypeInfo elementTypeInfo = fieldInfoStack.peek().getTypeInfo();
            final Class<?> boxedElementClass = toBoxedClass(elementTypeInfo.getJavaClass());
            if (!boxedElementClass.isInstance(value))
            {
                throw new ZserioError("ZserioTreeCreator: Unexpected value type '" + value.getClass() +
                        "', expecting '" + elementTypeInfo.getJavaClass() + "'!");
            }
        }

        @SuppressWarnings("unchecked")
        final List<Object> list = (List<Object>)valueStack.peek();
        list.add(value);
    }

    /**
     * Gets type info of the expected array element.
     *
     * @return Type info of the expected array element.
     */
    public TypeInfo getElementType()
    {
        if (state != State.IN_ARRAY)
            throw new ZserioError("ZserioTreeCreator: Cannot get element type in state '" + state + "'!");

        final FieldInfo fieldInfo = fieldInfoStack.peek();
        return fieldInfo.getTypeInfo();
    }

    private TypeInfo getTypeInfo()
    {
        return fieldInfoStack.empty() ? typeInfo : fieldInfoStack.peek().getTypeInfo();
    }

    private static FieldInfo findFieldInfo(TypeInfo typeInfo, String fieldName)
    {
        final List<FieldInfo> fields = typeInfo.getFields();
        for (FieldInfo field : fields)
        {
            if (field.getSchemaName().equals(fieldName))
                return field;
        }

        throw new ZserioError("ZserioTreeCreator: Field '" + fieldName + "' not found in '" +
                typeInfo.getSchemaName() + "'!");
    }

    private static Object createRoot(TypeInfo typeInfo)
    {
        try
        {
            final Constructor<?> constructor = typeInfo.getJavaClass().getDeclaredConstructor();
            return constructor.newInstance();
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException excpt)
        {
            throw new ZserioError("ZserioTreeCreator: Cannot call constructor of Zserio object '" +
                    typeInfo.getSchemaName() + "'!", excpt);
        }
    }

    private static Object createObject(FieldInfo fieldInfo, Object parent)
    {
        return createObject(fieldInfo, parent, null);
    }

    private static Object createObject(FieldInfo fieldInfo, Object parent, Integer elementIndex)
    {
        final TypeInfo typeInfo = fieldInfo.getTypeInfo();
        final List<ParameterInfo> parameters = typeInfo.getParameters();
        final Class<?>[] parametersTypes = new Class<?>[parameters.size()];
        for (int i = 0; i < parameters.size(); ++i)
            parametersTypes[i] = parameters.get(i).getTypeInfo().getJavaClass();
        try
        {
            final Constructor<?> constructor = typeInfo.getJavaClass().getDeclaredConstructor(parametersTypes);

            final Object[] arguments = new Object[parameters.size()];
            if (!parameters.isEmpty())
            {
                final List<BiFunction<Object, Integer, Object>> typeArguments = fieldInfo.getTypeArguments();
                for (int i = 0; i < typeArguments.size(); ++i)
                    arguments[i] = typeArguments.get(i).apply(parent, elementIndex);
            }

            return constructor.newInstance(arguments);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                InvocationTargetException excpt)
        {
            throw new ZserioError("ZserioTreeCreator: Cannot create Zserio object '" +
                    typeInfo.getSchemaName() + "'!", excpt);
        }
    }

    private static Object createArray(FieldInfo fieldInfo, List<?> list)
    {
        final Class<?> arrayClass = fieldInfo.getTypeInfo().getJavaClass();
        final Object array = Array.newInstance(arrayClass, list.size());
        for (int i = 0; i < list.size(); ++i)
            Array.set(array, i, list.get(i));

        return array;
    }

    private static void setField(TypeInfo parentTypeInfo, Object parent, FieldInfo fieldInfo, Object value)
    {
        final String setterName = fieldInfo.getSetterName();
        try
        {
            Class<?> fieldClass = fieldInfo.getTypeInfo().getJavaClass();
            if (fieldInfo.isArray())
            {
                // arrays have stored in Type Info Java class for element not for an array
                fieldClass = Array.newInstance(fieldClass, 0).getClass();
            }
            else if (fieldInfo.isOptional())
            {
                // optionals have stored in Type Info Java class for unboxed element not for a boxed element
                fieldClass = toBoxedClass(fieldClass);
            }
            final Method setter = parentTypeInfo.getJavaClass().getMethod(setterName, fieldClass);
            setter.invoke(parent, value);
        }
        catch (SecurityException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | NoSuchMethodException excpt)
        {
            throw new ZserioError("ZserioTreeCreator: Cannot set field '" + fieldInfo.getSchemaName() +
                    "' in Zserio object '" + parentTypeInfo.getSchemaName() + "'!", excpt);
        }
    }

    private static Class<?> toBoxedClass(Class<?> clazz)
    {
        final Class<?> boxedClazz = unboxedToBoxedClassMap.get(clazz);
        return (boxedClazz == null) ? clazz : boxedClazz;
    }

    private enum State
    {
        BEFORE_ROOT,
        IN_COMPOUND,
        IN_ARRAY,
        DONE
    }

    private static final Map<Class<?>, Class<?>> unboxedToBoxedClassMap = new HashMap<Class<?>, Class<?>>();
    static
    {
        unboxedToBoxedClassMap.put(boolean.class, Boolean.class);
        unboxedToBoxedClassMap.put(byte.class, Byte.class);
        unboxedToBoxedClassMap.put(short.class, Short.class);
        unboxedToBoxedClassMap.put(char.class, Character.class);
        unboxedToBoxedClassMap.put(int.class, Integer.class);
        unboxedToBoxedClassMap.put(long.class, Long.class);
        unboxedToBoxedClassMap.put(float.class, Float.class);
        unboxedToBoxedClassMap.put(double.class, Double.class);
    }

    private final TypeInfo typeInfo;
    private final Stack<FieldInfo> fieldInfoStack;
    private final Stack<Object> valueStack;

    private State state;
};
