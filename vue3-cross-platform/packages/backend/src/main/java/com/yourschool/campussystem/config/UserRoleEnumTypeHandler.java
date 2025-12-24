package com.yourschool.campussystem.config;

import com.yourschool.campussystem.enums.UserRoleEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserRoleEnum类型处理器
 * 用于MyBatis-Plus将数据库中的字符串转换为UserRoleEnum枚举
 */
@MappedTypes(UserRoleEnum.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class UserRoleEnumTypeHandler extends BaseTypeHandler<UserRoleEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRoleEnum parameter, JdbcType jdbcType) throws SQLException {
        // 将枚举转换为字符串存储到数据库（使用枚举的name）
        ps.setString(i, parameter.name());
    }

    @Override
    public UserRoleEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToEnum(value);
    }

    @Override
    public UserRoleEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToEnum(value);
    }

    @Override
    public UserRoleEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToEnum(value);
    }

    /**
     * 将字符串转换为UserRoleEnum枚举
     */
    private UserRoleEnum convertToEnum(String value) {
        if (value == null || value.trim().isEmpty()) {
            return UserRoleEnum.TOURIST; // 默认返回游客
        }
        
        try {
            // 先尝试使用name()方法转换（如"STUDENT"）
            return UserRoleEnum.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            // 如果name()转换失败，尝试使用code转换（如"student"）
            return UserRoleEnum.getByCode(value.toLowerCase().trim());
        }
    }
}

