import com.google.gson.Gson;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;

class Data {
    private StringBuffer ans= new StringBuffer();
    private HashMap actionMap=new HashMap();

    /**
     * @return  返回解析出来的hashmap
     * @throws IOException
     */
    HashMap input() throws IOException {
        Gson gson=new Gson();
        String readPath = "src/main/resources/action.json";
        File actionFile= new File(readPath);
        InputStreamReader reader=new InputStreamReader(
                new FileInputStream(actionFile));
        BufferedReader bufferedReader=new BufferedReader(reader);
        String tmp = "";
        while(true){
            try {
                if ((tmp =bufferedReader.readLine())==null) {
                    break;
                }
                else{
                    ans.append(tmp);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        actionMap=gson.fromJson(String.valueOf(ans),actionMap.getClass());
        bufferedReader.close();
        return actionMap;
    }
    void output(Hashtable<String, Integer> state, String writePath) throws IOException {
        state.put("Time", (int) System.currentTimeMillis());
        Gson gson=new Gson();
        String ret = gson.toJson(state, Hashtable.class);
        /*---------------------*/
        File stateFile=new File(writePath);
        OutputStreamWriter writer=new OutputStreamWriter(
                new FileOutputStream(stateFile));
        BufferedWriter bufferedWriter=new BufferedWriter(writer);
        bufferedWriter.write(ret);
        bufferedWriter.close();
    }
}
