package zserio.runtime.walker;

import test_object.WalkerNested;
import test_object.WalkerObject;
import test_object.WalkerUnion;

public class TestObjectCreator
{
    public static WalkerObject createWalkerObject()
    {
        return createWalkerObject(13, true);
    }

    public static WalkerObject createWalkerObject(long identifier, boolean createNested)
    {
        final WalkerUnion[] unionArray =
                new WalkerUnion[] {new WalkerUnion(), new WalkerUnion(), new WalkerUnion()};
        unionArray[0].setText("1");
        unionArray[1].setValue(2);
        unionArray[2].setNestedArray(new WalkerNested[] {new WalkerNested("nestedArray")});
        if (createNested)
        {
            return new WalkerObject(identifier, new WalkerNested("nested"), "test", unionArray, null);
        }
        else
        {
            return new WalkerObject(identifier, null, "test", unionArray, null);
        }
    }
}
