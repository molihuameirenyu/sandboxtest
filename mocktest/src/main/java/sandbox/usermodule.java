package sandbox;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ProcessController;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import org.kohsuke.MetaInfServices;
import sandbox.dto.AddMockDara;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@MetaInfServices(Module.class)
@Information(id = "usermodule", version = "0.0.1")
public class usermodule implements Module {
    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Command("beforeMock")
    public void beforeMock() throws Exception {
        List<AddMockDara> addMockDaras = new ArrayList<AddMockDara>();
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        String classname="";
        String methodname="";
        String returnvalue="";
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://11.80.10.176:3358/mock_mini?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false", "mock_mini_rw", "Dxswh7_w_JAZGVwn");
        statement = connection.createStatement();
        String sql = "select *  from mock_data";
        rs = statement.executeQuery(sql);
        while (rs.next()) {
            AddMockDara addMockDara = new AddMockDara();
            addMockDara.setApplicationName(rs.getString("application_name"));
            addMockDara.setClassname(rs.getString("classname"));
            addMockDara.setMethodname(rs.getString("methodname"));
            addMockDara.setMocktype(rs.getString("mocktype"));
            addMockDara.setReturnvalue(rs.getString("returnvalue"));
            addMockDara.setStatus(rs.getInt("status"));
            addMockDaras.add(addMockDara);
        }
        if (rs != null) {
            rs.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
        for (AddMockDara addMockDara : addMockDaras) {
             classname = addMockDara.getClassname();
             methodname = addMockDara.getMethodname();
             returnvalue = addMockDara.getReturnvalue();

        }
        final String Returnvalue = returnvalue;
        new EventWatchBuilder(moduleEventWatcher)
                .onClass(classname)
                .onBehavior(methodname)
                .onWatch(new AdviceListener() {
                    @Override
                    protected void before(Advice advice) throws Throwable {
                        Object[] parameterArray = advice.getParameterArray();
                        String className=advice.getBehavior().getDeclaringClass().getName();
                        String methodName=advice.getBehavior().getName();

                        String allReqParams="";
                        if (parameterArray != null) {
                            for (Object po : parameterArray) {
                                if (po != null) {
                                    String reqParams=JSONObject.toJSONString(po);
                                    allReqParams=allReqParams+reqParams;

                                }
                            }
                            String mockRes= Returnvalue;
                            ProcessController.returnImmediately(JSONObject.parseObject(mockRes.toString(), advice.getBehavior().getReturnType()));
                        }
                    }
                });
    }
}
