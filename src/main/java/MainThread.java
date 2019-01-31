import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

/**

 * Copyright (C), 2018-2021, HIT Zkx info. Edu.

 * FileName: MainThread.java

 * 类的详细说明

 **此类定义了环境中使用的主线程，Agent与环境的交互均在此线程的节拍下同步进行

 * @author 张恺欣
 * @Date    2018/11/26

 * @version 1.00

 */
class MainThread implements Runnable {
    private Hashtable<String,Integer> state=new Hashtable<String, Integer>();
    private Agent agentZkx=new Agent("zkx");
    private Agent agentCbz=new Agent("cbz");
    /**
     * 两个Agent的死亡状态判定
     */
    private boolean deadZ=false;
    private boolean deadC=false;
    private Hashtable<String,Integer> reward=new Hashtable<String,Integer>(){
        {
            put("rewardZkx",0);
            put("rewardCbz",0);
        }};
    /**
     * 记录上一次的actionMap，其中的Time时间戳用来判断文件是否更新
     */
    private HashMap<String,Integer> actionMap_old=new HashMap<String,Integer>(){
        {
            put("actionZkx",0);
            put("actionCbz",0);
            put("Time",0);
        }
    };
    private void setStateForOutPut(){
        state.put("blood1", (Integer) agentZkx.getState().get("blood"));
        state.put("blood2",(Integer) agentCbz.getState().get("blood"));
        state.put("sol1",(Integer) agentZkx.getState().get("sol"));
        state.put("sol2",(Integer) agentCbz.getState().get("sol"));
        state.put("act1",(Integer) agentZkx.getState().get("act"));
        state.put("act2",(Integer) agentCbz.getState().get("act"));
        state.put("dis",Math.abs((Integer) agentZkx.getState().get("dis")-(Integer) agentCbz.getState().get("dis")));
    }
    private void job() throws IOException {

        agentZkx.reward=0;
        agentCbz.reward=0;
        //JsonParser parser=new JsonParser();
        //JsonObject jsonObject=parser.parse(String.valueOf(ac)).getAsJsonObject();
        /*输入action*/
        Data data=new Data();
        HashMap actionMap = data.input();
        /*检测action.json是否更新了*/
        if(!actionMap.get("Time").equals(actionMap_old.get("Time"))) {
            Number tempZ = (Number) actionMap.get("actionZkx");
            int actionZkx = tempZ.intValue();
            Number tempC = (Number) actionMap.get("actionCbz");
            int actionCbz = tempC.intValue();
            /*双方状态设置,此处不进行实际攻击*/
            setState(actionZkx, agentZkx);
            setState(actionCbz, agentCbz);
            /*进行打斗*/
            agentZkx.attack(agentCbz);
            agentCbz.attack(agentZkx);
            /*死亡处理判断*/
            deadZ = agentZkx.judgeDie();
            deadC = agentCbz.judgeDie();
            /*汇总处理要输出的state*/
            setStateForOutPut();
            String writePathState = "src/main/resources/state.json";
            data.output(state, writePathState);
            /*汇总输出reward*/
            reward.put("rewardZkx", agentZkx.reward);
            reward.put("rewardCbz", agentCbz.reward);
            String writePathReward = "src/main/resources/reward.json";
            data.output(reward, writePathReward);
        }

    }

    private void setState(int action, Agent agent) {
        if(action ==1|| action ==2){
            agent.move(action);
        }
        else if(action >=3&& action <=5){
            agent.holdSelf(action);
        }
        else if(action >=6&& action <=8){
            agent.getState().put("act", action);//设置攻击状态
        }
        MainScreenOutput mainScreenOutput=new MainScreenOutput();
        mainScreenOutput.showStateUpdate(agent);
    }

    @Override
    public void run() {
        //1000ms,50帧，1000/50=20ms 作为sleep时间
        long update=0;//记录环境运行时间
        long sleep=0;//记录环境线程睡眠的时间
        while(true){
            long before=System.nanoTime();//获取当前时间(ns)
            long t=sleep+update;//记录上一帧花费的时间(ms)
            /*
            do something
             */
            try {
                job();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(deadZ||deadC){
                break;
            }
            update=(System.nanoTime()-before)/1000000L;//计算do something 的总耗时（ms)
            sleep=Math.max(2,20-update);//此次节拍内的剩余休眠时间

            try{
                Thread.sleep(sleep);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
