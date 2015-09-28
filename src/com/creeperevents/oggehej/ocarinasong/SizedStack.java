package com.creeperevents.oggehej.ocarinasong;

import java.util.Stack;

public class SizedStack<T> extends Stack<T> {
	private static int maxSize;

	private static final long serialVersionUID = 2864617517718853345L;
	protected ONote[] elementData;

	public SizedStack() {
		super();
	}

	/**
	 * Check for the largest amount of notes in any song
	 */
	static {
		for(Songs song : Songs.values())
			if(song.notes.length > maxSize)
				maxSize = song.notes.length;
	}

	@Override
	public T push(T object) {
		while (this.size() >= maxSize)
			this.remove(0);

		return super.push(object);
	}
}
