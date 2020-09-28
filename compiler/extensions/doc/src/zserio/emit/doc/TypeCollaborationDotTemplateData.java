package zserio.emit.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.AstNode;
import zserio.emit.common.ZserioEmitException;

public class TypeCollaborationDotTemplateData
{
    public TypeCollaborationDotTemplateData(AstNode zserioType, Iterable<AstNode> usedZserioTypes,
            Iterable<AstNode> usedByZserioTypes, String docRootPath) throws ZserioEmitException
    {
        typeName = DocEmitterTools.getZserioName(zserioType);
        packageList = new PackageList();
        relationList = new ArrayList<Relation>();

        addType(zserioType, packageList, docRootPath);

        for (AstNode usedZserioType : usedZserioTypes)
        {
            addType(usedZserioType, packageList, docRootPath);
            relationList.add(new Relation(typeName, DocEmitterTools.getZserioName(usedZserioType)));
        }

        for (AstNode usedByZserioType : usedByZserioTypes)
        {
            addType(usedByZserioType, packageList, docRootPath);
            relationList.add(new Relation(DocEmitterTools.getZserioName(usedByZserioType), typeName));
        }
    }

    public String getTypeName()
    {
        return typeName;
    }

    public Iterable<Package> getPackageList()
    {
        return packageList.getList();
    }

    public Iterable<Relation> getRelationList()
    {
        return relationList;
    }

    public static class PackageList
    {
        public PackageList()
        {
            nameToPackageMap = new HashMap<String, Package>();
        }

        public Package add(String packageName)
        {
            Package packageInst = nameToPackageMap.get(packageName);
            if (packageInst == null)
            {
                packageInst = new Package(packageName);
                nameToPackageMap.put(packageName, packageInst);
            }

            return packageInst;
        }

        Iterable<Package> getList()
        {
            return nameToPackageMap.values();
        }

        private final Map<String, Package> nameToPackageMap;
    }

    public static class Package
    {
        public Package(String name)
        {
            this.name = name;
            typeList = new ArrayList<Type>();
        }

        public void addType(Type type)
        {
            typeList.add(type);
        }

        public String getName()
        {
            return name;
        }

        public Iterable<Type> getTypeList()
        {
            return typeList;
        }

        private final String name;
        private final List<Type> typeList;
    }

    public static class Type
    {
        public Type(String name, String docUrl)
        {
            this.name = name;
            this.docUrl = docUrl;
        }

        public String getName()
        {
            return name;
        }

        public String getDocUrl()
        {
            return docUrl;
        }

        private final String name;
        private final String docUrl;
    }

    public static class Relation
    {
        public Relation(String typeNameFrom, String typeNameTo)
        {
            this.typeNameFrom = typeNameFrom;
            this.typeNameTo = typeNameTo;
        }

        public String getTypeNameFrom()
        {
            return typeNameFrom;
        }

        public String getTypeNameTo()
        {
            return typeNameTo;
        }

        private final String typeNameFrom;
        private final String typeNameTo;
    }

    private void addType(AstNode zserioType, PackageList packageList, String docRootPath)
            throws ZserioEmitException
    {
        final String packageName = DocEmitterTools.getZserioPackageName(zserioType).toString();
        Package packageInst = packageList.add(packageName);

        final String name = DocEmitterTools.getZserioName(zserioType);
        final String docUrl = DocEmitterTools.getDocUrlFromType(docRootPath, zserioType);
        packageInst.addType(new Type(name, docUrl));
    }

    private final String typeName;
    private final PackageList packageList;
    private final List<Relation> relationList;
}
