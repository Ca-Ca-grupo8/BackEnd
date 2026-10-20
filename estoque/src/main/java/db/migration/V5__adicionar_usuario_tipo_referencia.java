package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class V5__adicionar_usuario_tipo_referencia extends BaseJavaMigration {

    private static final Logger LOG = Logger.getLogger(V5__adicionar_usuario_tipo_referencia.class.getName());

    private static final String TABELA = "notificacao_envio";
    private static final String COLUNA = "tipo_referencia";

    @Override
    public void migrate(Context context) throws Exception {
        Connection conexao = context.getConnection();

        if (!tabelaExiste(conexao.getMetaData(), TABELA)) {
            LOG.info("Tabela " + TABELA + " nao existe; migration ignorada.");
            return;
        }

        if (!colunaExiste(conexao.getMetaData(), TABELA, COLUNA)) {
            LOG.info("Coluna " + COLUNA + " nao existe em " + TABELA + "; migration ignorada.");
            return;
        }

        try (Statement st = conexao.createStatement()) {
            st.execute(
                    "ALTER TABLE " + TABELA
                            + " MODIFY COLUMN " + COLUNA
                            + " ENUM('ESTOQUE','MALETA','REVENDEDORA','USUARIO') NOT NULL"
            );
        }

        LOG.info("Coluna " + COLUNA + " atualizada para incluir USUARIO em " + TABELA + ".");
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
