import java.util.Scanner;

/**

 * Copyright (C), 2018-2021, HIT Zkx info. Edu.

 * FileName: Agent.java

 *此类包含环境的运行入口，主函数

 * @author 张恺欣
 * @Date    2018/11/26

 * @version 1.00

 */
public class MainEnvironment {
    public static void main(String args[]) {
        System.out.println("请选择运行模式：0：训练模式 1：测试模式");
        Scanner scanner=new Scanner(System.in);
        int flag=scanner.nextInt();
        MainThread mainThread=new MainThread(flag);
        mainThread.run();

    }
}
