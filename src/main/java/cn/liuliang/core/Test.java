package cn.liuliang.core;

/**
 * @Package： cn.liuliang.core
 * @Author： liuliang
 * @CreateTime： 2020/7/20 - 14:34
 * @Description：
 */
public class Test {
    public static void main(String[] args) {
        //克隆项目到本地
        //JavaDoGit.Clone();
        //先创建一个文件（在目录中做了），提交并push到GitHub
        //JavaDoGit.commitAndPush("测试第一次提交文件");
        //创建分支，并推送到GitHub上
        //JavaDoGit.newBranch("dev_ceshi");
        //切换到该分支上，并同步代码
        //JavaDoGit.checkOut("dev_ceshi");
        //查看所有分支
        JavaDoGit.seeBranchList();
    }
}
