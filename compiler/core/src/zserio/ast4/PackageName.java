package zserio.ast4;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import zserio.tools.HashUtil;
import zserio.tools.StringJoinUtil;

/**
 * Abstraction for package name.
 *
 * Package name has in Zserio the format 'ID.ID.ID'. This class holds list of all 'ID's. This is used for
 * example by Package, Import, TypeReference or SymbolReference.
 */
public class PackageName implements Comparable<PackageName>, Serializable
{
    /**
     * Package name builder. Used to build an immutable package name.
     *
     * The built package name is cached within the builder until a next change is made.
     */
    public static final class Builder implements Serializable
    {
        /**
         * Gets the built package name.
         *
         * @return The built package name.
         */
        public PackageName get()
        {
            if (cachedPackageName != null)
                return cachedPackageName;

            cachedPackageName = new PackageName(new ArrayList<String>(idList));
            return cachedPackageName;
        }

        /**
         * Adds ID to the end of the package name.
         *
         * @param id ID to be added at the end of the building package name.
         *
         * @return This builder for easy building.
         */
        public Builder addId(String id)
        {
            cachedPackageName = null;
            idList.add(id);
            return this;
        }

        /**
         * Adds IDs to the end of the package name.
         *
         * @param ids IDs to be added at the end of the building package name.
         *
         * @return This builder for easy building.
         */
        public Builder addIds(Iterable<String> ids)
        {
            cachedPackageName = null;
            for (String id : ids)
                idList.add(id);
            return this;
        }

        /**
         * Appends given package name at the end of the building package name.
         *
         * @param packageName
         *
         * @return This builder for easy building.
         */
        public Builder append(PackageName packageName)
        {
            cachedPackageName = null;
            for (String id : packageName.getIdList())
                idList.add(id);
            return this;
        }

        /**
         * Removes ID from the end of the package name.
         *
         * @return Last ID in the package name or null if package name is empty.
         */
        public String removeLastId()
        {
            if (idList.isEmpty())
                return null;

            cachedPackageName = null;
            return idList.remove(idList.size() - 1);
        }

        private final List<String> idList = new ArrayList<String>();
        private PackageName cachedPackageName = null;
        private static final long serialVersionUID = 4652468631166184740L;
    }

    @Override
    public int compareTo(PackageName other)
    {
        return name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object other)
    {
        if ( !(other instanceof PackageName) )
            return false;

        return (this == other) || compareTo((PackageName)other) == 0;
    }

    @Override
    public int hashCode()
    {
        return HashUtil.hash(HashUtil.HASH_SEED, name);
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Converts the package name to string format using given separator.
     *
     * @param separator Separator string to use for conversion.
     *
     * @return Converted package name in string format.
     */
    public String toString(String separator)
    {
        return StringJoinUtil.joinStrings(idList, separator);
    }

    /**
     * Converts the package name to filesystem path.
     *
     * @return Filesystem path.
     */
    public String toFilesystemPath()
    {
        return toString(File.separator);
    }

    /**
     * Checks if package name is empty.
     *
     * @return true if package name is empty, otherwise false.
     */
    public boolean isEmpty()
    {
        return idList.isEmpty();
    }

    /**
     * Gets list of identifiers in the package name.
     *
     * @return List of identifiers in the package name.
     */
    public Iterable<String> getIdList()
    {
        return idList;
    }

    private PackageName(List<String> idList)
    {
        this.idList = idList;
        this.name = StringJoinUtil.joinStrings(idList, PACKAGE_NAME_SEPARATOR);
    }

    /** Empty package name constant. */
    public static final PackageName EMPTY = new PackageName(new ArrayList<String>());

    private static final long serialVersionUID = -4965489451342750477L;

    private static final String PACKAGE_NAME_SEPARATOR = ".";

    private final List<String> idList;
    private final String name;
}
