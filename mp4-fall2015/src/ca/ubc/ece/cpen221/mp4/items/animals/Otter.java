package ca.ubc.ece.cpen221.mp4.items.animals;

import javax.swing.ImageIcon;

import ca.ubc.ece.cpen221.mp4.Direction;
import ca.ubc.ece.cpen221.mp4.Food;
import ca.ubc.ece.cpen221.mp4.Location;
import ca.ubc.ece.cpen221.mp4.Util;
import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.commands.Command;
import ca.ubc.ece.cpen221.mp4.commands.MoveCommand;
import ca.ubc.ece.cpen221.mp4.commands.WaitCommand;
import ca.ubc.ece.cpen221.mp4.items.LivingItem;

public class Otter implements LivingItem {

    private static final ImageIcon otterImage = Util.loadImage("otter.gif");

    private static final int MEAT_CALORIES = 50;
    private static final int STRENGTH = 90;

    private Location location;
    private boolean isDead;
    
    public Otter(Location initialLocation) {
        this.location = initialLocation;
        this.isDead = false;
    }

    @Override
    public void moveTo(Location targetLocation) {
        location = targetLocation;

    }

    @Override
    public int getMovingRange() {
        
            return 1;
    }

    @Override
    public ImageIcon getImage() {

        return otterImage;
    }

    @Override
    public String getName() {
        
            return "Otter";
    }

    @Override
    public Location getLocation() {
        
            return location;
    }

    @Override
    public int getStrength() {

        return STRENGTH;
    }

    @Override
    public void loseEnergy(int energy) {
        isDead = true; 

    }

    @Override
    public boolean isDead() {
        
        return isDead;
    }

    @Override
    public int getPlantCalories() {
        
        return 0;
    }

    @Override
    public int getMeatCalories() {
        
        return MEAT_CALORIES;
    }

    @Override
    public int getCoolDownPeriod() {

        return 2;
    }

    @Override
    public Command getNextAction(World world) {
        
        Direction dir = Util.getRandomDirection();
        Location targetLocation = new Location(this.getLocation(), dir);
        if (Util.isValidLocation(world, targetLocation) && Util.isLocationEmpty(world, targetLocation)) {
            return new MoveCommand(this, targetLocation);
        }

        return new WaitCommand();
    }

    @Override
    public int getEnergy() {
        // TODO Auto-generated method stub
        return 100;
    }

    @Override
    public LivingItem breed() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void eat(Food food) {

    }

}
