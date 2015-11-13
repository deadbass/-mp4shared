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
 * If the fox is next to a rabbit and it below EATTHRESHOLD energy, it eats the
 * rabbit. Otherwise, if the fox is above BREEDTHRESHOLD energy, it breeds in a
 * random, open space. Otherwise, the fox moves towards the closest rabbit. If
 * the fox is below MOVETHRESHOLD energy, it waits. If no rabbit is within
 * sight, the fox moves in the direction in which it sees the fewest foxes. (The
 * motivation for this is that if the density of foxes is uniform, it's harder
 * for the rabbits to hide) If it sees no rabbits and no foxes, it waits.
 * Patiently...
 * 
 */
public class FoxAI extends AbstractAI {

    // These arrays allow iteration through the eight locations surrounding a
    // location. For example, adding xAround[0] to the x-component of a location
    // and yAround[0] to the y component of a location gives the location to
    // the north of the original location. Doing the same thing with 1 instead
    // of 0 gives the location to the northeast of the location, 2 gives the
    // location to the east, etc., etc.
    private int[] xAround = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private int[] yAround = { -1, -1, 0, 1, 1, 1, 0, -1 };

    private static double BREEDTHRESHOLD = 0.8;
    private static double MOVETHRESHOLD = 0.2;
    private static double EATTHRESHOLD = 0.9;

    public FoxAI() {
    }

    /**
     * Returns the closest fox-edible object within view of the specified
     * ArenaAnimal. If no edible objects are in view, returns null
     * 
     * @param world
     *            The ArenaWorld the animal exists in
     * @param animal
     *            The ArenaAnimal which you're trying to find the closest rabbit
     *            of
     * @return an Item which is the closest fox-edible object in view. null if
     *         no fox-edible object is within sight.
     */
    private Item closestEat(ArenaWorld world, ArenaAnimal animal) {
        Set<Item> possibleEats = world.searchSurroundings(animal);
        Iterator<Item> it = possibleEats.iterator();
        Item closestEat = animal;
        int distance = Integer.MAX_VALUE;
        while (it.hasNext()) {
            Item item = it.next();
            if ((item.getName().equals("Rabbit")
                    || item.getName().equals("Gnat"))
                    && (animal.getLocation()
                            .getDistance(item.getLocation()) < distance)) {
                closestEat = item;
                distance = animal.getLocation().getDistance(item.getLocation());
            }
        }
        if (distance < Integer.MAX_VALUE)
            return closestEat;
        else
            return null;
    }

    /**
     * Returns the direction in which the least foxes are seen. South loses all
     * ties, North loses ties except to South, and East wins all ties
     * 
     * @param world
     *            the ArenaWorld the animal exists in
     * @param animal
     *            the specified ArenaAnimal
     * @return the direction in which the least foxes are seen
     */
    private Direction leastFoxes(ArenaWorld world, ArenaAnimal animal) {
        Set<Item> possibleFoxes = world.searchSurroundings(animal);
        Iterator<Item> it = possibleFoxes.iterator();
        int northFoxes = 0;
        int southFoxes = 0;
        int eastFoxes = 0;
        int westFoxes = 0;
        while (it.hasNext()) {
            Item item = it.next();
            int xDistance = item.getLocation().getX()
                    - animal.getLocation().getX();
            int yDistance = item.getLocation().getY()
                    - animal.getLocation().getY();
            if (item.getName().equals("Fox")) {
                if (xDistance > 0)
                    eastFoxes++;
                else if (xDistance < 0)
                    westFoxes++;
                if (yDistance > 0)
                    southFoxes++;
                else if (yDistance < 0)
                    northFoxes++;
            }
        }
        if (eastFoxes <= westFoxes && eastFoxes <= northFoxes
                && eastFoxes <= southFoxes)
            return Direction.EAST;
        if (westFoxes <= eastFoxes && westFoxes <= northFoxes
                && westFoxes <= southFoxes)
            return Direction.WEST;
        if (northFoxes <= westFoxes && northFoxes <= eastFoxes
                && northFoxes <= southFoxes)
            return Direction.NORTH;
        else
            return Direction.SOUTH;
    }

    /**
     * returns the direction the animal should move in to move towards the goal.
     * Closes the largest distance first: if the goal is 3 north and 7 west of
     * the animal, west will be returned. Ties are broken randomly.
     * 
     * @param world
     *            the ArenaWorld the goal and animal exist in
     * @param animal
     *            the ArenaAnimal which is to be moving towards the goal
     * @param goal
     *            the Item towards which the ArenaAnimal is to move towards.
     *            Must be in the same ArenaWorld as animal. Must be in a
     *            different location than animal.
     * @return the Direction animal should move in to move towards the goal
     */
    private Direction goThisWay(ArenaWorld world, ArenaAnimal animal,
            Item goal) {
        int xDistance = goal.getLocation().getX() - animal.getLocation().getX();
        int yDistance = goal.getLocation().getY() - animal.getLocation().getY();
        double random = Math.random();

        if (random >= 0.5) {
            if (Math.abs(xDistance) > Math.abs(yDistance)) {
                if (xDistance > 0)
                    return Direction.EAST;
                else
                    return Direction.WEST;
            } else {
                if (yDistance > 0)
                    return Direction.SOUTH;
                else
                    return Direction.NORTH;
            }
        } else {
            if (Math.abs(yDistance) > Math.abs(xDistance)) {
                if (yDistance > 0)
                    return Direction.SOUTH;
                else
                    return Direction.NORTH;
            } else {
                if (xDistance > 0)
                    return Direction.EAST;
                else
                    return Direction.WEST;
            }
        }
    }
    
