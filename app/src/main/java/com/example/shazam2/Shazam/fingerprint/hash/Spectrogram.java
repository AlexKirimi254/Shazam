package com.example.shazam2.Shazam.fingerprint.hash;

import java.io.File;
import java.io.IOException;


import com.example.shazam2.Shazam.fingerprint.MainParameters;
import com.example.shazam2.Shazam.fingerprint.AudioFile;

public class Spectrogram {
	
	/**
	 * The sample size to use for the FFT
	 */

	
	/**
	 * Used when calculating the overlap for amplitudes
	 */
	private static final int OVERLAP_FACTOR = 2;
	
	/**
	 * The audio file that we are currently working with
	 */
	private final AudioFile audioFile;
	
	/**
	 * The spectrogram data for the audio file
	 */
	private double[][] data;
	
	/**
	 * Creates an object that can generate / render the spectrogram
	 * data for the given audio file
	 * 
	 * @param audioFile The current audio file to work with
	 */
	public Spectrogram(AudioFile audioFile) {
		this.audioFile = audioFile;
	}

	/**
	 * Generates the spectrogram for the given audio file
	 * 
	 * @return The spectrogram
	 */
	public double[][] getData() {
		if(data != null) {
			return data;
		}
		
		short[] amplitudes = audioFile.getSampleAmplitudes();
		int samples = amplitudes.length;
		
		// calculate the overlap
		if(OVERLAP_FACTOR > 1) {
			int numOverlappedSamples = samples * OVERLAP_FACTOR;
			int backSamples = MainParameters.FFT_SAMPLE_SIZE * (OVERLAP_FACTOR-1) / OVERLAP_FACTOR;
			int fftSampleSize_1= MainParameters.FFT_SAMPLE_SIZE - 1;
			short[] overlapAmp = new short[numOverlappedSamples];
			int position = 0;
			for (int i = 0; i < amplitudes.length; ++i){
				overlapAmp[position++] = amplitudes[i];
				if (position % MainParameters.FFT_SAMPLE_SIZE == fftSampleSize_1) {
					i -= backSamples;
				}
			}
			samples = numOverlappedSamples;
			amplitudes = overlapAmp;
		}
		
		// get the number of frames
		int frames = samples / MainParameters.FFT_SAMPLE_SIZE;
		
		// get the hamming window
		double[] window = getHammingWindow();
		
		// calculate the signals
		double[][] signals = new double[frames][];
		for(int f = 0; f < frames; ++f) {
			signals[f] = new double[MainParameters.FFT_SAMPLE_SIZE];
			int start = f * MainParameters.FFT_SAMPLE_SIZE;
			for(int n = 0; n <  MainParameters.FFT_SAMPLE_SIZE; ++n) {
				signals[f][n] = amplitudes[start + n] * window[n];
			}
		}
		
		// calculate the absolute spectogram
		double[][] absolute = new double[frames][];
		for(int i = 0; i < frames; ++i) {
			absolute[i] = getMagnitudes(signals[i]);
		}
		
		// normalize the absolute spectrogram
		if(absolute.length > 0) {
			
			int numFreqUnit = absolute[0].length;
			
			double[][] spectrogram = new double[frames][numFreqUnit];
			
			// calculate the maximum amplitude and minimum amplitude
			double maxAmp = Double.MIN_VALUE;
			double minAmp = Double.MAX_VALUE;
			for(int i = 0; i < frames; ++i) {
				for(int j = 0; j < numFreqUnit; ++j) {
					if(absolute[i][j] > maxAmp) {
						maxAmp = absolute[i][j];
					} else if(absolute[i][j] < minAmp) {
						minAmp = absolute[i][j];
					}
				}
			}
			
			// make sure the minAmp is greater than 0
			double minValidAmp = 0.00000000001F;
			if (minAmp == 0){
				minAmp = minValidAmp;
			}
			
			// calculate the normalized spectrogram
			double difference = Math.log10(maxAmp / minAmp);
			for(int i = 0; i < frames; ++i) {
				for(int j = 0; j < numFreqUnit; ++j) {
					if(absolute[i][j] < minValidAmp) {
						spectrogram[i][j] = 0;
					} else {
						if (j < MainParameters.MAX_FREQUENCY && j > MainParameters.MIN_FREQUENCY) {
							spectrogram[i][j] = Math.log(absolute[i][j] / minAmp) / difference;
						}else{
							spectrogram[i][j] = 1;
						}
					}
				}
			}
			this.data = spectrogram;
			return spectrogram;
		}
		return null;
	}
	
	/**
	 * Calculate the hamming window for the default FFT sample size
	 * 
	 * @return The hamming window
	 */
	private double[] getHammingWindow() {
		int m = MainParameters.FFT_SAMPLE_SIZE / 2;
		double r;
		double pi = Math.PI;
		double[] w = new double[MainParameters.FFT_SAMPLE_SIZE];
		r = pi / (m + 1);
		for (int n = -m; n < m; n++) {
			w[m + n] = 0.5f + 0.5f * Math.cos(n * r);
		}
		return w;
	}
	
	/**
	 * Get the frequency intensities
	 * 
	 * @param amplitudes amplitudes of the signal
	 * 
	 * @return intensities of each frequency unit: mag[frequency_unit] = intensity
	 */
	private double[] getMagnitudes(double[] amplitudes) {
		int sampleSize = amplitudes.length;

		// call the fft and transform the complex numbers
		new FFT(sampleSize / 2, -1).transform(amplitudes);

		// even indexes (0,2,4,6,...) are real parts
		// odd indexes (1,3,5,7,...) are imaginary parts
		int indexSize = sampleSize / 2;

		// FFT produces a transformed pair of arrays where the first half of the
		// values represent positive frequency components and the second half
		// represents negative frequency components.
		// we omit the negative ones
		int positiveSize = indexSize / 2;

		// calculate the intensities (magnitudes)
		double[] mag = new double[positiveSize];
		for (int i = 0; i < indexSize; i += 2) {
			mag[i / 2] = Math.sqrt(amplitudes[i] * amplitudes[i] + amplitudes[i + 1] * amplitudes[i + 1]);
		}
		return mag;
	}

}