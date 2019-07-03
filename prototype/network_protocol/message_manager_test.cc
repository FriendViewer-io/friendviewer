#include "prototype/network_protocol/message_manager.hh"

#include <iostream>
#include <vector>

#include "gtest/gtest.h"

namespace prototype {
namespace network_protocol {

namespace {
void append_data_chunk(int32_t len, std::vector<uint8_t> &out) {
    out.insert(out.end(), reinterpret_cast<uint8_t *>(&len), reinterpret_cast<uint8_t *>(&len + 1));
    out.reserve(out.size() + 4 + len);
    for (int32_t i = 0; i < len; i++) {
        out.emplace_back(i % 255);
    }
}
}  // namespace

// Single message per blob
TEST(MessageManager, retrieve_normal) {
    MessageManager mgr;
    std::vector<uint8_t> check_data;
    std::vector<uint8_t> test_data_1;
    std::vector<uint8_t> test_data_2;
    std::vector<uint8_t> test_data_3;
    append_data_chunk(250, test_data_1);
    append_data_chunk(0, test_data_2);
    append_data_chunk(66000, test_data_3);

    mgr.parse_blob(test_data_1);
    EXPECT_EQ(mgr.next_message_length(), 250);
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    EXPECT_EQ(mgr.next_message_length(), -1);
    EXPECT_TRUE(0 == std::memcmp(check_data.data(), test_data_1.data() + 4, 250));
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    check_data.clear();

    // fake some data inside check_data to make sure it's filled with an empty vector
    check_data.emplace_back(1);
    mgr.parse_blob(test_data_2);
    EXPECT_EQ(mgr.next_message_length(), 0);
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    EXPECT_EQ(mgr.next_message_length(), -1);
    EXPECT_EQ(check_data.size(), 0);
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    check_data.clear();

    mgr.parse_blob(test_data_3);
    EXPECT_EQ(mgr.next_message_length(), 66000);
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    EXPECT_EQ(mgr.next_message_length(), -1);
    EXPECT_TRUE(0 == std::memcmp(check_data.data(), test_data_3.data() + 4, 66000));
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    check_data.clear();
}

// Divide up two messages into three blobs
// First message is cut on data, second message is cut on size
TEST(MessageManager, retrieve_cutoff) {
    MessageManager mgr;
    std::vector<uint8_t> check_data;

    std::vector<uint8_t> test_data_1;
    std::vector<uint8_t> test_data_2;
    std::vector<uint8_t> test_data_3;

    std::vector<uint8_t> original;
    {
        append_data_chunk(60, original);
        append_data_chunk(70, original);
        test_data_1.insert(test_data_1.end(), original.begin(), original.begin() + 33);
        // byte 64 is the first byte of message 2's size descriptor
        test_data_2.insert(test_data_2.end(), original.begin() + 33, original.begin() + 65);
        test_data_3.insert(test_data_3.end(), original.begin() + 65, original.end());
    }
    mgr.parse_blob(test_data_1);
    EXPECT_EQ(mgr.next_message_length(), -1);
    mgr.parse_blob(test_data_2);
    EXPECT_EQ(mgr.next_message_length(), 60);
    mgr.parse_blob(test_data_3);
    EXPECT_EQ(mgr.next_message_length(), 60);

    EXPECT_TRUE(mgr.retrieve_message(check_data));
    EXPECT_EQ(check_data.size(), 60);
    EXPECT_EQ(mgr.next_message_length(), 70);
    EXPECT_TRUE(0 == std::memcmp(check_data.data(), original.data() + 4, 60));

    EXPECT_FALSE(mgr.retrieve_message(check_data));
    EXPECT_EQ(check_data.size(), 70);
    EXPECT_EQ(mgr.next_message_length(), -1);
    EXPECT_TRUE(0 == std::memcmp(check_data.data(), original.data() + 68, 70));
}

// Multiple messages in a single blob. This would likely be caused by nagle
TEST(MessageManager, retrieve_nagle) {
    MessageManager mgr;
    std::vector<uint8_t> check_data;
    std::vector<uint8_t> test_data;

    append_data_chunk(0, test_data);
    append_data_chunk(255, test_data);
    append_data_chunk(0, test_data);
    append_data_chunk(3000, test_data);

    mgr.parse_blob(test_data);

    EXPECT_EQ(mgr.next_message_length(), 0);
    EXPECT_TRUE(mgr.retrieve_message(check_data));
    EXPECT_EQ(check_data.size(), 0);

    EXPECT_EQ(mgr.next_message_length(), 255);
    EXPECT_TRUE(mgr.retrieve_message(check_data));
    EXPECT_EQ(check_data.size(), 255);
    EXPECT_TRUE(0 == std::memcmp(check_data.data(), test_data.data() + 4 + 4, 255));

    EXPECT_EQ(mgr.next_message_length(), 0);
    EXPECT_TRUE(mgr.retrieve_message(check_data));
    EXPECT_EQ(check_data.size(), 0);

    EXPECT_EQ(mgr.next_message_length(), 3000);
    EXPECT_FALSE(mgr.retrieve_message(check_data));
    EXPECT_EQ(check_data.size(), 3000);
    EXPECT_TRUE(0 == std::memcmp(check_data.data(), test_data.data() + 4 + 4 + 255 + 4 + 4, 3000));
}

}  // namespace network_protocol
}  // namespace prototype
