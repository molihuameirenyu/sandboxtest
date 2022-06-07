package sandbox.dto;
import lombok.Data;

import java.util.List;

@Data
public class AddMockDara {
    private Integer id;
    private String applicationName;

    /**
     * 应用部署ip地址
     */
    private String ip;

    /**
     * 类名
     */
    private String classname;

    /**
     * 方法名
     */
    private String methodname;

    /**
     * mock类型
     */
    private String mocktype;

    /**
     * 返回类型
     */
    private String returntype;

    /**
     * mock返回值
     */
    private String returnvalue;

    /**
     * 开关
     */
    private Integer status;

    private List<newmockparam> addparams;

    private Integer delFlag;


}
