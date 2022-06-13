package com.example.shazam2.Shazam.fingerprint;

import com.example.shazam2.Shazam.fingerprint.hash.FingerPrint;
import com.example.shazam2.Shazam.fingerprint.hash.Spectrogram;
import com.example.shazam2.Shazam.fingerprint.util.Hash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Represents an audio file. Converts the given file into
 * a wav file and then calculates the spectrogram and fingerprint
 * for the wav file.
 * 
 * @author Derek Honerlaw <honerlawd@gmail.com>
 */
public class AudioFile {
	
	/**
	 * The hash calculated from the content of the original audio file
	 */
	private final String fileHashString;
	
	/**
	 * The audio file that we are working with
	 */
	private final File file;
	
	/**
	 * The path to the converted wav file
	 */
	private final String wavFilePath;
	
	/**
	 * The audio file buffer for the given file
	 */
	private final AudioFileBuffer buffer;
	
	/**
	 * The audio file header information (wav header information)
	 */
	private final AudioFileHeader header;
	
	/**
	 * The fingerprint utility class
	 */
	private final FingerPrint fingerPrint;
	
	/**
	 * The spectrogram utility class
	 */
	private final Spectrogram spectrogram;


	public AudioFile(File file) throws Exception {
		
		// calculate the hash for the file name
		this.fileHashString = Hash.toHex(Hash.calculate(new FileInputStream(file)));
		
		// try and find and load the file
		this.file = file;
		if(!this.file.exists()) {
			throw new FileNotFoundException();
		}
		this.wavFilePath = file.getAbsolutePath();

		
		// load the file into the audio file buffer
		this.buffer = new AudioFileBuffer(this);
		
		// read the header information from the audio file
		this.header = new AudioFileHeader(this.buffer);
		
		// initialize the fingerprint utility class
		this.fingerPrint = new FingerPrint(this);
		
		// initialize the spectrogram utility class
		this.spectrogram = new Spectrogram(this);
	}
	

	/**
	 * 
	 * @return The file hash as a string
	 */
	public String getFileHashString() {
		return fileHashString;
	}
	
	/**
	 * 
	 * @return The WAV file path
	 */
	public String getWAVFilePath() {
		return wavFilePath;
	}
	
	/**
	 * 
	 * @return The header information for the given WAV file
	 */
	public AudioFileHeader getHeader() {
		return header;
	}
	
	/**
	 * 
	 * @return The buffer containing the WAV file contents
	 */
	public AudioFileBuffer getBuffer() {
		return buffer;
	}
	
	/**
	 * 
	 * @return The generated fingerprint for the WAV file
	 */
	public FingerPrint getFingerPrint() {
		return fingerPrint;
	}
	
	/**
	 * 
	 * @return The generated spectrogram for the WAV file
	 */
	public Spectrogram getSpectrogram() {
		return spectrogram;
	}
	
	/**
	 * Calculates the sample amplitudes from the WAV file data
	 * 
	 * @return The sample amplitudes of the wav file
	 */
	public short[] getSampleAmplitudes() {
		int bytePerSample = getHeader().getBitsPerSample() / 8;
		int samples = getBuffer().size() / bytePerSample;
		short[] amplitudes = new short[samples];
		int position = 0;
		for(int i = 0; i < samples; ++i) {
			short amplitude = 0;
			for(int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) {
				amplitude |= (short) ((getBuffer().get(position++) & 0xFF) << (byteNumber * 8));
			}
			amplitudes[i] = amplitude;
		}
		return amplitudes;
	}
	
}
