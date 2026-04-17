package com.esb.appback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Ensures critical TEXT columns are not truncated to VARCHAR(255).
 * Hibernate's ddl-auto=update does not alter existing column types,
 * so we do it explicitly on startup (idempotent – safe to run repeatedly).
 */
@Component
public class DatabaseMigration implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("[DatabaseMigration] Starting column type migration...");
        alterColumnToText("article", "image");
        alterColumnToText("article", "qr_code");
        alterColumnToText("article", "description");
        alterColumnToText("article", "donnee");
        System.out.println("[DatabaseMigration] Migration complete.");
    }

    private void alterColumnToText(String table, String column) {
        try {
            // Check current type first
            String currentType = jdbcTemplate.queryForObject(
                "SELECT data_type FROM information_schema.columns " +
                "WHERE table_name = ? AND column_name = ?",
                String.class, table, column
            );
            System.out.println("[DatabaseMigration] " + table + "." + column + " current type: " + currentType);

            if (!"text".equalsIgnoreCase(currentType)) {
                jdbcTemplate.execute("ALTER TABLE " + table + " ALTER COLUMN " + column + " TYPE TEXT");
                System.out.println("[DatabaseMigration] ✓ Altered " + table + "." + column + " VARCHAR -> TEXT");
            } else {
                System.out.println("[DatabaseMigration] ✓ " + table + "." + column + " already TEXT, skipped.");
            }
        } catch (Exception e) {
            System.out.println("[DatabaseMigration] ✗ Could not alter " + table + "." + column + ": " + e.getMessage());
        }
    }
}

