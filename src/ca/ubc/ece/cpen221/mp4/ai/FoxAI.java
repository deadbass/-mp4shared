package ca.ubc.ece.cpen221.mp4.ai;

import java.util.Iterator;
import java.util.Set;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.BreedCommand;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.EatCommand;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.Item;
import ca.ubc.ece.cpen221.mp4.items.animals.*;

/**
 * Your Fox AI.
 * 
 * If the fox is next to a rabbit and it is not 'full', it eats the rabbit.
 * Otherwise, if the fox is at >50% energy, it breeds in a random, open space.
 * Otherwise, the fox moves towards the closest rabbit.  
 * If no rabbit is within sight, the fox moves in the direction in which it sees the fewest foxes.
 * If it sees no rabbits and no foxes, it waits.  Patiently...
 * 
 */
public class FoxAI extends AbstractAI {

    private int[] xAround = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private int[] yAround = { -1, -1, 0, 1, 1, 1, 0, -1 };
    
    public FoxAI() {

	}
	
	private Item closestRabbit(ArenaWorld world, ArenaAnimal animal) {
	    Set<Item> possibleRabbits = world.searchSurroundings(animal);
        Iterator<Item> it = possibleRabbits.iterator();
        Item closestRabbit = animal;
        int distance = Integer.MAX_VALUE;
        while (it.hasNext()) {
            Item item = it.next();
            if ((item.getName().equals("Rabbit") || item.getName().equals("Gnat")) && (animal.getLocation().getDistance(item.getLocation()) < distance)) {
                closestRabbit = item;
                distance = animal.getLocation().getDistance(item.getLocation());
            }
        }
	    if(distance < Integer.MAX_VALUE)
	        return closestRabbit;
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
	    Item closestRabbit = closestRabbit(world, animal);
	    boolean full = animal.getEnergy() == animal.getMaxEnergy();
	    if(!full && closestRabbit != null && animal.getLocation().getDistance(closestRabbit.getLocation()) == 1){
	        return new EatCommand(animal, closestRabbit);
	    } else if ( animal.getEnergy() > animal.getMaxEnergy() * 0.5 ){
	        int i = 0;
	        while(i<xAround.length){
	            Location breedLocation = new Location(animal.getLocation().getX() + xAround[i], animal.getLocation().getY() + yAround[i]);
	            if(isLocationEmpty(world, animal, breedLocation)){
	                return new BreedCommand(animal, breedLocation);
	            }
	            i++;
	        }
	    } else if (closestRabbit != null){
	        Direction goThisWay = goThisWay(world, animal, closestRabbit);
	        Location moveLocation = new Location(animal.getLocation(), goThisWay);
	        if(isLocationEmpty(world, animal, moveLocation)){
	            return new MoveCommand(animal, moveLocation);
	        }
	    } else {
	        Direction goThisWay = leastFoxes(world, animal);
	        Location moveLocation = new Location(animal.getLocation(), goThisWay);
	        if(isLocationEmpty(world, animal, moveLocation)){
	            return new MoveCommand(animal, moveLocation);
	        } else return new WaitCommand();
	    }
		return new WaitCommand();
	}
}
