package org.ly.commons.network.socket.basic.message;

import org.ly.commons.network.socket.SocketLogger;
import org.ly.commons.network.socket.basic.SocketContext;
import org.ly.commons.network.socket.basic.message.impl.SocketMessage;
import org.ly.commons.network.socket.crypto.KeyManager;
import org.ly.commons.network.socket.utils.SocketUtils;
import org.lyj.commons.cryptograph.MD5;
import org.lyj.commons.cryptograph.pem.RSAHelper;
import org.lyj.commons.util.StringUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SocketMessageDispatcher
        extends SocketLogger {

    // ------------------------------------------------------------------------
    //                      c o n s t
    // ------------------------------------------------------------------------

    private static final String ROOT = "./keyStore";

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final String _name;
    private final KeyManager _keys;
    private final SocketMessagePublicKeyCache _encode_key_cache;

    private String _encode_key; // [public] used to encode outbound message
    private String _decode_key; // [private] used to decode inbound messages

    private String _encode_signature;// MD5 hash for further _encode_key validation

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public SocketMessageDispatcher(final String name) {
        _name = name;
        _keys = new KeyManager(ROOT, name);
        _encode_key_cache = new SocketMessagePublicKeyCache();

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public String name() {
        return _name;
    }

    public String publicKey() {
        return _keys.publicKeyString();
    }

    public String encodeKey() {
        return _encode_key;
    }

    public SocketMessageDispatcher encodeKey(final String value) {
        _encode_key = value;
        _encode_signature = MD5.encode(value);
        return this;
    }

    public void write(final AsynchronousSocketChannel socket,
                      final SocketContext context,
                      final SocketMessage message)
            throws ExecutionException, InterruptedException, TimeoutException {

        // write data
        this.writeData(socket, context, message, context.timeout());


    }

    public SocketMessage read(final AsynchronousSocketChannel socket,
                              final SocketContext context) {
        // read data
        final SocketMessage message = SocketUtils.read(socket, context.timeout());
        if (null != message && !message.isHandShake()) {

            // decode
            this.decode(message, context);

        }
        return message;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        _decode_key = _keys.privateKeyString(); // used to decode messages
        this.encodeKey("");
    }

    private void writeData(final AsynchronousSocketChannel socket,
                           final SocketContext context,
                           final SocketMessage message,
                           final int timeout_ms)
            throws ExecutionException, InterruptedException, TimeoutException {

        if (!message.isHandShake()) {

            // encode
            this.encode(message, context);

        }

        final ByteBuffer send_buffer = ByteBuffer.wrap(message.bytes());
        final Future<Integer> futureWriteResult = socket.write(send_buffer);
        futureWriteResult.get(timeout_ms, TimeUnit.MILLISECONDS);
        send_buffer.clear();
    }

    private void encode(final SocketMessage message,
                        final SocketContext context) {
        try {
            if (!message.isHandShake() && StringUtils.hasText(_encode_key)) {

                // encrypt the body using a public key
                message.body(encrypt(message.body()));

                // write public key for encrypted response
                message.signature(_encode_signature);
            }
        } catch (Throwable t) {
            // unable to encode due an encoding error
            super.error("encode", t);
        }
    }

    private void decode(final SocketMessage message,
                        final SocketContext context) {
        try {
            if (!message.isHandShake() && StringUtils.hasText(_encode_key)) {

                // encrypt the body using a public key
                message.body(decrypt(message.body()));

            }
        } catch (Throwable t) {
            // unable to encode due an encoding error
            super.error("decode", t);
        }
    }

    private byte[] encrypt(byte[] data) throws Exception {
        final byte[] encrypted = RSAHelper.encrypt(data, _encode_key);
        return encrypted;
    }

    private byte[] decrypt(byte[] data) throws Exception {
        final byte[] decrypted = RSAHelper.decrypt(data, _decode_key);
        return decrypted;
    }

}
