#include "gtest/gtest.h"

#include "zserio/Walker.h"
#include "zserio/Traits.h"
#include "zserio/TypeInfo.h"
#include "zserio/Reflectable.h"
#include "zserio/Array.h"
#include "zserio/ArrayTraits.h"
#include "zserio/PackingContext.h"
#include "zserio/BitStreamWriter.h"
#include <zserio/OptionalHolder.h>

using namespace zserio::literals;

namespace zserio
{

namespace
{

class DummyNested
{
public:
    using allocator_type = ::std::allocator<uint8_t>;

    explicit DummyNested(const allocator_type& allocator = allocator_type()) :
            m_text_(allocator)
    {}

    template <typename ZSERIO_T_text,
            is_field_constructor_enabled_t<ZSERIO_T_text, DummyNested, allocator_type> = 0>
    explicit DummyNested(
            ZSERIO_T_text&& text_,
            const allocator_type& allocator = allocator_type()) :
            DummyNested(allocator)
    {
        m_text_ = ::std::forward<ZSERIO_T_text>(text_);
    }

    static void createPackingContext(PackingContextNode&)
    {}

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

        static const ::std::array<FieldInfo, 1> fields = {
            FieldInfo{
                makeStringView("text"), // schemaName
                BuiltinTypeInfo::getString(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                false, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            }
        };

        static const Span<ParameterInfo> parameters;

        static const Span<FunctionInfo> functions;

        static const StructTypeInfo typeInfo = {
            makeStringView("DummyNested"), templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            explicit Reflectable(DummyNested& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(DummyNested::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IReflectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("text"))
                {
                    return ReflectableFactory::getString(m_object.getText(), get_allocator());
                }
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyNested'!";
            }

            virtual void write(BitStreamWriter&) override
            {
            }

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

        private:
            DummyNested& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    const string<>& getText() const
    {
        return m_text_;
    }

private:
    string<> m_text_;
};

class DummyUnion
{
private:
    class ZserioElementFactory_nestedArray
    {};

    using ZserioArrayType_nestedArray = Array<::std::vector<DummyNested>,
            ObjectArrayTraits<DummyNested, ZserioElementFactory_nestedArray>,
            ArrayType::AUTO>;

public:
    using allocator_type = ::std::allocator<uint8_t>;

    enum ChoiceTag : int32_t
    {
        CHOICE_value = 0,
        CHOICE_text = 1,
        CHOICE_nestedArray = 2,
        UNDEFINED_CHOICE = -1
    };

    explicit DummyUnion(const allocator_type& allocator = allocator_type()) noexcept :
            m_choiceTag(UNDEFINED_CHOICE),
            m_objectChoice(allocator)
    {
    }

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

        static const ::std::array<FieldInfo, 3> fields = {
            FieldInfo{
                makeStringView("value"), // schemaName
                BuiltinTypeInfo::getUInt32(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                false, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            },
            FieldInfo{
                makeStringView("text"), // schemaName
                BuiltinTypeInfo::getString(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                false, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            },
            FieldInfo{
                makeStringView("nestedArray"), // schemaName
                DummyNested::typeInfo(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                true, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            }
        };

        static const Span<ParameterInfo> parameters;

        static const Span<FunctionInfo> functions;

        static const UnionTypeInfo typeInfo = {
            makeStringView("DummyUnion"), templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            explicit Reflectable(DummyUnion& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(DummyUnion::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IReflectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("value"))
                {
                    return ReflectableFactory::getUInt32(m_object.getValue(), get_allocator());
                }
                if (name == makeStringView("text"))
                {
                    return ReflectableFactory::getString(m_object.getText(), get_allocator());
                }
                if (name == makeStringView("nestedArray"))
                {
                    return ReflectableFactory::getCompoundArray(m_object.getNestedArray(), get_allocator());
                }
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyUnion'!";
            }

            virtual StringView getChoice() const override
            {
                switch (m_object.choiceTag())
                {
                case CHOICE_value:
                    return makeStringView("value");
                case CHOICE_text:
                    return makeStringView("text");
                case CHOICE_nestedArray:
                    return makeStringView("nestedArray");
                default:
                    return {};
                }
            }

            virtual void write(BitStreamWriter&) override
            {
            }

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

        private:
            DummyUnion& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    ChoiceTag choiceTag() const
    {
        return m_choiceTag;
    }

    uint32_t getValue() const
    {
        return m_objectChoice.get<uint32_t>();
    }

    void setValue(uint32_t value_)
    {
        m_choiceTag = CHOICE_value;
        m_objectChoice = value_;
    }

    const string<>& getText() const
    {
        return m_objectChoice.get<string<>>();
    }

    void setText(const string<>& text_)
    {
        m_choiceTag = CHOICE_text;
        m_objectChoice = text_;
    }

    ::std::vector<DummyNested>& getNestedArray()
    {
        return m_objectChoice.get<ZserioArrayType_nestedArray>().getRawArray();
    }

    void setNestedArray(const ::std::vector<DummyNested>& nestedArray_)
    {
        m_choiceTag = CHOICE_nestedArray;
        m_objectChoice = ZserioArrayType_nestedArray(nestedArray_,
                ObjectArrayTraits<DummyNested, ZserioElementFactory_nestedArray>());
    }

private:
    ChoiceTag m_choiceTag;
    AnyHolder<> m_objectChoice;
};

class DummyObject
{
private:
    class ZserioElementFactory_unionArray
    {};

    class ZserioElementFactory_optionalUnionArray
    {};

    using ZserioArrayType_unionArray = Array<::std::vector<DummyUnion>,
            ObjectArrayTraits<DummyUnion, ZserioElementFactory_unionArray>, ArrayType::AUTO>;

    using ZserioArrayType_optionalUnionArray = Array<::std::vector<DummyUnion>,
            ObjectArrayTraits<DummyUnion, ZserioElementFactory_optionalUnionArray>, ArrayType::AUTO>;

public:
    using allocator_type = ::std::allocator<uint8_t>;

    DummyObject(const allocator_type& allocator = allocator_type()) :
            m_identifier_(uint32_t()),
            m_nested_(NullOpt),
            m_text_(allocator),
            m_unionArray_(ObjectArrayTraits<DummyUnion, ZserioElementFactory_unionArray>(), allocator),
            m_optionalUnionArray_(NullOpt)
    {}

    template <typename ZSERIO_T_nested,
            typename ZSERIO_T_text,
            typename ZSERIO_T_unionArray,
            typename ZSERIO_T_optionalUnionArray>
    DummyObject(
            uint32_t identifier_,
            ZSERIO_T_nested&& nested_,
            ZSERIO_T_text&& text_,
            ZSERIO_T_unionArray&& unionArray_,
            ZSERIO_T_optionalUnionArray&& optionalUnionArray_,
            const allocator_type& allocator = allocator_type()) :
            DummyObject(allocator)
    {
        m_identifier_ = identifier_;
        m_nested_ = ::std::forward<ZSERIO_T_nested>(nested_);
        m_text_ = ::std::forward<ZSERIO_T_text>(text_);
        m_unionArray_ = ZserioArrayType_unionArray(::std::forward<ZSERIO_T_unionArray>(unionArray_),
                ObjectArrayTraits<DummyUnion, ZserioElementFactory_unionArray>());
        m_optionalUnionArray_ = createOptionalArray<ZserioArrayType_optionalUnionArray>(
                ::std::forward<ZSERIO_T_optionalUnionArray>(optionalUnionArray_),
                ObjectArrayTraits<DummyUnion, ZserioElementFactory_optionalUnionArray>());
    }

    static const ITypeInfo& typeInfo()
    {
        static const StringView templateName;
        static const Span<TemplateArgumentInfo> templateArguments;

        static const ::std::array<FieldInfo, 5> fields = {
            FieldInfo{
                makeStringView("identifier"), // schemaName
                BuiltinTypeInfo::getUInt32(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                false, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            },
            FieldInfo{
                makeStringView("nested"), // schemaName
                DummyNested::typeInfo(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                true, // isOptional
                makeStringView("getIdentifier() != 0"), // optionalClause
                {}, // constraint
                false, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            },
            FieldInfo{
                makeStringView("text"), // schemaName
                BuiltinTypeInfo::getString(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                false, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            },
            FieldInfo{
                makeStringView("unionArray"), // schemaName
                DummyUnion::typeInfo(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                false, // isOptional
                {}, // optionalClause
                {}, // constraint
                true, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            },
            FieldInfo{
                makeStringView("optionalUnionArray"), // schemaName
                DummyUnion::typeInfo(), // typeInfo
                {}, // typeArguments
                {}, // alignment
                {}, // offset
                {}, // initializer
                true, // isOptional
                {}, // optionalClause
                {}, // constraint
                true, // isArray
                {}, // arrayLength
                false, // isPacked
                false // isImplicit
            }
        };

        static const Span<ParameterInfo> parameters;

        static const Span<FunctionInfo> functions;

        static const StructTypeInfo typeInfo = {
            makeStringView("DummyObject"), templateName, templateArguments,
            fields, parameters, functions
        };

        return typeInfo;
    }

    IReflectablePtr reflectable(const allocator_type& allocator = allocator_type())
    {
        class Reflectable : public ReflectableAllocatorHolderBase<allocator_type>
        {
        public:
            explicit Reflectable(DummyObject& object, const allocator_type& allocator) :
                    ReflectableAllocatorHolderBase<allocator_type>(DummyObject::typeInfo(), allocator),
                    m_object(object)
            {}

            virtual IReflectablePtr getField(StringView name) const override
            {
                if (name == makeStringView("identifier"))
                {
                    return ReflectableFactory::getUInt32(m_object.getIdentifier(), get_allocator());
                }
                if (name == makeStringView("nested"))
                {
                    if (!m_object.isNestedSet())
                        return nullptr;

                    return m_object.getNested().reflectable(get_allocator());
                }
                if (name == makeStringView("text"))
                {
                    return ReflectableFactory::getString(m_object.getText(), get_allocator());
                }
                if (name == makeStringView("unionArray"))
                {
                    return ReflectableFactory::getCompoundArray(m_object.getUnionArray(), get_allocator());
                }
                if (name == makeStringView("optionalUnionArray"))
                {
                    if (!m_object.isOptionalUnionArraySet())
                        return nullptr;

                    return ReflectableFactory::getCompoundArray(
                            m_object.getOptionalUnionArray(), get_allocator());
                }
                throw CppRuntimeException("Field '") + name + "' doesn't exist in 'DummyObject'!";
            }

            virtual void write(BitStreamWriter&) override
            {
            }

            virtual size_t bitSizeOf(size_t) const override
            {
                return 0;
            }

        private:
            DummyObject& m_object;
        };

        return std::allocate_shared<Reflectable>(allocator, *this, allocator);
    }

    uint32_t getIdentifier() const
    {
        return m_identifier_;
    }

    DummyNested& getNested()
    {
        return m_nested_.value();
    }

    bool isNestedSet() const
    {
        return m_nested_.hasValue();
    }

    const string<>& getText() const
    {
        return m_text_;
    }

    ::std::vector<DummyUnion>& getUnionArray()
    {
        return m_unionArray_.getRawArray();
    }

    ::std::vector<DummyUnion>& getOptionalUnionArray()
    {
        return m_optionalUnionArray_.value().getRawArray();
    }

    bool isOptionalUnionArraySet() const
    {
        return m_optionalUnionArray_.hasValue();
    }

private:
    uint32_t m_identifier_;
    InplaceOptionalHolder<DummyNested> m_nested_;
    string<> m_text_;
    ZserioArrayType_unionArray m_unionArray_;
    InplaceOptionalHolder<ZserioArrayType_optionalUnionArray> m_optionalUnionArray_;
};

DummyObject createDummyObject(uint32_t identifier = 13, bool createNested = true)
{
    std::vector<DummyUnion> unionArray;
    unionArray.resize(3);
    unionArray[0].setText("1");
    unionArray[1].setValue(2);
    unionArray[2].setNestedArray(std::vector<DummyNested>{{DummyNested{"nestedArray"}}});
    if (createNested)
    {
        return DummyObject(identifier, DummyNested("nested"), "test", std::move(unionArray), NullOpt);
    }
    else
    {
        return DummyObject(identifier, NullOpt, "test", std::move(unionArray), NullOpt);
    }
}

class TestWalkObserver : public IWalkObserver
{
public:
    typedef std::map<StringView, std::vector<IReflectablePtr>> CapturesMap;

    TestWalkObserver()
    {
        // initialize empty captures
        m_captures["beginRoot"_sv];
        m_captures["endRoot"_sv];
        m_captures["beginArray"_sv];
        m_captures["endArray"_sv];
        m_captures["beginCompound"_sv];
        m_captures["endCompound"_sv];
        m_captures["visitValue"_sv];
    }

    virtual void beginRoot(const IReflectablePtr& compound) override
    {
        m_captures["beginRoot"_sv].push_back(compound);
    }

    virtual void endRoot(const IReflectablePtr& compound) override
    {
        m_captures["endRoot"_sv].push_back(compound);
    }

    virtual void beginArray(const IReflectablePtr& array, const FieldInfo&) override
    {
        m_captures["beginArray"_sv].push_back(array);
    }

    virtual void endArray(const IReflectablePtr& array, const FieldInfo&) override
    {
        m_captures["endArray"_sv].push_back(array);
    }

    virtual void beginCompound(const IReflectablePtr& compound, const FieldInfo&, size_t) override
    {
        m_captures["beginCompound"_sv].push_back(compound);
    }

    virtual void endCompound(const IReflectablePtr& compound, const FieldInfo&, size_t) override
    {
        m_captures["endCompound"_sv].push_back(compound);
    }

    virtual void visitValue(const IReflectablePtr& value, const FieldInfo&, size_t) override
    {
        m_captures["visitValue"_sv].push_back(value);
    }

    const std::vector<IReflectablePtr>& getCaptures(StringView captureName) const
    {
        return m_captures.find(captureName)->second;
    }

private:
    CapturesMap m_captures;
};

class TestWalkFilter : public IWalkFilter
{
public:
    TestWalkFilter& beforeArray(bool beforeArray) { m_beforeArray = beforeArray; return *this; }
    TestWalkFilter& afterArray(bool afterArray) { m_afterArray = afterArray; return *this; }
    TestWalkFilter& onlyFirstElement(bool onlyFirstElement)
    {
        m_onlyFirstElement = onlyFirstElement; return *this;
    }
    TestWalkFilter& beforeCompound(bool beforeCompound) { m_beforeCompound = beforeCompound; return *this; }
    TestWalkFilter& afterCompound(bool afterCompound) { m_afterCompound = afterCompound; return *this; }
    TestWalkFilter& beforeValue(bool beforeValue) { m_beforeValue = beforeValue; return *this; }
    TestWalkFilter& afterValue(bool afterValue) { m_afterValue = afterValue; return *this; }

    virtual bool beforeArray(const IReflectablePtr&, const FieldInfo&) override
    {
        m_isFirstElement = true;
        return m_beforeArray;
    }

    virtual bool afterArray(const IReflectablePtr&, const FieldInfo&) override
    {
        m_isFirstElement = false;
        return m_afterArray;
    }

    virtual bool beforeCompound(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return m_beforeCompound;
    }

    virtual bool afterCompound(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        bool goToNext = !(m_onlyFirstElement && m_isFirstElement);
        m_isFirstElement = false;
        return goToNext && m_afterCompound;
    }

    virtual bool beforeValue(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return m_beforeValue;
    }

    virtual bool afterValue(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return m_afterValue;
    }

private:
    bool m_beforeArray = true;
    bool m_afterArray = true;
    bool m_onlyFirstElement = false;
    bool m_beforeCompound = true;
    bool m_afterCompound = true;
    bool m_beforeValue = true;
    bool m_afterValue = true;
    bool m_isFirstElement = false;
};

class DummyBitmask
{
public:
    static const ITypeInfo& typeInfo()
    {
        static const std::array<ItemInfo, 1> values = {
            ItemInfo{"ZERO"_sv, "UINT32_C(0)"_sv}
        };

        static const BitmaskTypeInfo typeInfo = {
            "DummyBitmask"_sv, BuiltinTypeInfo::getUInt32(), {}, values
        };

        return typeInfo;
    }

    IReflectablePtr reflectable(const ::std::allocator<uint8_t>& allocator = ::std::allocator<uint8_t>())
    {
        class Reflectable : public ReflectableBase<::std::allocator<uint8_t>>
        {
        public:
            explicit Reflectable(DummyBitmask) :
                    ReflectableBase<::std::allocator<uint8_t>>(DummyBitmask::typeInfo())
            {}

            void write(BitStreamWriter&) override
            {}

            size_t bitSizeOf(size_t) const override
            {
                return 0;
            }
        };

        return ::std::allocate_shared<Reflectable>(allocator, *this);
    }
};

} // namespace

TEST(WalkerTest, dummyObject)
{
    // test to improve coverage
    std::vector<DummyUnion> unionArray;
    unionArray.resize(1);
    unionArray[0].setNestedArray(std::vector<DummyNested>{{DummyNested{"nestedArray"}}});
    std::vector<DummyUnion> optionalUnionArray;
    optionalUnionArray.resize(2);
    optionalUnionArray[0].setNestedArray(std::vector<DummyNested>{{DummyNested{"nestedArray"}}});
    DummyObject dummyObject(13, DummyNested("nested"), "test", std::move(unionArray),
            std::move(optionalUnionArray));

    PackingContextNode dummyPackingContextNode{std::allocator<uint8_t>()};
    dummyObject.getNested().createPackingContext(dummyPackingContextNode);

    BitStreamWriter dummyWriter(nullptr, 0);

    IReflectablePtr dummyReflectable = dummyObject.reflectable();
    ASSERT_THROW(dummyReflectable->getField("nonexistent"), CppRuntimeException);
    ASSERT_EQ(0, dummyReflectable->bitSizeOf(0));
    ASSERT_NO_THROW(dummyReflectable->write(dummyWriter));

    IReflectablePtr optionalUnionArrayReflectable = dummyReflectable->getField("optionalUnionArray");
    ASSERT_NE(nullptr, optionalUnionArrayReflectable);
    ASSERT_EQ(2, optionalUnionArrayReflectable->size());
    IReflectablePtr unionReflectable1 = optionalUnionArrayReflectable->at(0);
    ASSERT_NE(nullptr, unionReflectable1);
    ASSERT_THROW(unionReflectable1->getField("nonexistent"), CppRuntimeException);
    ASSERT_EQ(0, unionReflectable1->bitSizeOf(0));
    ASSERT_NO_THROW(unionReflectable1->write(dummyWriter));

    IReflectablePtr unionReflectable2 = optionalUnionArrayReflectable->at(1);
    ASSERT_NE(nullptr, unionReflectable2);
    ASSERT_EQ(""_sv, unionReflectable2->getChoice());

    IReflectablePtr nestedArrayReflectable = unionReflectable1->getField("nestedArray");
    ASSERT_NE(nullptr, nestedArrayReflectable);
    ASSERT_EQ(1, nestedArrayReflectable->size());
    IReflectablePtr nestedReflectable = nestedArrayReflectable->at(0);
    ASSERT_NE(nullptr, nestedReflectable);
    ASSERT_THROW(nestedReflectable->getField("nonexistent"), CppRuntimeException);
    ASSERT_EQ(0, nestedReflectable->bitSizeOf(0));
    ASSERT_NO_THROW(nestedReflectable->write(dummyWriter));

    DummyBitmask dummyBitmask;
    IReflectablePtr bitmaskReflectable = dummyBitmask.reflectable();
    ASSERT_EQ(0, bitmaskReflectable->bitSizeOf(0));
    ASSERT_NO_THROW(bitmaskReflectable->write(dummyWriter));
}

TEST(WalkerTest, walkNonCompound)
{
    DefaultWalkObserver defaultObserver;
    DefaultWalkFilter defaultFilter;
    Walker walker(defaultObserver, defaultFilter);
    DummyBitmask dummyBitmask;

    ASSERT_THROW(walker.walk(dummyBitmask.reflectable()), CppRuntimeException);
}

TEST(WalkerTest, walk)
{
    TestWalkObserver observer;
    DefaultWalkFilter defaultFilter;
    Walker walker(observer, defaultFilter);
    DummyObject dummyObject = createDummyObject();
    walker.walk(dummyObject.reflectable());

    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(5, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(3)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginCompound"_sv).at(4)->getTypeInfo().getSchemaName());

    ASSERT_EQ(5, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endCompound"_sv).at(3)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(4)->getTypeInfo().getSchemaName());

    ASSERT_EQ(7, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("nested", observer.getCaptures("visitValue"_sv).at(1)->toString());
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(2)->toString());
    ASSERT_EQ("1", observer.getCaptures("visitValue"_sv).at(3)->toString());
    ASSERT_EQ(2, observer.getCaptures("visitValue"_sv).at(4)->toUInt());
    ASSERT_EQ("nestedArray", observer.getCaptures("visitValue"_sv).at(5)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(6));
}

TEST(WalkerTest, walkWrongOptionalCondition)
{
    // use case: optional condition states that the optional is used, but it is not set!
    TestWalkObserver observer;
    DefaultWalkFilter defaultFilter;
    Walker walker(observer, defaultFilter);
    DummyObject dummyObject = createDummyObject(13, false);
    walker.walk(dummyObject.reflectable());

    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(4, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginCompound"_sv).at(3)->getTypeInfo().getSchemaName());

    ASSERT_EQ(4, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(3)->getTypeInfo().getSchemaName());

    ASSERT_EQ(7, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(1));
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(2)->toString());
    ASSERT_EQ("1", observer.getCaptures("visitValue"_sv).at(3)->toString());
    ASSERT_EQ(2, observer.getCaptures("visitValue"_sv).at(4)->toUInt());
    ASSERT_EQ("nestedArray", observer.getCaptures("visitValue"_sv).at(5)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(6));
}

TEST(WalkerTest, walkSkipCompound)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.beforeCompound(false);
    Walker walker(observer, filter);
    DummyObject dummyObject = createDummyObject();
    walker.walk(dummyObject.reflectable());

    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_TRUE(observer.getCaptures("beginCompound"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endCompound"_sv).empty());

    ASSERT_EQ(3, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(1)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(2));
}

TEST(WalkerTest, walkSkipSiblings)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.afterValue(false);
    Walker walker(observer, filter);
    DummyObject dummyObject = createDummyObject();
    walker.walk(dummyObject.reflectable());

    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_TRUE(observer.getCaptures("beginArray"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endArray"_sv).empty());

    ASSERT_TRUE(observer.getCaptures("beginCompound"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endCompound"_sv).empty());

    ASSERT_EQ(1, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
}

TEST(WalkerTest, walkSkipAfterNested)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.afterCompound(false);
    Walker walker(observer, filter);
    DummyObject dummyObject = createDummyObject();
    walker.walk(dummyObject.reflectable());

    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_TRUE(observer.getCaptures("beginArray"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endArray"_sv).empty());

    ASSERT_EQ(1, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("nested", observer.getCaptures("visitValue"_sv).at(1)->toString());
}

TEST(WalkerTest, walkOnlyFirstElement)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.onlyFirstElement(true);
    Walker walker(observer, filter);
    DummyObject dummyObject = createDummyObject();
    walker.walk(dummyObject.reflectable());

    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyObject"_sv, observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("beginCompound"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("DummyNested"_sv, observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("DummyUnion"_sv, observer.getCaptures("endCompound"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(5, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("nested", observer.getCaptures("visitValue"_sv).at(1)->toString());
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(2)->toString());
    ASSERT_EQ("1", observer.getCaptures("visitValue"_sv).at(3)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(4));
}

TEST(DefaultWalkObserverTest, allMethods)
{
    DefaultWalkObserver defaultObserver;
    IWalkObserver& walkObserver = defaultObserver;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];

    ASSERT_NO_THROW(walkObserver.beginRoot(dummyReflectable));
    ASSERT_NO_THROW(walkObserver.endRoot(dummyReflectable));
    ASSERT_NO_THROW(walkObserver.beginArray(dummyReflectable, dummyFieldInfo));
    ASSERT_NO_THROW(walkObserver.endArray(dummyReflectable, dummyFieldInfo));
    ASSERT_NO_THROW(walkObserver.beginCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_NO_THROW(walkObserver.endCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_NO_THROW(walkObserver.visitValue(dummyReflectable, dummyFieldInfo));
}

TEST(DefaultWalkFilterTest, allMethods)
{
    DefaultWalkFilter defaultFilter;
    IWalkFilter& walkFilter = defaultFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];

    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
}

TEST(DepthFilterTest, depth0)
{
    DepthWalkFilter depthWalkFilter(0);
    IWalkFilter& walkFilter = depthWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];

    ASSERT_FALSE(walkFilter.beforeArray(dummyReflectable, dummyFieldInfo)); // 0
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyFieldInfo)); // 0

    ASSERT_FALSE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo)); // 0
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo)); // 0

    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo)); // 0
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo)); // 0
}

TEST(DepthFilterTest, depth1)
{
    DepthWalkFilter depthWalkFilter(1);
    IWalkFilter& walkFilter = depthWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];

    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyFieldInfo)); // 0
    ASSERT_FALSE(walkFilter.beforeArray(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_FALSE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyFieldInfo)); // 0

    ASSERT_TRUE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo)); // 0
    ASSERT_FALSE(walkFilter.beforeArray(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_FALSE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo)); // 0

