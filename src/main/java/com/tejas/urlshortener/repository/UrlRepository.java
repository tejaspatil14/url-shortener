package com.tejas.urlshortener.repository;

import com.tejas.urlshortener.model.UrlMapping;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Optional;

// Only this class talks to PostgreSQL (through JdbcTemplate)
@Repository
public class UrlRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<UrlMapping> mapper = (rs, rowNum) -> new UrlMapping(
            rs.getLong("id"),
            rs.getString("original_url"),
            rs.getString("short_code"),
            rs.getLong("click_count"));

    public UrlRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Inserts the long URL and returns the auto-generated id. */
    public long insert(String originalUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO urls (original_url) VALUES (?)",
                    new String[]{"id"});   // ask only for the id column
            ps.setString(1, originalUrl);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void updateShortCode(long id, String shortCode) {
        jdbc.update("UPDATE urls SET short_code = ? WHERE id = ?", shortCode, id);
    }

    public Optional<UrlMapping> findByShortCode(String shortCode) {
        return jdbc.query("SELECT * FROM urls WHERE short_code = ?", mapper, shortCode)
                .stream().findFirst();
    }

    public void incrementClicks(long id) {
        jdbc.update("UPDATE urls SET click_count = click_count + 1 WHERE id = ?", id);
    }
}
