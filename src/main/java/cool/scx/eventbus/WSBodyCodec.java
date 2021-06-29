package cool.scx.eventbus;

import cool.scx.bo.WSBody;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.*;

public class WSBodyCodec implements MessageCodec<WSBody, WSBody> {

    @Override
    public void encodeToWire(Buffer buffer, WSBody scxWSBody) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try (var o = new ObjectOutputStream(b)) {
            o.writeObject(scxWSBody);
            o.close();
            buffer.appendBytes(b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WSBody decodeFromWire(int pos, Buffer buffer) {
        final ByteArrayInputStream b = new ByteArrayInputStream(buffer.getBytes());
        WSBody msg = null;
        try (ObjectInputStream o = new ObjectInputStream(b)) {
            msg = (WSBody) o.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public WSBody transform(WSBody scxWSBody) {
        return scxWSBody;
    }

    @Override
    public String name() {
        return "ScxWSBodyCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}
