package dev.lpa.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChannelSelectorServer {
  
  public static void main(String[] args) {
    
    try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
      serverChannel.socket().bind(new InetSocketAddress(5000));
      serverChannel.configureBlocking(false);
      Selector selector = Selector.open();
      serverChannel.register(selector, SelectionKey.OP_ACCEPT);
      
      while (true) {
        
        selector.select();  // blocking, overload with timeOut to stop blocking
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          iterator.remove();
          
          if (key.isAcceptable()) {
            SocketChannel clientChannel = serverChannel.accept();
            System.out.println("Client connected: " +
                                clientChannel.getRemoteAddress());
          }
        }
      }
    } catch (IOException io) {
      System.out.println(io.getMessage());
    }
  }
}
