#ifndef ZSERIO_I_INTROSPECTABLE_H_INC
#define ZSERIO_I_INTROSPECTABLE_H_INC

#include <memory>

#include "zserio/ITypeInfo.h"
#include "zserio/BitBuffer.h"
#include "zserio/String.h"
#include "zserio/RebindAlloc.h"
#include "zserio/AnyHolder.h"
#include "zserio/BitStreamWriter.h"

namespace zserio
{

/**
 * Interface for introspectable view to instances of zserio objects.
 *
 * \note Users are responsible to maintain life-time of introspected objects which must exist
 * as long as the views are used.
 *
 * \note The object in this context may be also an instance of a built-in type.
 */
template <typename ALLOC = std::allocator<uint8_t>>
class IBasicIntrospectable
{
public:
    /** Shared pointer to the introspectable interface. */
    using Ptr = std::shared_ptr<IBasicIntrospectable>;

    /**
     * Destructor.
     */
    virtual ~IBasicIntrospectable() {}

    /**
     * Gets type info for the current zserio object.
     *
     * \return Reference to the static instance of type info.
     */
    virtual const ITypeInfo& getTypeInfo() const = 0;

    /**
     * Gets whether the introspected object is an array.
     *
     * \return True when the object is an array, false otherwise.
     */
    virtual bool isArray() const = 0;

    /**
     * Gets introspectable view to the field (i.e. member) with the given schema name.
     *
     * \note Can be called only when the introspected object is a zserio compound type.
     *
     * \param name Field schema name.
     *
     * \return Introspectable view to the requested field.
     *
     * \throw CppRuntimeException When the introspected object is not a compound type or when the field with
     *                            the given name doesn't exist or when the field getter itself throws.
     */
    virtual Ptr getField(StringView name) const = 0;

    /**
     * Sets the field (i.e. member) with the given schema name.
     *
     * \param name Field schema name.
     * \param value Value to set. The type must exactly match the type of the zserio field mapped to C++!
     *
     * \throw CppRuntimeException When the introspected object is not a compuind type or when the field with
     *                            the given name doesn't exist or when the provided value is of a wrong type
     *                            or when the field setter itself throws.
     */
    virtual void setField(StringView name, const AnyHolder<ALLOC>& value) = 0;

    /**
     * Gets introspectable view to the parameter (i.e. member) with the given schema name.
     *
     * \note Can be called only when the introspected object is a zserio compound type.
     *
     * \param name Parameter schema name.
     *
     * \return Introspectable view to the requested paramter.
     *
     * \throw CppRuntimeException When the introspected object is not a compound type or when the paramter with
     *                            the given name doesn't exist or when the parameter getter itself throws.
     */
    virtual Ptr getParameter(StringView name) const = 0;

    /**
     * Calls function with the given name on the introspected zserio object and gets introspectable view to its
     * result.
     *
     * \note Can be called only when the introspected object is a zserio compound type.
     *
     * \param name Function schema name.
     *
     * \return Introspectable view to the value returns from the called function.
     *
     * \throw CppRuntimeException When the introspected object is not a compound type or when the function with
     *                            the given name doesn't exist or the the function call itself throws.
     */
    virtual Ptr callFunction(StringView name) const = 0;

    /**
     * Gets name of the field which is active in the introspected choice type.
     *
     * \note Applicable only on zserio unions and choices.
     *
     * \return Name of the active field (i.e. currently selected choice).
     *
     * \throw CppRuntimeException When the introspected object is not a choice type (or union).
     */
    virtual StringView getChoice() const = 0;

    /**
     * Universal accessor to zserio entities within the zserio sub-tree represented by the introspected object.
     *
     * Supports dot notation corresponding to the tree defined in zserio language. Can access fields or
     * paramters or call functions within the zserio sub-tree.
     *
     * Examples:
     * * 'fieldA.param' - Gets introspectable view to parameter 'param' whithin the parameterized field 'fieldA'.
     * * 'child.getValue' - Gets introspectable view to result of the function called on field ̈́'child'.
     * * 'child.nonexisting.field' - Gets nullptr since the path doesn't represent a valid entity.
     *
     * \param path Dot notation corresponding to the zserio tree.
     *
     * \return Introspectable view to the result of the given path. Returns nullptr when the path doesn't exist
     *         or when the requested operation throws CppRuntimeException.
     */
    virtual Ptr find(StringView path) const = 0;

    /**
     * \copydoc IBasicIntrospectable::find
     *
     * Overloaded method provided for convenience.
     */
    virtual Ptr operator[](StringView path) const = 0;

    /**
     * Gets size of the introspected array.
     *
     * \note Can be called only when the introspected object is an array.
     *
     * \return Size of the introspected array.
     *
     * \throw CppRuntimeException When the introspected object is not an array.
     */
    virtual size_t size() const = 0;

    /**
     * Gets introspectable view to an array element.
     *
     * \note Can be called only when the introspected object is an array.
     *
     * \return Introspectable view to an array element on the given index.
     *
     * \throw CppRuntimeException When the introspected object is not an array.
     * \throw std::out_of_range When the given index is out of bounds of the underlying array.
     */
    virtual Ptr at(size_t index) const = 0;

    /**
     * \copydoc IBasicIntrospectable::at
     *
     * Overloaded method provided for convenience.
     */
    virtual Ptr operator[](size_t index) const = 0;

    /**
     * Gets bool value of the bool introspectable.
     *
     * \return Bool value.
     * \throw CppRuntimeException When the introspected object is not a bool type.
     */
    virtual bool getBool() const = 0;

