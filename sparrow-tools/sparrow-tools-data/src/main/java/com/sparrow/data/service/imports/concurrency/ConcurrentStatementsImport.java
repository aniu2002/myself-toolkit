package com.sparrow.data.service.imports.concurrency;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportColumn;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.concurrency.ConcurrencyJobManager;
import com.sparrow.data.tools.imports.reader.DataReader;
import com.sparrow.data.tools.sql.ParsedSql;
import com.sparrow.data.tools.sql.SqlTool;

/**
 * @author YZC
 * @version 1.0 (2014-3-23)
 * @modify
 */
public class ConcurrentStatementsImport {
	private final ParsedSql parsedSql;
	private final ImportColumn parasRelations[];
	private final Map<String, Object> gloabParas;

	private Connection connection;
	private DataReader reader;

	public ConcurrentStatementsImport(ImportTemplate importTemplate) {
		this(importTemplate, null);
	}

	public ConcurrentStatementsImport(ImportTemplate importTemplate,
			Map<String, Object> gloabParas) {
		this(importTemplate,
				SqlTool.parseSqlStatement(importTemplate.getSql()), gloabParas);
	}

	public ConcurrentStatementsImport(ImportTemplate importTemplate,
			ParsedSql parsedSql, Map<String, Object> gloabParas) {
		this.parsedSql = parsedSql;
		this.parasRelations = ImportConfigHelper.getParaRelations(
				importTemplate, parsedSql.getParameters(), null, null);
		this.gloabParas = gloabParas;
	}

	public DataReader getReader() {
		return reader;
	}

	public void setReader(DataReader reader) {
		this.reader = reader;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	ConcurrentStatementsImportJob createImportJob(List<Object> data) {
		ConcurrentStatementsImportJob importJob = new ConcurrentStatementsImportJob(
				this.parsedSql.getActualSql(), this.parasRelations,
				this.gloabParas);
		importJob.setData(data);
		importJob.setConnection(this.connection);
		return importJob;
	}

	public void doImport() {
		int jobs = 5;
		List<Object> data = null;
		DataReader reader = this.reader;
		ConcurrentStatementsImportJob impJobs[] = new ConcurrentStatementsImportJob[jobs];
		int idx = 0;
		try {
			reader.open();
			while ((data = reader.read(300)) != null) {
				impJobs[idx++] = this.createImportJob(data);
				if (idx == jobs) {
					ConcurrencyJobManager.getImportJobManager().submitAndWait(
							impJobs);
					this.connection.commit();
					idx = 0;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
