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
 * If the rabbit is next to some grass and it is not at maximum energy, it eats
 * the grass. Otherwise, if it's at greater than 50% energy, it breeds in a
 * random, open space. Otherwise, it moves in the direction where it sees the
 * least foxes. If it doesn't see any foxes, it moves towards the closest piece
 * of grass. If it doesn't see any grass, it moves in the direction it sees the
 * most rabbits in. (The motivation for this is if the rabbits are all in one
 * place, a lot of foxes will starve.)
 */
public class RabbitAI extends AbstractAI {

    private int[] xAround = { 0, 1, 1, 1, 0, -1, -1, -1 };
    private int[] yAround = { -1, -1, 0, 1, 1, 1, 0, -1 };

    private static double BREEDTHRESHOLD = 0.7;
    private static double MOVETHRESHOLD = 0.2;

    public RabbitAI() {
    }

    /**
     * 'rotates' a direction clockwise: if North is input, East is output
     * 
     * @param direction
     *            The direction to be rotated
     * @return The (clockwise) rotated direction
     */
    private Direction rotate(Direction direction) {
        if (direction == Direction.NORTH)
            return Direction.EAST;
        if (direction == Direction.EAST)
            return Direction.SOUTH;
        if (direction == Direction.SOUTH)
            return Direction.WEST;
        else
            return Direction.NORTH;
    }

    /**
     * returns the opposite of the given direction
     * 
     * @param direction
     *            A non-null Direction
     * @return The opposite of direction; if direction is North, South is
     *         returned.
     */
    private Direction opposite(Direction direction) {
        if (direction == Direction.NORTH)
            return Direction.SOUTH;
        if (direction == Direction.EAST)
            return Direction.WEST;
        if (direction == Direction.SOUTH)
            return Direction.NORTH;
        else
            return Direction.EAST;
    }

    /**
     * returns the closest instance within view of animal of an Item with the
     * name itemName.
     * 
     * @param world
     *            The ArenaWorld containing the animal and item
     * @param animal
     *            The ArenaAnimal looking for the item
     * @param itemName
     *            The name of the item to be found
     * @return the closest instance of an Item with the name itemName to animal.
     *         returns null if no such Item is in view of animal
     */
    private Item closestItem(ArenaWorld world, ArenaAnimal animal,
            String itemName) {
        Set<Item> possibleItem = world.searchSurroundings(animal);
        Iterator<Item> it = possibleItem.iterator();
        Item closestItem = animal;
        int distance = Integer.MAX_VALUE;
        while (it.hasNext()) {
            Item item = it.next();
            if (item.getName().equals(itemName) && (animal.getLocation()
                    .getDistance(item.getLocation()) < distance)) {
                closestItem = item;
                distance = animal.getLocation().getDistance(item.getLocation());
            }
        }
        if (distance < Integer.MAX_VALUE)
            return closestItem;
        else
            return null;
    }

    /**
     * returns the Direction in which the least of the Item with the name
     * ItemName is seen by animal. East is returned preferentially, followed by West, then North and South
     * 
     * @param world
     *            The ArenaWorld the animal lives in
     * @param animal
     *            The ArenaAnimal looking for items
     * @param itemName
     *            The name of the item to be avoided
     * @return the Direction in which the fewest Items with the name ItemName
     *         are seen. 
     */
    private Direction leastItem(ArenaWorld world, ArenaAnimal animal,
            String itemName) {
        Set<Item> possibleFoxes = world.searchSurroundings(animal);
        Iterator<Item> it = possibleFoxes.iterator();
        int northItem = 0;
        int southItem = 0;
        int eastItem = 0;
        int westItem = 0;
        while (it.hasNext()) {
            Item item = it.next();
            int xDistance = item.getLocation().getX()
                    - animal.getLocation().getX();
            int yDistance = item.getLocation().getY()
                    - animal.getLocation().getY();
            if (item.getName().equals(itemName)) {
                if (xDistance > 0)
                    eastItem++;
                else if (xDistance < 0)
                    westItem++;
                if (yDistance > 0)
                    southItem++;
                else if (yDistance < 0)
                    northItem++;
            }
        }
        if (eastItem <= westItem && eastItem <= northItem
                && eastItem <= southItem)
            return Direction.EAST;
        if (westItem <= eastItem && westItem <= northItem
                && westItem <= southItem)
            return Direction.WEST;
        if (northItem <= westItem && northItem <= eastItem
                && northItem <= southItem)
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
        Item closestGrass = closestItem(world, animal, "grass");
        Item closestFox = closestItem(world, animal, "Fox");
        Item closestRabbit = closestItem(world, animal, "Rabbit");

        boolean full = animal.getEnergy() == animal.getMaxEnergy();
        // if the rabbit isn't full and it's next to grass, it eats the grass
        if (!full && closestGrass != null && animal.getLocation()
                .getDistance(closestGrass.getLocation()) == 1) {
            return new EatCommand(animal, closestGrass);
        }
        // if the rabbit's energy is above BREEDTHRESHOLD, it breeds in the
        // first available space, starting with the space direction north and
        // rotating clockwise
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
        // if the rabbit can see a fox, it moves in the direction of the least
        // foxes. If that direction is blocked, it tries the two directions
        // orthagonal to that direction.
        else if (closestFox != null) {
            Direction goThisWay = leastItem(world, animal, "Foxes");
            // Direction goThisWay = opposite(goThisWay(world, animal,
            // closestFox));
            Direction wrongWay = opposite(goThisWay);
            Location moveLocation;

            for (int i = 0; i < 4; i++) {
                moveLocation = new Location(animal.getLocation(), goThisWay);
                if (isLocationEmpty(world, animal, moveLocation)
                        && goThisWay != wrongWay) {
                    return new MoveCommand(animal, moveLocation);
                } else
                    goThisWay = rotate(goThisWay);
            }
        }
        // if the rabbit sees some grass, it moves towards it
        else if (closestGrass != null) {
            Direction goThisWay = goThisWay(world, animal, closestGrass);
            Location moveLocation = new Location(animal.getLocation(),
                    goThisWay);
            if (isLocationEmpty(world, animal, moveLocation)) {
                return new MoveCommand(animal, moveLocation);
            }
        }
        // if the rabbit is below MOVETHRESHOLD energy, it waits
        else if (animal.getEnergy() < animal.getMaxEnergy() * MOVETHRESHOLD) {
            return new WaitCommand();
        }
        // the rabbit tries to move in the direction it sees the least rabbits
        else if (closestRabbit != null) {
            Direction goThisWay = leastItem(world, animal, "Rabbit");
            Location moveLocation = new Location(animal.getLocation(),
                    goThisWay);
            if (isLocationEmpty(world, animal, moveLocation)) {
                return new MoveCommand(animal, moveLocation);
            }
        }
        // if none of the above worked, it waits
        return new WaitCommand();
    }
}