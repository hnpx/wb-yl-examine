package com.pig4cloud.pig.common.security.util.mysql;

import com.px.pa.modulars.examine.vo.ExamineTaskBySysTemVo;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IsMainService {


    /**
     * 查询所有没有插入系统中的任务
     *
     * @return
     */
    public List<ExamineTaskBySysTemVo> getTaskListBystatus() {
        List<ExamineTaskBySysTemVo> examineTaskBySysTemVoList = new ArrayList<>();
        GetUtil util = new GetUtil();
        String sql = "SELECT * from task_id where tasksync = 'I' or tasksync = 'U'";
        Object[] param = {};
        Connection con = util.getconnection();
        Jdbcresult rm = util.executeQuery(con, sql, param);
        ResultSet rs = rm.rs;
        PreparedStatement pt = rm.pt;
        try {
            while (rs.next()) {
                ExamineTaskBySysTemVo examineTaskBySysTemVo = new ExamineTaskBySysTemVo();
                examineTaskBySysTemVo.setRowguid(rs.getString("rowguid"));
                examineTaskBySysTemVo.setTasktype(rs.getInt("tasktype"));
                examineTaskBySysTemVo.setShopName(rs.getString("shop_name"));
                examineTaskBySysTemVo.setShopNum(rs.getString("shop_num"));
                examineTaskBySysTemVo.setShopAddress(rs.getString("shop_address"));
                examineTaskBySysTemVo.setUserName(rs.getString("user_name"));
                examineTaskBySysTemVo.setShopTel(rs.getString("shop_tel"));
                examineTaskBySysTemVo.setIdCard(rs.getString("id_card"));
                String applyTime = rs.getString("apply_time");
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime ldt = LocalDateTime.parse(applyTime,df);
                examineTaskBySysTemVo.setApplyTime(ldt);
                examineTaskBySysTemVoList.add(examineTaskBySysTemVo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            util.closeAll(rs, pt, con);
        }
        return examineTaskBySysTemVoList;
    }


    public void edit(String rowguid) {

        GetUtil util = new GetUtil();
        String sql = "update task_id set tasksync = ? where rowguid = ? ";
        Connection con = util.getconnection();
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,"S");
            ps.setString(2,rowguid);
            ps.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }
    public void insert(String rowguid,Integer status,String remark) {

        GetUtil util = new GetUtil();
        String sql = "insert into task_result (rowguid,status,remark,create_time) VALUE (?,?,?,?)";
        Connection con = util.getconnection();
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,rowguid);
            ps.setInt(2,status);
            ps.setString(3,remark);
            ps.setObject(4,LocalDateTime.now());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

}
