package com.github.argon.sos.mod.sdk.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a data structure as a tree
 *
 * @param <T> type of the object in the tree
 */
public class TreeNode<T> implements Iterable<TreeNode<T>> {
    private final T data;
    @Getter
    @Accessors(fluent = true)
    private TreeNode<T> parent;
    @Getter
    @Accessors(fluent = true)
    private final List<TreeNode<T>> nodes;

    /**
     * Returns the data object in this tree node
     *
     * @return data object in this tree node
     */
    public T get() {
        return data;
    }

    /**
     * Creates a new {@link TreeNode} with the given object as data
     *
     * @param data to store in this node
     */
    public TreeNode(T data) {
        this.data = data;
        this.nodes = new LinkedList<>();
    }

    /**
     * Tells whether this node has a given object as a child
     *
     * @param child to look for
     * @return whether the child is in this node or not
     */
    public boolean has(T child) {
        return get(child) != null;
    }

    /**
     * Returns the depth of this node
     *
     * @return depth of this node
     */
    public int depth() {
        if (this.isRoot())
            return 0;
        else
            return parent.depth() + 1;
    }

    /**
     * Tells whether this node is at root level / at the beginning.
     *
     * @return whether node is at root
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Tells whether this node is a leaf / at the end.
     *
     * @return whether node is a leaf
     */
    public boolean isLeaf() {
        return nodes.isEmpty();
    }

    /**
     * Returns the {@link TreeNode} of the given child.
     *
     * @param child to look for
     * @return tree node of given child
     */
    @Nullable
    public TreeNode<T> get(T child) {
        for (TreeNode<T> node : nodes) {
            if (node.is(child)) {
                return node;
            }
        }

        return null;
    }

    /**
     * Checks whether given data is stored in this node.
     *
     * @param data to check
     * @return whether data is stored in this node
     */
    public boolean is(T data) {
        return this.data.equals(data);
    }

    /**
     * Creates and adds a new {@link TreeNode} with given child {@link TreeNode}.
     *
     * @param child node to add
     * @return new tree node
     */
    public TreeNode<T> node(TreeNode<T> child) {
        child.parent = this;
        this.nodes.add(child);
        return child;
    }

    /**
     * Creates and adds a new {@link TreeNode} with given child.
     *
     * @param child to add in this node
     * @return new tree node
     */
    public TreeNode<T> node(T child) {
        TreeNode<T> childNode = new TreeNode<>(child);
        return node(childNode);
    }

    /**
     * Creates and adds a new {@link TreeNode}s from given children.
     *
     * @param children to store in new node
     * @return this node
     */
    public TreeNode<T> nodes(Collection<T> children) {
        children.forEach(this::node);
        return this;
    }

    /**
     * For streaming through the tree.
     *
     * @return stream of this tree.
     */
    public Stream<TreeNode<T>> stream() {
        return Stream.of(this);
    }

    /**
     * Will create a {@link String} from the stored data.
     *
     * @return stored data as string
     */
    @Override
    public String toString() {
        return data != null ? data.toString() : "[null]";
    }

    /**
     * For iterating through each node in the tree via the {@link TreeNodeIterator}.
     * The tree will be flattened for iteration.
     *
     * @return the iterator for iterating
     */
    @Override
    public @NotNull Iterator<TreeNode<T>> iterator() {
        return new TreeNodeIterator<>(this);
    }
}
