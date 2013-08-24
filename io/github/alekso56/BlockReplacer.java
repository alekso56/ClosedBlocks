package io.github.alekso56;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class BlockReplacer extends JavaPlugin implements Listener{
	public static int x,y,z;
	public static int i1;
	public static int current = 1;
	public World world;
	String[] CATArray = new String[1000];
	public static Map<String, String> ST = new HashMap<String, String>(); // selected tool
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		getLogger().info("saved config");
		saveConfig();
		//make scheduler here for replaceblock
		CATArray = loadArray();
		if(CATArray[1] == null){CATArray[1] = String.valueOf(current);getLogger().info("set array to current");}
		else{current = Integer.parseInt(CATArray[1]);getLogger().info("set current to array");}
		getLogger().info("loaded arrayData!");
	}
 
	public void onDisable(){
		CATArray[1] = String.valueOf(current);
		saveArray(CATArray);
		getLogger().info("Saved array data!");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("test")){
			getLogger().info(sender.getName() + " activated spam!");
			sender.sendMessage("here's your spam!");
			return true;
		}
		//else
		return false; 
	}
	 public void saveArray(String[] cATArray2) {
     try {
        FileOutputStream fos = new FileOutputStream("blockReplacer.db");
        GZIPOutputStream gzos = new GZIPOutputStream(fos);
        ObjectOutputStream out = new ObjectOutputStream(gzos);
        out.writeObject(cATArray2);
        out.flush();
        out.close();
     }
     catch (IOException e) {
         System.out.println(e); 
     }
  }
  
  public void splitString(String assetClasses) {
	  StringTokenizer stringtokenizer = new StringTokenizer(assetClasses, ":");
	  if (stringtokenizer.hasMoreElements()) {
			  int x = Integer.parseInt(stringtokenizer.nextToken());
			  int y = Integer.parseInt(stringtokenizer.nextToken());
			  int z = Integer.parseInt(stringtokenizer.nextToken());
			  int Material = Integer.parseInt(stringtokenizer.nextToken());
			  String timeStamp = stringtokenizer.nextToken();
	          //pls fix pls
			  if(timeStamp == "bigger than config value then"){
				  world.getBlockAt(x,y,z).setTypeId(Material); 
			  }
	  }
}
  private String joinString(int x2, int y2, int z2, int typeId, int i) {
		String y = x2 + ":" +y2+":" +z2+ ":" +typeId+ ":"+i;
		return y;
	}

  public String[] loadArray() {
      try {
        FileInputStream fis = new FileInputStream("blockReplacer.db");
        GZIPInputStream gzis = new GZIPInputStream(fis);
        ObjectInputStream in = new ObjectInputStream(gzis);
        String[] input_array = (String[])in.readObject();
        in.close();
        return input_array;
      }
      catch (Exception e) {
    	  getLogger().info("Database not found, will save db on exit");
    	  saveArray(CATArray);
          return CATArray;
      }
  }
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block b = event.getBlock();
        org.bukkit.Material b1 = b.getType();
        int timeStamp = Integer.parseInt(cTime());
        String dbString =  joinString(b.getX(),b.getY(),b.getZ(),b.getTypeId(),timeStamp);
        if (world == null){Player player = event.getPlayer();
    	world = player.getWorld();}
        if (b1 == Material.LOG && ST.get(event.getPlayer().getName()) == "AXE")
        {
        	//savedb
        	getLogger().info(dbString + " is dbstring");
        	getLogger().info(current + " is tbP");
        	CATArray[current]= dbString;
        	current = current + 1;
        }
        else if (b1 == Material.LEAVES && ST.get(event.getPlayer().getName()) == "SWORD")
        {
        	//savedb
        }
        else if(b1 == Material.LOG || b1 == Material.LEAVES && !event.getPlayer().isFlying()){event.setCancelled(true);}
     }

	@EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event){
        Player p = event.getPlayer();
        ItemStack i = p.getInventory().getItem(event.getNewSlot());
        if(i == null){i1 = 0;}
        else{i1 = i.getTypeId();}
        Integer id = i1;
		getLogger().info(id + " is the typeID!");
        getLogger().info(event.getNewSlot() + " is the itemslot!");
        if(id == 269 || id == 256 || id == 273 || id == 277 ){p.sendMessage(p.getName() + " equipped A SPADEEEE!!!"); ST.put(p.getName(), "SPADE");}
        else if(id == 270 || id == 257 || id == 274 || id == 278){p.sendMessage(p.getName() + " equipped An PICKAXEe!!!"); ST.put(p.getName(), "PICKAXE");}
        else if(id == 271 || id == 286 || id == 279 || id == 275){p.sendMessage(p.getName() + " equipped An AXE!!!"); ST.put(p.getName(), "AXE");}
        else if(id == 268 || id == 267 || id == 272 || id == 276){p.sendMessage(p.getName() + " equipped A sword!!!"); ST.put(p.getName(), "SWORD");}
        else{ST.put(p.getName(), "NONE");} //player has no tool
                }
    
    public static String cTime() {
    	Calendar cal = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat("hhmmss");
		String currentTime = sdf.format(cal.getTime());
    	System.out.println(currentTime);
		return currentTime;
    }
	
     @EventHandler
     public void onBlockPlace(BlockPlaceEvent event){
    	 Player send = event.getPlayer();
    	 if (send.getInventory().contains(Material.matchMaterial(getConfig().getString("Launch.Item")))) {
             send.sendMessage(ChatColor.GOLD + "Launching ...");
             send.playSound(send.getLocation(), Sound.BLAZE_DEATH, 1.0F, 1.0F);
             send.setVelocity(new Vector(40, 10, 40));
             Vector dir = send.getLocation().getDirection();
             send.setVelocity(dir.multiply(8));
             send.setFallDistance(-150.0F);
             send.sendMessage(ChatColor.translateAlternateColorCodes('&', "LAWNCHING"));
           }
     }
    }