import oscP5.*;
import netP5.*;
  
OscP5 oscP5;
DrumDots ds;
Rain r;


void setup() {
  size(800,800);
  stroke(255);
  oscP5 = new OscP5(this,12000);  
  ds = new DrumDots();
  r = new Rain();
}
void draw() {
  background(0);
  ds.run();
  r.run();
}

void oscEvent(OscMessage msg) {
  float freq = msg.get(0).floatValue();
  float amp = msg.get(1).floatValue();
  String name = msg.get(2).toString();
  
  println("SuperCollider says", "freq", freq, "amp", amp, name); 

  if(name.equals("kick")) {
  ds.makeDot(freq, height/2);
  }
  if(name.equals("bass")) {
   ds.makeDot(50, round(map(freq, 87, 124, height-100, 400)));
  }
  if(name.equals("hihat")) {
    ds.makeDot(amp*200 + 10, 50);
    ds.makeDot(amp*200 + 10, height - 50);
  }
  if(name.equals("boohp")) {
    r.makeRain(freq, amp);
  }
    
}

class Rain {
  ArrayList<Drop> rain;
  
  //Constructor
  Rain() {
    rain = new ArrayList<Drop>();
  }
  
  //Runs the Rain
  void run() {
    for(int i =0; i < rain.size(); i++) {
      Drop d = rain.get(i);
      d.run();
      if(d.outBounds()) {
       rain.remove(i);
      }
    }
  }
  
  void makeRain(float freq, float amp) {
    PVector vect;
    PVector vel;
    int spd;
    for(int i = 0; i < height; i = i + 20) {
      for(int k = 0; k < width; k = k + 20) {
        vect = new PVector(i, k);
        spd = round(5*freq/1059);
        vel = new PVector(spd, spd);
        Drop d = new Drop(vect, vel);
        rain.add(d);
      }
    }
  }

}

class Drop {
  PVector location;
  PVector velocity;
  
  
  Drop(PVector pos, PVector vel) {
    velocity = vel;
    location = pos;
  }
  void display() {
    stroke(255);
    fill(255, 255, 255);
    line(location.x, location.y, location.x + 10, location.y + 10);
  }
  
  void update() {
    location.add(velocity);
      
  }
  
  //Runs update and display methods
  void run() {
    update();
    display();
  }
  
  //Checks to see if drop is out of bounds. Used to destroy drops off screen
  boolean outBounds() {
    return ((location.x > width) && (location.y > height));
  }
}
  
  
class DrumDots {
  ArrayList<Dot> dots;
  
  //Constructor
  DrumDots() {
    dots = new ArrayList<Dot>();
  }
  
  //Runs the Dots
  void run() {
    for(int i =0; i < dots.size(); i++) {
      Dot d = dots.get(i);
      d.run();
      if(d.outBounds()) {
       dots.remove(i);
      }
    }
  }
  
  void makeDot(float freq, int pos){
    Dot d = new Dot(freq, pos);
    dots.add(d);
  }
  
  void applyBass(float amp) {
    for(int i = 0; i < dots.size(); i++) {
      Dot d = dots.get(i);
      d.applyBass(amp);
    }
  }
}

class Dot {
  PVector location;
  float size = 25;
  PVector velocity = new PVector(5, 0);
  
  
  Dot(float freq, int pos) {
    location = new PVector(0, pos);
    size = freq/2;
  }
  void display() {
    stroke(255);
    fill(255, 255, 255);
    ellipse(location.x, location.y, size, size);
  }
  
  void update() {
    location.add(velocity);
      
  }
  
  //Runs update and display methods
  void run() {
    update();
    display();
  }
  
  //Checks to see if dot is out of bounds. Used to destroy dots off screen
  boolean outBounds() {
    return (location.x > width);
  }
  
  //Effects the y-coordinate of the circles for the drum
  void applyBass(float amp) {
    size = size*0.5;
  }
}