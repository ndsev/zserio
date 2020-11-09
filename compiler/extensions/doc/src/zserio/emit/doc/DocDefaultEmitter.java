package zserio.emit.doc;

import zserio.emit.common.DefaultTreeWalker;

abstract class DocDefaultEmitter extends DefaultTreeWalker
{
    @Override
    public boolean traverseTemplateInstantiations()
    {
        return false;
    }

    static final String CONTENT_DIRECTORY = "content";
    static final String CSS_DIRECTORY = "css";
    static final String JS_DIRECTORY = "js";
    static final String RESOURCES_DIRECTORY = "resources";
    static final String SYMBOL_COLLABORATION_DIRECTORY = "diagrams";

    protected static final String DOC_TEMPLATE_LOCATION = "doc/";
}
