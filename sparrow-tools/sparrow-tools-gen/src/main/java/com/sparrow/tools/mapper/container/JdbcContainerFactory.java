package com.sparrow.tools.mapper.container;

public class JdbcContainerFactory extends ContainerFactory {
	final Container container = new JdbcConnContainer();

	public Container getContainer() {
		return this.container;
	}

}
