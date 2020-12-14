package device;

public class DeviceApplication {
	public static void main(String[] args) {
		DeviceServiceImpl service = new DeviceServiceImpl();
		
		try {
			while (true) {
				service.runDevice();
				Thread.sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
