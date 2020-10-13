package zserio.emit.doc;

import zserio.ast.ArrayInstantiation;
import zserio.ast.AstNode;
import zserio.ast.BitmaskType;
import zserio.ast.BitmaskValue;
import zserio.ast.BooleanType;
import zserio.ast.ChoiceType;
import zserio.ast.Constant;
import zserio.ast.DynamicBitFieldType;
import zserio.ast.EnumItem;
import zserio.ast.EnumType;
import zserio.ast.ExternType;
import zserio.ast.Field;
import zserio.ast.FixedBitFieldType;
import zserio.ast.FloatType;
import zserio.ast.Function;
import zserio.ast.InstantiateType;
import zserio.ast.Parameter;
import zserio.ast.PubsubMessage;
import zserio.ast.PubsubType;
import zserio.ast.ServiceMethod;
import zserio.ast.ServiceType;
import zserio.ast.SqlDatabaseType;
import zserio.ast.SqlTableType;
import zserio.ast.StdIntegerType;
import zserio.ast.StringType;
import zserio.ast.StructureType;
import zserio.ast.Subtype;
import zserio.ast.TemplateArgument;
import zserio.ast.TemplateParameter;
import zserio.ast.TypeInstantiation;
import zserio.ast.TypeReference;
import zserio.ast.UnionType;
import zserio.ast.VarIntegerType;
import zserio.ast.ZserioAstDefaultVisitor;
import zserio.tools.ZserioToolPrinter;

class AstNodeNameMapper
{
    public static String getName(AstNode node)
    {
        final NameVisitor visitor = new NameVisitor();
        node.accept(visitor);

        final String name = visitor.getName();
        if (name == null)
        {
            ZserioToolPrinter.printWarning(node, "Unhandled AST node '" + node.getClass().getName() +
                    "' in AstNodeNameMapper!");

            return "UnknownName";
        }

        return name;
    }

    private static class NameVisitor extends ZserioAstDefaultVisitor
    {
        public String getName()
        {
            return name;
        }

        @Override
        public void visitConstant(Constant constant)
        {
            name = constant.getName();
        }

        @Override
        public void visitSubtype(Subtype subtype)
        {
            name = subtype.getName();
        }

        @Override
        public void visitStructureType(StructureType structureType)
        {
            name = structureType.getName();
        }

        @Override
        public void visitChoiceType(ChoiceType choiceType)
        {
            name = choiceType.getName();
        }

        @Override
        public void visitUnionType(UnionType unionType)
        {
            name = unionType.getName();
        }

        @Override
        public void visitEnumType(EnumType enumType)
        {
            name = enumType.getName();
        }

        @Override
        public void visitBitmaskType(BitmaskType bitmaskType)
        {
            name = bitmaskType.getName();
        }

        @Override
        public void visitSqlTableType(SqlTableType sqlTableType)
        {
            name = sqlTableType.getName();
        }

        @Override
        public void visitSqlDatabaseType(SqlDatabaseType sqlDatabaseType)
        {
            name = sqlDatabaseType.getName();
        }

        @Override
        public void visitServiceType(ServiceType serviceType)
        {
            name = serviceType.getName();
        }

        @Override
        public void visitPubsubType(PubsubType pubsubType)
        {
            name = pubsubType.getName();
        }

        @Override
        public void visitField(Field field)
        {
            name = field.getName();
        }

        @Override
        public void visitEnumItem(EnumItem enumItem)
        {
            name = enumItem.getName();
        }

        @Override
        public void visitBitmaskValue(BitmaskValue bitmaskValue)
        {
            name = bitmaskValue.getName();
        }

        @Override
        public void visitServiceMethod(ServiceMethod serviceMethod)
        {
            name = serviceMethod.getName();
        }

        @Override
        public void visitPubsubMessage(PubsubMessage pubsubMessage)
        {
            name = pubsubMessage.getName();
        }

        @Override
        public void visitFunction(Function function)
        {
            name = function.getName();
        }

        @Override
        public void visitParameter(Parameter parameter)
        {
            name = parameter.getName();
        }

        @Override
        public void visitTypeReference(TypeReference typeReference)
        {
            name = typeReference.getReferencedTypeName();
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
        public void visitStdIntegerType(StdIntegerType stdIntegerType)
        {
            name = stdIntegerType.getName();
        }

        @Override
        public void visitVarIntegerType(VarIntegerType varIntegerType)
        {
            name = varIntegerType.getName();
        }

        @Override
        public void visitFixedBitFieldType(FixedBitFieldType fixedBitFieldType)
        {
            name = fixedBitFieldType.getName();
        }

        @Override
        public void visitDynamicBitFieldType(DynamicBitFieldType dynamicBitFieldType)
        {
            name = dynamicBitFieldType.getName();
        }

        @Override
        public void visitBooleanType(BooleanType booleanType)
        {
            name = booleanType.getName();
        }

        @Override
        public void visitStringType(StringType stringType)
        {
            name = stringType.getName();
        }

        @Override
        public void visitFloatType(FloatType floatType)
        {
            name = floatType.getName();
        }

        @Override
        public void visitExternType(ExternType externType)
        {
            name = externType.getName();
        }

        @Override
        public void visitTemplateParameter(TemplateParameter templateParameter)
        {
            name = templateParameter.getName();
        }

        @Override
        public void visitTemplateArgument(TemplateArgument templateArgument)
        {
            templateArgument.getTypeReference().accept(this);
        }

        @Override
        public void visitInstantiateType(InstantiateType templateInstantiation)
        {
            name = templateInstantiation.getName();
        }

        private String name = null;
    }
}
