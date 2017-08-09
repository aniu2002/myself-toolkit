package com.sparrow.collect.task.gif;

import com.sparrow.collect.cache.bloom.UrlCheck;
import com.sparrow.collect.cache.bloom.UrlCheck4Guava;
import com.sparrow.collect.orm.extractor.ResultSetHandler;
import com.sparrow.collect.orm.jdbc.DataSourceConnectionFactory;
import com.sparrow.collect.orm.session.Session;
import com.sparrow.collect.persist.PersistConfig;
import com.sparrow.collect.persist.stor.FileDataSqlStore;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.JsonMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/7/30 0030.
 */
public class SaveDownData {
    public static void main(String args[]) {
        writeBloomData();
    }

    static void saveRecord() {
        PersistConfig config = JsonMapper.bean(FileIOUtil.readString("classpath:persist-config.json"),
                PersistConfig.class);
        FileDataSqlStore sqlStore = new FileDataSqlStore(config);
        sqlStore.initialize();
        BufferedReader reader = FileIOUtil.getBufferedReader(new File("D:\\fanhao\\extract\\ad.txt"), FileIOUtil.DEFAULT_ENCODING);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isEmpty(line.trim()))
                    continue;
                sqlStore.save(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
        sqlStore.close();
    }

    static void writeBloomData() {
        PersistConfig config = JsonMapper.bean(FileIOUtil.readString("classpath:persist-config.json"),
                PersistConfig.class);
        Session session = new Session(new DataSourceConnectionFactory(config.getProps()));

        final UrlCheck urlCheck = UrlCheck4Guava.getInstance("D:\\fanhao\\extract");
        session.query("select icons from gif_info", new ResultSetHandler() {
            @Override
            public void handle(ResultSet rs) throws SQLException {
                String url = rs.getString("icons");
                System.out.println(url);
                urlCheck.add(url);
            }
        });
        urlCheck.close();
    }
}
