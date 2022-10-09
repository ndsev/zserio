package zserio.extension.json;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class JsonElement {

    public JsonElement(String name) {
        this.name = name;
    }

    public JsonElement(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setOptional(Boolean optional) {
        this.isOptional = optional;
    }

    public Boolean isOptional() {
        return this.isOptional;
    }

    public void setPackable(Boolean isPackable) {
        this.isPackable = isPackable;
    }

    public Boolean isPackable() {
        return this.isPackable;
    }

    public void addChildren(JsonElement element) {
        if (children == null) {
            children = new ArrayList<JsonElement>();
        }
        children.add(element);
    }

    public void setChildren(List<JsonElement> children) {
        this.children = children;
    }

    public List<JsonElement> getChildren() {
        return children;
    }

    private final String name;

    // @JSONField (serialize = false)
    private String type = null;

    // @JSONField (serialize = false)
    private String comment = null;

    @JSONField (serialize = false)
    private Boolean isOptional = false;

    @JSONField (serialize = false)
    private Boolean isPackable = false;

    private List<JsonElement> children = null;
}