    ASSERT_TRUE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo)); // 0
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo)); // 0
}

TEST(RegexWalkFilterTest, regexAllMatch)
{
    RegexWalkFilter regexWalkFilter(".*");
    IWalkFilter& walkFilter = regexWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];
    const FieldInfo& dummyArrayFieldInfo = DummyObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
}

TEST(RegexWalkFilterTest, regexPrefixMatch)
{
    RegexWalkFilter regexWalkFilter("nested\\..*");
    IWalkFilter& walkFilter = regexWalkFilter;
    DummyObject dummyObject = createDummyObject();
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& identifierFieldInfo = dummyObject.typeInfo().getFields()[0];
    IReflectablePtr identifierReflectable = dummyReflectable->getField("identifier");
    ASSERT_FALSE(walkFilter.beforeValue(identifierReflectable, identifierFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(identifierReflectable, identifierFieldInfo));

    const FieldInfo& nestedFieldInfo = dummyObject.typeInfo().getFields()[1];
    IReflectablePtr nestedReflectable = dummyReflectable->getField("nested");
    ASSERT_TRUE(walkFilter.beforeCompound(nestedReflectable, nestedFieldInfo));
    const FieldInfo& textFieldInfo = nestedFieldInfo.typeInfo.getFields()[0];
    IReflectablePtr textReflectable = nestedReflectable->getField("text");
    ASSERT_TRUE(walkFilter.beforeValue(textReflectable, textFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(textReflectable, textFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(nestedReflectable, nestedFieldInfo));

    // ignore text

    const FieldInfo& unionArrayFieldInfo = dummyObject.typeInfo().getFields()[3];
    IReflectablePtr unionArrayReflectable = dummyReflectable->getField("unionArray");
    ASSERT_FALSE(walkFilter.beforeArray(unionArrayReflectable, unionArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(unionArrayReflectable, unionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexArrayMatch)
{
    RegexWalkFilter regexWalkFilter("unionArray\\[\\d+\\]\\.nes.*");
    IWalkFilter& walkFilter = regexWalkFilter;
    DummyObject dummyObject = createDummyObject();
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& unionArrayFieldInfo = dummyObject.typeInfo().getFields()[3];
    IReflectablePtr unionArrayReflectable = dummyReflectable->getField("unionArray");
    ASSERT_TRUE(walkFilter.beforeArray(unionArrayReflectable, unionArrayFieldInfo));

    ASSERT_FALSE(walkFilter.beforeCompound(unionArrayReflectable->at(0), unionArrayFieldInfo, 0));
    ASSERT_TRUE(walkFilter.afterCompound(unionArrayReflectable->at(0), unionArrayFieldInfo, 0));

    ASSERT_FALSE(walkFilter.beforeCompound(unionArrayReflectable->at(1), unionArrayFieldInfo, 1));
    ASSERT_TRUE(walkFilter.afterCompound(unionArrayReflectable->at(1), unionArrayFieldInfo, 1));

    ASSERT_TRUE(walkFilter.beforeCompound(unionArrayReflectable->at(2), unionArrayFieldInfo, 2));
    ASSERT_TRUE(walkFilter.afterCompound(unionArrayReflectable->at(2), unionArrayFieldInfo, 2));

    ASSERT_TRUE(walkFilter.afterArray(unionArrayReflectable, unionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexArrayNoMatch)
{
    RegexWalkFilter regexWalkFilter("^unionArray\\[\\d*\\]\\.te.*");
    IWalkFilter& walkFilter = regexWalkFilter;

    std::vector<DummyUnion> unionArray;
    unionArray.resize(1);
    unionArray[0].setNestedArray(std::vector<DummyNested>{{DummyNested{"nestedArray"}}});
    DummyObject dummyObject (13, DummyNested("nested"), "test", std::move(unionArray), NullOpt);
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& unionArrayFieldInfo = dummyObject.typeInfo().getFields()[3];
    IReflectablePtr unionArrayReflectable = dummyReflectable->getField("unionArray");
    ASSERT_FALSE(walkFilter.beforeArray(unionArrayReflectable, unionArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(unionArrayReflectable, unionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexNullCompoundMatch)
{
    RegexWalkFilter regexWalkFilter("nested");
    IWalkFilter& walkFilter = regexWalkFilter;

    DummyObject dummyObject = createDummyObject(0, false);
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& nestedFieldInfo = dummyObject.typeInfo().getFields()[1];
    IReflectablePtr nestedReflectable = dummyReflectable->getField("nested");
    ASSERT_EQ(nullptr, nestedReflectable);
    // note that the null compounds are processed as values!
    ASSERT_TRUE(walkFilter.beforeValue(nestedReflectable, nestedFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(nestedReflectable, nestedFieldInfo));
}

TEST(RegexWalkFilterTest, regexNullCompoundNoMatch)
{
    RegexWalkFilter regexWalkFilter("^nested\\.text$");
    IWalkFilter& walkFilter = regexWalkFilter;

    DummyObject dummyObject = createDummyObject(0, false);
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& nestedFieldInfo = dummyObject.typeInfo().getFields()[1];
    IReflectablePtr nestedReflectable = dummyReflectable->getField("nested");
    ASSERT_EQ(nullptr, nestedReflectable);
    // note that the null compounds are processed as values!
    ASSERT_FALSE(walkFilter.beforeValue(nestedReflectable, nestedFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(nestedReflectable, nestedFieldInfo));
}

TEST(RegexWalkFilterTest, regexNullArrayMatch)
{
    RegexWalkFilter regexWalkFilter("optionalUnionArray");
    IWalkFilter& walkFilter = regexWalkFilter;

    DummyObject dummyObject = createDummyObject();
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& optionalUnionArrayFieldInfo = dummyObject.typeInfo().getFields()[4];
    IReflectablePtr optionalUnionArrayReflectable = dummyReflectable->getField("optionalUnionArray");
    ASSERT_EQ(nullptr, optionalUnionArrayReflectable);
    // note that the null arrays are processed as values!
    ASSERT_TRUE(walkFilter.beforeValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexNullArrayNoMatch)
{
    RegexWalkFilter regexWalkFilter("^optionalUnionArray\\.\\[\\d+\\]\\.nestedArray.*");
    IWalkFilter& walkFilter = regexWalkFilter;

    DummyObject dummyObject = createDummyObject();
    IReflectablePtr dummyReflectable = dummyObject.reflectable();

    const FieldInfo& optionalUnionArrayFieldInfo = dummyObject.typeInfo().getFields()[4];
    IReflectablePtr optionalUnionArrayReflectable = dummyReflectable->getField("optionalUnionArray");
    ASSERT_EQ(nullptr, optionalUnionArrayReflectable);
    // note that the null arrays are processed as values!
    ASSERT_FALSE(walkFilter.beforeValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo));
}

TEST(ArrayLengthWalkFilterTest, length0)
{
    ArrayLengthWalkFilter arrayLengthWalkFilter(0);
    IWalkFilter& walkFilter = arrayLengthWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];
    const FieldInfo& dummyArrayFieldInfo = DummyObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo, 0));
    ASSERT_FALSE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo, 0));
    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo, 1));
    ASSERT_FALSE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo, 1));
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));

    ASSERT_TRUE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo, 0));
    ASSERT_FALSE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo, 0));
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
}

