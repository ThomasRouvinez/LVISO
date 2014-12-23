// -----------------------------------------------------------
// Variables.
// -----------------------------------------------------------

final int width = window.innerWidth;
final int height = window.innerHeight;
final float framerate = 0.5;

// Lists.
ArrayList sensors;
ArrayList participants;
ArrayList positioningValues; //([0] = multf)
HashMap<String,String> participantColor;
HashMap<Integer,Integer> tableCount;
HashMap<Integer,Integer> tableDrawn;

// -----------------------------------------------------------
// Setup.
// -----------------------------------------------------------

void setup() {
	size(width,height);
	frameRate(framerate);
	sensors = new ArrayList();
	participants = new ArrayList();
	positioningValues = new ArrayList();
	participantColor = new HashMap<String,String>();
	tableCount = new HashMap<Integer,Integer>();
	tableDrawn = new HashMap<Integer,Integer>();
}

// -----------------------------------------------------------
// Game loop.
// -----------------------------------------------------------

void draw(){
	// Initial setup.
	background(200);
	fill(#000000);
	rect(0, height-70, width, 70);
	
	// Draw all black circles for shadowing.
	for(int p=0, end=sensors.size(); p<end; p++){
		Sensor pt = (Sensor) sensors.get(p);
		pt.draw(); 
	}
	
	// Draw all participants.
	for(int p=0, end=participants.size(); p<end; p++){
		Participant pa = (Participant) participants.get(p);
		
		// Compute slice representation.
		float slice = 360.0 / tableCount.get(pa.sensor.id);
		int drawn = tableDrawn.get(pa.sensor.id);
		
		// Get appropriate color.
		String tagColor = participantColor.get(pa.tagID);
		tagColor = "FF" + tagColor;
		
		// Draw the participant.
		fill(unhex(tagColor));
		stroke(0, 0, 0);
		drawSegment(pa.sensor.x, pa.sensor.y, 100, 100, drawn * slice, (drawn + 1) * slice);
		tableDrawn.put(pa.sensor.id, tableDrawn.get(pa.sensor.id) + 1);
	}
	
	// Draw all sensors last to cover up the colors.
	for(int p=0, end=sensors.size(); p<end; p++){
		Sensor pt = (Sensor) sensors.get(p);
		pt.draw(); 
	}
}

// -----------------------------------------------------------
// Functions.
// -----------------------------------------------------------

Sensor addSensor(int id, int x, int y, int radius){
	Sensor pt = new Sensor(id, x, y, radius);
    sensors.add(pt);
    return pt;
}

Participant addParticipant(int sensorID, float rssi, String tagID){
	Participant pa = new Participant(sensorID, rssi, tagID);
	participants.add(pa);
	return pa;
}

void addParticipantConfig(String tagID, String color){
	String tagColor = color.replace('#','');
	participantColor.put(tagID, tagColor);
}

void clearParticipants(){
	// Remove all participants from last iteration.
	participants.clear();
	
	for (Map.Entry entry : tableCount.entrySet()){
		tableCount.put(entry.getKey(), 0);
		tableDrawn.put(entry.getKey(), 0);
	}
}

void addPosValue(float value){
	positioningValues.add(value);
}

void drawSegment(int x, int y, int width, int height, float startAngle, float stopAngle){ 
	arc(x, y, width, height, radians(startAngle), radians(stopAngle));
}

// -----------------------------------------------------------
// Classes.
// -----------------------------------------------------------

class Sensor{
    int id, x, y, radius;
	
    Sensor(int id, int x, int y, int radius){
		this.id=id; 
		this.x=x; 
		this.y=y; 
		this.radius=radius;

		// Initialize the tables.
		tableCount.put(this.id, 0);
		tableDrawn.put(this.id, 0);
	}
	
    void draw() {
		stroke(255,255,255);
		fill(#000000);
		ellipse(x,y,60,60);
	}
}

class Participant{
	String tagID;
	int sensorID;
	float rssi;
	Sensor sensor;
	
	Participant(int sensorID, float rssi, String tagID){
		this.sensorID=sensorID; 
		this.rssi=rssi;
		this.tagID=tagID;
		
		// Find table x-y coordinates.
		for(int p=0, end=sensors.size(); p<end; p++){
			Sensor pt = (Sensor) sensors.get(p);
			
			if(pt.id == sensorID){
				this.sensor = pt;
				tableCount.put(this.sensorID, tableCount.get(sensorID) + 1);
				break;
			}
		}
	}
}