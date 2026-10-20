package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class V4__adicionar_status_usuario extends BaseJavaMigration {

    private static final Logger LOG = Logger.getLogger(V4__adicionar_status_usuario.class.getName());

    private static final String TABELA = "usuario";
    private static final String COLUNA = "status";

    @Override
    public void migrate(Context context) throws Exception {
        Connection conexao = context.getConnection();

        if (!tabelaExiste(conexao.getMetaData(), TABELA)) {
            LOG.info("Tabela " + TABELA + " nao existe; migration ignorada.");
            return;
        }

        try (Statement st = conexao.createStatement()) {
            if (!colunaExiste(conexao.getMetaData(), TABELA, COLUNA)) {
                st.execute(
                        "ALTER TABLE " + TABELA
                                + " ADD COLUMN " + COLUNA + " VARCHAR(20) NULL"
                );
                LOG.info("Coluna " + COLUNA + " adicionada em " + TABELA + ".");
            }

            st.execute(
                    "UPDATE " + TABELA
                            + " SET " + COLUNA + " = 'ATIVO'"
                            + " WHERE " + COLUNA + " IS NULL"
            );
        }

        LOG.info("Usuarios existentes sem status marcados como ATIVO em " + TABELA + ".");
    }

    private boolean tabelaExiste(DatabaseMetaData meta, String tabela) throws SQLException {
        try (ResultSet rs = meta.getTables(null, null, "%", null)) {
            while (rs.next()) {
                if (tabela.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean colunaExiste(DatabaseMetaData meta, String tabela, String coluna) throws SQLException {
        try (ResultSet rs = meta.getColumns(null, null, null, null)) {
            while (rs.next()) {
                if (tabela.equalsIgnoreCase(rs.getString("TABLE_NAME"))
                        && coluna.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }
}
