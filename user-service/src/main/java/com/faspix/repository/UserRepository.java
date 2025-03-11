package com.faspix.repository;

import com.faspix.dto.ResponseUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final static String FIND_USERS_SQL = "" +
            "SELECT u.id userId, u.email, u.username, kr.name role FROM user_entity u " +
            "JOIN user_role_mapping ur ON u.id = ur.user_id " +
            "JOIN keycloak_role kr on kr.id = ur.role_id " +
            "WHERE u.username != 'service-account-microservice-client' " +
            "LIMIT ? OFFSET ?";


    public List<ResponseUserDTO> findUsers(int page, int size) {
        Map<String, ResponseUserDTO> userMap = new HashMap<>();

        int offset = page * size;
        PreparedStatementSetter preparedStatement = ps -> {
            ps.setInt(1, size);
            ps.setInt(2, offset);
        };

        jdbcTemplate.query(FIND_USERS_SQL, preparedStatement, (rs, rowNum) -> {
            String userId = rs.getString("userId");
            String email = rs.getString("email");
            String username = rs.getString("username");
            String role = rs.getString("role");

            ResponseUserDTO user = userMap.get(userId);
            if (user == null) {
                user = ResponseUserDTO.builder()
                        .userId(userId)
                        .email(email)
                        .username(username)
                        .roles(new ArrayList<>())
                        .build();
                userMap.put(userId, user);
            }

            if (!user.getRoles().contains(role)) {
                user.getRoles().add(role);
            }

            return null;
        });

        return new ArrayList<>(userMap.values());
    }


}
