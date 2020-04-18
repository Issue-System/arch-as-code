package net.trilogy.arch.adapter;

import net.trilogy.arch.domain.ArchitectureDataStructure;
import org.h2.Driver;
import org.h2.tools.Csv;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class DatabaseAdapter {
    private final Connection connection;

    private DatabaseAdapter() throws SQLException, IOException {
        connection = Driver.load().connect("jdbc:h2:mem:aac", new Properties());
        initializeSchema();
    }

    private void initializeSchema() throws SQLException, IOException {
        URL resource = this.getClass().getClassLoader().getResource("schema/initialize_c4_model_schema.sql");
        String path = Objects.requireNonNull(resource).getPath();
        String queries = Files.readString(Paths.get(path), Charset.defaultCharset());
        connection.createStatement().execute(queries);
    }

    public static DatabaseAdapter load() throws SQLException, IOException {
        return new DatabaseAdapter();
    }

    public void write(ArchitectureDataStructure dataStructure) throws SQLException {
        connection.createStatement().executeUpdate("INSERT INTO hello_world_table VALUES (1, 'HELLO, WORLD!')");
    }

    public File export(File toCsvFile) throws SQLException {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM hello_world_table");
        new Csv().write(toCsvFile.getAbsolutePath(), resultSet, "UTF-8");
        return toCsvFile;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
