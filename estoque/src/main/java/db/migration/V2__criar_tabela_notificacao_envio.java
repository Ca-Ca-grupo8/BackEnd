package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class V2__criar_tabela_notificacao_envio extends BaseJavaMigration {

    private static final Logger LOG = Logger.getLogger(V2__criar_tabela_notificacao_envio.class.getName());

    private static final String TABELA = "notificacao_envio";

    @Override
    public void migrate(Context context) throws Exception {
        Connection conexao = context.getConnection();

        if (tabelaExiste(conexao.getMetaData(), TABELA)) {
            LOG.info("Tabela " + TABELA + " ja existe; criacao ignorada.");
            return;
        }

        try (Statement st = conexao.createStatement()) {
            st.execute(
                    "CREATE TABLE " + TABELA + " ("
                            + "id_notificacao_envio BIGINT NOT NULL AUTO_INCREMENT, "
                            + "maleta_id BIGINT NOT NULL, "
                            + "template_id BIGINT NOT NULL, "
                            + "data_envio DATETIME NOT NULL, "
                            + "PRIMARY KEY (id_notificacao_envio), "
                            + "CONSTRAINT uk_notificacao_envio_maleta_template "
                            + "UNIQUE (maleta_id, template_id)"
                            + ")"
            );
        }

        LOG.info("Tabela " + TABELA + " criada.");
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
}