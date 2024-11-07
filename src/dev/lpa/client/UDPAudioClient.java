package dev.lpa.client;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPAudioClient {
  
  private static final int SERVER_PORT = 5000;
  public static final int PACKET_SIZE = 1024;
  
  
  public static void main(String[] args) {
  
    try (DatagramSocket clientSocket = new DatagramSocket()) {
    
      byte[] audioFileName = "AudioClip.wav".getBytes();
      DatagramPacket packet1 = new DatagramPacket(audioFileName,
        audioFileName.length, InetAddress.getLocalHost(), SERVER_PORT);
      clientSocket.send(packet1);
      
      playStreamAudio(clientSocket);
    } catch (IOException | LineUnavailableException io) {
      System.out.println(io.getMessage());
    }
  }
  
  private static void playStreamAudio(DatagramSocket clientSocket)
    throws SocketException, LineUnavailableException {
    
    clientSocket.setSoTimeout(2000);
    AudioFormat format = new AudioFormat(22050, 16, // uses Swing
      1, true, false);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
    line.open();
    line.start();
    
    byte[] buffer = new byte[PACKET_SIZE];
    while (true) {
      try {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        clientSocket.receive(packet);
        line.write(buffer, 0, buffer.length);
      } catch (IOException io) {
        System.out.println(io.getMessage());
        break;
      }
    }
    line.close();
  }
}
