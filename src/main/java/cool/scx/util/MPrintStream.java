package cool.scx.util;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;

public class MPrintStream extends PrintStream {

    private final JTextArea jTextArea;

    public MPrintStream(OutputStream out, JTextArea jTextArea) {
        super(out);
        this.jTextArea = jTextArea;
    }


    @Override
    public void write(byte[] buf, int off, int len) {
        final String message = new String(buf, off, len);
        super.write(buf, off, len);
        SwingUtilities.invokeLater(() -> jTextArea.append(message + "\r"));
    }
}
