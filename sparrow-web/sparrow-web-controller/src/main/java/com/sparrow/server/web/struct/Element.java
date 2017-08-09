package com.sparrow.server.web.struct;

public class Element implements Position {
	private Object element;
	private Element next;

	public Element(Object element, Element next) {
		super();
		this.element = element;
		this.next = next;
	}

	public Element() {
		this(null, null);
	}

	@Override
	public Object getElem() {
		return this.element;
	}

	@Override
	public Object setElem(Object obj) {
		Object oldElem = this.element;
		this.element = obj;
		return oldElem;
	}

	public void setNext(Element next) {
		this.next = next;
	}

	public Element getNext() {
		return next;
	}
}