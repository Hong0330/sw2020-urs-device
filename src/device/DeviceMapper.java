package device;

public interface DeviceMapper {
	public String viewCardInfo() throws Exception;
	public String sendAuthInfo(String cardNo) throws Exception;
	public void controlMotor(String status) throws Exception;
	public void controlLED() throws Exception;
}
