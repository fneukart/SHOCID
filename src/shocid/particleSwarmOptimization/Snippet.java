//package shocid.particleSwarmOptimization;
//
//import java.util.Random;
//
//public class Snippet {
//	private static final int SWARM_SIZE = 0;
//
//	private void initializeSwarm() {
//	 Particle p;
//	 Random generator = new Random();
//	
//	 for (int i = 0; i < SWARM_SIZE; i++) {
//	 p = new Particle();
//	 double posX = generator.nextDouble() * 3.0 + 1.0;
//	 double posY = generator.nextDouble() * 2.0 - 1.0;
//	 p.setLocation(new Position(posX, posY));
//	
//	 double velX = generator.nextDouble() * 2.0 - 1.0;
//	 double velY = generator.nextDouble() * 2.0 - 1.0;
//	 p.setVelocity(new Velocity(velX, velY));
//	
//	 swarm.add(p);
//	 }
//	 }
//	
//	public void execute() {
//	 Random generator = new Random();
//	 initializeSwarm();
//	
//	 evolutionaryStateEstimation();
//	
//	 int t = 0;
//	 double w;
//	
//	 while (t < MAX_ITERATION) {
//	 // calculate corresponding f(i,t) corresponding to location x(i,t)
//	 calculateAllFitness();
//	
//	 // update pBest
//	 if (t == 0) {
//	 for (int i = 0; i < SWARM_SIZE; i++) {
//	 pBest[i] = fitnessList[i];
//	 pBestLoc.add(swarm.get(i).getLocation());
//	 }
//	 } else {
//	 for (int i = 0; i < SWARM_SIZE; i++) {
//	 if (fitnessList[i] < pBest[i]) {
//	 pBest[i] = fitnessList[i];
//	 pBestLoc.set(i, swarm.get(i).getLocation());
//	 }
//	 }
//	 }
//	
//	 int bestIndex = getBestParticle();
//	 // update gBest
//	 if (t == 0 || fitnessList[bestIndex] < gBest) {
//	 gBest = fitnessList[bestIndex];
//	 gBestLoc = swarm.get(bestIndex).getLocation();
//	 }
//	
//	 w = W_UP - (((double) t) / MAX_ITERATION) * (W_UP - W_LO);
//	
//	 for (int i = 0; i < SWARM_SIZE; i++) {
//	 // update particle Velocity
//	 double r1 = generator.nextDouble();
//	 double r2 = generator.nextDouble();
//	 double lx = swarm.get(i).getLocation().getX();
//	 double ly = swarm.get(i).getLocation().getY();
//	 double vx = swarm.get(i).getVelocity().getX();
//	 double vy = swarm.get(i).getVelocity().getY();
//	 double pBestX = pBestLoc.get(i).getX();
//	 double pBestY = pBestLoc.get(i).getY();
//	 double gBestX = gBestLoc.getX();
//	 double gBestY = gBestLoc.getY();
//	
//	 double newVelX = (w * vx) + (r1 * C1) * (pBestX - lx) + (r2 * C2) * (gBestX - lx);
//	 double newVelY = (w * vy) + (r1 * C1) * (pBestY - ly) + (r2 * C2) * (gBestY - ly);
//	 swarm.get(i).setVelocity(new Velocity(newVelX, newVelY));
//	
//	 // update particle Location
//	 double newPosX = lx + newVelX;
//	 double newPosY = ly + newVelY;
//	 swarm.get(i).setLocation(new Position(newPosX, newPosY));
//	 }
//	
//	 t++;
//	 }
//	 }
//	
//}

