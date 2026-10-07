package com.caeca.estoque.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.logging.Logger;

/**
 * Executa as migrations do Flyway depois que o Hibernate terminou de criar/atualizar o
 * schema ({@code ddl-auto=update}).
 *
 * <p>O {@code spring.flyway.enabled} fica desligado de propósito: a autoconfiguração do
 * Spring Boot roda o Flyway <b>antes</b> do Hibernate, e nesse momento a tabela
 * {@code status_maleta} ainda não existe em base nova. A migration seria gravada como
 * aplicada sem ter semeado nada e a base ficaria sem status. Como {@code ApplicationRunner}
 * roda no fim do refresh do contexto, aqui o schema já existe.
 *
 * <p>Em base já existente o Flyway cria o baseline em 0 e aplica só o que falta, então
 * subir a aplicação várias vezes não repete a carga.
 */
@Component
public class BootstrapFlyway implements ApplicationRunner {

    private static final Logger LOG = Logger.getLogger(BootstrapFlyway.class.getName());

    private static final String LOCALIZACAO_MIGRATIONS = "classpath:db/migration";

    private final DataSource dataSource;

    public BootstrapFlyway(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOG.info("Flyway: aplicando migrations de " + LOCALIZACAO_MIGRATIONS);

        Flyway.configure()
                .dataSource(dataSource)
                .locations(LOCALIZACAO_MIGRATIONS)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .load()
                .migrate();

        LOG.info("Flyway: migrations aplicadas com sucesso");
    }
}