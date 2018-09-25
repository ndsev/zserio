package zserio.emit.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import zserio.ast.SqlDatabaseType;
import zserio.emit.java.types.JavaNativeType;
import zserio.tools.HashUtil;

public final class MasterDatabaseTemplateData extends JavaTemplateData
{
    public MasterDatabaseTemplateData(TemplateDataContext context, List<SqlDatabaseType> sqlDatabaseTypes)
    {
        super(context);

        rootPackageName = context.getJavaRootPackageName();
        final JavaNativeTypeMapper javaNativeTypeMapper = context.getJavaNativeTypeMapper();
        this.withWriterCode = context.getWithWriterCode();
        this.withValidationCode = context.getWithValidationCode();
        databases = new ArrayList<DatabaseItemData>();
        for (SqlDatabaseType sqlDatabaseType : sqlDatabaseTypes)
            databases.add(new DatabaseItemData(javaNativeTypeMapper, sqlDatabaseType));
    }

    public String getRootPackageName()
    {
        return rootPackageName;
    }

    public boolean getWithWriterCode()
    {
        return withWriterCode;
    }

    public boolean getWithValidationCode()
    {
        return withValidationCode;
    }

    public Iterable<DatabaseItemData> getDatabases()
    {
        Collections.sort(databases);
        return databases;
    }

    public static class DatabaseItemData implements Comparable<DatabaseItemData>
    {
        public DatabaseItemData(JavaNativeTypeMapper javaNativeTypeMapper, SqlDatabaseType databaseType)
        {
            final JavaNativeType javaType = javaNativeTypeMapper.getJavaType(databaseType);
            name = javaType.getName();
            typeName = javaType.getFullName();
        }

        public String getTypeName()
        {
            return typeName;
        }

        /**
         * Compare entries by their (short) name.
         *
         * This mimics the original behavior of the MasterDatabase emitter. This then
         * affects the order of the databases in the output file.
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
    }

    private final String                    rootPackageName;
    private final boolean                   withWriterCode;
    private final boolean                   withValidationCode;
    private final List<DatabaseItemData>    databases;
}
