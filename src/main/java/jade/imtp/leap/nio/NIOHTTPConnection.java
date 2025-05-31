/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jade.imtp.leap.nio;



import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import jade.imtp.leap.exception.ICPException;

/**
 *
 * @author eduard
 */
public class NIOHTTPConnection extends NIOJICPConnection {

    private NIOHTTPHelper helper;

    @Override
    void init(SocketChannel channel) throws ICPException {
        super.init(channel);
        helper = new NIOHTTPHelper(this);
        addBufferTransformer(helper);
    }
  
    private static Logger log = Logger.getLogger(NIOHTTPConnection.class.getName());

}
