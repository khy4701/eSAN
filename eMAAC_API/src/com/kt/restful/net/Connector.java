package com.kt.restful.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public abstract class Connector implements Runnable {
	private static Logger logger = LogManager.getLogger(Connector.class);
	
	protected static final int BUFFER_SIZE = 8192*8*2;//���ۻ���� ������ ������ �������� �վ� omp�� 512k
	 

	protected ConnectObserver	connectObserver;

	// private String ipAddr;
	// private int port;

	protected Socket			socket;
	protected Thread			reader;

	protected DataInputStream	dataIn;
	protected DataOutputStream	dataOut;

	protected Receiver			receiver;
	protected byte[]			buffer;
	protected Header			header;

	public Connector(String ipAddr, int port) {
		this.buffer = new byte[BUFFER_SIZE];
		this.header = new Header();

		try {
			if (connect(ipAddr, port)) {
				reader = new Thread(this, "Reader");
				reader.start();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Connector(Receiver receiver, String ipAddr, int port) {

		this.receiver = receiver;

		this.buffer = new byte[BUFFER_SIZE];
		this.header = new Header();

		try {
			if (connect(ipAddr, port)) {
				reader = new Thread(this, "Reader");
				reader.start();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
/**
 * @param connectObserver �� connection�� ������ ��
 * @param receiver socket���� ���� ���� �޽����� ���� ��
 * @param ipAddr ���� �� IP address
 * @param port ��� ���� ���� �� port
 */
	public Connector(ConnectObserver connectObserver, Receiver receiver, String ipAddr, int port) {
		this.connectObserver = connectObserver;

		this.receiver = receiver;
		// this.ipAddr = ipAddr;
		// this.port = port;

		this.buffer = new byte[BUFFER_SIZE];
		this.header = new Header();

		try {
			if (connect(ipAddr, port)) {
				reader = new Thread(this, "Reader");
				reader.start();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

/**
 * ������ �ھ� �Ǿ��ִ��� �� ���ڰ�~
 * 
 * @return ���� ������ Ʈ����
 */
	public boolean isConnected() {
		if (socket == null) {
			return false;
		}

		return this.socket.isConnected();
	}

	public void run() {
		Thread thisThread = Thread.currentThread();

		while (reader == thisThread) {
			try {
				readMessage();
			} catch (Exception e) {
				logger.error(socket.getInetAddress().getHostName() + ":" + socket.getPort() + " exception occured");
				logger.error(e);
				logger.error("not connected...");
				logger.error(this.getClass().getName() + "-" + e.getMessage());
				e.printStackTrace();
				reader = null;
				break;
			}
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			logger.error(e);
		}

		socket = null;
	}

	protected abstract void readMessage() throws IOException;

	protected abstract boolean sendMessage(String result);

	private boolean connect(String ipAddr, int port) {
		try {
			socket = new Socket();
			
			socket.setReceiveBufferSize(BUFFER_SIZE);
			socket.connect(new InetSocketAddress(ipAddr, port));

			dataIn = new DataInputStream(new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE));
			dataOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), BUFFER_SIZE));
			logger.error("Socket Connection Success..");

			
		} catch (SocketException se) {
			logger.error("Socket closed..");

			socket = null;
			return false;
		} catch (IOException e) {
			socket = null;
			return false;
		}

		return this.isConnected();
	}
	
	protected class Header {
		protected int	mapType	= 0;
		protected int	segFlag;
		protected int	seqNo;

		public void readHeader(DataInputStream dataIn) throws IOException {
			mapType = dataIn.readInt();
			segFlag = dataIn.readByte();
			seqNo = dataIn.readByte();
			
			dataIn.readByte();
			dataIn.readByte();
		}
	}
}
