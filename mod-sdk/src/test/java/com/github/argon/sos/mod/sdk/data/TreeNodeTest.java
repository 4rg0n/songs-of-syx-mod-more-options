package com.github.argon.sos.mod.sdk.data;

import com.github.argon.sos.mod.sdk.testing.ModSdkExtension;
import com.github.argon.sos.mod.sdk.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ModSdkExtension.class)
class TreeNodeTest {
    @Test
    void test() {
        TreeNode<String> root = new TreeNode<>("root");

        assertThat(root.depth()).isEqualTo(0);
        assertThat(root.isLeaf()).isTrue();

        TreeNode<String> branch = new TreeNode<>("branch");
        root.node(branch);

        assertThat(branch.depth()).isEqualTo(1);
        assertThat(branch.isLeaf()).isTrue();

        TreeNode<String> leaf = new TreeNode<>("leaf");
        branch.node(leaf);

        assertThat(leaf.depth()).isEqualTo(2);
        assertThat(leaf.isLeaf()).isTrue();

        List<String> nodeList = Lists.of("root", "branch", "leaf");
        for (TreeNode<String> node : root) {
            assertThat(nodeList).contains(node.get());
        }

        assertThat(root.isLeaf()).isFalse();
        assertThat(branch.isLeaf()).isFalse();
        assertThat(root.get("branch")).isEqualTo(branch);
        assertThat(branch.nodes().get(0)).isEqualTo(leaf);
    }
}