package com.faspix.repository;

import com.faspix.dto.ResponseUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final static String FIND_USERS_SQL =
            "SELECT u.id userId, u.username, u.email, " +
             "STRING_AGG(kr.name, ', ' ORDER BY kr.name) AS roles " +
             "FROM user_entity u " +
             "JOIN user_role_mapping ur ON u.id = ur.user_id " +
             "JOIN keycloak_role kr ON kr.id = ur.role_id " +
             "WHERE (lower(u.username) LIKE lower(concat('%', ?, '%')) " +
             "OR lower(u.email) LIKE lower(concat('%', ?, '%'))) " +
             "GROUP BY u.id, u.username, u.email " +
             "LIMIT ? OFFSET ?";


    public List<ResponseUserDTO> findUsers(String nickname, String email, int page, int size) {
        int offset = page * size;
        PreparedStatementSetter preparedStatement = ps -> {
            ps.setString(1, nickname);
            ps.setString(2, email);
            ps.setInt(3, size);
            ps.setInt(4, offset);
        };
        return jdbcTemplate.query(FIND_USERS_SQL, preparedStatement, (rs, rowNum) -> ResponseUserDTO.builder()
                .userId(rs.getString("userId"))
                .email(rs.getString("email"))
                .username(rs.getString("username"))
                .roles(Arrays.asList(rs.getString("roles").split(",\\s*")))
                .build()
        );
    }


}
