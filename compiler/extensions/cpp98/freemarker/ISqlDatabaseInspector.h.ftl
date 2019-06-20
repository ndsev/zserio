<#include "FileHeader.inc.ftl">
<@file_header generatorDescription/>

<@include_guard_begin rootPackage.path, "ISqlDatabaseInspector"/>

#include <string>

#include <zserio/inspector/BlobInspectorTree.h>
#include <zserio/BitStreamReader.h>
#include <zserio/BitStreamWriter.h>

#include "<@include_path rootPackage.path, "IInspectorParameterProvider.h"/>"

<@namespace_begin rootPackage.path/>

class ISqlDatabaseInspector
{
public:
    virtual bool convertBitStreamToBlobTree(const std::string& tableName, const std::string& blobName,
            zserio::BitStreamReader& reader,
            ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
            zserio::BlobInspectorTree& tree) const = 0;

    virtual bool convertBlobTreeToBitStream(const std::string& tableName, const std::string& blobName,
            const zserio::BlobInspectorTree& tree,
            ${rootPackage.name}::IInspectorParameterProvider& parameterProvider,
            zserio::BitStreamWriter& writer) const = 0;

    virtual bool doesBlobExist(const std::string& tableName, const std::string& blobName) const = 0;

    virtual ~ISqlDatabaseInspector()
    {}
};

<@namespace_end rootPackage.path/>

<@include_guard_end rootPackage.path, "ISqlDatabaseInspector"/>
