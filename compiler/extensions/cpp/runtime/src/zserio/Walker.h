#ifndef ZSERIO_WALKER_H_INC
#define ZSERIO_WALKER_H_INC

#include <functional>
#include <regex>

#include "zserio/IReflectable.h"
#include "zserio/ITypeInfo.h"
#include "zserio/WalkerConst.h"
#include "zserio/IWalkObserver.h"
#include "zserio/IWalkFilter.h"

namespace zserio
{

/**
 * Walker through zserio objects, based on generated type info (see -withTypeInfoCode) and
 * reflectable interface (see -withReflectionCode).
 */
class Walker
{
public:
    /**
     * Constructor.
     *
     * \param walkObserver Observer to use during walking.
     * \param walkFilter Walk filter to use.
     */
    Walker(const IWalkObserverPtr& walkObserver, const IWalkFilterPtr& walkFilter);

    /**
     * Walks given relfectable zserio compound object.
     *
     * \param reflectable Zserio compound object to walk.
     */
    void walk(const IReflectablePtr& reflectable);

private:
    void walkFields(const IReflectablePtr& compound, const ITypeInfo& typeInfo);
    bool walkField(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo);
    bool walkFieldValue(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT);

    IWalkObserverPtr m_walkObserver;
    IWalkFilterPtr m_walkFilter;
};

/**
 * Default walk observer which just does nothing.
 */
class DefaultWalkObserver : public IWalkObserver
{
public:
    virtual void beginRoot(const IReflectablePtr&) override {}
    virtual void endRoot(const IReflectablePtr&) override {}

    virtual void beginArray(const IReflectablePtr&, const FieldInfo&) override {}
    virtual void endArray(const IReflectablePtr&, const FieldInfo&) override {}

    virtual void beginCompound(const IReflectablePtr&, const FieldInfo&, size_t) override {}
    virtual void endCompound(const IReflectablePtr&, const FieldInfo&, size_t) override {}

    virtual void visitValue(const IReflectablePtr&, const FieldInfo&, size_t) override {}
};

/**
 * Default walk filter which filters nothing.
 */
class DefaultWalkFilter : public IWalkFilter
{
public:
    virtual bool beforeArray(const IReflectablePtr&, const FieldInfo&) override
    {
        return true;
    }

    virtual bool afterArray(const IReflectablePtr&, const FieldInfo&) override
    {
        return true;
    }

    virtual bool beforeCompound(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return true;
    }

    virtual bool afterCompound(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return true;
    }

    virtual bool beforeValue(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return true;
    }

    virtual bool afterValue(const IReflectablePtr&, const FieldInfo&, size_t) override
    {
        return true;
    }
};

/**
 * Walk filter which allows to walk only to the given maximum depth.
 */
class DepthWalkFilter : public IWalkFilter
{
public:
    /**
     * Constructor.
     *
     * \param maxDepth Maximum depth to walk to.
     */
    explicit DepthWalkFilter(size_t maxDepth);

    virtual bool beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    size_t m_maxDepth;
    size_t m_depth;
};

/**
 * Walk filter which allows to walk only paths matching the given regex.
 *
 * The path is constructed from field names within the root object, thus the root object itself
 * is not part of the path.
 *
 * Array elements have the index appended to the path so that e.g. "compound.arrayField[0]" will match
 * only the first element in the array "arrayField".
 */
class RegexWalkFilter : public IWalkFilter
{
public:
    /**
     * Constructor.
     *
     * \param pathRegex Path regex to use for filtering.
     */
    explicit RegexWalkFilter(const char* pathRegex);

    virtual bool beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    void appendPath(const FieldInfo& fieldInfo, size_t elementIndex);
    void popPath(const FieldInfo& fieldInfo, size_t elementIndex);
    std::string getCurrentPath() const;
    bool matchSubtree(const IReflectablePtr& value, const FieldInfo& fieldInfo) const;

    std::vector<std::string> m_currentPath;
    std::regex m_pathRegex;
};

/**
 * Walk filter which allows to walk only to the given maximum array length.
 */
class ArrayLengthWalkFilter : public IWalkFilter
{
public:
    /**
     * Constructor.
     *
     * \param maxArrayLength Maximum array length to walk to.
     */
    explicit ArrayLengthWalkFilter(size_t maxArrayLength);

    virtual bool beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    bool filterArrayElement(size_t elementIndex);

    size_t m_maxArrayLength;
};

/**
 * Walk filter which implements composition of particular filters.
 *
 * The filters are called sequentially and logical and is applied on theirs results.
 * Note that all filters are always called.
 */
class AndWalkFilter : public IWalkFilter
{
public:
    /**
     * Constructor.
     *
     * \param walkFilters List of filters to use in composition.
     */
    explicit AndWalkFilter(const std::vector<IWalkFilterPtr>& walkFilters);

    virtual bool beforeArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;
    virtual bool afterArray(const IReflectablePtr& array, const FieldInfo& fieldInfo) override;

    virtual bool beforeCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterCompound(const IReflectablePtr& compound, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

    virtual bool beforeValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;
    virtual bool afterValue(const IReflectablePtr& value, const FieldInfo& fieldInfo,
            size_t elementIndex) override;

private:
    template <typename FilterFunc, typename ...Args>
    bool applyFilters(FilterFunc filterFunc, Args... args)
    {
        bool result = true;
        for (const IWalkFilterPtr& walkFilter : m_walkFilters)
            result &= ((*walkFilter).*filterFunc)(args...);
        return result;
    }

    std::vector<IWalkFilterPtr> m_walkFilters;
};

} // namespace zserio

#endif // ZSERIO_WALKER_H_INC
