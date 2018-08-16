package zserio.emit.doc;


import antlr.collections.AST;
import zserio.ast.CompoundType;
import zserio.ast.ConstType;
import zserio.ast.EnumType;
import zserio.ast.Package;
import zserio.ast.ServiceType;
import zserio.ast.Subtype;
import zserio.ast.TokenAST;
import zserio.ast.ZserioType;
import zserio.tools.PackageManager;

public class ContentEmitter extends DefaultHtmlEmitter
{
    private final CompoundEmitter ce;
    private final EnumerationEmitter ee;
    private final SubtypeEmitter se;
    private final ConstTypeEmitter cte;


    public ContentEmitter(String outputPath, boolean withSvgDiagrams)
    {
        super(outputPath);

        ce = new CompoundEmitter(outputPath, withSvgDiagrams);
        ee = new EnumerationEmitter(outputPath, withSvgDiagrams);
        se = new SubtypeEmitter(outputPath, withSvgDiagrams);
        cte = new ConstTypeEmitter(outputPath, withSvgDiagrams);
    }


    @Override
    public void beginRoot(AST root)
    {
        setCurrentFolder(CONTENT_FOLDER);
    }

    @Override
    public void endRoot()
    {
        emitPackage(PackageManager.get().defaultPackage);
    }

    @Override
    public void endPackage(AST p)
    {
        emitPackage(currentPackage);
    }

    private void emitPackage(Package pkg)
    {
        for (String typeName : pkg.getLocalTypeNames())
        {
            ZserioType t = pkg.getLocalType(typeName);
            TokenAST type = (TokenAST) t;
            if (type instanceof CompoundType)
            {
                ce.emit((CompoundType) type);
            }
            else if (type instanceof EnumType)
            {
                ee.emit((EnumType) type);
            }
            else if (type instanceof Subtype)
            {
                se.emit((Subtype) type);
            }
            else if (type instanceof ConstType)
            {
                cte.emit((ConstType) type);
            }
            else if (type instanceof ServiceType)
            {
                // TODO: not implemented for ServiceType
            }
            else
            {
                throw new RuntimeException("don't know how to emit content for type " + type);
            }
        }
    }
}
