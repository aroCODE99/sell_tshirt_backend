package com.aro.Scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenScheduleTask {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenScheduleTask.class);
    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenScheduleTask(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void removeRefreshToken() {
        try {
            // I think the query is perfectly normal
            String query = "DELETE FROM refresh_token WHERE expiry_date < NOW()";

            int rows = jdbcTemplate.update(query);

            if (rows > 0) {
                log.info("Deleted {} expired refresh tokens", rows);
            }
        } catch (Exception e) {
            log.error("Scheduling Error", e);
        }
    }

}

