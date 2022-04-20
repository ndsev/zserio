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

class Walker
{
public:
    Walker(const IWalkObserverPtr& walkObserver, const IWalkFilterPtr& filter);

    void walk(const IReflectablePtr& reflectable);

private:
    void walkFields(const IReflectablePtr& compound);
    bool walkField(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo);
    bool walkFieldValue(const IReflectablePtr& reflectable, const FieldInfo& fieldInfo,
            size_t elementIndex = WALKER_NOT_ELEMENT);

    IWalkObserverPtr m_walkObserver;
    IWalkFilterPtr m_walkFilter;
};

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

class DepthWalkFilter : public IWalkFilter
{
public:
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

class RegexWalkFilter : public IWalkFilter
{
public:
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

class ArrayLengthWalkFilter : public IWalkFilter
{
public:
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

class AndWalkFilter : public IWalkFilter
{
public:
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
