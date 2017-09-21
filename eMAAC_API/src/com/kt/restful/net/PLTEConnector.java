package com.kt.restful.net;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.restful.constants.LogFlagProperty;
import com.kt.restful.constants.esanProperty;
import com.kt.restful.model.ProvifMsgType;
import com.kt.restful.service.pushTriggerService;

public class PLTEConnector extends Connector {
	
	private static Logger logger = LogManager.getLogger(PLTEConnector.class);
	
	private DataInputStream din;

	private boolean msgReadStarted;
	private int reservedMsgSize;
	private int totalReadSize, currentReadSize;
	private int[] msgSize;
	private static PLTEConnector dbmConnector;
	private boolean logFlag = false;
	
	public static int reqId = 1000;
	
	
	public static PLTEConnector getInstance() {
		if(dbmConnector == null || !dbmConnector.isConnected()) {
			dbmConnector = new PLTEConnector();
		}
		return dbmConnector;
	}
	
	public PLTEConnector() {
		super(PLTEManager.getInstance(), esanProperty.getPropPath("esan_plte_ipaddress"), Integer.parseInt(esanProperty.getPropPath("esan_plte_port")));
		msgSize = new int[4];
		for( int i=0; i<msgSize.length; i++ )
		    msgSize[i] = -1;
		
		try {
						
			if(LogFlagProperty.getPropPath("log_flag").equals("ON")) {
				logFlag = true;
			} else {
				logFlag = false;
			}
			
			//logger.info("logFlag : " + logFlag);
		} catch (Exception ex) {
			logger.error("LogFlagProperty Load Excetpion Occured : " + ex.getMessage());
		}
	}	

