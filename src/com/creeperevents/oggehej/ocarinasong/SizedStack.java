package com.creeperevents.oggehej.ocarinasong;

import java.util.Stack;

public class SizedStack<T> extends Stack<T>
{
	private static final long serialVersionUID = 2864617517718853345L;
	private int maxSize = 7;
	protected ONote[] elementData;

	public SizedStack()
	{
		super();
	}

	@Override
	public T push(T object)
	{
		while (this.size() >= maxSize)
			this.remove(0);

		return super.push(object);
	}
}