    /**
     * Gets 8-bit signed integral value of the int8_t introspectable.
     *
     * \return 8-bit signed integral value.
     * \throw CppRuntimeException When the introspected object is not a int8_t type.
     */
    virtual int8_t getInt8() const = 0;

    /**
     * Gets 16-bit signed integral value of the int16_t introspectable.
     *
     * \return 16-bit signed integral value.
     * \throw CppRuntimeException When the introspected object is not a int16_t type.
     */
    virtual int16_t getInt16() const = 0;

    /**
     * Gets 32-bit signed integral value of the int32_t introspectable.
     *
     * \return 32-bit signed integral value.
     * \throw CppRuntimeException When the introspected object is not a int32_t type.
     */
    virtual int32_t getInt32() const = 0;

    /**
     * Gets 64-bit signed integral value of the int64_t introspectable.
     *
     * \return 64-bit signed integral value.
     * \throw CppRuntimeException When the introspected object is not a int64_t type.
     */
    virtual int64_t getInt64() const = 0;

    /**
     * Gets 8-bit unsigned integral value of the uint8_t introspectable.
     *
     * \return 8-bit unsigned integral value.
     * \throw CppRuntimeException When the introspected object is not a uint8_t type.
     */
    virtual uint8_t getUInt8() const = 0;

    /**
     * Gets 16-bit unsigned integral value of the uint16_t introspectable.
     *
     * \return 16-bit unsigned integral value.
     * \throw CppRuntimeException When the introspected object is not a uint16_t type.
     */
    virtual uint16_t getUInt16() const = 0;

    /**
     * Gets 32-bit unsigned integral value of the uint32_t introspectable.
     *
     * \return 32-bit unsigned integral value.
     * \throw CppRuntimeException When the introspected object is not a uint32_t type.
     */
    virtual uint32_t getUInt32() const = 0;

    /**
     * Gets 64-bit unsigned integral value of the uint64_t introspectable.
     *
     * \return 64-bit unsigned integral value.
     * \throw CppRuntimeException When the introspected object is not a uint64_t type.
     */
    virtual uint64_t getUInt64() const = 0;

    /**
     * Gets float value of the float introspectable.
     *
     * \return Float value.
     * \throw CppRuntimeException When the introspected object is not a float type.
     */
    virtual float getFloat() const = 0;

    /**
     * Gets double value of the double introspectable.
     *
     * \return Double value.
     * \throw CppRuntimeException When the introspected object is not a double type.
     */
    virtual double getDouble() const = 0;

    /**
     * Gets reference to the string value of the string introspectable.
     *
     * \return Reference to the string value.
     * \throw CppRuntimeException When the introspected object is not a string type.
     */
    virtual StringView getString() const = 0;

    /**
     * Gets reference to the introspected bit buffer.
     *
     * \return Reference to the bit buffer.
     * \throw CppRuntimeException When the introspected object is not a bit buffer (i.e. extern type).
     */
    virtual const BasicBitBuffer<ALLOC>& getBitBuffer() const = 0;

    /**
     * Converts any signed integral value to 64-bit singed integer.
     *
     * Works also for enum types defined with signed underlying type.
     *
     * \return 64-bit signed integral value.
     * \throw CppRuntimeException When the introspected object cannot be converted to a signed integral value.
     */
    virtual int64_t toInt() const = 0;

    /**
     * Converts any unsigned integral value to 64-bit unsigned integer.
     *
     * Works also for bitmask and enum typed defined with unsigned underlying type.
     *
     * \return 64-bit unsigned integral value.
     * \throw CppRuntimeException When the introspected object cannot be converted to
     *                            an unsigned integral value.
     */
    virtual uint64_t toUInt() const = 0;

    /**
     * Converts any numeric value to double.
     *
     * Works also for bitmask and enum types.
     *
     * \return Double value.
     * \throw CppRuntimeException When the introspected object cannot be converted to double.
     */
    virtual double toDouble() const = 0;

    /**
     * Converts an introspected object to string.
     *
     * Works for all integral types including bool, bitmask and enum types and for string types.
     *
     * \note Floating point types are not currently supported!
     * \note The conversion to string can be implemented for more types in future versions.
     *
     * \param allocator Allocator to use for the string allocation.
     *
     * \return String value representing the introspected object.
     */
    virtual string<RebindAlloc<ALLOC, char>> toString(const ALLOC& allocator = ALLOC()) const = 0;

    /**
     * Writes the introspected object to a bit stream using the given bit stream writer.
     *
     * \note Works for all introspectable types except arrays!
     *
     * \param writer Bit stream writer to use.
     *
     * \throw CppRuntimeException When the introspected object is an array.
     */
    virtual void write(BitStreamWriter& writer) = 0;
};

/** Typedef to introspectable interface provided for convenience - using default std::allocator<uint8_t>. */
/** \{ */
using IIntrospectable = IBasicIntrospectable<>;
using IIntrospectablePtr = IIntrospectable::Ptr;
/** \} */

/**
 * Gets introspectable view for the given enum item.
 *
 * \param value Enum value to introspect.
 * \param allocator Allocator to use for introspectable allocation.
 *
 * \return Enum introspectable view.
 */
template <typename T, typename ALLOC = std::allocator<uint8_t>>
typename IBasicIntrospectable<ALLOC>::Ptr enumIntrospectable(T value, const ALLOC& allocator = ALLOC());

} // namespace zserio

#endif // ZSERIO_I_INTROSPECTABLE_H_INC