	public boolean sendMessage(String command, List<String[]> params, int clientReqID, String akey) {
		try {
			StringBuffer bodySB = new StringBuffer();
			for(int i = 0; i < params.size(); i++) {
				if(i != 0) bodySB.append(",");
//				if(i != 0) bodySB.append(";");
				bodySB.append(String.format("%s=%s",  params.get(i)[0],  params.get(i)[1]));
			}
			
			/*
			 * =====================================
			 * MESSAGE TYPE
			 * =====================================
			 * [1][API_NAME]  = [64]   - Header
			 * [2][SEQ_NUM ]  = [8]    - Header
			 * [3][AUTH_KEY]  = [65]   - Header
			 * [4][RES_CODE]  = [4]    - Header
			 * [5][BODY_LEN]  = [4]    - Body
			 * [6][BODY    ]  = [BODY] - Body
			 * =====================================
			 */
			
			//bodyLen
			int bodyLen = bodySB.toString().length();
						
			// [0]. DATA_TOTAL_LEN
			dataOut.write(toBytes(64+8+64+4+4+bodyLen));
			
			// [1]. API_NAME
			dataOut.write(command.getBytes());
			for(int i = 0; i < 64 - command.length(); i++)
				dataOut.write("\0".getBytes());
			
			// [2]. SEQ_NUM
			dataOut.write((clientReqID+"").getBytes()); 
			for(int i = 0; i < 8 - (clientReqID+"").length(); i++)
				dataOut.write("\0".getBytes());
			
			// [3]. AUTH KEY
			dataOut.write(akey.getBytes());
			for(int i = 0; i < 64 - akey.length(); i++)
				dataOut.write("\0".getBytes());
	
			// [4]. RES_CODE
			dataOut.writeInt(0);

			// [5]. BODY_LEN
			dataOut.write(toBytes(bodyLen));
			//dataOut.writeInt(bodyLen);
									
			// [6]. BODY
			dataOut.write(bodySB.toString().getBytes());
			dataOut.flush();
			
			if(isLogFlag()) {
			logger.info("=============================================");
			logger.info("RESTIF -> PROVIB TCP SEND");
			logger.info("apiName : " + command);
			logger.info("tid : " + clientReqID);
			logger.info("akey : " + akey);
			logger.info("bodyLen : " + bodyLen);
			logger.info("==============BODY==================");
			logger.info(bodySB.toString());
			logger.info("====================================");
			logger.info("=============================================");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean sendMessage(String command, int resCode, int clientReqID) {
		try {
			StringBuffer bodySB = new StringBuffer();
//			for(int i = 0; i < params.size(); i++) {
//				if(i != 0) bodySB.append(",");
////				if(i != 0) bodySB.append(";");
//				bodySB.append(String.format("%s=%s",  params.get(i)[0],  params.get(i)[1]));
//			}
			
			bodySB.append("");
			/*
			 * =====================================
			 * MESSAGE TYPE
			 * =====================================
			 * [1][API_NAME]  = [64]   - Header
			 * [2][SEQ_NUM ]  = [8]    - Header
			 * [3][RES_CODE]  = [4]    - Header
			 * [4][BODY_LEN]  = [4]    - Body
			 * [5][BODY    ]  = [BODY] - Body
			 * =====================================
			 */
			
			//bodyLen
			int bodyLen = bodySB.toString().length();
						
			// [0]. DATA_TOTAL_LEN
			dataOut.write(toBytes(64+8+4+4+bodyLen));
			
			// [1]. API_NAME
			dataOut.write(command.getBytes());
			for(int i = 0; i < 64 - command.length(); i++)
				dataOut.write("\0".getBytes());
			
			// [2]. SEQ_NUM
			dataOut.write((clientReqID+"").getBytes()); 
			for(int i = 0; i < 8 - (clientReqID+"").length(); i++)
				dataOut.write("\0".getBytes());
	
			// [3]. RES_CODE
			dataOut.writeInt(resCode);

			// [4]. BODY_LEN
			dataOut.write(toBytes(bodyLen));
			//dataOut.writeInt(bodyLen);
									
			// [5]. BODY
			dataOut.write(bodySB.toString().getBytes());
			dataOut.flush();
			
			if(isLogFlag()) {
			logger.info("=============================================");
			logger.info("RESTIF -> PLTEIB TCP RESPONSE");
			logger.info("apiName : " + command);
			logger.info("tid : " + clientReqID);
			logger.info("bodyLen : " + bodyLen);
			logger.info("==============BODY==================");
			logger.info(bodySB.toString());
			logger.info("====================================");
			logger.info("=============================================");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	
    private static byte[] toBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)(i>>24);
        result[1] = (byte)(i>>16);
        result[2] = (byte)(i>>8);
        result[3] = (byte)(i);
        return result;
    }   
	
	
	public boolean sendMessage(String req) {
		
		try {
			dataOut.write(req.getBytes());
			
			dataOut.flush();
		} catch (Exception e) {
			logger.error("Message Send Error Message - " + e );
			return false;
		}
		
		return true;
	}
	
	public static int byteToInt(byte[] bytes, ByteOrder order) {
		 
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(order);
 
		// buff사이즈는 4인 상태임
		// bytes를 put하면 position과 limit는 같은 위치가 됨.
		buff.put(bytes);
		// flip()가 실행 되면 position은 0에 위치 하게 됨.
		buff.flip();
 
		return buff.getInt(); // position위치(0)에서 부터 4바이트를 int로 변경하여 반환
	}

	/*
	 * =====================================
	 * MESSAGE TYPE
	 * =====================================
	 * [1][API_NAME]  = [64]   - Header
	 * [2][SEQ_NUM ]  = [8]    - Header
	 * [3][RES_CODE]  = [4]    - Header
	 * [4][BODY_LEN]  = [4]    - Body
	 * [5][BODY    ]  = [BODY] - Body
	 * =====================================
	 */	
	protected void readMessage() throws IOException {
		if (msgReadStarted == false) {
		    //reservedMsgSize = byteToInt(toBytes(dataIn.readInt()), ByteOrder.LITTLE_ENDIAN);
		    reservedMsgSize = byteToInt(toBytes(dataIn.readInt()), ByteOrder.BIG_ENDIAN);
		    
			if (reservedMsgSize > BUFFER_SIZE) {
				logger.info(
					"(DBM) ReservedMsgSize is larger than "+ BUFFER_SIZE+ " : " + reservedMsgSize);
				throw new IOException("Larger than " + BUFFER_SIZE + " bytes");
			}

			msgReadStarted = true;
			totalReadSize = 0;
		}

		currentReadSize = dataIn.read(buffer, totalReadSize, reservedMsgSize - totalReadSize);
		
		if (totalReadSize + currentReadSize == reservedMsgSize) {
			
			din = new DataInputStream(new ByteArrayInputStream(buffer));
			try {
				
				ProvifMsgType pmt = new ProvifMsgType();
				pmt.setProvifMsgType(din);

				if (isLogFlag()) {
					logger.info("=============================================");
					logger.info("PLTEIB -> RESTIF TCP RECEIVE");
					logger.info("apiName : " + pmt.getApiName());
					logger.info("tid : " + pmt.getSeqNo());
					logger.info("aKey : " + pmt.getAuthKey());
					logger.info("resCode : " + pmt.getResCode());
					logger.info("bodyLen : " + pmt.getReservedMsgSize());
					logger.info("==============BODY==================");
					logger.info(pmt.getData());
					logger.info("====================================");
					logger.info("=============================================");
				}				
				
				// REQUEST ==> Client Mode로 동작
				if(pmt.getApiName().toUpperCase().equals("PUSHTRIGGER")){
					// 	public pushTriggerService(String apiName, int tid, String mdn, String imsi, String body ){
					pushTriggerService pService = new pushTriggerService(pmt.getApiName(), Integer.parseInt(pmt.getSeqNo()), pmt.getMdn() ,pmt.getImsi(), pmt.getData());
					pService.start();
				}else
					receiver.receiveMessage(pmt.getData(), pmt.getResCode(), Integer.parseInt(pmt.getSeqNo()));
			} catch (Exception e) {
				e.printStackTrace();
			}

			msgReadStarted = false;
		} else if (totalReadSize + currentReadSize > reservedMsgSize) {
			throw new IOException("It is never occured, but...");
		} else {
			totalReadSize += currentReadSize;
		}
	}
	
	public synchronized boolean isLogFlag() {
		return logFlag;
	}

	public void setLogFlag(boolean logFlag) {
		this.logFlag = logFlag;
	}	
}