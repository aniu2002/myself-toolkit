package com.sparrow.common.statistic;

import com.sparrow.common.statistic.data.StatisticData;

public class StatisticBuilder {
	private String caption;
	private String xLabel;
	private String yLabel;

	private StatisticBuilder(String caption) {
		this.caption = caption;
	}

	public static StatisticBuilder create(String caption) {
		return new StatisticBuilder(caption);
	}

	public StatisticBuilder xLabel(String xLabel) {
		this.xLabel = xLabel;
		return this;
	}

	public StatisticBuilder yLabel(String yLabel) {
		this.yLabel = yLabel;
		return this;
	}

	public StatisticData build() {
		StatisticData vo = new StatisticData();
		vo.setCaption(this.caption);
		vo.setXdLabel(this.xLabel);
		vo.setYdLabel(this.yLabel);
		return vo;
	}
}
