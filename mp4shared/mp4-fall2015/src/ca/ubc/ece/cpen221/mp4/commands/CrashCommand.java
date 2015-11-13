package ca.ubc.ece.cpen221.mp4.commands;

import ca.ubc.ece.cpen221.mp4.World;
import ca.ubc.ece.cpen221.mp4.items.Vehicle;

public class CrashCommand implements Command {
    private final Vehicle v1;
    private final Vehicle v2;

    public CrashCommand(Vehicle v1, Vehicle v2) {
        this.v1 = v1;
        this.v2 = v2;
        
    }

    @Override
    public void execute(World world) {
        
        if (v1.getStrength() > v2.getStrength()){
            v1.loseEnergy(v2.getStrength());
        }
        
        if (v2.getStrength() > v1.getStrength()){
            v2.loseEnergy(v2.getStrength());
        }
        
        else{
            v1.loseEnergy(v2.getStrength());
            v2.loseEnergy(v1.getStrength());
        }
        
    }

}
