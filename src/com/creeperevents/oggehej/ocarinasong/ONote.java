package com.creeperevents.oggehej.ocarinasong;

public enum ONote {
	UP(20),
	DOWN(11),
	LEFT(17),
	RIGHT(15),
	A(8),
	REST(-1);

	private int num;

	private ONote(int num) {
		this.num = num;
	}

	/**
	 * Return the pitch (float between 0 and 2) for the note
	 * @return Pitch
	 */
	public float getPitch() {
		return toPitch(num);
	}

	/**
	 * Return the pitch (a float between 0 and 2) of a {@code NoteBlock} setting
	 * @param num An integer between 0 and 24
	 * @return Pitch
	 */
	public static float toPitch(int num) {
		switch (num) {
		case 0: return 0.5F;
		case 1: return 0.53F;
		case 2: return 0.56F;
		case 3: return 0.6F;
		case 4: return 0.63F;
		case 5: return 0.67F;
		case 6: return 0.7F;
		case 7: return 0.76F;
		case 8: return 0.8F;
		case 9: return 0.84F;
		case 10: return 0.9F;
		case 11: return 0.94F;
		case 12: return 1.0F;
		case 13: return 1.06F;
		case 14: return 1.12F;
		case 15: return 1.18F;
		case 16: return 1.26F;
		case 17: return 1.34F;
		case 18: return 1.42F;
		case 19: return 1.5F;
		case 20: return 1.6F;
		case 21: return 1.68F;
		case 22: return 1.78F;
		case 23: return 1.88F;
		case 24: return 2.0F;
		default: return 0.0F;
		}
	}
}
