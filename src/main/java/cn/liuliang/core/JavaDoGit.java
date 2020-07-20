package cn.liuliang.core;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @Package： cn.liuliang.core
 * @Author： liuliang
 * @CreateTime： 2020/7/20 - 11:43
 * @Description： Java操作git类
 */
public class JavaDoGit extends AttributeAssemble {

    private static final Logger logger = LoggerFactory.getLogger(JavaDoGit.class);


    /**
     * clone操作，将远程Git仓库克隆到本地目录中
     */
    public static void Clone() {
        Git git = null;
        try {
            logger.info("正在克隆远程Git项目到：" + LOCAL_REPERTOTY_PATH + "，请稍等......");
            git = Git
                    .cloneRepository()  //克隆仓库 //设置clone下来的分支 //克隆地址 //用户名密码 //本地地址 //克隆
                    .setBranch("master")
                    .setURI(GIT_REPERTOTY_URL)
                    .setCredentialsProvider(UPS)
                    .setDirectory(new File(LOCAL_REPERTOTY_PATH))
                    .call();
            logger.info("远程地址：" + GIT_REPERTOTY_URL + " 已经克隆到本地目录：" + LOCAL_REPERTOTY_PATH);
        } catch (Exception e) {
            //错误处理
            logger.info(e.getMessage() + " 本地地址：" + LOCAL_REPERTOTY_PATH + " 已经被初始化或不存在，克隆失败！");
        } finally {
            if (null != git) {
                git.close();
            }
        }
    }

    /**
     * 本地新建仓库
     */
    public static void create() {
        //本地新建仓库地址
        Repository newRepo = null;
        try {
            logger.info("正在初始化本地仓库：" + INIT_PATH + GIT_SUFFIX);
            newRepo = FileRepositoryBuilder.create(new File(INIT_PATH + GIT_SUFFIX));
            newRepo.create();
            logger.info("初始化成功！");
        } catch (IOException e) {
            logger.info(e.getMessage() + "初始化本地仓库失败：" + INIT_PATH + GIT_SUFFIX);
        } finally {
            if (null != newRepo) {
                newRepo.close();
            }
        }
    }

    /**
     * 新建一个分支并同步到远程仓库
     *
     * @param branchName
     * @throws IOException
     * @throws GitAPIException
     */
    public static String newBranch(String branchName) {
        String newBranchIndex = LOCAL_REPERTOTY_PATH + branchName;
        String gitPathURI = "";
        Git git = null;
        try {
            //检查新建的分支是否已经存在，如果存在则将已存在的分支强制删除并新建一个分支
            git = Git.open(new File(LOCAL_REPERTOTY_PATH));
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().equals(newBranchIndex)) {
                    logger.info("Removing branch before");
                    git.branchDelete().setBranchNames(branchName).setForce(true)
                            .call();
                    break;
                }
            }
            //新建分支
            Ref ref = git.branchCreate().setName(branchName).call();
            //推送到远程
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(GIT_USERNAME, GIT_PASSWORD))
                    .add(ref).call();
            gitPathURI = GIT_REPERTOTY_URL + " " + "feature/" + branchName;
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if (null != git) {
                git.close();
            }
        }
        return gitPathURI;
    }

    /**
     * 切换分支
     *
     * @param branchName 分支版本名称
     * @return 操作结果
     */
    public static boolean checkOut(String branchName) {
        //定义操作结果标志
        boolean flag = true;
        Git git = null;
        try {
            git = Git.open(new File(LOCAL_REPERTOTY_PATH));
            CheckoutCommand checkout = git.checkout();
            //如果分支在本地已存在，直接checkout即可。
            if (branchNameExist(git, branchName)) {
                checkout.setCreateBranch(false).setName(branchName).call();
            } else {
                //如果分支在本地不存在，需要创建这个分支，并追踪到远程分支上面。
                checkout.setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName).call();
            }
            logger.info("切换到分支：" + branchName);
            logger.info("开始更新代码到该分支，请稍等......");
            PullCommand pullCmd = git.pull();
            pullCmd.setCredentialsProvider(UPS)
                    .call();
            logger.info("更新代码到该分支成功！");
        } catch (Exception e) {
            //错误处理
            logger.info(e.getMessage() + " : 切换分支出现错误，错误路径是：" + LOCAL_REPERTOTY_PATH);
            flag = false;
        } finally {
            if (null != git) {
                git.close();
            }
        }
        return flag;
    }

    /**
     * 判断分支是否存在
     *
     * @param git
     * @param branchName
     * @return
     * @throws GitAPIException
     */
    private static boolean branchNameExist(Git git, String branchName) throws GitAPIException {
        //得到所有分支
        List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
        for (Ref ref : refs) {
            if (ref.getName().contains(branchName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 提交并推送到git
     *
     * @param message 提交信息
     * @return 返回提交版本号
     */
    public static String commitAndPush(String message) {
        logger.info("正在后台提交，请稍后......");
        Git git = null;
        RevCommit revCommit = null;
        try {
            git = Git.open(new File(LOCAL_REPERTOTY_PATH));
            //将所有文件添加到暂存区
            git.add().addFilepattern(".").call();
            CommitCommand commitCmd = git.commit();
            //设置凭证
            revCommit = commitCmd.setCommitter(NAME, EMAIL)
                    .setMessage(message).call();
            //推送到远程
            git.push()
                    .setCredentialsProvider(UPS)
                    .call();
            logger.info("操作成功！");
        } catch (Exception e) {
            //错误处理
            logger.info(e.getMessage() + "操作失败！");
        } finally {
            //资源关闭
            if (null != git) {
                git.close();
            }
        }
        return revCommit.getName();
    }


    /**
     * 查看所有分支
     */
    public static void seeBranchList() {
        Git git = null;
        try {
            git = Git.open(new File(LOCAL_REPERTOTY_PATH));
            //得到所有分支名称
            List<Ref> refs = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
            for (Ref ref : refs) {
                System.out.println(ref.getName());
            }
        } catch (Exception e) {
            //错误处理
            e.printStackTrace();
        } finally {
            //资源关闭
            if (null != git) {
                git.close();
            }
        }
    }


}
