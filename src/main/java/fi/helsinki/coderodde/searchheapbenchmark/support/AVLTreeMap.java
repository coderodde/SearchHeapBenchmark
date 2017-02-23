package fi.helsinki.coderodde.searchheapbenchmark.support;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public final class AVLTreeMap<K extends Comparable<? super K>, V>
implements Map<K, V> {

    private static final class Node<K, V> {
        
        /**
         * The key of this node.
         */
        K key;
        
        /**
         * The satellite data.
         */
        V value;
        
        /**
         * The left child.
         */
        Node<K, V> left;
        
        /**
         * The right child.
         */
        Node<K, V> right;
        
        /**
         * The parent node.
         */
        Node<K, V> parent;
        
        /**
         * The height of this node.
         */
        int height;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
    /**
     * The root node of this tree.
     */
    private Node<K, V> root;
    
    /**
     * The number of key/value pairs in this map.
     */
    private int size;
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        K element = (K) key;
        Node<K, V> node = root;
        int cmp;
        
        while (node != null && (cmp = element.compareTo(node.key)) != 0) {
            if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        
        return node != null;
    }
    
    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(
                "This AVLTreeMap does not support 'containsValue'.");
    }

    @Override
    public V get(Object key) {
        K element = (K) key;
        Node<K, V> node = root;
        int cmp;
        
        while (node != null) {
            cmp = element.compareTo(node.key);
            
            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node.value;
            }
        }
        
        return null;
    }

    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key, "The input key is null.");
        
        if (root == null) {
            root = new Node<>(key, value);
            size = 1;
            return null;
        }
        
        Node<K, V> parent = null;
        Node<K, V> node = root;
        int cmp;
        
        while (node != null) {
            cmp = key.compareTo(node.key);
            
            if (cmp == 0) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
            
            parent = node;
            
            if (cmp < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        
        Node<K, V> newnode = new Node<>(key, value);
        
        if (key.compareTo(parent.key) < 0) {
            parent.left = newnode;
        } else {
            parent.right = newnode;
        }
        
        newnode.parent = parent;
        size++;
        fixAfterInsertion(newnode);
        return null;
    }

    @Override
    public V remove(Object key) {
        K element = (K) key;
        Node<K, V> x = root;
        int cmp;
        
        while (x != null && (cmp = element.compareTo(x.key)) != 0) {
            if (cmp < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }
        
        if (x == null) {
            return null;
        }
        
        V returnValue = x.value;
        x = deleteNode(x);
        fixAfterDeletion(x);
        size--;
        return returnValue;
    }
    
    public K getMinimumKey() {
        if (size == 0) {
            throw new NoSuchElementException("Reading from empty AVLTree.");
        }
        
        Node<K, V> node = root;
        
        while (node.left != null) {
            node = node.left;
        }
        
        return node.key;
    }
    
    public V getMinimumKeyValue() {
        if (size == 0) {
            throw new NoSuchElementException("Reading from empty AVLTree.");
        }
        
        Node<K, V> node = root;
        
        while (node.left != null) {
            node = node.left;
        }
        
        return node.value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException(
                "This AVLTreeMap does not support 'keySet'."); 
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException(
                "This AVLTreeMap does not support 'values'.");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException(
                "This AVLTreeMap does not support 'entrySet'.");
    }
    
    private void fixAfterInsertion(Node<K, V> node) {
        Node<K, V> parent = node.parent;
        Node<K, V> grandParent;
        Node<K, V> subTree;
        
        while (parent != null) {
            if (height(parent.left) == height(parent.right) + 2) {
                grandParent = parent.parent;
                
                if (height(parent.left.left) >= height(parent.left.right)) {
                    subTree = rightRotate(parent);
                } else {
                    subTree = leftRightRotate(parent);
                }
                
                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }
                
                if (grandParent != null) {
                    grandParent.height = 
                            Math.max(height(grandParent.left),
                                     height(grandParent.right)) + 1;
                }
                
                return;
            } else if (height(parent.right) == height(parent.left) + 2) {
                grandParent = parent.parent;
                
                if (height(parent.right.right) >= height(parent.right.left)) {
                    subTree = leftRotate(parent);
                } else {
                    subTree = rightLeftRotate(parent);
                }
                
                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }
                
                if (grandParent != null) {
                    grandParent.height =
                            Math.max(height(grandParent.left),
                                     height(grandParent.right)) + 1;
                }
                
                return;
            }
            
            parent.height = Math.max(height(parent.left), 
                                     height(parent.right)) + 1;
            parent = parent.parent;
        }
    }
    
    private void fixAfterDeletion(Node<K, V> node) {
        Node<K, V> parent = node.parent;
        Node<K, V> grandParent;
        Node<K, V> subTree;
        
        while (parent != null) {
            if (height(parent.left) == height(parent.right) + 2) {
                grandParent = parent.parent;
                
                if (height(parent.left.left) >= height(parent.left.right)) {
                    subTree = rightRotate(parent);
                } else {
                    subTree = leftRightRotate(parent);
                }
                
                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }
                
                if (grandParent != null) {
                    grandParent.height = 
                            Math.max(height(grandParent.left),
                                     height(grandParent.right)) + 1;
                }
            } else if (height(parent.right) == height(parent.left) + 2) {
                grandParent = parent.parent;
                
                if (height(parent.right.right) >= height(parent.right.left)) {
                    subTree = leftRotate(parent);
                } else {
                    subTree = rightLeftRotate(parent);
                }
                
                if (grandParent == null) {
                    root = subTree;
                } else if (grandParent.left == parent) {
                    grandParent.left = subTree;
                } else {
                    grandParent.right = subTree;
                }
                
                if (grandParent != null) {
                    grandParent.height =
                            Math.max(height(grandParent.left),
                                     height(grandParent.right)) + 1;
                }
            }
            
            parent.height = Math.max(height(parent.left), 
                                     height(parent.right)) + 1;
            parent = parent.parent;
        }
    }
    
    private Node<K, V> deleteNode(Node<K, V> node) {
        if (node.left == null && node.right == null) {
            // 'node' has no chldren.
            Node<K, V> parent = node.parent;
            
            if (parent == null) {
                root = null;
                return node;
            }
            
            if (node == parent.left) {
                parent.left = null;
            } else {
                parent.right = null;
            }
            
            return node;
        }
        
        if (node.left == null || node.right == null) {
            // 'node' has only one chldren.
            Node<K, V> child;
            
            if (node.left != null) {
                child = node.left;
            } else {
                child = node.right;
            }
            
            Node<K, V> parent = node.parent;
            child.parent = parent;
            
            if (parent == null) {
                root = child;
                return node;
            }
            
            if (node == parent.left) {
                parent.left = child;
            } else {
                parent.right = child;
            }
            
            return node;
        }
        
        // 'node' has both children.
        K tmpKey = node.key;
        Node<K, V> successor = minimumNode(node.right);
        node.key = successor.key;
        node.value = successor.value;
        
        Node<K, V> child = successor.right;
        Node<K, V> parent = successor.parent;
        
        if (parent.left == successor) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        
        if (child != null) {
            child.parent = parent;
        }
        
//        successor.key = tmpKey;
        return successor;
    }
    
    private static <K, V> Node<K, V> minimumNode(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        
        return node;
    }
    
    private static <K, V> Node<K, V> leftRotate(Node<K, V> node1) {
        Node<K, V> node2 = node1.right;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.right = node2.left;
        node2.left = node1;
        
        if (node1.right != null) {
            node1.right.parent = node1;
        }
        
        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }
    
    private static <K, V> Node<K, V> rightRotate(Node<K, V> node1) {
        Node<K, V> node2 = node1.left;
        node2.parent = node1.parent;
        node1.parent = node2;
        node1.left = node2.right;
        node2.right = node1;
        
        if (node1.left != null) {
            node1.left.parent = node1;
        }
        
        node1.height = Math.max(height(node1.left), height(node1.right)) + 1;
        node2.height = Math.max(height(node2.left), height(node2.right)) + 1;
        return node2;
    }
    
    private static <K, V> Node<K, V> leftRightRotate(Node<K, V> node1) {
        Node<K, V> node2 = node1.left;
        node1.left = leftRotate(node2);
        return rightRotate(node1);
    }
    
    private static <K, V> Node<K, V> rightLeftRotate(Node<K, V> node1) {
        Node<K, V> node2 = node1.right;
        node1.right = rightRotate(node2);
        return leftRotate(node1);
    }
    
    private static <K, V> int height(Node<K, V> node) {
        return node == null ? -1 : node.height;
    }
}
