// -----------------------------------------------------------
// Variables.
// -----------------------------------------------------------

// Constants.
final int rssiThreshold = 12;	// to be defined with real rests on the readers.
final int width = window.innerWidth;
final int height = window.innerHeight;
final float framerate = 1;
final int tableSize = height / 12;
final int firstRawSize = height / 7;

// Lists.
ArrayList sensors;
ArrayList participants;
ArrayList positioningValues; //([0] = multf)

// Hashmaps.
HashMap<String,String> participantColor;
HashMap<String,String> interestTags;
HashMap<Integer,Integer> tableCountFirstRow;
HashMap<Integer,Integer> tableDrawnFirstRow;
HashMap<Integer,Integer> tableCountSecondRow;
HashMap<Integer,Integer> tableDrawnSecondRow;

// -----------------------------------------------------------
// Setup & Initialization.
// -----------------------------------------------------------

void setup() {
	size(width,height);
	frameRate(framerate);
	
	sensors = new ArrayList();
	participants = new ArrayList();
	positioningValues = new ArrayList();
	
	participantColor = new HashMap<String,String>();
	interestTags = new HashMap<String,String>();
	
	tableCountFirstRow = new HashMap<Integer,Integer>();
	tableDrawnFirstRow = new HashMap<Integer,Integer>();
	tableCountSecondRow = new HashMap<Integer,Integer>();
	tableDrawnSecondRow = new HashMap<Integer,Integer>();
}

// -----------------------------------------------------------
// Game loop.
// -----------------------------------------------------------

void draw(){
	// Background.
	fill(#ffffff);
	rect(0, 0, width, height-(height / 10));
	
	// Draw sensors backgrounds.
	for(int p=0, end=sensors.size(); p<end; p++){
		Sensor pt = (Sensor) sensors.get(p);
		
		if(tableCountFirstRow.get(pt.id) > 0){
			stroke(0,0,0);
			fill(#000000);
			ellipse(pt.x,pt.y,firstRawSize,firstRawSize);
		}
	}
	
	// Draw all participants.
	for(int p=0, end=participants.size(); p<end; p++){
		Participant pa = (Participant) participants.get(p);
		
		// Compute slice representation.
		float slice = 360.0 / tableCountFirstRow.get(pa.sensor.id);
		int drawn = tableDrawnFirstRow.get(pa.sensor.id);
		
		// Get appropriate color.
		String tagColor = participantColor.get(pa.tagID);
		tagColor = "FF" + tagColor;
		
		// Draw the participant.
		fill(unhex(tagColor));
		stroke(0, 0, 0);
		drawSegment(pa.sensor.x, pa.sensor.y, firstRawSize, firstRawSize, (drawn * slice)+2, ((drawn + 1) * slice) -2);
		tableDrawnFirstRow.put(pa.sensor.id, tableDrawnFirstRow.get(pa.sensor.id) + 1);
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
	participants = new ArrayList();
	
	for (Map.Entry entry : tableCountFirstRow.entrySet()){
		tableCountFirstRow.put(entry.getKey(), 0);
		tableDrawnFirstRow.put(entry.getKey(), 0);
	}
}

void addPosValue(float value){
	positioningValues.add(value);
}

void addInterestColor(String color, String description){
	if(interestTags.containsKey(color.replace('#','')) == false){
		String tagColor = color.replace('#','');
		interestTags.put(tagColor, description);
	}
}

void drawSegment(int x, int y, int width, int height, float startAngle, float stopAngle){ 
	arc(x, y, width, height, radians(startAngle), radians(stopAngle));
}

void drawLegendBar(){
	// Compute display.
	int loop = 0;
	int count = interestTags.size();
	int spacing = (width - (width / 10)) / count;
	int leftMargin = (width / 20);
	int topMargin = height-(height / 11);
	int fontSize = width / 55;
	
	// Draw the bar.
	fill(#000000);
	rect(0, height-(height / 10), width, (height / 10));
	
	// Draw the legends.
	for (Map.Entry entry : interestTags.entrySet()){
		// Get appropriate color.
		String tagColor = entry.getKey();
		tagColor = "FF" + tagColor;
		
		// Draw the participant.
		fill(unhex(tagColor));
		rect(leftMargin + (loop * spacing), topMargin, (height / 20), (height / 20));
		
		fill(#ffffff);
		textSize(fontSize);
		text(entry.getValue(), leftMargin + (loop * spacing) + (height / 18), topMargin + (height / 25)); 
		
		loop += 1;
	}
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
		tableCountFirstRow.put(this.id, 0);
		tableDrawnFirstRow.put(this.id, 0);
	}
	
    void draw() {
		stroke(255,255,255);
		fill(#000000);
		ellipse(x,y,tableSize,tableSize);
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
				tableCountFirstRow.put(this.sensorID, tableCountFirstRow.get(sensorID) + 1);
				break;
			}
		}
	}
}