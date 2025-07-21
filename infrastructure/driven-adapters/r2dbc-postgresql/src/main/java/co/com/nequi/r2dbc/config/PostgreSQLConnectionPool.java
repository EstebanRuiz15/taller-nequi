package co.com.nequi.r2dbc.config;

import java.time.Duration;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Option;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PostgresqlConnectionProperties.class)
public class PostgreSQLConnectionPool {
	// TODO: change pool connection properties based on your resources.
	public static final int INITIAL_SIZE = 12;
	public static final int MAX_SIZE = 15;
	public static final int MAX_IDLE_TIME = 30;


	@Bean
	public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties pgProperties) {
		return buildConnectionConfiguration(pgProperties);
	}

	private ConnectionPool buildConnectionConfiguration(PostgresqlConnectionProperties properties) {
		// Configurar ConnectionFactory 
		var builder = io.r2dbc.spi.ConnectionFactoryOptions.builder()
				.option(io.r2dbc.spi.ConnectionFactoryOptions.DRIVER, "postgresql")
				.option(io.r2dbc.spi.ConnectionFactoryOptions.HOST, properties.getHost())
				.option(io.r2dbc.spi.ConnectionFactoryOptions.PORT, properties.getPort())
				.option(io.r2dbc.spi.ConnectionFactoryOptions.DATABASE, properties.getDatabase())
				.option(io.r2dbc.spi.ConnectionFactoryOptions.USER, properties.getUsername())
				.option(io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD, properties.getPassword());
		
		// Solo habilitar SSL para RDS o producción
		if (properties.getHost().contains("rds.amazonaws.com") || 
		    properties.getHost().contains("prod")) {
			builder.option(io.r2dbc.spi.ConnectionFactoryOptions.SSL, true)
				   .option(Option.valueOf("sslMode"), "require")
				   .option(Option.valueOf("sslRootCert"), "");
		} else {
			// Para desarrollo local deshabilitar SSL
			builder.option(io.r2dbc.spi.ConnectionFactoryOptions.SSL, false);
		}
		
		ConnectionFactory connectionFactory = ConnectionFactories.get(builder.build());

		ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
				.connectionFactory(connectionFactory)
				.name("api-postgres-connection-pool")
				.initialSize(INITIAL_SIZE)
				.maxSize(MAX_SIZE)
				.maxIdleTime(Duration.ofMinutes(MAX_IDLE_TIME))
				.validationQuery("SELECT 1")
				.build();

		return new ConnectionPool(poolConfiguration);
	}
}
