package ca.ubc.ece.cpen221.mp4.ai;

import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.commands.BreedCommand;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.EatCommand;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.ArenaAnimal;
import ca.ubc.ece.cpen221.mp4.items.animals.Fox;
import ca.ubc.ece.cpen221.mp4.items.animals.Rabbit;

/**
 * Your Rabbit AI.
 * 
 * If the rabbit is next to some grass and it is not at maximum energy, it eats the grass.
 * Otherwise, if it's at greater than 50% energy, it breeds in a random, open space.
 * Otherwise, it moves in the direction where it sees the least foxes.
 * If it doesn't see any foxes, it moves towards the closest piece of grass.
 * If it doesn't see any grass, it moves in the direction it sees the most rabbits in.
 */
public class RabbitAI extends AbstractAI {

    private int[] xAround = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private int[] yAround = { -1, -1, 0, 1, 1, 1, 0, -1 };

	public RabbitAI() {
	}

	private Item closestGrass(ArenaWorld world, ArenaAnimal animal) {
        Set<Item> possibleGrass = world.searchSurroundings(animal);
        Iterator<Item> it = possibleGrass.iterator();
        Item closestGrass = animal;
        int distance = Integer.MAX_VALUE;
        while (it.hasNext()) {
            Item item = it.next();
            if ( item.getName().equals("grass") && (animal.getLocation().getDistance(item.getLocation()) < distance)) {
                closestGrass = item;
                distance = animal.getLocation().getDistance(item.getLocation());
            }
        }
        if(distance < Integer.MAX_VALUE)
            return closestGrass;
        else
            return null;
    }
	
	   private Item closestFox(ArenaWorld world, ArenaAnimal animal) {
	        Set<Item> possibleFox = world.searchSurroundings(animal);
	        Iterator<Item> it = possibleFox.iterator();
	        Item closestFox = animal;
	        int distance = Integer.MAX_VALUE;
	        while (it.hasNext()) {
	            Item item = it.next();
	            if ( item.getName().equals("Fox") && (animal.getLocation().getDistance(item.getLocation()) < distance)) {
	                closestFox = item;
	                distance = animal.getLocation().getDistance(item.getLocation());
	            }
	        }
	        if(distance < Integer.MAX_VALUE)
	            return closestFox;
	        else
	            return null;
	    }
	
	private Direction leastFoxes(ArenaWorld world, ArenaAnimal animal) {
        Set<Item> possibleFoxes = world.searchSurroundings(animal);
        Iterator<Item> it = possibleFoxes.iterator();
        int northFoxes = 0;
        int southFoxes = 0;
        int eastFoxes = 0;
        int westFoxes = 0;
        while (it.hasNext()) {
            Item item = it.next();
            if ( item.getName().equals("Fox") ) {
                if((item.getLocation().getX() - animal.getLocation().getX()) > 0) eastFoxes ++;
                else if((item.getLocation().getX() - animal.getLocation().getX()) < 0) westFoxes ++;
                if((item.getLocation().getY() - animal.getLocation().getY()) > 0) southFoxes ++;
                else if((item.getLocation().getY() - animal.getLocation().getY()) < 0) northFoxes ++;
            }
        }
        if( eastFoxes <= westFoxes && eastFoxes <= northFoxes && eastFoxes <= southFoxes) return Direction.EAST;
        if( westFoxes <= eastFoxes && westFoxes <= northFoxes && westFoxes <= southFoxes) return Direction.WEST;
        if( northFoxes <= westFoxes && northFoxes <= eastFoxes && northFoxes <= southFoxes) return Direction.NORTH;
        else return Direction.SOUTH;
    }
	
	private Direction mostRabbits(ArenaWorld world, ArenaAnimal animal) {
        Set<Item> possibleRabbits = world.searchSurroundings(animal);
        Iterator<Item> it = possibleRabbits.iterator();
        int northRabbits = 0;
        int southRabbits = 0;
        int eastRabbits = 0;
        int westRabbits = 0;
        while (it.hasNext()) {
            Item item = it.next();
            if ( item.getName().equals("Rabbit") ) {
                if((item.getLocation().getX() - animal.getLocation().getX()) > 0) eastRabbits ++;
                else if((item.getLocation().getX() - animal.getLocation().getX()) < 0) westRabbits ++;
                if((item.getLocation().getY() - animal.getLocation().getY()) > 0) southRabbits ++;
                else if((item.getLocation().getY() - animal.getLocation().getY()) < 0) northRabbits ++;
            }
        }
        if( eastRabbits >= westRabbits && eastRabbits >= northRabbits && eastRabbits >= southRabbits) return Direction.EAST;
        if( westRabbits >= eastRabbits && westRabbits >= northRabbits && westRabbits >= southRabbits) return Direction.WEST;
        if( northRabbits >= westRabbits && northRabbits >= eastRabbits && northRabbits >= southRabbits) return Direction.NORTH;
        else return Direction.SOUTH;
    }
	
    private Direction goThisWay(ArenaWorld world, ArenaAnimal animal,
            Item goal) {
        int xDistance = goal.getLocation().getX() - animal.getLocation().getX();
        int yDistance = goal.getLocation().getY() - animal.getLocation().getY();
        if(xDistance != 0 && Math.abs(xDistance) > Math.abs(yDistance)){
            if(xDistance > 0) return Direction.EAST;
            else return Direction.WEST;
        } else {
            if(yDistance > 0) return Direction.SOUTH;
            else return Direction.NORTH;
        }
    }
    
    public boolean isLocationEmpty(ArenaWorld world, ArenaAnimal animal, Location location) {
        if (!Util.isValidLocation(world, location)) {
            return false;
        }
        Set<Item> possibleMoves = world.searchSurroundings(animal);
        Iterator<Item> it = possibleMoves.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getLocation().equals(location)) {
                return false;
            }
        }
        return true;
    }
	
	@Override
	public Command getNextAction(ArenaWorld world, ArenaAnimal animal) {
		Item closestGrass = closestGrass(world, animal);
		Item closestFox = closestFox(world, animal);
		boolean full = animal.getEnergy() == animal.getMaxEnergy();
		if(!full && closestGrass != null && animal.getLocation().getDistance(closestGrass.getLocation()) == 1){
            return new EatCommand(animal, closestGrass);
		} else if ( animal.getEnergy() > animal.getMaxEnergy() * 0.5 ){
            int i = 0;
            while(i<xAround.length){
                Location breedLocation = new Location(animal.getLocation().getX() + xAround[i], animal.getLocation().getY() + yAround[i]);
                if(isLocationEmpty(world, animal, breedLocation)){
                    return new BreedCommand(animal, breedLocation);
                }
                i++;
            }
		} else if (closestFox != null){
            Direction goThisWay = leastFoxes(world, animal);
            Location moveLocation = new Location(animal.getLocation(), goThisWay);
            if(isLocationEmpty(world, animal, moveLocation)){
                return new MoveCommand(animal, moveLocation);
            }
		} else if (closestGrass != null){
            Direction goThisWay = goThisWay(world, animal, closestGrass);
            Location moveLocation = new Location(animal.getLocation(), goThisWay);
            if(isLocationEmpty(world, animal, moveLocation)){
                return new MoveCommand(animal, moveLocation);
            } else return new WaitCommand();
        } else {
            Direction goThisWay = mostRabbits(world, animal);
            Location moveLocation = new Location(animal.getLocation(), goThisWay);
            if(isLocationEmpty(world, animal, moveLocation)){
                return new MoveCommand(animal, moveLocation);
            }
        }
	    // TODO: Change this. Implement your own AI rules.
            
		return new WaitCommand();
	}
}
