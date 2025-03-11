package com.faspix.repository;

import com.faspix.dto.ResponseUserDTO;
import com.faspix.dto.ResponseUserShortDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final static String FIND_USERS_SQL =
            "SELECT u.id userId, u.username, u.email, " +
                    "STRING_AGG(kr.name, ', ' ORDER BY kr.name) AS roles " +
                    "FROM user_entity u " +
                    "JOIN user_role_mapping ur ON u.id = ur.user_id " +
                    "JOIN keycloak_role kr ON kr.id = ur.role_id " +
                    "WHERE (lower(u.username) LIKE lower(:nickname) " +
                    "OR lower(u.email) LIKE lower(:email)) " +
                    "GROUP BY u.id, u.username, u.email " +
                    "LIMIT :size OFFSET :offset";


    private final static String FIND_ALL_SQL =
            "SELECT u.id, u.username " +
                    "FROM user_entity u " +
                    "WHERE u.id IN (:ids)";


    public List<ResponseUserDTO> findUsers(String nickname, String email, int page, int size) {
        int offset = page * size;
        Map<String, Object> params = new HashMap<>();
        params.put("nickname", "%" + nickname + "%");
        params.put("email", "%" + email + "%");
        params.put("size", size);
        params.put("offset", offset);

        return namedParameterJdbcTemplate.query(FIND_USERS_SQL, params, (rs, rowNum) ->
                ResponseUserDTO.builder()
                        .userId(rs.getString("userId"))
                        .email(rs.getString("email"))
                        .username(rs.getString("username"))
                        .roles(Arrays.asList(rs.getString("roles").split(",\s*")))
                        .build()
        );
    }


    public List<ResponseUserShortDTO> findAll(Set<String> ids) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);

        RowMapper<ResponseUserShortDTO> rowMapper = (rs, rowNum) -> ResponseUserShortDTO.builder()
                .userId(rs.getString("id"))
                .username(rs.getString("username"))
                .build();

        return namedParameterJdbcTemplate.query(FIND_ALL_SQL, parameters, rowMapper);
    }



}
