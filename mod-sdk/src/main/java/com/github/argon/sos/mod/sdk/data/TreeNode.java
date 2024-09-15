package com.github.argon.sos.mod.sdk.data;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class TreeNode<T> implements Iterable<TreeNode<T>> {
    private final T data;
    @Getter
    @Accessors(fluent = true)
    private TreeNode<T> parent;
    @Getter
    @Accessors(fluent = true)
    private final List<TreeNode<T>> nodes;

    public T get() {
        return data;
    }

    public TreeNode(T data) {
        this.data = data;
        this.nodes = new LinkedList<>();
    }

    public boolean has(T child) {
        return get(child) != null;
    }

    public int depth() {
        if (this.isRoot())
            return 0;
        else
            return parent.depth() + 1;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return nodes.isEmpty();
    }

    @Nullable
    public TreeNode<T> get(T child) {
        for (TreeNode<T> node : nodes) {
            if (node.is(child)) {
                return node;
            }
        }

        return null;
    }

    public boolean is(T child) {
        return this.data.equals(child);
    }

    public TreeNode<T> node(TreeNode<T> child) {
        child.parent = this;
        this.nodes.add(child);
        return child;
    }

    public TreeNode<T> node(T child) {
        TreeNode<T> childNode = new TreeNode<>(child);
        return node(childNode);
    }

    public TreeNode<T> nodes(Collection<T> childs) {
        childs.forEach(this::node);
        return this;
    }

    public Stream<TreeNode<T>> stream() {
        return Stream.of(this);
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[null]";
    }

    @Override
    public @NotNull Iterator<TreeNode<T>> iterator() {
        return new TreeNodeIterator<>(this);
    }
}
