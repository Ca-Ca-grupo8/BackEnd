package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class V3__adicionar_data_nascimento_revendedora extends BaseJavaMigration {

    private static final Logger LOG = Logger.getLogger(V3__adicionar_data_nascimento_revendedora.class.getName());

    @Override
    public void migrate(Context context) throws Exception {
        Connection conexao = context.getConnection();
        DatabaseMetaData meta = conexao.getMetaData();

        if (!existeTabela(meta, "revendedora")) {
            LOG.info("Tabela revendedora nao existe; ignorando V3.");
            return;
        }

        if (existeColuna(meta, "revendedora", "data_nascimento")) {
            LOG.info("Coluna data_nascimento ja existe; ignorando V3.");
            return;
        }

        try (Statement st = conexao.createStatement()) {
            st.execute("ALTER TABLE revendedora ADD COLUMN data_nascimento DATE NULL");
            LOG.info("Coluna data_nascimento adicionada.");
        }
    }

    private boolean existeTabela(DatabaseMetaData meta, String nome) throws SQLException {
        try (ResultSet rs = meta.getTables(null, null, null, new String[]{"TABLE"})) {
            while (rs.next()) {
                String table = rs.getString("TABLE_NAME");
                if (nome.equalsIgnoreCase(table)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean existeColuna(DatabaseMetaData meta, String tabela, String coluna) throws SQLException {
        try (ResultSet rs = meta.getColumns(null, null, null, null)) {
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                if (tabela.equalsIgnoreCase(tableName) && coluna.equalsIgnoreCase(columnName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
