package zserio.ast;

import java.util.List;

public class TemplateParameter extends AstNodeBase
{
    public TemplateParameter(AstLocation location, String name)
    {
        super(location);

        this.name = name;
    }

    @Override
    public void accept(ZserioAstVisitor visitor)
    {
        visitor.visitTemplateParameter(this);
    }

    public String getName()
    {
        return name;
    }

    static int indexOf(List<TemplateParameter> templateParameters, String name)
    {
        for (int i = 0; i < templateParameters.size(); ++i)
        {
            if (templateParameters.get(i).getName().equals(name))
                return i;
        }
        return -1;
    }

    private final String name;
}