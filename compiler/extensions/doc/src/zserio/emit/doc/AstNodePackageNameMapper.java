package zserio.emit.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PackageName;
import zserio.ast.PubsubType;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TemplateArgument;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;

class AstNodePackageNameMapper
{
    public static PackageName getPackageName(AstNode node)
    {
        final PackageNameVisitor visitor = new PackageNameVisitor();
        node.accept(visitor);

        final PackageName packageName = visitor.getPackageName();
        if (packageName != null && packageName.isEmpty())
            return DEFAULT_PACKAGE_NAME;

        return packageName;
    }

    private static class PackageNameVisitor extends ZserioAstDefaultVisitor
    {
        public PackageName getPackageName()
        {
            return packageName;
        }

        @Override
        public void visitPackage(Package unitPackage)
        {
            packageName = unitPackage.getPackageName();
        }

        @Override
        public void visitConstant(Constant constant)
        {
            packageName = constant.getPackage().getPackageName();
        }

        @Override
        public void visitSubtype(Subtype subtype)
        {
            packageName = subtype.getPackage().getPackageName();
        }

        @Override
        public void visitStructureType(StructureType structureType)
        {
            packageName = structureType.getPackage().getPackageName();
        }

        @Override
        public void visitChoiceType(ChoiceType choiceType)
        {
            packageName = choiceType.getPackage().getPackageName();
        }

        @Override
        public void visitUnionType(UnionType unionType)
        {
            packageName = unionType.getPackage().getPackageName();
        }

        @Override
        public void visitEnumType(EnumType enumType)
        {
            packageName = enumType.getPackage().getPackageName();
        }

        @Override
        public void visitBitmaskType(BitmaskType bitmaskType)
        {
            packageName = bitmaskType.getPackage().getPackageName();
        }

        @Override
        public void visitSqlTableType(SqlTableType sqlTableType)
        {
            packageName = sqlTableType.getPackage().getPackageName();
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
        {
            packageName = sqlDatabaseType.getPackage().getPackageName();
        }

        @Override
        public void visitServiceType(ServiceType serviceType)
        {
            packageName = serviceType.getPackage().getPackageName();
        }

        @Override
        public void visitPubsubType(PubsubType pubsubType)
        {
            packageName = pubsubType.getPackage().getPackageName();
        }

        @Override
        public void visitTypeReference(TypeReference typeReference)
        {
            final ZserioType type = typeReference.getType();
            if (type != null) // only if it isn't a template parameter
                type.accept(this);
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
        {
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                ((ArrayInstantiation)typeInstantiation).getElementTypeInstantiation()
                        .getTypeReference().accept(this);
            }
            else
            {
                typeInstantiation.getTypeReference().accept(this);
            }
        }

        @Override
        public void visitInstantiateType(InstantiateType templateInstantiation)
        {
            packageName = templateInstantiation.getPackage().getPackageName();
        }

        @Override
        public void visitTemplateArgument(TemplateArgument templateArgument)
        {
            templateArgument.getTypeReference().accept(this);
        }

        private PackageName packageName = null;
    }

    private static PackageName DEFAULT_PACKAGE_NAME =
            new PackageName.Builder().addId("zserio_default_package").get();
}
