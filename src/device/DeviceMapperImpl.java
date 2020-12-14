package device;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import com.google.gson.Gson;
import com.pi4j.component.motor.impl.GpioStepperMotorComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceMapperImpl implements DeviceMapper {
	private static int lockDeviceNo;
	private static String lockStatus;
	private static String url;
	
	static {
		try {
			File file = new File("/home/pi/workspace/config.properties");
			
			Properties properties = new Properties();
			properties.load(new FileInputStream(file));
			
			lockDeviceNo = Integer.parseInt(properties.getProperty("lockDeviceNo"));
			url = properties.getProperty("url");
			lockStatus = "C";
			
			System.out.println("lockDeviceNo : " + lockDeviceNo);
			System.out.println("url : " + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String viewCardInfo() throws Exception {
		String cardNo = "";
		String uid[] = null;
        
		Process process = Runtime.getRuntime().exec("nfc-poll");
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        
		String line = "";
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("UID")) {
				uid = line.split("\\s");
				for (int i = 0; i < uid.length; i++) {
					cardNo = cardNo + uid[i];
				}
				
				cardNo = cardNo.split(":")[1];
			}
		}
		
		return cardNo;
	}

	@Override
	public String sendAuthInfo(String cardNo) throws Exception {
		if (!cardNo.isEmpty()) {
			Message message = new Message();
			message.setCardNo(cardNo);
			message.setLockDeviceNo(lockDeviceNo);
			message.setStatus(lockStatus);
			message.setCode("200");
			
			Gson gson = new Gson();
			String jsonMsg = gson.toJson(message);
			
			OkHttpClient client = new OkHttpClient();
			
			Request request = new Request.Builder()
			        .url(url)
			        .post(RequestBody.create(MediaType.parse("application/json; charset=utf8"), jsonMsg))
			        .build();
			
			Response response = client.newCall(request).execute();
			
			if (response.isSuccessful()) {
				String responsMsg = response.body().string();
				System.out.println("responseMsg :" + responsMsg);
				
				Message responseMessage = gson.fromJson(responsMsg, Message.class);
				
				if (responseMessage.getCode().equals("200")) {
					return responseMessage.getStatus();
				}
			} else {
				
				return null;
			}
		}
		
		return null;
	}

	@Override
	public void controlMotor(String status) throws Exception {
		if (status != null 
				&& !lockStatus.equals(status)) {
			final GpioController gpio = GpioFactory.getInstance();
			
			final GpioPinDigitalOutput[] pins = {
				    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, PinState.LOW),
				    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW),
				    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW),
				    gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW) };
			
			gpio.setShutdownOptions(true, PinState.LOW, pins);
			
			GpioStepperMotorComponent motor = new GpioStepperMotorComponent(pins);
			
			byte[] double_step_sequence = new byte[4];
		    double_step_sequence[0] = (byte) 0b0011;
		    double_step_sequence[1] = (byte) 0b0110;
		    double_step_sequence[2] = (byte) 0b1100;
		    double_step_sequence[3] = (byte) 0b1001;
		    
		    motor.setStepInterval(2);
			motor.setStepSequence(double_step_sequence);
			
			motor.setStepsPerRevolution(2038);
			
			if ("O".equals(status)) {
				motor.rotate(0.25);
				
				lockStatus = "O";
			} else {
				motor.rotate(-0.25);
				
				lockStatus = "C";
			}
			
			motor.stop();
			
			gpio.shutdown();
			
			for (GpioPinDigitalOutput pin : pins) {
				gpio.unprovisionPin(pin);
			}
		}
	}
	
	@Override
	public void controlLED() throws Exception {
		Pin OUTPUT_GPIO_06 = RaspiPin.GPIO_06;
		
		GpioController gpio = GpioFactory.getInstance();
		
		GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(OUTPUT_GPIO_06, "LED", PinState.LOW);
		
		for (int i = 0; i < 3; i++) {
			pin.high();
			Thread.sleep(500);
			
			pin.low();
			Thread.sleep(500);
		}
		
		gpio.shutdown();
		
		gpio.unprovisionPin(pin);
	}
}
