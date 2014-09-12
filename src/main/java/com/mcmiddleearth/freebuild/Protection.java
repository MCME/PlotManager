/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.material.Dye;
/**
 *
 * @author Donovan, aaldim, Ivan1pl
 */
public final class Protection implements Listener{
    boolean canBuild;
    //on relog cant build
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.isCancelled())
            return;
        Player p = event.getPlayer();
        Block b = event.getBlock();
        Material mat = b.getType();
        if(mat.equals(Material.SIGN_POST)){
            Sign s = (Sign) b.getState();
            if (s.getLine(0).equalsIgnoreCase(ChatColor.RED + "[ThemedBuild]")) {
                if (mat == Material.SIGN_POST && p.hasPermission("plotmanager.create")){
                    p.sendMessage(ChatColor.RED + "[PlotManager] removed plotsign!");
                }
                else if (mat == Material.SIGN_POST) {
                    p.sendMessage(ChatColor.RED + "[PlotManager] no permission for that! ask staff to un-claim your plot!");
                    event.setCancelled(true);
                }
            }

        }

        Location ploc=b.getLocation();
        canBuild = false;
        if(DBmanager.plots.containsKey(p.getName())){
            List<Plot> pPlots = DBmanager.plots.get(p.getName());
//            p.sendMessage(pPlots.toString());
            for(Plot plot : pPlots){
                if(ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]))){
                    canBuild = true;//ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]));
                }
            }
        }
        if(!canBuild){
//            if(!p.hasPermission("plotmanager.create"))
                event.setCancelled(true);
        }
    }
    private void blockPlaceProtect(Block b, Player p, Cancellable e){
        if(e.isCancelled())
            return;
        Location ploc=b.getLocation();
        canBuild = false;
        if(DBmanager.plots.containsKey(p.getName())){
            List<Plot> pPlots = DBmanager.plots.get(p.getName());
            for(Plot plot : pPlots){
                if(ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]))){
                    canBuild = true;//ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]));
                }
            }
        }
        if(!canBuild){
//            if(!p.hasPermission("plotmanager.create"))
                e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Block b = e.getBlock();
        blockPlaceProtect(b,p,e);
        if(!e.isCancelled() && b.getType() == Material.BED_BLOCK){
            b = b.getRelative(((Bed) b.getState().getData()).getFacing());
            if(b.isEmpty()){
                blockPlaceProtect(b,p,e);
            }
        }
    }
    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent e){
        if(e.hasItem() && !e.isCancelled()){
            Block b = e.getClickedBlock();
            Player p = e.getPlayer();
            ItemStack item = e.getItem();
            Material material = item.getType();
            if(material == Material.INK_SACK && ((Dye) item.getData()).getColor() == DyeColor.WHITE && e.hasBlock()){
                blockPlaceProtect(b,p,e);
            }
            else if(material == Material.WATER_BUCKET || material == Material.LAVA_BUCKET){
                BlockFace face = e.getBlockFace();
                blockPlaceProtect(b,p,e);
                blockPlaceProtect(b.getRelative(face),p,e);
            }
            else if(material == Material.BUCKET){
                if(b.getType() == Material.WATER || b.getType() == Material.LAVA){
                    blockPlaceProtect(b,p,e);
                }
                else{
                    e.setCancelled(true);
                }
            }
        }
    }
    private boolean isInPlot(Block b){
        Location ploc=b.getLocation();
        Set<String> keys = DBmanager.plots.keySet();
        List<Plot> pPlots;
        for(String k : keys){
            pPlots = DBmanager.plots.get(k);
            for(Plot plot : pPlots){
                if(ploc.getWorld().equals(plot.getW())&&((ploc.getBlockX()<plot.Boundx[1] && ploc.getBlockX()>plot.Boundx[0])&&(ploc.getBlockZ()<plot.Boundz[1] && ploc.getBlockZ()>plot.Boundz[0]))){
                    return true;
                }
            }
        }
        return false;
    }
    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e){
        if(e.isCancelled())
            return;
        BlockFace face = e.getDirection();
        List<Block> blocks = e.getBlocks();
        for(Block b: blocks){
            if(!isInPlot(b) || !isInPlot(b.getRelative(face))){
                e.setCancelled(true);
                return;
            }
        }
    }
    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e){
        if(e.isCancelled() || !e.isSticky())
            return;
        Block b = e.getRetractLocation().getBlock();
        if(!b.isEmpty() && !isInPlot(b)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent e){
        Block b = e.getToBlock();
        if(!isInPlot(b)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e)
    {       
        HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) e;
        Entity removerEntity = entityEvent.getRemover();
        Player p = (Player) removerEntity;
        
        if(!canBuild){
            if(!p.hasPermission("plotmanager.create")) {
                if(e.getEntity() instanceof Painting || e.getEntity() instanceof ItemFrame) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
