package device;

public class DeviceServiceImpl implements DeviceService {
	public void runDevice() throws Exception {
		DeviceMapper deviceMapper = new DeviceMapperImpl();
		
		String cardNo = deviceMapper.viewCardInfo();
		System.out.println("cardNo : " + cardNo);
		if (cardNo != null 
				&& !(cardNo.trim().isEmpty())) {
			String status = deviceMapper.sendAuthInfo(cardNo);
			System.out.println("status : " + status);
			if (status != null) {
				deviceMapper.controlMotor(status);
			} else {
				deviceMapper.controlLED();
			}
		}
	}
}
