package cn.liuliang.core;

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Package： cn.liuliang.core
 * @Author： liuliang
 * @CreateTime： 2020/7/20 - 12:49
 * @Description： 属性集合类
 */
public class AttributeAssemble {

    private static final Logger logger = LoggerFactory.getLogger(AttributeAssemble.class);

    /**
     * 下载已有仓库到本地路径
     */
    static String LOCAL_REPERTOTY_PATH;
    /**
     * 本地路径新建
     */
    static String INIT_PATH;
    /**
     * 远程仓库地址
     */
    static String GIT_REPERTOTY_URL;
    /**
     * 用户名
     */
    static String GIT_USERNAME;
    /**
     * 密码
     */
    static String GIT_PASSWORD;
    /**
     * git目录后缀
     */
    static final String GIT_SUFFIX = "/.git";
    /**
     * 用户凭证
     */
    static String NAME;
    static String EMAIL;
    /**
     * 用户GitHUb上账号密码对象
     */
    static UsernamePasswordCredentialsProvider UPS;

    /**
     * 初始化属性
     */
    static {
        try {
            InputStream resourceAsStream = AttributeAssemble.class.getClassLoader().getResourceAsStream("myGit.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            LOCAL_REPERTOTY_PATH = (String) properties.get("localRepertotyPath");
            GIT_REPERTOTY_URL = (String) properties.get("gitRepertotyUrl");
            GIT_USERNAME = (String) properties.get("gitUserName");
            GIT_PASSWORD = (String) properties.get("gitPassWord");
            INIT_PATH = (String) properties.get("initPath");
            NAME = (String) properties.get("name");
            EMAIL = (String) properties.get("email");
            //绑定账户密码
            UPS = new UsernamePasswordCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
        } catch (IOException e) {
            logger.info(e.getMessage() + "配置文件【myGit.properties】加载出错");
        }
    }


}
