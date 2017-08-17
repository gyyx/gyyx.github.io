package cn.gyyx.test.binarytree;

/**
 * 保留
 * @author gy
 *
 */
public class Main {
    
    
    /**
     * 保留
     * @author gy
     *
     */
    public static class Node{
        public Node(int value) {
            this.value = value;
        }
        
        public int value;
        public Node left;
        public Node right;
        
        @Override
        public String toString() {
            return "Node [value=" + value + "]";
        }
    }

    /**
     * 清理内容，保留题干
     * @param args
     */
    public static void main(String[] args) {
        

    }

    /**
     * 保留
     * @param node
     * @return
     */
    private static int depth(Node node){
        if(node == null){
            return 0;
        }
        
        return 1 + Math.max(depth(node.left), depth(node.right));
    }

    /**
     * 保留：前序遍历
     * @param node
     */
    private static void preOrderTraversal(Node node){
        if(node == null){
            return;
        }
        System.out.print(String.format("%d,", node.value));
        preOrderTraversal(node.left);
        preOrderTraversal(node.right);
    }
    
    /**
     * 保留：中序遍历
     * @param node
     */
    private static void inOrderTraversal(Node node){
        if(node == null){
            return;
        }
        inOrderTraversal(node.left);
        System.out.print(String.format("%d,", node.value));
        inOrderTraversal(node.right);
    }
    
    /**
     * 保留：后序遍历
     * @param node
     */
    private static void postOrderTraversal(Node node){
        if(node == null){
            return;
        }
        postOrderTraversal(node.left);
        postOrderTraversal(node.right);
        System.out.print(String.format("%d,", node.value));
    }
}
