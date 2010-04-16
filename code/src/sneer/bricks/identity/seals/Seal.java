package sneer.bricks.identity.seals;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.ram.arrays.ImmutableByteArray;

public class Seal extends Hash {

	public Seal(ImmutableByteArray bytes_) {
		super(bytes_);
	}
	
}