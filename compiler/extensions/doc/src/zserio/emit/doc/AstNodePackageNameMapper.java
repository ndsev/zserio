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
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.ast.ZserioType;
import zserio.emit.common.PackageMapper;

class AstNodePackageNameMapper
{
    public static PackageName getPackageName(AstNode node, PackageMapper packageMapper)
    {
        final PackageVisitor visitor = new PackageVisitor();
        node.accept(visitor);

        final Package pkg = visitor.getPackage();

        return (pkg != null) ? packageMapper.getPackageName(pkg) : PackageName.EMPTY;
    }

    private static class PackageVisitor extends ZserioAstDefaultVisitor
    {
        public Package getPackage()
        {
            return pkg;
        }

        @Override
        public void visitConstant(Constant constant)
        {
            pkg = constant.getPackage();
        }

        @Override
        public void visitSubtype(Subtype subtype)
        {
            pkg = subtype.getPackage();
        }

        @Override
        public void visitStructureType(StructureType structureType)
        {
            pkg = structureType.getPackage();
        }

        @Override
        public void visitChoiceType(ChoiceType choiceType)
        {
            pkg = choiceType.getPackage();
        }

        @Override
        public void visitUnionType(UnionType unionType)
        {
            pkg = unionType.getPackage();
        }

        @Override
        public void visitEnumType(EnumType enumType)
        {
            pkg = enumType.getPackage();
        }

        @Override
        public void visitBitmaskType(BitmaskType bitmaskType)
        {
            pkg = bitmaskType.getPackage();
        }

        @Override
        public void visitSqlTableType(SqlTableType sqlTableType)
        {
            pkg = sqlTableType.getPackage();
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
        {
            pkg = sqlDatabaseType.getPackage();
        }

        @Override
        public void visitServiceType(ServiceType serviceType)
        {
            pkg = serviceType.getPackage();
        }

        @Override
        public void visitPubsubType(PubsubType pubsubType)
        {
            pkg = pubsubType.getPackage();
        }

        @Override
        public void visitTypeReference(TypeReference typeReference)
        {
            final ZserioType type = typeReference.getType();
            type.accept(this);
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
        {
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                final ArrayInstantiation arrayInstantiation = (ArrayInstantiation)typeInstantiation;
                final ZserioType elementType = arrayInstantiation.getElementTypeInstantiation().getType();
                elementType.accept(this);
            }
            else
            {
                final ZserioType type = typeInstantiation.getType();
                type.accept(this);
            }
        }

        @Override
        public void visitInstantiateType(InstantiateType templateInstantiation)
        {
            pkg = templateInstantiation.getPackage();
        }

        private Package pkg = null;
    }
}
