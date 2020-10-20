package zserio.emit.doc;

import zserio.ast.SqlDatabaseType;
import zserio.emit.common.ZserioEmitException;

public class SqlDatabaseTemplateData extends CompoundTypeTemplateData
{
    public SqlDatabaseTemplateData(TemplateDataContext context, SqlDatabaseType sqlDatabaseType)
            throws ZserioEmitException
    {
        super(context, sqlDatabaseType);

        structureDiagramSvg = (context.getWithSvgDiagrams()) ? DbStructureDotEmitter.getSvgDbStructureHtmlLink(
                sqlDatabaseType, context.getDbStructureDirectory()) : null;
    }

    public String getStructureDiagramSvg()
    {
        return structureDiagramSvg;
    }

    private final String structureDiagramSvg;
}
