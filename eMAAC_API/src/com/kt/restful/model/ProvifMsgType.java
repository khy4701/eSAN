package com.kt.restful.model;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.kt.util.Util;

//typedef struct {
//    char    apiName[64]; /* Get APN, Get Subscriber LTE Roaming Restrictions, �� */
//    char    seq_no[8]; /* sequence number; ���� �� �״�� ��ȯ */             sample �� Transaction-ID 6�ڸ��� �Ǿ� ����.
//    char    res_code[8]; /* ��û �ÿ��� �� ��� */
//    char    data[8000]; /* ��û,���� �޽��� ���� */
//} ProvifMsgType;
public class ProvifMsgType {
	
	private static Logger logger = LogManager.getLogger(ProvifMsgType.class);
	
	private String apiName;
	private String seqNo;
	private String imsi;
	private String mdn;
	private String ipAddress;
	private String apiHost;
	private int apiPort;
	private int resCode;
	private String data;
	private String authKey;
	
	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	private byte[] apiNameBuffer;
	private byte[] seqNoBuffer;
	private byte[] dataBuffer;
	private byte[] authKeyBuffer;
	private byte[] apiHostBuffer;
	
	private static int API_NAME_LEN = 64;
	private static int SEQ_NO_LEN = 8;
	private static int IMSI_LEN = 16;
	private static int MDN_LEN = 12;
	private static int IP_ADDRESS_LEN = 64;
	private static int AUTH_KEY_LEN = 64;
	private static int API_HOST_LEN = 64;
	
	public ProvifMsgType() {
		apiName = "";
		seqNo = "";
		imsi = "";
		mdn = "";
		ipAddress = "";
		apiHost = "";
		apiPort = 0;
		resCode = 0;
		data = "";
		
		apiNameBuffer = new byte[API_NAME_LEN];
		apiHostBuffer = new byte[API_HOST_LEN];
		authKeyBuffer = new byte[AUTH_KEY_LEN];
		seqNoBuffer = new byte[SEQ_NO_LEN];

		msgSize = new int[4];
		for( int i=0; i<msgSize.length; i++ )
		    msgSize[i] = -1;
	}
	
	
	
	private int[] msgSize = new int[4];
	private int reservedMsgSize;
	
	
	/*
	 * =====================================
	 * MESSAGE TYPE
	 * =====================================
	 * [1][API_NAME]  = [64]   - Header
	 * [2][SEQ_NUM ]  = [8]    - Header
	 * [3][AUTH_KEY]  = [64]   - Header
	 * [4][API_HOST]  = [64]   - Header
	 * [5][API_PORT]  = [64]   - Header
	 * [6][RES_CODE]  = [4]    - Header
	 * [7][BODY_LEN]  = [4]    - Body
	 * [7][BODY    ]  = [BODY] - Body
	 * =====================================
	 */	

	public void setProvifMsgType(DataInputStream in) throws IOException {
				
		// [1] API_NAME
		in.read(apiNameBuffer, 0, apiNameBuffer.length);
		this.setApiName(Util.nullTrim(new String(apiNameBuffer)));

		// [2] SEQ_NUM
		in.read(seqNoBuffer, 0, seqNoBuffer.length);
		this.setSeqNo(Util.nullTrim(new String(seqNoBuffer)));
				
		// [3] AUTH_KEY
		in.read(authKeyBuffer, 0, authKeyBuffer.length);
		this.setAuthKey(Util.nullTrim(new String(authKeyBuffer)));

		// [4] API_HOST
		in.read(apiHostBuffer, 0, apiHostBuffer.length);
		this.setApiHost(Util.nullTrim(new String(apiHostBuffer)));
		
		// [5] API_PORT
		this.setApiPort(byteToInt(toBytes(in.readInt()), ByteOrder.LITTLE_ENDIAN));
		
		// [6] RES_CODE
		this.setResCode(byteToInt(toBytes(in.readInt()), ByteOrder.LITTLE_ENDIAN));
//		this.setResCode(byteToInt(toBytes(in.readInt()), ByteOrder.BIG_ENDIAN));
		
		// [7] BODY_LEN
		//reservedMsgSize = byteToInt(toBytes(in.readInt()), ByteOrder.LITTLE_ENDIAN);
		reservedMsgSize = byteToInt(toBytes(in.readInt()), ByteOrder.BIG_ENDIAN);
	    dataBuffer = new byte[reservedMsgSize];
		
//		this.setResCode(byteToInt(toBytes(in.readInt()), ByteOrder.BIG_ENDIAN));
//		
//		reservedMsgSize = byteToInt(toBytes(in.readInt()), ByteOrder.BIG_ENDIAN);
//	    dataBuffer = new byte[reservedMsgSize];
	    
	    // [8] BODY
		in.read(dataBuffer, 0, dataBuffer.length);
		this.setData(Util.nullTrim(new String(dataBuffer)));
		
		// Parsing and Saving on Class 
		String seperator = ",";
		this.parsingBody(this.getData(), seperator);
	}
	
    private static byte[] toBytes(int i) {
        byte[] result = new byte[4];
        result[0] = (byte)(i>>24);
        result[1] = (byte)(i>>16);
        result[2] = (byte)(i>>8);
        result[3] = (byte)(i);
        return result;
    }   
	
	public static int byteToInt(byte[] bytes, ByteOrder order) {
		 
		ByteBuffer buff = ByteBuffer.allocate(Integer.SIZE/8);
		buff.order(order);
 
		// buff������� 4�� ������
		// bytes�� put�ϸ� position�� limit�� ���� ��ġ�� ��.
		buff.put(bytes);
		// flip()�� ���� �Ǹ� position�� 0�� ��ġ �ϰ� ��.
		buff.flip();
 
		return buff.getInt(); // position��ġ(0)���� ���� 4����Ʈ�� int�� �����Ͽ� ��ȯ
	}

	public String getApiName() {
		return apiName;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getMdn() {
		return mdn;
	}

	public void setMdn(String mdn) {
		this.mdn = mdn;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getReservedMsgSize() {
		return reservedMsgSize;
	}

	public void setReservedMsgSize(int reservedMsgSize) {
		this.reservedMsgSize = reservedMsgSize;
	}
	
	public void setApiHost(String apiHost){
		this.apiHost = apiHost;
	}
	
	public void setApiPort(int apiPort){
		this.apiPort = apiPort;
	}
	
	public String getApiHost(){
		return this.apiHost;
	}
	
	public int getApiPort(){
		return this.apiPort;
	}
	
	public void parsingBody(String body, String seperator){
				
		List<String[]> params = new ArrayList<String[]>();
		StringTokenizer st = new StringTokenizer(body, seperator);
		
		while(st.hasMoreTokens()){
			String []param = st.nextToken().split("=");			
			params.add(param);			
		}
		
		for (int i = 0; i < params.size() ; i++){			
			if( params.get(i)[0].equals("mdn") )
				mdn = params.get(i)[1];
			
			if( params.get(i)[0].equals("imsi") )
				imsi = params.get(i)[1];			
		}
		
	}
		
}
