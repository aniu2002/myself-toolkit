package com.au.cache.tools;

import java.util.Random;

public class FreeCounter {
	/**
	 * The sample size to use
	 */
	static final int DEFAULT_SAMPLE_SIZE = 15;
	static final Random RANDOM = new Random();

	private static int calculateSampleSize(int populationSize) {
		if (populationSize < DEFAULT_SAMPLE_SIZE) {
			return populationSize;
		} else {
			return DEFAULT_SAMPLE_SIZE;
		}
	}

	/**
	 * 
	 * <p>Title: generateRandomSample</p>
	 * <p>Description: 随机组织索引</p>
	 * @param populationSize
	 * @return
	 * @author Yzc
	 */
	public static int[] generateRandomSample(int populationSize) {
		int sampleSize = calculateSampleSize(populationSize);
		int[] offsets = new int[sampleSize];

		if (sampleSize != 0) {
			int maxOffset = 0;
			maxOffset = populationSize / sampleSize;
			for (int i = 0; i < sampleSize; i++) {
				offsets[i] = RANDOM.nextInt(maxOffset);
			}
		}
		return offsets;
	}
}
