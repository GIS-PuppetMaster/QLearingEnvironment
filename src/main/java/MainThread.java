import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.HashSet;
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
    int flag=0;
    public MainThread(int flag){
        this.flag=flag;
    }
    private Hashtable<String,Long> state= new Hashtable<String, Long>();
    private Agent agentZkx=new Agent("zkx");
    private Agent agentCbz=new Agent("cbz");
    /**
     * 两个Agent的死亡状态判定
     */
    private boolean deadZ=false;
    private boolean deadC=false;
    private Hashtable<String,Long> reward=new Hashtable<String,Long>(){
        {
            put("rewardZkx",0L);
            put("rewardCbz",0L);
        }};
    /**
     * 记录上一次的actionMap，其中的Time时间戳用来判断文件是否更新
     */
    private HashMap<String, Long> actionMapOld =new HashMap<String, Long>(){
        {
            put("actionZkx",  0L);
            put("actionCbz", 0L);
            put("Time",0L);
        }
    };
    private long traingingTimeOld=0;
    private void setStateForOutPut(){
        state.put("blood1", Long.valueOf(agentZkx.getState().get("blood").toString()));
        state.put("blood2", Long.valueOf(agentCbz.getState().get("blood").toString()));
        state.put("sol1",   Long.valueOf(agentZkx.getState().get("sol").toString()));
        state.put("sol2",   Long.valueOf(agentCbz.getState().get("sol").toString()));
        state.put("act1",   Long.valueOf(agentZkx.getState().get("act").toString()));
        state.put("act2",   Long.valueOf(agentCbz.getState().get("act").toString()));
        state.put("dis",Math.abs((Long.valueOf(agentZkx.getState().get("dis").toString()))-(Long.valueOf(agentCbz.getState().get("dis").toString()))));
    }
    private void setStateForTrainingOutPut(){
        setStateForOutPut();
        state.put("reward1",agentZkx.reward);
        state.put("reward2",agentCbz.reward);
    }
    private void setActionMapOld(Long actionZkx, Long actionCbz,Long time){
        actionMapOld.put("actionZkx",actionZkx);
        actionMapOld.put("actionCbz",actionCbz);
        actionMapOld.put("Time", time);
    }
    private void job() throws IOException {

        agentZkx.reward=0;
        agentCbz.reward=0;
        //JsonParser parser=new JsonParser();
        //JsonObject jsonObject=parser.parse(String.valueOf(ac)).getAsJsonObject();
        /*输入action*/
        Data data=new Data();
        HashMap actionMap = data.input("src/main/resources/action.json");
        HashMap stateMap= data.input("src/main/resources/trainingState.json");
        if(flag==1) {
            /*检测action.json是否更新了*/
            double nowTime= (double) actionMap.get("Time");
            long Time= (long) nowTime;
            if (Time != actionMapOld.get("Time")) {
                /*测试模式*/

                Number tempZ = (Number) actionMap.get("actionZkx");
                Long actionZkx = Long.valueOf(tempZ.intValue());
                Number tempC = (Number) actionMap.get("actionCbz");
                Long actionCbz = Long.valueOf(tempC.intValue());
                /*设置当前actionMap为actionMapOld*/
                double time = (double) actionMap.get("Time");
                setActionMapOld(actionZkx, actionCbz, (long) time);
                /*双方状态设置,此处不进行实际攻击，只设置action*/
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
                System.out.println();
                System.out.println();
                /*汇总输出reward*/
               /* reward.put("rewardZkx", agentZkx.reward);
                reward.put("rewardCbz", agentCbz.reward);
                String writePathReward = "src/main/resources/reward.json";
                data.output(reward, writePathReward);
                */
            }
        }
        else {
            double nowTime= (double) stateMap.get("Time");
            long Time= (long) nowTime;
            if(Time!=traingingTimeOld){
                /*训练模式*/
                    //设置训练时state、
                    double value=Double.valueOf(stateMap.get("blood1").toString());
                    int var=(int)value;
                    agentZkx.setState("blood",var);

                    value=Double.valueOf( stateMap.get("sol1").toString());
                    var=(int)value;
                    agentZkx.setState("sol",var );

                    value=Double.valueOf( stateMap.get("dis1").toString());
                    var= (int) value;
                    agentZkx.setState("dis",var );

                    value= Double.valueOf( stateMap.get("act1").toString());
                    var= (int) value;
                    agentZkx.setState("act",var);

                    value=Double.valueOf(stateMap.get("blood2").toString());
                    var= (int) value;
                    agentCbz.setState("blood",var);

                    value=Double.valueOf( stateMap.get("sol2").toString());
                    var= (int) value;
                    agentCbz.setState("sol",var);

                    value= Double.valueOf( stateMap.get("dis2").toString());
                    var=(int)value;
                    agentCbz.setState("dis",var);

                    value=Double.valueOf( stateMap.get("act2").toString());
                    var=(int)value;
                    agentCbz.setState("act",var);

                    traingingTimeOld= (long) ((double)stateMap.get("Time"));
                    /*进行打斗*/
                    agentZkx.attack(agentCbz);
                    agentCbz.attack(agentZkx);
                    /*死亡处理判断*/
                    deadZ = agentZkx.judgeDie();
                    deadC = agentCbz.judgeDie();
                    /*包含reward*/
                    setStateForTrainingOutPut();
                    String writePathState = "src/main/resources/nextState.json";
                    data.output(state, writePathState);
                    System.out.println();
                    System.out.println();
                }
        }
    }

    private void setState(Long action, Agent agent) {
        if(action ==1|| action ==2){
            agent.move(action);
        }
        else if(action >=3&& action <=5){
            agent.holdSelf(action);
        }
        else if(action >=6&& action <=8){
            //设置攻击状态
            agent.getState().put("act", action);
        }
        MainScreenOutput mainScreenOutput=new MainScreenOutput();
        mainScreenOutput.showStateUpdate(agent);
    }

    @Override
    public void run() {
        //1000ms,50帧，1000/50=20ms 作为sleep时间
        //记录环境运行时间
        long update=0;
        //记录环境线程睡眠的时间
        long sleep=0;
        while(true){
            //获取当前时间(ns)
            long before=System.nanoTime();
            //记录上一帧花费的时间(ms)
            long t=sleep+update;
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
            //计算do something 的总耗时（ms)
            update=(System.nanoTime()-before)/1000000L;
            //此次节拍内的剩余休眠时间
            sleep=Math.max(2,20-update);

            try{
                Thread.sleep(sleep);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}
