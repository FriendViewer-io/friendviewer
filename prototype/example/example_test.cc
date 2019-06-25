#include "gtest/gtest.h"

namespace test {
namespace {
class TestableClass {
 public:
    bool returns_true() { return true; }
    int returns_one() { return 1; }
};
}  // namespace

TEST(TestableClass, good_test) {
    TestableClass inst;
    EXPECT_TRUE(inst.returns_true());
    EXPECT_EQ(inst.returns_one(), 1);
    EXPECT_GT(inst.returns_one(), 0);
}
TEST(TestableClass, good_test2) {
    TestableClass inst;
    EXPECT_TRUE(true);
}
}  // namespace test
