package shocid.particleSwarmOptimization;
/**
 *
 * @author gandhim
 */
public class Particle {
 private Position location;
 private Velocity velocity;
 private double fitness;

 public double getFitness() {
 calculateFitness();
 return fitness;
 }

 public void calculateFitness() {
 double x = this.location.getX();
 double y = this.location.getY();

 fitness = Math.pow((2.8125 - x + x * Math.pow(y, 4)), 2) +
 Math.pow((2.25 - x + x * Math.pow(y, 2)), 2) +
 Math.pow((1.5 - x + x * y), 2);
 }

 public Position getLocation() {
 return location;
 }

 public void setLocation(Position location) {
 this.location = location;
 }

 public Velocity getVelocity() {
 return velocity;
 }

 public void setVelocity(Velocity velocity) {
 this.velocity = velocity;
 }
}