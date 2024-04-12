package com.github.argon.sos.moreoptions.util;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> implements Iterable<TreeNode<T>> {
    private final T data;
    @Getter
    @Accessors(fluent = true)
    private TreeNode<T> parent;
    @Getter
    @Accessors(fluent = true)
    private final List<TreeNode<T>> children;

    public T get() {
        return data;
    }

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public boolean has(T child) {
        return get(child) != null;
    }

    public int getDepth() {
        if (this.isRoot())
            return 0;
        else
            return parent.getDepth() + 1;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Nullable
    public TreeNode<T> get(T child) {
        for (TreeNode<T> node : children) {
            if (node.is(child)) {
                return node;
            }
        }

        return null;
    }

    public boolean is(T child) {
        return this.data.equals(child);
    }

    public TreeNode<T> add(TreeNode<T> child) {
        child.parent = this;
        this.children.add(child);
        return child;
    }

    public TreeNode<T> add(T child) {
        TreeNode<T> childNode = new TreeNode<>(child);
        return add(childNode);
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[null]";
    }

    @Override
    public @NotNull Iterator<TreeNode<T>> iterator() {
        return new TreeNodeIterator<>(this);
    }

//    public static <T> TreeNode<T> of(List<T> things) {
//        assert !things.isEmpty() : "given things must not be empty";
//        TreeNode<T> currentNode = null;
//        TreeNode<T> rootNode = null;
//        for (T thing : things) {
//            if (rootNode == null) {
//                rootNode = new TreeNode<>(thing);
//                currentNode = rootNode;
//            } else {
//                currentNode.add(thing);
//            }
//        }
//
//        return rootNode;
//    }
}
