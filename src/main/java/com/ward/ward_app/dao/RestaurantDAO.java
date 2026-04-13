package com.ward.ward_app.dao;

import com.ward.ward_app.vo.RestaurantVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RestaurantDAO {

	private final JdbcTemplate jdbcTemplate;

	public RestaurantDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<RestaurantVO> findAll() {
		String sql = """
			select
				name,
				category,
				summary,
				address,
				latitude,
				longitude,
				rating
			from restaurant
			order by id
			""";

		return jdbcTemplate.query(
			sql,
			(rs, rowNum) -> new RestaurantVO(
				rs.getString("name"),
				rs.getString("category"),
				rs.getString("summary"),
				rs.getString("address"),
				rs.getDouble("latitude"),
				rs.getDouble("longitude"),
				rs.getDouble("rating")
			)
		);
	}
}
