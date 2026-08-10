package com.example.mall.util;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EncryptHandler extends BaseTypeHandler<String> {
    // 密钥(16*N位数)，一旦指定不能再修改
    private static final String AES_KEY = "BgKLBcLdAgZK7s64";
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 对参数进行加密
        if (StringUtils.isEmpty(parameter)) {
            ps.setString(i, null);
        }else {
            // 加密后存入数据库
            String encrypt = AES.encrypt(parameter, AES_KEY);
            ps.setString(i, encrypt);
        }

    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 获取结果集的值
        String value = rs.getString(columnName);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        // 返回解密的值
        return AES.decrypt(value,AES_KEY);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 获取结果集的值
        String value = rs.getString(columnIndex);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        // 返回解密的值
        return AES.decrypt(value,AES_KEY);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 获取结果集的值
        String value = cs.getString(columnIndex);
        if(StringUtils.isEmpty(value)){
            return null;
        }
        // 返回解密的值
        return AES.decrypt(value,AES_KEY);
    }
}
