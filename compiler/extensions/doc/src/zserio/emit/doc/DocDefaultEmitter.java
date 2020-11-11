package zserio.emit.doc;

import zserio.emit.common.DefaultTreeWalker;

abstract class DocDefaultEmitter extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    protected static final String DOC_TEMPLATE_LOCATION = "doc/";
}
