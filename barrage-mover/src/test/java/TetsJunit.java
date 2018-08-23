import com.barrage.common.Log;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * @author liyang
 * @description:
 * @date 2018/4/4
 */
public class TetsJunit {

    public static void main(String[] args) {

        ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(1);
        buffer.writeInt(2);
        buffer.writeInt(3);
        buffer.writeInt(4);
        buffer.writeInt(5);

        String str = null;

        try {
            str.toUpperCase();
        } catch (Exception e) {
            Log.errorLogger.error("{}",145456,e);
        }


        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\java-projects\\danmu\\barrage-mover\\target\\logs\\barrage-mover\\barrage.log"));
            String log;
            while ((log = bufferedReader.readLine()) != null) {


                System.out.println(new String(log.getBytes("gbk"),"utf-8"));

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
