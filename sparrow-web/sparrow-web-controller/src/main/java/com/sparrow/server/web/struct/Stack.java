package com.sparrow.server.web.struct;

public class Stack {
	private Element top;
	private int size;

	public Stack() {
		super();
		this.top = new Element();
		this.size = 0;
	}

	public int getSize() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Object getTop() throws Exception {
		if (isEmpty())
			throw new Exception("错误：栈为空！");
		return top.getNext().getElem();
	}

	public Object pop() throws Exception {
		if (isEmpty())
			throw new Exception("错误：栈为空！");
		Element first = top.getNext();
		Element second = first.getNext();
		top.setNext(second);
		size--;
		return first.getElem();
	}

	public void push(Object obj) {
		Element first = top.getNext();
		Element newNode = new Element(obj, first);
		top.setNext(newNode);
		size++;
	}

	public void clear() {
		top.setNext(null);
		size = 0;
	}

	public void showMe() {
		Element node = top.getNext();
		while (node.getNext() != null) {
			System.out.print(node.getElem() + "->");
			node = node.getNext();
		}
		System.out.println(node.getElem());
	}
}