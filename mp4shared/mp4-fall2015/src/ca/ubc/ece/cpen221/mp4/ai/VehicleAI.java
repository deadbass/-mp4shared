package ca.ubc.ece.cpen221.mp4.ai;

import ca.ubc.ece.cpen221.mp4.ArenaWorld;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.items.Vehicle;

/**
 * The AI interface for all vehicle AIs.
 */
public interface VehicleAI {

    /**
     * Decides the next action to be taken, given the state of the World and the
     * vehicle.
     *
     * @param world
     *            the current World
     * @param vehicle
     *            the vehicle waiting for the next action
     * @return the next action for vehicle
     */
    Command getNextAction(ArenaWorld world, Vehicle vehicle);

}
