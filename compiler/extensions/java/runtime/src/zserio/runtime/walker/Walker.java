package zserio.runtime.walker;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import zserio.runtime.ZserioError;
import zserio.runtime.typeinfo.FieldInfo;
import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.typeinfo.TypeInfoUtil;

/**
 * Walker through zserio objects, based on generated type info (see -withTypeInfoCode).
 */
public class Walker
{
    /**
     * Constructor from walk observer.
     *
     * @param walkObserver Observer to use during walking.
     */
    public Walker(WalkObserver walkObserver)
    {
        this(walkObserver, new DefaultWalkFilter());
    }

    /**
     * Constructor from walk observer and walk filter.
     *
     * @param walkObserver Observer to use during walking.
     * @param walkFilter Walk filter to use.
     */
    public Walker(WalkObserver walkObserver, WalkFilter walkFilter)
    {
        this.walkObserver = walkObserver;
        this.walkFilter = walkFilter;
    }

    /**
     * Walks given zserio object which must be generated with type_info (see -withTypeInfoCode options).
     *
     * @param zserioObject Zserio object to walk.
     */
    public void walk(Object zserioObject)
    {
        final TypeInfo typeInfo = callTypeInfoMethod(zserioObject);
        if (!TypeInfoUtil.isCompound(typeInfo.getSchemaType()))
        {
            throw new ZserioError("Walker: Root object '" + typeInfo.getSchemaName() +
                    "' is not a compound type!");
        }

        walkObserver.beginRoot(zserioObject);
        walkFields(zserioObject, typeInfo);
        walkObserver.endRoot(zserioObject);
    }

    private TypeInfo callTypeInfoMethod(Object zserioObject)
    {
        try
        {
            final Method typeInfoMethod = zserioObject.getClass().getMethod("typeInfo");
            if (!typeInfoMethod.getReturnType().equals(TypeInfo.class))
                throw new ZserioError("Walker: Zserio object has wrong typeInfo method!");
            final TypeInfo typeInfo = (TypeInfo)typeInfoMethod.invoke(zserioObject);

            return typeInfo;
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException e)
        {
            throw new ZserioError(
                    "Walker: Zserio object must have type info enabled (see zserio option -withTypeInfoCode)!");
        }
    }

    private int callChoiceTagMethod(Object zserioObject)
    {
        try
        {
            final Method choiceTagMethod = zserioObject.getClass().getMethod("choiceTag");
            if (!choiceTagMethod.getReturnType().equals(Integer.TYPE))
                throw new ZserioError("Walker: Zserio object has wrong choiceTag method!");
            final int choiceTag = (Integer)choiceTagMethod.invoke(zserioObject);

            return choiceTag;
        }
        catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                InvocationTargetException e)
        {
            throw new ZserioError("Walker: Zserio object does not have choiceTag() method!");
        }
    }

    private int getUndefinedChoiceField(Object zserioObject)
    {
        try
        {
            final Field undefinedChoiceField = zserioObject.getClass().getField("UNDEFINED_CHOICE");
            final int undefinedChoice = undefinedChoiceField.getInt(zserioObject);

            return undefinedChoice;
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            throw new ZserioError("Walker: Zserio object does not have UNDEFINED_CHOICE field!");
        }
    }

    private Object callGetterMethod(Object zserioObject, FieldInfo fieldInfo)
    {
        final String getterName = fieldInfo.getGetterName();
        try
        {
            final Method getterMethod = zserioObject.getClass().getMethod(getterName);
            final Object result = getterMethod.invoke(zserioObject, new Object[0]);

            return result;
        }
        catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException |
                InvocationTargetException e)
        {
            throw new ZserioError("Walker: Zserio object does not have " + getterName + "() method!");
        }
    }

    private void walkFields(Object zserioObject, TypeInfo typeInfo)
    {
        final List<FieldInfo> fields = typeInfo.getFields();
        if (TypeInfoUtil.hasChoice(typeInfo.getSchemaType()))
        {
            // union or choice
            final int choiceTag = callChoiceTagMethod(zserioObject);
            final int undefinedChoice = getUndefinedChoiceField(zserioObject);
            if (choiceTag != undefinedChoice)
            {
                final FieldInfo field = fields.get(choiceTag);
                walkField(callGetterMethod(zserioObject, field), field);
            }
            // else: uninitialized or empty branch
        }
        else
        {
            // structure
            for (FieldInfo field : fields)
            {
                if (!walkField(callGetterMethod(zserioObject, field), field))
                    break;
            }
        }
    }

    private boolean walkField(Object field, FieldInfo fieldInfo)
    {
        if (field != null && field.getClass().isArray())
        {
            if (walkFilter.beforeArray(field, fieldInfo))
            {
                walkObserver.beginArray(field, fieldInfo);

                final int length = Array.getLength(field);
                for (int i = 0; i < length; i ++)
                {
                    final Object element = Array.get(field, i);
                    if (!walkFieldValue(element, fieldInfo, i))
                        break;
                }

                walkObserver.endArray(field, fieldInfo);
            }

            return walkFilter.afterArray(field, fieldInfo);
        }
        else
        {
            return walkFieldValue(field, fieldInfo, WalkerConst.NOT_ELEMENT);
        }
    }

    private boolean walkFieldValue(Object field, FieldInfo fieldInfo, int elementIndex)
    {
        final TypeInfo typeInfo = fieldInfo.getTypeInfo();
        if (field != null && TypeInfoUtil.isCompound(typeInfo.getSchemaType()))
        {
            if (walkFilter.beforeCompound(field, fieldInfo, elementIndex))
            {
                walkObserver.beginCompound(field, fieldInfo, elementIndex);
                walkFields(field, typeInfo);
                walkObserver.endCompound(field, fieldInfo, elementIndex);
            }

            return walkFilter.afterCompound(field, fieldInfo, elementIndex);
        }
        else
        {
            if (walkFilter.beforeValue(field, fieldInfo, elementIndex))
                walkObserver.visitValue(field, fieldInfo, elementIndex);

            return walkFilter.afterValue(field, fieldInfo, elementIndex);
        }
    }

    private final WalkObserver walkObserver;
    private final WalkFilter walkFilter;
};
