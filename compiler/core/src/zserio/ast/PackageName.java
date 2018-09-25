package zserio.ast;

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
     * Adds ID to the end of the package name.
     *
     * @param id ID to be added at the end of the package name.
     */
    public void addId(String id)
    {
        idList.add(id);
        if (name.isEmpty())
            name += id;
        else
            name += PACKAGE_NAME_SEPARATOR + id;
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

        final String removedPathElement = idList.remove(idList.size() - 1);

        if (idList.isEmpty())
            name = "";
        else
            name = name.substring(0, name.length() - removedPathElement.length() - 1);

        return removedPathElement;
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
    public List<String> getIdList()
    {
        return idList;
    }

    private static final long serialVersionUID = -4965489451342750477L;

    private static final String PACKAGE_NAME_SEPARATOR = ".";

    private final List<String> idList = new ArrayList<String>();
    private String name = "";
}
