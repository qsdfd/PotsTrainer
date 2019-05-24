import org.osbot.rs07.accessor.XNPC;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.util.concurrent.TimeUnit;
import java.awt.*;

@ScriptManifest(name = "Pots trainer", author = "dokato", version = 1.74, info = "", logo = "") 
public class Main extends Script {
	
	private static final Color standardTxtColor = new Color(255, 255, 255);
	
	//vial of water = 227
	
	//toadflax = 2998
	
	//toadflax potion = 3002
	//guam potion = 91
	
	//eye of newt = 221
	
	//attack potion = 121
	
	
	private int ingredient_id;
	private int vial_id;

	private int pot_id;
	
	private long potsInBank;
	
	private boolean startb = true;
	
    private long timeRan;
    private long timeBegan;
	
	private String status;
	@Override
    public void onStart(){
		this.timeBegan = System.currentTimeMillis();
		setIds();
		potsInBank = 0;
    }
    
    private void setIds() {
		ingredient_id = 221;
		vial_id = 91;
    	pot_id = 121;
    	
	}

	@Override
    public void onExit() {
    }


    @Override
    public int onLoop() throws InterruptedException{
    	status="loop started"; 	
    	
    	procedures();
    	
    	if(readyToMakePots()){
    		makePots();
    	}else{
    		bank();
    	}
    	
    	return random(59,215);
    }
    
    private void procedures() {
    	status="checking yaw angle";
    	if(getCamera().getYawAngle() == 0){
    		status="moving yaw";
    		getCamera().moveYaw(random(200,330));
    	}
    	status="checking pitch angle";
    	if(getCamera().getPitchAngle() > 55){
    		status="moving pitch";
    		getCamera().movePitch(random(21,48));
    	}
	}

	private void makePots() throws InterruptedException {
    	status="checking if bank is still open";
    	if(getBank().isOpen()){
    		status="closing bank";
    		getBank().close();
    		sleep(random(12,217));
    	}else{
    		status="checking if selected item is ingredient";
    		if(getInventory().isItemSelected()
    				&& !getInventory().getSelectedItemName().equals("Eye of newt")){
    			getInventory().deselectItem();
    		}
    		status="checking if i'm animating";
    		if(!myPlayer().isAnimating()){
    			if(isDialogOpen()){
    				status="about to click on the make potion button";
    				getWidgets().get(270, 14, 29).interact("Make");
    				sleep(random(940,1748));
    			}else{
    				status="checking if there is an item selected in inv";
        			if(getInventory().isItemSelected()){
        				status="checking if selected item is the ingredient itself";
    	    			if(getInventory().getSelectedItemName().equals(getInventory().getItem(ingredient_id).getName())){
    	    				status="about to use the ingredient on the vial of water";
    	    				getInventory().getItem(vial_id).interact("Use");
    	    				sleep(random(1236,1874));
    	    			}
        			}else{
        				status="about to click on the ingredient";
        				getInventory().getItem(ingredient_id).interact("Use");
        				sleep(random(120,412));
        			}
    			}
    		}else{
    			hoverSomewhere();
    			status="waiting";
    			sleep(random(2789,4987));
    		}
    	}
	}

	private void hoverSomewhere() {
	}

	private boolean isDialogOpen() {
		status="checking if is in the dialogue";
		return getDialogues().inDialogue() && !getDialogues().isPendingContinuation();
	}

	private void bank() throws InterruptedException {
		deselectItem();
    	status="checking if bank is open";
    	if(getBank().isOpen()){
    		if(getInventory().isEmpty()){
    			if(!hasIngredientInInv()){
        			status="checking if has ingredients in bank";
        			if(!getBank().contains(ingredient_id)){
        				status="no ingredients in bank, stopping";
        				log("No ingredient in bank");
        				stop();
        			}else{
            			status="withdrawing ingredients from bank";
            			getBank().withdraw(ingredient_id, 14);
            			sleep(random(57,259));
        			}
        		}
        		if(!hasVialInInv()){
        			status="checking if has vials of water in bank";
        			if(!getBank().contains(vial_id)){
        				status="no vials of water in bank, stopping";
        				log("No vials of water in bank");
        				stop();
        			}else{
            			status="withdrawing vials of water from bank";
            			getBank().withdraw(vial_id, 14);
            			sleep(random(57,289));
        			}
        		}	
    		}else{
    			getBank().depositAll();
    			sleep(random(57,174));
    			incrementPotsinBank();
    		}
    	}else{
    		status="opening bank booth";
    		getBank().open();
    	}
	}

	private Character<XNPC> getBanker() {
		return getNpcs().closest("Banker");
	}

	private void incrementPotsinBank() {
		if(getBank().contains(pot_id)){
			potsInBank = getBank().getAmount(pot_id);
		}
	}

	private void deselectItem() throws InterruptedException {
		if(getInventory().isItemSelected()){
			getInventory().deselectItem();
			sleep(random(54,123));
		}
	}

	private boolean readyToMakePots() {
		status="Checking if has good setup inv";
    	return hasVialInInv() && hasIngredientInInv();
	}

	private boolean hasIngredientInInv() {
		status="checking if has ingredient in inv";
		return getInventory().contains(ingredient_id);
	}

	private boolean hasVialInInv() {
		status="checking if has water vials in inv";
		return getInventory().contains(vial_id);
	}

	@Override
    public void onPaint(Graphics2D g1){
    	
    	if(this.startb){
    		this.startb=false;
    		this.timeBegan=System.currentTimeMillis();
    	}
    	this.timeRan = (System.currentTimeMillis() - this.timeBegan);
		
		Graphics2D g = g1;

		int startY = 120;
		int increment = 15;
		int value = (-increment);
		int x = 20;
		
		g.setFont(new Font("Arial", 0, 13));
		g.setColor(standardTxtColor);
		g.drawString("Acc: " + getBot().getUsername().substring(0, getBot().getUsername().indexOf('@')), x,getY(startY, value+=increment));
		g.drawString("World: " + getWorlds().getCurrentWorld(),x,getY(startY, value+=increment));
		value+=increment;
		g.drawString("Version: " + getVersion(), x, getY(startY, value+=increment));
		g.drawString("Runtime: " + ft(this.timeRan), x, getY(startY, value+=increment));
		g.drawString("Status: " + status, x, getY(startY, value+=increment));
		value+=increment;
		g.drawString("Pots in bank: " + potsInBank, x, getY(startY, value+=increment));		
		value+=increment;
		g.drawString("Hebrlore lvl: " + getSkills().getStatic(Skill.HERBLORE), x, getY(startY, value+=increment));		
		g.drawString("Exp to next lvl: " + getExpToNextLevel(Skill.HERBLORE), x, getY(startY, value+=increment));		

    }
    
    private int getExpToNextLevel(Skill skill) {
    	int currentLevel = getSkills().getStatic(skill);
    	int nextLevel = currentLevel + 1;
    	return (getSkills().getExperienceForLevel(nextLevel) - getSkills().getExperience(skill));
	}

	private int getY(int startY, int value){
		return startY + value;
	}
    
    private void fillRect(Graphics2D g, Rectangle rect){
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}
    
    public void onMessage(Message message) throws InterruptedException {
    }
    
	private String ft(long duration) {
		String res = "";
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration)
				- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
						.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
						.toMinutes(duration));
		if (days == 0L) {
			res = hours + ":" + minutes + ":" + seconds;
		} else {
			res = days + ":" + hours + ":" + minutes + ":" + seconds;
		}
		return res;
	}
}