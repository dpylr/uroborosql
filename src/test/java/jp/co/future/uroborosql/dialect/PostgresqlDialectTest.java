package jp.co.future.uroborosql.dialect;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.JDBCType;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

import org.junit.Test;

import jp.co.future.uroborosql.connection.ConnectionSupplier;
import jp.co.future.uroborosql.mapping.JavaType;

/**
 * PostgresqlDialectの個別実装部分のテストケース
 *
 * @author H.Sugimoto
 *
 */
public class PostgresqlDialectTest {

	@Test
	public void testAccept12() {
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
				return "PostgreSQL";
			}
		};

		Dialect dialect = StreamSupport.stream(ServiceLoader.load(Dialect.class).spliterator(), false)
				.filter(d -> d.accept(supplier)).findFirst().orElseGet(DefaultDialect::new);

		assertThat(dialect, instanceOf(PostgresqlDialect.class));
	}

	@Test
	public void testEscapeLikePattern() {
		Dialect dialect = new PostgresqlDialect();
		assertThat(dialect.escapeLikePattern(""), is(""));
		assertThat(dialect.escapeLikePattern("pattern"), is("pattern"));
		assertThat(dialect.escapeLikePattern("%pattern"), is("$%pattern"));
		assertThat(dialect.escapeLikePattern("_pattern"), is("$_pattern"));
		assertThat(dialect.escapeLikePattern("pat%tern"), is("pat$%tern"));
		assertThat(dialect.escapeLikePattern("pat_tern"), is("pat$_tern"));
		assertThat(dialect.escapeLikePattern("pattern%"), is("pattern$%"));
		assertThat(dialect.escapeLikePattern("pattern_"), is("pattern$_"));
		assertThat(dialect.escapeLikePattern("pat[]tern"), is("pat[]tern"));
		assertThat(dialect.escapeLikePattern("pat％tern"), is("pat％tern"));
		assertThat(dialect.escapeLikePattern("pat＿tern"), is("pat＿tern"));
	}

	@Test
	public void testSupports() {
		Dialect dialect = new PostgresqlDialect();
		assertThat(dialect.supportsBulkInsert(), is(true));
		assertThat(dialect.supportsLimitClause(), is(true));
		assertThat(dialect.supportsNullValuesOrdering(), is(true));
		assertThat(dialect.isRemoveTerminator(), is(true));
		assertThat(dialect.isRollbackToSavepointBeforeRetry(), is(true));
	}

	@Test
	public void testGetLimitClause() {
		Dialect dialect = new PostgresqlDialect();
		assertThat(dialect.getLimitClause(3, 5), is("LIMIT 3 OFFSET 5" + System.lineSeparator()));
		assertThat(dialect.getLimitClause(0, 5), is("OFFSET 5" + System.lineSeparator()));
		assertThat(dialect.getLimitClause(3, 0), is("LIMIT 3 " + System.lineSeparator()));
		assertThat(dialect.getLimitClause(0, 0), is(""));
	}

	@Test
	public void testGetJavaType() {
		Dialect dialect = new PostgresqlDialect();
		assertEquals(dialect.getJavaType(JDBCType.OTHER, "json").getClass(), JavaType.of(String.class).getClass());
		assertEquals(dialect.getJavaType(JDBCType.OTHER, "jsonb").getClass(), JavaType.of(String.class).getClass());
		assertEquals(dialect.getJavaType(JDBCType.OTHER, "other").getClass(), JavaType.of(Object.class).getClass());
	}

}
