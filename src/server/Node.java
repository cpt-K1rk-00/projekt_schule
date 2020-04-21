package server;

public class Node<T> {
 
	private T data = null;
 
	private List<Node<T>> children = new List<Node<T>>();
 
	private Node<T> parent = null;
 
	public Node(T data) {
		this.data = data;
	}
 
	public Node<T> addChild(Node<T> child) {
		child.setParent(this);
		this.children.append(child);
		return child;
	}
 
	public List<Node<T>> getChildren() {
		return children;
	}
 
	public T getData() {
		return data;
	}
 
	public void setData(T data) {
		this.data = data;
	}
 
	private void setParent(Node<T> parent) {
		this.parent = parent;
	}
 
	public Node<T> getParent() {
		return parent;
	}
}