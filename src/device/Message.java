package device;

import java.io.Serializable;

public class Message implements Serializable {
	private String code;
	private String cardNo;
	private String status;
	private int lockDeviceNo;
	
	public Message() {
	}

	public Message(String code, String cardNo, String status, int lockDeviceNo) {
		super();
		this.code = code;
		this.cardNo = cardNo;
		this.status = status;
		this.lockDeviceNo = lockDeviceNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getLockDeviceNo() {
		return lockDeviceNo;
	}

	public void setLockDeviceNo(int lockDeviceNo) {
		this.lockDeviceNo = lockDeviceNo;
	}
}
