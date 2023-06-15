package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvExporter {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CsvExporter(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<String> queryTableColumns(String tableName) throws SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            List<String> columns = new ArrayList<>();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                columns.add(columnName);
            }
            return columns;
        }
    }

    public void exportTableToCsv(String tableName) throws IOException, SQLException {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            List<String> columns = new ArrayList<>();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                columns.add(columnName);
            }


            String query = "SELECT * FROM " + tableName;
            List<Object[]> rows = jdbcTemplate.query(query, new Object[]{}, (rs, rowNum) -> {
                int columnCount = rs.getMetaData().getColumnCount();
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                return row;
            });

            try (FileWriter writer = new FileWriter(tableName + ".csv")) {
                // 写入标题
                for(int i=0; i<columns.size(); i++) {
                    if (i > 0) {
                        writer.append(",");
                    }
                    writer.append(columns.get(i));
                }
                writer.append("\n");
                // 写入数据
                for (Object[] row : rows) {
                    for (int i = 0; i < row.length; i++) {
                        if (i > 0) {
                            writer.append(",");
                        }
                        writer.append(row[i] == null ? "" : row[i].toString());
                    }
                    writer.append("\n");
                }
            }
        }
    }
}