TEST(AndWalkFilterTest, empty)
{
    AndWalkFilter andWalkFilter({});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];
    const FieldInfo& dummyArrayFieldInfo = DummyObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
}

TEST(AndWalkFilterTest, trueTrue)
{
    TestWalkFilter trueFilter1;
    TestWalkFilter trueFilter2;
    AndWalkFilter andWalkFilter({std::ref(trueFilter1), std::ref(trueFilter2)});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];
    const FieldInfo& dummyArrayFieldInfo = DummyObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_TRUE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
}

TEST(AndWalkFilterTest, falseFalse)
{
    TestWalkFilter falseFilter1;
    falseFilter1.beforeArray(false);
    falseFilter1.afterArray(false);
    falseFilter1.beforeCompound(false);
    falseFilter1.afterCompound(false);
    falseFilter1.beforeValue(false);
    falseFilter1.afterValue(false);
    TestWalkFilter falseFilter2;
    falseFilter2.beforeArray(false);
    falseFilter2.afterArray(false);
    falseFilter2.beforeCompound(false);
    falseFilter2.afterCompound(false);
    falseFilter2.beforeValue(false);
    falseFilter2.afterValue(false);
    AndWalkFilter andWalkFilter({std::ref(falseFilter1), std::ref(falseFilter2)});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];
    const FieldInfo& dummyArrayFieldInfo = DummyObject::typeInfo().getFields()[3];

    ASSERT_FALSE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_FALSE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_FALSE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_FALSE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
}

TEST(AndWalkFilterTest, trueFalse)
{
    TestWalkFilter trueFilter;
    TestWalkFilter falseFilter;
    falseFilter.beforeArray(false);
    falseFilter.afterArray(false);
    falseFilter.beforeCompound(false);
    falseFilter.afterCompound(false);
    falseFilter.beforeValue(false);
    falseFilter.afterValue(false);
    AndWalkFilter andWalkFilter({std::ref(trueFilter), std::ref(falseFilter)});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectablePtr dummyReflectable = nullptr;
    const FieldInfo& dummyFieldInfo = DummyObject::typeInfo().getFields()[0];
    const FieldInfo& dummyArrayFieldInfo = DummyObject::typeInfo().getFields()[3];

    ASSERT_FALSE(walkFilter.beforeArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_FALSE(walkFilter.afterArray(dummyReflectable, dummyArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_FALSE(walkFilter.afterCompound(dummyReflectable, dummyFieldInfo));
    ASSERT_FALSE(walkFilter.beforeValue(dummyReflectable, dummyFieldInfo));
    ASSERT_FALSE(walkFilter.afterValue(dummyReflectable, dummyFieldInfo));
}

} // namespace zserio
