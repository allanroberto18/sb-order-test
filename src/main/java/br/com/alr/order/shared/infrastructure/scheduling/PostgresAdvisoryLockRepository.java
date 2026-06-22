package br.com.alr.order.shared.infrastructure.scheduling;

import br.com.alr.order.shared.application.port.out.DistributedLockRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Repository
public class PostgresAdvisoryLockRepository implements DistributedLockRepository {

  private final JdbcTemplate jdbcTemplate;

  public PostgresAdvisoryLockRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");
  }

  @Override
  public boolean tryAcquire(String lockName) {
    Long lockKey = toLockKey(lockName);
    Boolean acquired = jdbcTemplate.queryForObject("select pg_try_advisory_lock(?)", Boolean.class, lockKey);
    return Boolean.TRUE.equals(acquired);
  }

  @Override
  public void release(String lockName) {
    jdbcTemplate.queryForObject("select pg_advisory_unlock(?)", Boolean.class, toLockKey(lockName));
  }

  private long toLockKey(String lockName) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
      byte[] digest = messageDigest.digest(lockName.getBytes(StandardCharsets.UTF_8));
      return ByteBuffer.wrap(digest, 0, Long.BYTES).getLong();
    } catch (NoSuchAlgorithmException exception) {
      throw new IllegalStateException("SHA-256 algorithm is not available", exception);
    }
  }
}
