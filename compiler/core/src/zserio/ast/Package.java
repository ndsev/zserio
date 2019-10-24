package zserio.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import zserio.tools.HashUtil;
import zserio.tools.ZserioToolPrinter;

/**
 * AST node for one package defined in the language.
 *
 * Package is represented by one translation unit (one source file).
 */
public class Package extends DocumentableAstNode
{
    /**
     * Constructor.
     *
     * @param location    AST node location.
     * @param packageName Name of the package.
     * @param imports     List of all imports defined in the package.
     * @param localTypes  Map of all available local types defined in the package.
     * @param docComment  Documentation comment belonging to this node.
     */
    public Package(AstLocation location, PackageName packageName, List<Import> imports, DocComment docComment)
    {
        super(location, docComment);

        this.packageName = packageName;
        this.imports = imports;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitPackage(this);
    }

    @Override
    public void visitChildren(ZserioAstVisitor visitor)
    {
        super.visitChildren(visitor);

        for (Import packageImport : imports)
            packageImport.accept(visitor);

        for (ZserioType type : localTypes.values())
            type.accept(visitor);
    }

    /**
     * Gets name of the package.
     *
     * @return Package name.
     */
    public PackageName getPackageName()
    {
        return packageName;
    }

    /**
     * Gets imports which are defined in this package.
     *
     * @return List of all imports defined in this package.
     */
    public List<Import> getImports()
    {
        return Collections.unmodifiableList(imports);
    }

