package com.example.shazam2.Shazam.fingerprint.hash.peak;

import com.example.shazam2.Shazam.fingerprint.util.Hash;

/**
 * Represents a two peaks in a given audio file
 * with the ability to hash the two peaks
 * 
 * @author Derek Honerlaw <honerlawd@gmail.com>
 */
public final class HashedPeak {
	
	private final Peak one;
	private final Peak two;
	private final int delta;
	private final int time;
	
	public HashedPeak(Peak one, Peak two, int delta, int time) {
		this.one = one;
		this.two = two;
		this.delta = delta;
		this.time = time;
	}
	
	public Peak getPeakOne() {
		return one;
	}
	
	public Peak getPeakTwo() {
		return two;
	}
	
	public int getDelta() {
		return delta;
	}

	public int getTime() {return time;}
	
	public byte[] getHash() {
		return Hash.calculate(one.getFreq() + "|" + two.getFreq() + "|" + delta);
	}
	
	public String getHashAsHex() {
		return Hash.toHex(getHash());
	}
	
}