package cool.scx.util;

import javax.swing.*;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * <p>MPrintStream class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class MPrintStream extends PrintStream {

    private final JTextArea jTextArea;

    /**
     * <p>Constructor for MPrintStream.</p>
     *
     * @param out a {@link java.io.OutputStream} object.
     * @param jTextArea a {@link javax.swing.JTextArea} object.
     */
    public MPrintStream(OutputStream out, JTextArea jTextArea) {
        super(out);
        this.jTextArea = jTextArea;
    }


    /** {@inheritDoc} */
    @Override
    public void write(byte[] buf, int off, int len) {
        final String message = new String(buf, off, len);
        super.write(buf, off, len);
        SwingUtilities.invokeLater(() -> jTextArea.append(message + "\r"));
    }
}
