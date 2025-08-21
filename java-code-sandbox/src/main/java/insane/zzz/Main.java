package insane.zzz;


import java.net.URL;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println(Main.class.getClassLoader().getResource("security.policy").toString());
        // 获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        // 获取资源路径
        URL resource = classLoader.getResource("");
        System.out.println(resource);
        System.out.println(resource.getPath());

    }
}