    /**
     * Adds a new type to this package.
     *
     * @param type Zserio type to add.
     */
    void addType(ZserioType type)
    {
        final ZserioType addedType = localTypes.put(type.getName(), type);
        if (addedType != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    type.getLocation(), "'" + type.getName() + "' is already defined in this package!");
            stackedException.pushMessage(addedType.getLocation(), "    First defined here");
            throw stackedException;
        }
        if (type instanceof InstantiateType)
            localInstantiations.add((InstantiateType)type);
    }

    /**
     * Adds a new template instantiation to this package.
     *
     * @param name              Name of the generated template instantiation.
     * @param instantiation     Template instantiation.
     */
    void addTemplateInstantiation(String name, TemplatableType instantiation)
    {
        final ZserioType addedType = localTypes.get(name);
        if (addedType != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    instantiation.getLocation(), "'" + name + "' is already defined in this package!");
            stackedException.pushMessage(addedType.getLocation(), "    First defined here");
            throw stackedException;
        }

        final TemplatableType addedInstantiation = templateInstantiations.put(name, instantiation);
        if (addedInstantiation != null)
        {
            final ParserStackedException stackedException = new ParserStackedException(
                    instantiation.getLocation(),
                    "Instantiation name '" + name + "' already exits!");

            final Iterator<TypeReference> descendingIterator =
                    addedInstantiation.getInstantiationReferenceStack().descendingIterator();
            while (descendingIterator.hasNext())
            {
                final TypeReference instantiationReference = descendingIterator.next();
                if (descendingIterator.hasNext())
                {
                    stackedException.pushMessage(instantiationReference.getLocation(),
                            "    Required in instantiation of '" +
                                    instantiationReference.getReferencedTypeName() + "' from here");
                }
                else
                {
                    stackedException.pushMessage(instantiationReference.getLocation(),
                            addedInstantiation.getTemplate() == instantiation.getTemplate()
                                    ? "    First instantiated here"
                                    : "    First seen in instantiation of '" +
                                            instantiationReference.getReferencedTypeName() + "' from here");
                }
            }
            throw stackedException;
        }
    }

    /**
     * Gets Zserio type for given type name with its package if it's visible for this package.
     *
     * @param ownerNode       AST node which holds type to resolve (used for ParserException).
     * @param typePackageName Package name of the type to resolve.
     * @param typeName        Type name to resolve.
     *
     * @return Zserio type if given type name is visible for this package or null if given type name is unknown.
     */
    ZserioType getVisibleType(AstNode ownerNode, PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = getAllVisibleTypes(typePackageName, typeName);
        final int numFoundTypes = foundTypes.size();
        if (numFoundTypes > 1)
        {
            final String firstPackageName = foundTypes.get(0).getPackage().getPackageName().toString();
            final String secondPackageName = foundTypes.get(1).getPackage().getPackageName().toString();
            throw new ParserException(ownerNode, "Ambiguous type reference '" + typeName +
                    "' found in packages '" + firstPackageName + "' and '" + secondPackageName + "'!");
        }

        return (numFoundTypes == 1) ? foundTypes.get(0) : null;
    }

    /**
     * Gets Zserio type for given type name with its package if it's visible for this package.
     *
     * This method does not throw exception in case of ambiguous type. It just returns null in this case.
     *
     * @param typePackageName Package name of the type to resolve.
     * @param typeName        Type name to resolve.
     *
     * @return Zserio type if given type name is visible for this package or null if given type name is unknown.
     */
    ZserioType getVisibleType(PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = getAllVisibleTypes(typePackageName, typeName);

        return (foundTypes.size() == 1) ? foundTypes.get(0) : null;
    }

    Set<InstantiateType> getVisibleInstantiations()
    {
        Set<InstantiateType> visibleInstantitaions =
                new HashSet<InstantiateType>(localInstantiations);
        for (Package pkg : importedPackages)
            visibleInstantitaions.addAll(pkg.getVisibleInstantiations());
        for (SingleTypeName singleType : importedSingleTypes)
        {
            final Package singleTypePackage = singleType.getPackageType();
            final ZserioType type = singleTypePackage.localTypes.get(singleType.getTypeName());
            if (type instanceof InstantiateType)
                visibleInstantitaions.add((InstantiateType)type);
        }
        return visibleInstantitaions;
    }

    /**
     * Imports types to this package.
     *
     * This method resolves all imports which belong to this package
     *
     * @param packageNameMap Map of all available package name to the package object.
     */
    void importTypes(Map<PackageName, Package> packageNameMap)
    {
        for (Import importedNode : imports)
        {
            final PackageName importedPackageName = importedNode.getImportedPackageName();
            final Package importedPackage = packageNameMap.get(importedPackageName);
            if (importedPackage == null)
            {
                // imported package has not been found => this could happen only for default packages
                throw new ParserException(importedNode, "Default package cannot be imported!");
            }

            final String importedTypeName = importedNode.getImportedTypeName();
            if (importedTypeName == null)
            {
                // this is package import
                if (importedPackages.contains(importedPackage))
                    ZserioToolPrinter.printWarning(importedNode, "Duplicated import of package '" +
                            importedPackageName.toString() + "'.");

                // check redundant single type imports
                final List<SingleTypeName> redundantSingleTypeImports = new ArrayList<SingleTypeName>();
                for (SingleTypeName importedSingleType : importedSingleTypes)
                {
                    if (importedSingleType.getPackageType().getPackageName().equals(importedPackageName))
                    {
                        ZserioToolPrinter.printWarning(importedNode, "Import of package '" +
                                importedPackageName.toString() + "' overwrites single type import '" +
                                importedSingleType.getTypeName() + "'.");
                        redundantSingleTypeImports.add(importedSingleType);
                    }
                }

                // remove all redundant single type imports to avoid ambiguous error
                for (SingleTypeName redundantSingleTypeImport : redundantSingleTypeImports)
                    importedSingleTypes.remove(redundantSingleTypeImport);

                importedPackages.add(importedPackage);
            }
            else
            {
                // this is single type import
                if (importedPackages.contains(importedPackage))
                {
                    ZserioToolPrinter.printWarning(importedNode, "Single type '" + importedTypeName +
                            "' imported already by package import.");
                    // don't add it to imported single types because this type would become ambiguous
                }
                else
                {
                    final SingleTypeName importedSingleType = new SingleTypeName(importedPackage,
                            importedTypeName);
                    if (importedSingleTypes.contains(importedSingleType))
                        ZserioToolPrinter.printWarning(importedNode, "Duplicated import of type '" +
                                importedTypeName + "'.");

                    final ZserioType importedZserioType = importedPackage.getLocalType(importedPackageName,
                            importedTypeName);
                    if (importedZserioType == null)
                        throw new ParserException(importedNode, "Unknown type '" + importedTypeName +
                                "' in imported package '" + importedPackageName + "'!");

                    importedSingleTypes.add(importedSingleType);
                }
            }
        }
    }

    private List<ZserioType> getAllVisibleTypes(PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = new ArrayList<ZserioType>();
        final ZserioType foundLocalType = getLocalType(typePackageName, typeName);
        if (foundLocalType != null)
        {
            foundTypes.add(foundLocalType);
        }
        else
        {
            // because we must check for ambiguous types, we must collect all found types
            foundTypes.addAll(getTypesFromImportedPackages(typePackageName, typeName));
            foundTypes.addAll(getTypesFromImportedSingleTypes(typePackageName, typeName));
        }

        return foundTypes;
    }

    private ZserioType getLocalType(PackageName typePackageName, String typeName)
    {
        if (!typePackageName.isEmpty() && !typePackageName.equals(getPackageName()))
            return null;

        return localTypes.get(typeName);
    }

    private List<ZserioType> getTypesFromImportedPackages(PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = new ArrayList<ZserioType>();
        for (Package importedPackage : importedPackages)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            final ZserioType importedType = importedPackage.getLocalType(typePackageName, typeName);
            if (importedType != null)
                foundTypes.add(importedType);
        }

        return foundTypes;
    }

    private List<ZserioType> getTypesFromImportedSingleTypes(PackageName typePackageName, String typeName)
    {
        final List<ZserioType> foundTypes = new ArrayList<ZserioType>();
        for (SingleTypeName importedSingleType : importedSingleTypes)
        {
            // don't exit the loop if something has been found, we need to check for ambiguities
            if (typeName.equals(importedSingleType.getTypeName()))
            {
                final Package importedPackage = importedSingleType.getPackageType();
                final ZserioType importedType = importedPackage.getLocalType(typePackageName, typeName);
                if (importedType != null)
                    foundTypes.add(importedType);
            }
        }

        return foundTypes;
    }

    private static class SingleTypeName implements Comparable<SingleTypeName>
    {
        public SingleTypeName(Package packageType, String typeName)
        {
            this.packageType = packageType;
            this.typeName = typeName;
        }

        @Override
        public int compareTo(SingleTypeName other)
        {
            final int result = typeName.compareTo(other.typeName);
            if (result != 0)
                return result;

            return packageType.getPackageName().compareTo(other.packageType.getPackageName());
        }

        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof SingleTypeName)
                return compareTo((SingleTypeName)other) == 0;

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, typeName);
            hash = HashUtil.hash(hash, packageType.getPackageName());

            return hash;
        }

        public Package getPackageType()
        {
            return packageType;
        }

        public String getTypeName()
        {
            return typeName;
        }

        private final Package packageType;
        private final String typeName;
    }

    private final PackageName packageName;
    private final List<Import> imports;

    // this must be a LinkedHashMap because of 'Cyclic dependency' error checked in ZserioAstTypeResolver
    private final LinkedHashMap<String, ZserioType> localTypes = new LinkedHashMap<String, ZserioType>();
    private final HashSet<InstantiateType> localInstantiations = new HashSet<InstantiateType>();
    private final LinkedHashMap<String, TemplatableType> templateInstantiations =
            new LinkedHashMap<String, TemplatableType>();

    private final Set<Package> importedPackages = new HashSet<Package>();
    // this must be a TreeSet because of 'Ambiguous type reference' error checked in getVisibleType()
    private final Set<SingleTypeName> importedSingleTypes = new TreeSet<SingleTypeName>();
}
