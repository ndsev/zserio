package zserio.runtime.typeinfo;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.function.ToLongBiFunction;

/**
 * Type information for compound type field.
 */
public class FieldInfo
{
    /**
     * Constructor.
     *
     * @param schemaName Field schema name.
     * @param getterName Field getter name.
     * @param setterName Field setter name.
     * @param typeInfo Field type info.
     * @param typeArguments Field type arguments.
     * @param alignment Field alignment or null in case of no alignment.
     * @param offset Field offset or null in case of no offset.
     * @param initializer Field initializer or null in case of no initializer.
     * @param isOptional Flag whether the field is optional.
     * @param optionalCondition Field optional condition or null if field is not optional.
     * @param isUsedIndicatorName Field "is used" indicator name.
     * @param isSetIndicatorName Field "is set" indicator name.
     * @param constraint Field constraint or null if the field has no constraint.
     * @param isArray Flag whether the field is an array.
     * @param arrayLength Array length or null if the field is not an array or is auto/implicit array.
     * @param isPacked Flag whether the field is a packed array.
     * @param isImplicit Flag whether the field is an implicit array.
     */
    public FieldInfo(String schemaName, String getterName, String setterName, TypeInfo typeInfo,
            List<BiFunction<Object, Integer, Object>> typeArguments, IntSupplier alignment,
            BiFunction<Object, Integer, Object> offset, Supplier<Object> initializer, boolean isOptional,
            Predicate<Object> optionalCondition, String isUsedIndicatorName, String isSetIndicatorName,
            Predicate<Object> constraint, boolean isArray, ToIntFunction<Object> arrayLength,
            boolean isPacked, boolean isImplicit)
    {
        this.schemaName = schemaName;
        this.getterName = getterName;
        this.setterName = setterName;
        this.typeInfo = typeInfo;
        this.typeArguments = typeArguments;
        this.alignment = alignment;
        this.offset = offset;
        this.initializer = initializer;
        this.isOptional = isOptional;
        this.optionalCondition = optionalCondition;
        this.isUsedIndicatorName = isUsedIndicatorName;
        this.isSetIndicatorName = isSetIndicatorName;
        this.constraint = constraint;
        this.isArray = isArray;
        this.arrayLength = arrayLength;
        this.isPacked = isPacked;
        this.isImplicit = isImplicit;
    }

    /**
     * Gets field schema name.
     *
     * @return Name of the field as is defined in zserio schema.
     */
    public String getSchemaName()
    {
        return schemaName;
    }

    /**
     * Gets field getter name.
     *
     * @return Name of the field getter as is in generated code.
     */
    public String getGetterName()
    {
        return getterName;
    }

    /**
     * Gets field setter name.
     *
     * @return Name of the field setter as is in generated code.
     */
    public String getSetterName()
    {
        return setterName;
    }

    /**
     * Gets type information for the field.
     *
     * @return Field type info.
     */
    public TypeInfo getTypeInfo()
    {
        return typeInfo;
    }

    /**
     * Gets sequence of field type arguments.
     *
     * @return Unmodifiable list of type arguments.
     */
    public List<BiFunction<Object, Integer, Object>> getTypeArguments()
    {
        return Collections.unmodifiableList(typeArguments);
    }

    /**
     * Gets field alignment expression.
     *
     * @return Field alignment or null in case of no alignment.
     */
    public IntSupplier getAlignment()
    {
        return alignment;
    }

    /**
     * Gets field offset expression.
     *
     * @return Field offset or null in case of no offset.
     */
    public BiFunction<Object, Integer, Object> getOffset()
    {
        return offset;
    }

    /**
     * Gets field initializer expression.
     *
     * @return Field initializer or null in case of no initializer.
     */
    public Supplier<Object> getInitializer()
    {
        return initializer;
    }

    /**
     * Gets whether the field is optional.
     *
     * @return True if the field is optional, false otherwise.
     */
    public boolean isOptional()
    {
        return isOptional;
    }

    /**
     * Gets field optional condition expression.
     *
     * @return Optional condition or null if the field is not optional.
     */
    public Predicate<Object> getOptionalCondition()
    {
        return optionalCondition;
    }

    /**
     * Gets field "is used" indicator name.
     *
     * @return Name of the field "is used" indicator as is in generated code.
     */
    public String getIsUsedIndicatorName()
    {
        return isUsedIndicatorName;
    }

    /**
     * Gets field "is set" indicator name.
     *
     * @return Name of the field "is set" indicator as is in generated code.
     */
    public String getIsSetIndicatorName()
    {
        return isSetIndicatorName;
    }

    /**
     * Gets field constraint expression.
     *
     * @return Constraint or null if the field does not have constraint.
     */
    public Predicate<Object> getConstraint()
    {
        return constraint;
    }

    /**
     * Gets whether the field is an array.
     *
     * @return True if field is array, false otherwise.
     */
    public boolean isArray()
    {
        return isArray;
    }

    /**
     * Gets array field length expression.
     *
     * @return Array length or null if the field is not array or is auto/implicit array.
     */
    public ToIntFunction<Object> getArrayLength()
    {
        return arrayLength;
    }

    /**
     * Gets whether the field is a packed array.
     *
     * @return True if the field is array and packed, false otherwise.
     */
    public boolean isPacked()
    {
        return isPacked;
    }

    /**
     * Gets whether the field is an implicit array.
     *
     * @return True if the field is array and implicit.
     */
    public boolean isImplicit()
    {
        return isImplicit;
    }

    private final String schemaName;
    private final String getterName;
    private final String setterName;
    private final TypeInfo typeInfo;
    private final List<BiFunction<Object, Integer, Object>> typeArguments;
    private final IntSupplier alignment;
    private final BiFunction<Object, Integer, Object> offset;
    private final Supplier<Object> initializer;
    private final boolean isOptional;
    private final Predicate<Object> optionalCondition;
    private final String isUsedIndicatorName;
    private final String isSetIndicatorName;
    private final Predicate<Object> constraint;
    private final boolean isArray;
    private final ToIntFunction<Object> arrayLength;
    boolean isPacked;
    boolean isImplicit;
}