    /**
     * returns the direction the animal should move in to move towards the goal.
     * Closes the smallest distance first: if the goal is 3 north and 7 west of
     * the animal, north will be returned. Ties are broken randomly.
     * 
     * @param world
     *            the ArenaWorld the goal and animal exist in
     * @param animal
     *            the ArenaAnimal which is to be moving towards the goal
     * @param goal
     *            the Item towards which the ArenaAnimal is to move towards.
     *            Must be in the same ArenaWorld as animal. Must be in a
     *            different location than animal.
     * @return the Direction animal should move in to move towards the goal
     */
    private Direction goThisWay2(ArenaWorld world, ArenaAnimal animal,
            Item goal) {
        int xDistance = goal.getLocation().getX() - animal.getLocation().getX();
        int yDistance = goal.getLocation().getY() - animal.getLocation().getY();
        double random = Math.random();

        if (random >= 0.5) {
            if (Math.abs(xDistance) > Math.abs(yDistance)) {
                if (yDistance > 0)
                    return Direction.SOUTH;
                else
                    return Direction.NORTH;
            } else {
                if (xDistance > 0)
                    return Direction.EAST;
                else
                    return Direction.WEST;
            }
        } else {
            if (Math.abs(yDistance) > Math.abs(xDistance)) {
                if (xDistance > 0)
                    return Direction.EAST;
                else
                    return Direction.WEST;
            } else {
                if (yDistance > 0)
                    return Direction.SOUTH;
                else
                    return Direction.NORTH;
            }
        }
    }

    /**
     * returns a boolean which indicates whether or not the specified location
     * is empty. The specified location must be within view of animal
     * 
     * @param world
     *            The ArenaWorld the location and animal exist in
     * @param animal
     *            The ArenaAnimal which is looking at the location
     * @param location
     *            The Location which is being tested for emptiness
     * @return true if the location is valid (within the confines of the map)
     *         and empty; false otherwise
     */
    public boolean isLocationEmpty(ArenaWorld world, ArenaAnimal animal,
            Location location) {
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
        Item closestEat = closestEat(world, animal);
        boolean full = animal.getEnergy() == animal.getMaxEnergy();

        // if the fox's energy is below EATTHRESHOLD and there's a rabbit next
        // to it, it eats the
        // rabbit
        if (animal.getEnergy() < animal.getMaxEnergy() * EATTHRESHOLD
                && closestEat != null && animal.getLocation()
                        .getDistance(closestEat.getLocation()) == 1) {
            return new EatCommand(animal, closestEat);
        }
        // if the fox is at more than BREEDTHRESHOLD energy, it breeds in the
        // first
        // available location, looking clockwise from north
        else if (animal.getEnergy() > animal.getMaxEnergy() * BREEDTHRESHOLD) {
            for (int i = 0; i < xAround.length; i++) {
                Location breedLocation = new Location(
                        animal.getLocation().getX() + xAround[i],
                        animal.getLocation().getY() + yAround[i]);
                if (isLocationEmpty(world, animal, breedLocation)) {
                    return new BreedCommand(animal, breedLocation);
                }
            }
        }
        // if there's an edible object in sight, the fox moves towards it.
        else if (closestEat != null) {
            Direction goThisWay;
            Location moveLocation;
            goThisWay = goThisWay(world, animal, closestEat);
            moveLocation = new Location(animal.getLocation(), goThisWay);
            if (isLocationEmpty(world, animal, moveLocation)) {
                return new MoveCommand(animal, moveLocation);
            }
            else{
                goThisWay = goThisWay2(world, animal, closestEat);
                moveLocation = new Location(animal.getLocation(), goThisWay);
                if (isLocationEmpty(world, animal, moveLocation)) {
                    return new MoveCommand(animal, moveLocation);
                }
            }
        }
        // if the fox is below MOVETHRESHOLD energy, it waits
        else if (animal.getEnergy() < animal.getMaxEnergy() * MOVETHRESHOLD) {
            return new WaitCommand();
        }
        // the fox tries is to move in the direction where there
        // are the least foxes. If it can't make this move, it waits.
        else {
            Direction goThisWay;
            Location moveLocation;
            goThisWay = leastFoxes(world, animal);
            moveLocation = new Location(animal.getLocation(), goThisWay);
            if (isLocationEmpty(world, animal, moveLocation)) {
                return new MoveCommand(animal, moveLocation);
            }
        }
        return new WaitCommand();
    }
}