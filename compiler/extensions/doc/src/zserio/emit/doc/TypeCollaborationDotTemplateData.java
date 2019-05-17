package zserio.emit.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zserio.ast.ZserioType;
import zserio.emit.common.ZserioEmitException;

/**
 * The type collaboration diagram data used for FreeMarker template during DOT generation.
 */
public class TypeCollaborationDotTemplateData
{
    /**
     * The constructor.
     *
     * @param zserioType        The zserio type for which to generate 'used' diagram.
     * @param usedZserioTypes   The list of zserio types which are used by zserioType.
     * @param usedByZserioTypes The list of zserio types which uses the zserioType.
     * @param docRootPath       The root path of the generated documentation for links or null
     *                          if links are not required.
     *
     * @throws ZserioEmitException Throws in case of any internal error.
     */
    public TypeCollaborationDotTemplateData(ZserioType zserioType, Iterable<ZserioType> usedZserioTypes,
            Iterable<ZserioType> usedByZserioTypes, String docRootPath) throws ZserioEmitException
    {
        typeName = zserioType.getName();
        packageList = new PackageList();
        relationList = new ArrayList<Relation>();

        addType(zserioType, packageList, docRootPath);

        for (ZserioType usedZserioType : usedZserioTypes)
        {
            addType(usedZserioType, packageList, docRootPath);
            relationList.add(new Relation(typeName, usedZserioType.getName()));
        }

        for (ZserioType usedByZserioType : usedByZserioTypes)
        {
            addType(usedByZserioType, packageList, docRootPath);
            relationList.add(new Relation(usedByZserioType.getName(), typeName));
        }
    }

    /**
     * Returns the name of the zserio type for which the collaboration diagram is generated.
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * Returns the list of the used packages.
     */
    public Iterable<Package> getPackageList()
    {
        return packageList.getList();
    }

    /**
     * Returns the list of the zserio type relations.
     */
    public Iterable<Relation> getRelationList()
    {
        return relationList;
    }

    /**
     * Helper class to hold list of packages.
     */
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

    /**
     * Helper class to model the package for FreeMarker template.
     */
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

        private final String        name;
        private final List<Type>    typeList;
    }

    /**
     * Helper class to model the type for FreeMarker template.
     */
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

        private final String    name;
        private final String    docUrl;
    }

    /**
     * Helper class to model the relation for FreeMarker template.
     */
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

        private final String    typeNameFrom;
        private final String    typeNameTo;
    }

    private void addType(ZserioType zserioType, PackageList packageList, String docRootPath)
            throws ZserioEmitException
    {
        final String packageName = zserioType.getPackage().getPackageName().toString();
        Package packageInst = packageList.add(packageName);

        final String name = zserioType.getName();
        final String docUrl = DocEmitterTools.getDocUrlFromType(docRootPath, zserioType);
        packageInst.addType(new Type(name, docUrl));
    }

    private final String            typeName;
    private final PackageList       packageList;
    private final List<Relation>    relationList;
}
