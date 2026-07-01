package com.github.argon.sos.mod.sdk.data;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * For iterating through each node in a tree.
 * The nodes will be flattened.
 *
 * @param <T> type of stored data in node
 */
public class TreeNodeIterator<T> implements Iterator<TreeNode<T>> {

    private enum ProcessStages {
        PROCESS_PARENT,
        PROCESS_CHILD_CUR_NODE,
        PROCESS_CHILD_SUB_NODE
    }

    private final TreeNode<T> treeNode;
    @Nullable
    private ProcessStages doNext;
    @Nullable
    private TreeNode<T> next;
    private final Iterator<TreeNode<T>> childrenCurNodeIter;
    private Iterator<TreeNode<T>> childrenSubNodeIter;

    /**
     * Creates a new {@link TreeNodeIterator} from given {@link TreeNode}
     *
     * @param treeNode to create the iterator from
     */
    public TreeNodeIterator(TreeNode<T> treeNode) {
        this.treeNode = treeNode;
        this.doNext = ProcessStages.PROCESS_PARENT;
        this.childrenCurNodeIter = treeNode.nodes().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {

        if (this.doNext == ProcessStages.PROCESS_PARENT) {
            this.next = this.treeNode;
            this.doNext = ProcessStages.PROCESS_CHILD_CUR_NODE;
            return true;
        }

        if (this.doNext == ProcessStages.PROCESS_CHILD_CUR_NODE) {
            if (childrenCurNodeIter.hasNext()) {
                TreeNode<T> childDirect = childrenCurNodeIter.next();
                childrenSubNodeIter = childDirect.iterator();
                this.doNext = ProcessStages.PROCESS_CHILD_SUB_NODE;
                return hasNext();
            }

            else {
                this.doNext = null;
                return false;
            }
        }

        if (this.doNext == ProcessStages.PROCESS_CHILD_SUB_NODE) {
            if (childrenSubNodeIter.hasNext()) {
                this.next = childrenSubNodeIter.next();
                return true;
            }
            else {
                this.next = null;
                this.doNext = ProcessStages.PROCESS_CHILD_CUR_NODE;
                return hasNext();
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public TreeNode<T> next() {
        return this.next;
    }

    /**
     * Not implemented
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
