package com.sparrow.data.tools.imports.extract;

import java.io.File;

import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.extract.csv.CsvExtractor;
import com.sparrow.data.tools.imports.extract.excel.Excel2003Extractor;
import com.sparrow.data.tools.imports.extract.excel.Excel2007Exrator;
import com.sparrow.data.tools.imports.extract.excel.ExcelExtractor;
import com.sparrow.data.tools.store.FileType;

/**
 * 建造者模式，构造负责对象，结构清晰明了
 *
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class ExtractorBuilder {
    private final File file;
    private ImpSetting impSetting;
    private FileType type;

    private ExtractorBuilder(File file) {
        this.file = file;
    }

    public static ExtractorBuilder create(String file) {
        return create(new File(file));
    }

    public static ExtractorBuilder create(File file) {
        return new ExtractorBuilder(file);
    }

    public ExtractorBuilder fileType(FileType type) {
        this.type = type;
        return this;
    }

    void checkAndInitImpSetting() {
        if (this.impSetting == null)
            this.impSetting = new ImpSetting();
    }

    public ExtractorBuilder excelStartSheet(int startSheet) {
        this.checkAndInitImpSetting();
        this.impSetting.setStartSheet(startSheet);
        return this;
    }

    public ExtractorBuilder excelStartRow(int startRow) {
        this.checkAndInitImpSetting();
        this.impSetting.setStartRow(startRow);
        return this;
    }

    public ExtractorBuilder excelStartColumn(int startCol) {
        this.checkAndInitImpSetting();
        this.impSetting.setStartCol(startCol);
        return this;
    }

    public ExtractorBuilder maxRows(int max) {
        this.checkAndInitImpSetting();
        this.impSetting.setMaxRows(max);
        return this;
    }

    public ExtractorBuilder columnLimit(int limit) {
        this.checkAndInitImpSetting();
        this.impSetting.setLimit(limit);
        return this;
    }

    public ExtractorBuilder importSetting(ImpSetting impSetting) {
        if (impSetting != null)
            this.impSetting = impSetting;
        return this;
    }

    public DataExtractor build() {
        DataExtractor extractor;
        FileType ty = this.type;
        switch (ty) {
            case Csv:
                extractor = new CsvExtractor(file);
                break;
            case Excel:
                extractor = new ExcelExtractor(file);
                break;
            case Excel2003:
                extractor = new Excel2003Extractor(file);
                break;
            case Excel2007:
                extractor = new Excel2007Exrator(file);
                break;

            default:
                extractor = new CsvExtractor(file);
        }
        if (this.impSetting != null)
            extractor.setImpSetting(this.impSetting);
        return extractor;
    }
}
