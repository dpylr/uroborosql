package jp.co.future.uroborosql.dialect;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import org.junit.Test;

import jp.co.future.uroborosql.connection.ConnectionSupplier;

/**
 * Oracle10Dialectの個別実装部分のテストケース
 *
 * @author H.Sugimoto
 *
 */
public class Oracle10DialectTest {

	@Test
	public void testAccept10() {
		ConnectionSupplier supplier = new ConnectionSupplier() {

			@Override
			public Connection getConnection(final String alias) {
				return null;
			}

			@Override
			public Connection getConnection() {
				return null;
			}

			@Override
			public String getDatabaseName() {
				return "Oracle-10.1";
			}
		};

		Dialect dialect = StreamSupport.stream(ServiceLoader.load(Dialect.class).spliterator(), false)
				.filter(d -> d.accept(supplier)).findFirst().orElseGet(DefaultDialect::new);

		assertThat(dialect, instanceOf(Oracle10Dialect.class));
	}

	@Test
	public void testAcceptUnder10() {
		ConnectionSupplier supplier = new ConnectionSupplier() {

			@Override
			public Connection getConnection(final String alias) {
				return null;
			}

			@Override
			public Connection getConnection() {
				return null;
			}

			@Override
			public String getDatabaseName() {
				return "Oracle-9.1";
			}
		};

		Dialect dialect = StreamSupport.stream(ServiceLoader.load(Dialect.class).spliterator(), false)
				.filter(d -> d.accept(supplier)).findFirst().orElseGet(DefaultDialect::new);

		assertThat(dialect, instanceOf(Oracle10Dialect.class));
	}

	@Test
	public void testAcceptOver10() {
		ConnectionSupplier supplier = new ConnectionSupplier() {

			@Override
			public Connection getConnection(final String alias) {
				return null;
			}

			@Override
			public Connection getConnection() {
				return null;
			}

			@Override
			public String getDatabaseName() {
				return "Oracle-11.1";
			}
		};

		Dialect dialect = StreamSupport.stream(ServiceLoader.load(Dialect.class).spliterator(), false)
				.filter(d -> d.accept(supplier)).findFirst().orElseGet(DefaultDialect::new);

		assertThat(dialect, not(instanceOf(Oracle10Dialect.class)));
	}

	@Test
	public void testEscapeLikePattern() {
		Dialect dialect = new Oracle10Dialect();
		assertThat(dialect.escapeLikePattern(""), is(""));
		assertThat(dialect.escapeLikePattern("pattern"), is("pattern"));
		assertThat(dialect.escapeLikePattern("%pattern"), is("\\%pattern"));
		assertThat(dialect.escapeLikePattern("_pattern"), is("\\_pattern"));
		assertThat(dialect.escapeLikePattern("pat%tern"), is("pat\\%tern"));
		assertThat(dialect.escapeLikePattern("pat_tern"), is("pat\\_tern"));
		assertThat(dialect.escapeLikePattern("pattern%"), is("pattern\\%"));
		assertThat(dialect.escapeLikePattern("pattern_"), is("pattern\\_"));
		assertThat(dialect.escapeLikePattern("pat[]tern"), is("pat[]tern"));
		assertThat(dialect.escapeLikePattern("pat％tern"), is("pat％tern"));
		assertThat(dialect.escapeLikePattern("pat＿tern"), is("pat＿tern"));
	}

}