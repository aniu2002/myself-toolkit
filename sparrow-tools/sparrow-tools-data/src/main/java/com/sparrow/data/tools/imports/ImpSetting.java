package com.sparrow.data.tools.imports;

public class ImpSetting {
	/** 导入导出时数据填充从哪个电子薄开始 */
	private int startSheet;
	/** 导入导出时数据填充从哪行开始 */
	private int startRow;
	private int startCol;
	private int limit;
    private int maxRows;

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getStartCol() {
		return startCol;
	}

	public void setStartCol(int startCol) {
		this.startCol = startCol;
	}

	public int getStartSheet() {
		return startSheet;
	}

	public void setStartSheet(int startSheet) {
		this.startSheet = startSheet;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
}
