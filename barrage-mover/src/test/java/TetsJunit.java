import com.barrage.common.Log;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

        Log.d("");


    }
}
