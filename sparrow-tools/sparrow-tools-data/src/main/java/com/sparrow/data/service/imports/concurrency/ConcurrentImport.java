package com.sparrow.data.service.imports.concurrency;

import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportColumn;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.concurrency.ConcurrencyJobManager;
import com.sparrow.data.tools.imports.reader.DataReader;
import com.sparrow.data.tools.sql.ParsedSql;
import com.sparrow.data.tools.sql.SqlTool;
import com.sparrow.orm.template.simple.OperateTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author YZC
 * @version 1.0 (2014-3-23)
 * @modify
 */
public class ConcurrentImport {
	private final ParsedSql parsedSql;
	private final ImportColumn parasRelations[];
	private final Map<String, Object> gloabParas;

	private DataReader reader;

	public ConcurrentImport(ImportTemplate importTemplate) {
		this(importTemplate, null);
	}

	public ConcurrentImport(ImportTemplate importTemplate,
			Map<String, Object> gloabParas) {
		this(importTemplate,
				SqlTool.parseSqlStatement(importTemplate.getSql()), gloabParas);
	}

	public ConcurrentImport(ImportTemplate importTemplate, ParsedSql parsedSql,
			Map<String, Object> gloabParas) {
		this.parsedSql = parsedSql;
		this.parasRelations = ImportConfigHelper.getParaRelations(
				importTemplate, parsedSql.getParameters(), null, null);
		this.gloabParas = gloabParas;
	}
    private OperateTemplate operateTemplate;

    public OperateTemplate getOperateTemplate() {
        return operateTemplate;
    }

    public void setOperateTemplate(OperateTemplate operateTemplate) {
        this.operateTemplate = operateTemplate;
    }


	public DataReader getReader() {
		return reader;
	}

	public void setReader(DataReader reader) {
		this.reader = reader;
	}

	ConcurrentImportJob createImportJob(List<Object> data) {
		ConcurrentImportJob importJob = new ConcurrentImportJob(
				this.parsedSql.getActualSql(), this.parasRelations,
				this.gloabParas);
		importJob.setData(data);
		importJob.setOperateTemplate(this.operateTemplate);
		return importJob;
	}

	public void doImport() {
		int jobs = 5;
		List<Object> data = null;
		DataReader reader = this.reader;
		ConcurrentImportJob impJobs[] = new ConcurrentImportJob[jobs];
		int idx = 0;
		try {
			reader.open();
			while ((data = reader.read(300)) != null) {
				impJobs[idx++] = this.createImportJob(data);
				if (idx == jobs) {
					ConcurrencyJobManager.getImportJobManager().submitAndWait(
							impJobs);
					idx = 0;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
