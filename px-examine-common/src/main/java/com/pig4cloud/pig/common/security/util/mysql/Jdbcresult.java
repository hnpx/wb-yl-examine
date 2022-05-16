package com.pig4cloud.pig.common.security.util.mysql;

import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Data
public class Jdbcresult {

    public PreparedStatement pt;
    public ResultSet rs;
}
