package zserio.emit.cpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import zserio.ast.Field;
import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;
import zserio.emit.cpp.types.CppNativeType;
import zserio.tools.HashUtil;

public class MasterDatabaseTemplateData extends CppTemplateData
{
    public MasterDatabaseTemplateData(TemplateDataContext context, List<SqlDatabaseType> sqlDatabaseTypes)
            throws ZserioEmitException
    {
        super(context);

        final CppNativeTypeMapper cppNativeTypeMapper = context.getCppNativeTypeMapper();
        databases = new ArrayList<DatabaseItemData>();
        for (SqlDatabaseType sqlDatabaseType : sqlDatabaseTypes)
            databases.add(new DatabaseItemData(cppNativeTypeMapper, sqlDatabaseType, this));
    }

    public Iterable<DatabaseItemData> getDatabases()
    {
        Collections.sort(databases);
        return databases;
    }

    public static class DatabaseItemData implements Comparable<DatabaseItemData>
    {
        public DatabaseItemData(CppNativeTypeMapper cppNativeTypeMapper, SqlDatabaseType databaseType,
                IncludeCollector includeCollector) throws ZserioEmitException
        {
            final CppNativeType nativeDatabaseType = cppNativeTypeMapper.getCppType(databaseType);
            includeCollector.addHeaderIncludesForType(nativeDatabaseType);

            // FIXME: native and mapped names?
            name = databaseType.getName();
            typeName = nativeDatabaseType.getFullName();

            tableNames = new TreeSet<String>();
            for (Field field : databaseType.getFields())
            {
                tableNames.add(field.getName());
            }
        }

        public String getName()
        {
            return name;
        }

        public String getTypeName()
        {
            return typeName;
        }

        public Iterable<String> getTableNames()
        {
            return tableNames;
        }

        /**
         * Compare entries by their (short) name.
         */
        @Override
        public int compareTo(DatabaseItemData other)
        {
            return name.compareTo(other.name);
        }

        // to make FindBugs happy
        @Override
        public boolean equals(Object other)
        {
            if (this == other)
                return true;

            if (other instanceof DatabaseItemData)
            {
                return compareTo((DatabaseItemData)other) == 0;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int hash = HashUtil.HASH_SEED;
            hash = HashUtil.hash(hash, name);
            hash = HashUtil.hash(hash, typeName);
            return hash;
        }

        private final String name;
        private final String typeName;
        private final SortedSet<String> tableNames;
    }

    private final List<DatabaseItemData> databases;
}
