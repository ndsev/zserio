package zserio.extension.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.EnumType;
import zserio.ast.InstantiateType;
import zserio.ast.Package;
import zserio.ast.PubsubType;
import zserio.ast.RuleGroup;
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
import zserio.ast.ZserioTemplatableType;
import zserio.ast.ZserioType;

/**
 * Mapper for AST node package.
 *
 * The class maps any AST node to its package. For example, considering AST node for constant, it returns
 * the package where the constant is defined.
 *
 * It can return null if the package for given AST node is unknown.
 */
final class AstNodePackageMapper
{
    public static Package getPackage(AstNode node)
    {
        final PackageVisitor visitor = new PackageVisitor();
        node.accept(visitor);

        return visitor.getPackage();
    }

    private static final class PackageVisitor extends ZserioAstDefaultVisitor
    {
        public Package getPackage()
        {
            return pkg;
        }

        @Override
        public void visitPackage(Package unitPackage)
        {
            pkg = unitPackage;
        }

        @Override
        public void visitConstant(Constant constant)
        {
            pkg = constant.getPackage();
        }

        @Override
        public void visitRuleGroup(RuleGroup ruleGroup)
        {
            pkg = ruleGroup.getPackage();
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
            if (!typeReference.getTemplateArguments().isEmpty()) // we never show instantiations directly
            {
                final ZserioTemplatableType instantiationOrTemplate =
                        (ZserioTemplatableType)typeReference.getType();
                if (instantiationOrTemplate.getTemplate() != null) // is an instantiation
                    instantiationOrTemplate.getTemplate().accept(this);
                else // is a template instantiated within another template
                    instantiationOrTemplate.accept(this);
            }
            else if (type != null) // only if it isn't a template parameter
            {
                type.accept(this);
            }
        }

        @Override
        public void visitTypeInstantiation(TypeInstantiation typeInstantiation)
        {
            if (typeInstantiation instanceof ArrayInstantiation)
            {
                ((ArrayInstantiation)typeInstantiation)
                        .getElementTypeInstantiation()
                        .getTypeReference()
                        .accept(this);
            }
            else
            {
                typeInstantiation.getTypeReference().accept(this);
            }
        }

        @Override
        public void visitInstantiateType(InstantiateType templateInstantiation)
        {
            pkg = templateInstantiation.getPackage();
        }

        @Override
        public void visitTemplateArgument(TemplateArgument templateArgument)
        {
            templateArgument.getTypeReference().accept(this);
        }

        private Package pkg = null;
    }
}
