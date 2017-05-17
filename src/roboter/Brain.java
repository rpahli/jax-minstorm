package roboter;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

public class Brain {

	private final MovePilot p;
	private final EV3ColorSensor cL, cR;
	private final EV3TouchSensor ts;
	private final RegulatedMotor klappe;
	public Brain(MovePilot pilot, EV3ColorSensor colorLeft, EV3ColorSensor colorRight, EV3TouchSensor touchSensor, RegulatedMotor klappe) {
		ts = touchSensor;
		p=pilot;
		cL= colorLeft;
		cR= colorRight;
		this.klappe = klappe;
	}

	public State updateState(State current) {
		//Finde Sensorzustand
		
		int lC = cL.getColorID(), rC = cR.getColorID();
		
		Program.out(lC+", "+rC + " " + ts.getCurrentMode());
		
		switch(current) {
		case FAHR_ZUR_COMPANY_WEG_VORAUS:
		case FAHR_ZUR_COMPANY_WEG_LINKS:
		case FAHR_ZUR_COMPANY_WEG_RECHTS:
			if (isBlue(lC) || isBlue(rC)) {
				return State.UNLOAD;
			}
			
			if(isWhite(lC) && isRed(rC)) {
				return State.FAHR_ZUR_COMPANY_WEG_RECHTS;
			}
			
			if (isRed(lC) && isWhite(rC)) {
				return State.FAHR_ZUR_COMPANY_WEG_LINKS;
			}
			
			return State.FAHR_ZUR_COMPANY_WEG_VORAUS;
		
			
		case FIND_LOADING_ZONE_WEG_VORAUS:
		case FIND_LOADING_ZONE_WEG_LINKS:
		case FIND_LOADING_ZONE_WEG_RECHTS:
			if (isBlue(lC) || isBlue(rC)) {
				return State.LOAD;
			}
			
			if(isWhite(lC) && isRed(rC)) {
				return State.FIND_LOADING_ZONE_WEG_RECHTS;
			}
			
			if (isRed(lC) && isWhite(rC)) {
				return State.FIND_LOADING_ZONE_WEG_LINKS;
			}
			
			return State.FIND_LOADING_ZONE_WEG_VORAUS;
		
		case UNLOAD:
			return State.RUMDREHN;
			//drehen FIXME
		case RUMDREHN:
			return State.FIND_LOADING_ZONE_WEG_VORAUS;
		case LOAD:
		case IDLE:
			SensorMode tm = ts.getTouchMode();
			float[] t = new float[tm.sampleSize()];
			tm.fetchSample(t, 0);
			if(t[0]==1) {
				
				return State.FAHR_ZUR_COMPANY_WEG_VORAUS;
			}
		default:
		}
		
		return State.IDLE;
	}

	private boolean isBlue(int color) {
		return color == 2;
	}
	
	private boolean isWhite(int color) {
		return color == 6;
	}
	
	private boolean isRed(int color) {
		return color == 0;
	}

	public void progressInState(State current) {
		switch(current) {
		case LOAD:
			onArrivedAtLoadingZone(); break;
			
		case FAHR_ZUR_COMPANY_WEG_VORAUS:
			onFollowWay(); break;
		case FAHR_ZUR_COMPANY_WEG_RECHTS:
			onFindWayToTheRight(); break;
		case FAHR_ZUR_COMPANY_WEG_LINKS:
			onFindWayToTheLeft(); break;
			
		case UNLOAD:
			onArrivedAtCompany(); break;
			
		case FIND_LOADING_ZONE_WEG_VORAUS:
			onFollowWay(); break;
		case FIND_LOADING_ZONE_WEG_LINKS:
			onFindWayToTheLeft(); break;
		case FIND_LOADING_ZONE_WEG_RECHTS:
			onFindWayToTheRight(); break;
			
		case RUMDREHN:
			onRumdrehn(); break;
		default:
		case IDLE:
			onIdle(); break;
		
		}
	}

	private void onRumdrehn() {
		
		p.rotate(180);
		// TODO Auto-generated method stub
		
	}

	private void onFollowWay() {
		
		//drive ahead
		forward();
		
		
	}

	private void onFindWayToTheRight() {
		
		//turn right 10°
		p.arc(+9.0, 15);
		//drive ahead
		forward();
		
	}

	private void onFindWayToTheLeft() {
		
		//turn left 10°
		p.arc(-9.0, 15);
		//drive ahead
		forward();
		
	}

	private void onArrivedAtLoadingZone() {
		//stop driving
		p.stop();
		//beep
		beep();
	}

	private void onArrivedAtCompany() {
		//stop driving
		p.stop();
		//beep;
		beep();
		//start unload-thingie
		klappe.forward();
		//wait
		Delay.msDelay(500);
		//stop unload-thingie
		klappe.stop();
		
//		Delay.msDelay(500);
//		klappe.forward();
//		Delay.msDelay(500);;
//		klappe.stop();
	}


	private void onIdle() {

		
	}

	private void beep() {
		Sound.playTone(450, 100);		
	}
	
	private void forward() {
		if (!p.isMoving())
		p.backward();
	}
	
	
}
