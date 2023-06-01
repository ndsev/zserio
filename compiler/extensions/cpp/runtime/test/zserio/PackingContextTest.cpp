#include "gtest/gtest.h"

#include "zserio/PackingContext.h"

namespace zserio
{

TEST(PackingContextTest, constructor)
{
    PackingContextNode packingContextNode{std::allocator<uint8_t>()};

    ASSERT_TRUE(packingContextNode.getChildren().empty());
    ASSERT_FALSE(packingContextNode.hasContext());
}

TEST(PackingContextTest, moveConstructor)
{
    PackingContextNode packingContextNode{std::allocator<uint8_t>()};
    packingContextNode.createChild();
    packingContextNode.createChild().createContext();

    ASSERT_EQ(2, packingContextNode.getChildren().size());
    ASSERT_FALSE(packingContextNode.getChildren().at(0).hasContext());
    ASSERT_TRUE(packingContextNode.getChildren().at(1).hasContext());

    PackingContextNode packingContextNodeMoved(std::move(packingContextNode));

    ASSERT_EQ(2, packingContextNodeMoved.getChildren().size());
    ASSERT_FALSE(packingContextNodeMoved.getChildren().at(0).hasContext());
    ASSERT_TRUE(packingContextNodeMoved.getChildren().at(1).hasContext());
}

TEST(PackingContextTest, moveAssignmentOperator)
{
    PackingContextNode packingContextNode{std::allocator<uint8_t>()};
    packingContextNode.createChild();
    packingContextNode.createChild().createContext();

    ASSERT_EQ(2, packingContextNode.getChildren().size());
    ASSERT_FALSE(packingContextNode.getChildren().at(0).hasContext());
    ASSERT_TRUE(packingContextNode.getChildren().at(1).hasContext());

    PackingContextNode packingContextNodeMoved{std::allocator<uint8_t>()};
    packingContextNodeMoved = std::move(packingContextNode);

    ASSERT_EQ(2, packingContextNodeMoved.getChildren().size());
    ASSERT_FALSE(packingContextNodeMoved.getChildren().at(0).hasContext());
    ASSERT_TRUE(packingContextNodeMoved.getChildren().at(1).hasContext());
}

TEST(PackingContextTest, children)
{
    PackingContextNode packingContextNode{std::allocator<uint8_t>()};

    packingContextNode.reserveChildren(2);
    packingContextNode.createChild();
    ASSERT_EQ(1, packingContextNode.getChildren().size());
    ASSERT_FALSE(packingContextNode.getChildren().at(0).hasContext());
    packingContextNode.createChild();
    ASSERT_EQ(2, packingContextNode.getChildren().size());
    ASSERT_FALSE(packingContextNode.getChildren().at(1).hasContext());
}

TEST(PackingContextTest, context)
{
    PackingContextNode packingContextNode{std::allocator<uint8_t>()};

    auto& child = packingContextNode.createChild();
    ASSERT_FALSE(child.hasContext());
    child.createContext();
    ASSERT_TRUE(child.hasContext());
    ASSERT_NO_THROW(child.getContext());
}

}
