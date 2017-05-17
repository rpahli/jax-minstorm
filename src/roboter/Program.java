package roboter;

import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class Program {

	public static void out(String str) {
		LCD.clear(0);
		LCD.drawString(str, 0, 0);
		System.out.println(str);
	}

	public static void printStatus() {
		LCD.clear(1);
		LCD.drawString(String.format("State: %s", current), 0, 1);
	}

	private static boolean alive = true;
	private static State current = State.IDLE;
	private static Brain strategy = null;

	public static void main(String[] args) {
		setup();
		Delay.msDelay(100);
		mainLoop();
	}

	private static void setup() {
		
		RegulatedMotor motorLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		RegulatedMotor motorRight = new EV3LargeRegulatedMotor(MotorPort.D);
		
		Wheel[] wheels = { WheeledChassis.modelWheel(motorLeft,  3.5).offset(9.0),
				WheeledChassis.modelWheel(motorRight, 3.5).offset(-9.0)
		};
		
		Chassis chassis = new WheeledChassis(wheels, WheeledChassis.TYPE_DIFFERENTIAL);
	
		
		final MovePilot pilot = new MovePilot(chassis);
		
	
		RegulatedMotor klappe = new EV3MediumRegulatedMotor(MotorPort.B);
	
		
		EV3ColorSensor colorLeft = new EV3ColorSensor(SensorPort.S1);
		EV3ColorSensor colorRight = new EV3ColorSensor(SensorPort.S4);
		
		EV3TouchSensor ts = new EV3TouchSensor(SensorPort.S2);
		
		strategy = new Brain(pilot, colorLeft, colorRight,ts,klappe);

		Button.ENTER.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(Key k) {
				alive = false;
//				current = State.IDLE;
//				pilot.stop();
			}

			@Override
			public void keyPressed(Key k) {
				// TODO Auto-generated method stub

			}
		});
		
//		Button.LEFT.addKeyListener(new KeyListener() {
//			@Override
//			public void keyReleased(Key k) {
//				alive = false;
////				current = State.IDLE;
////				pilot.stop();
//			}
//
//			@Override
//			public void keyPressed(Key k) {
//				// TODO Auto-generated method stub
//
//			}
//		});

	}

	private static void mainLoop() {
		while (alive) {

			current = strategy.updateState(current);
			printStatus();

			strategy.progressInState(current);
			Delay.msDelay(10);

		}
	}

}
