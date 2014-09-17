/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mcmiddleearth.freebuild;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.Stairs;

/**
 *
 * @author Donovan, Ivan1pl
 */
public class Theme {
    @Getter
    private String theme;
    @Getter
    private Location cent;
    @Getter
    private ArrayList<Plot> plots = new ArrayList<Plot>();
    @Getter
    private ArrayList<Plot> currplots = new ArrayList<Plot>();
    @Getter
    private int x_left;
    @Getter
    private int x_right;
    @Getter
    private String model;
    
    public Theme(String name, String url, String model){
        int sizez = DBmanager.currModel.getSizez();
        int sizez2 = DBmanager.curr.plots.get(0).getSizeZ();
        this.theme = name;
        this.model = model;
        Location ocent = DBmanager.curr.getCent().clone();
        ocent.add(0, 0, sizez+sizez2+13);
        this.cent=ocent;
        this.Generate();
    }
    public Theme(String name, String url, Location loc, String model){
        this.theme = name;
        this.model = model;
        this.cent = loc.add(0 , -1, 0);
        this.Generate();
    }
    public Theme(String name, Location loc, ArrayList<Plot> plotz, ArrayList<Plot> currs, int xl, int xr, String model){
        this.theme = name;
        this.model = model;
        this.cent = loc.add(0 , -1, 0);
        this.plots.addAll(plotz);
        this.currplots.addAll(currs);
        this.x_left = xl;
        this.x_right = xr;
    }
    private void genSign(BlockFace face){
        Location l = getCent().clone();
        if(face == BlockFace.EAST){
            l.subtract(3, -1, 0);
        }
        else if(face == BlockFace.WEST){
            l.add(3, 1, 0);
        }
        Block sign = l.getBlock();
        sign.setType(Material.SIGN_POST);
        org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.SIGN_POST);
        s.setFacingDirection(face);
        sign.setType(Material.SIGN_POST);
        Sign plotSign = (Sign) sign.getState();
        plotSign.setData(s);
        plotSign.setLine(0, ChatColor.BOLD + "Main");
        plotSign.setLine(1, ChatColor.BOLD + "ThemedBuild");
        plotSign.setLine(2, theme);
        plotSign.update();
    }
    private void Generate(){
        int sizez = DBmanager.currModel.getSizez()+1;
        Location loc = cent;
        BlockState state;
        Stairs stairs;
        for(int z = loc.getBlockZ()-sizez-6; z<loc.getBlockZ()+sizez+7; z++){
            for(int x = loc.getBlockX()-1; x<loc.getBlockX() + 2; x++){
                Location lc = new Location(loc.getWorld(), x, loc.getBlockY(), z);
                lc.getBlock().setType(Material.SANDSTONE);
            }
            Location lc = new Location(loc.getWorld(), loc.getBlockX()-2, loc.getBlockY(), z);
            lc.getBlock().setType(Material.BRICK_STAIRS);
            state = lc.getBlock().getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.WEST);
            state.setData(stairs);
            state.update(true);
            lc = new Location(loc.getWorld(), loc.getBlockX()+2, loc.getBlockY(), z);
            lc.getBlock().setType(Material.BRICK_STAIRS);
            state = lc.getBlock().getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.EAST);
            state.setData(stairs);
            state.update(true);
        }
        genSign(BlockFace.EAST);
        genSign(BlockFace.WEST);
        this.genPlots(true);
    }
    private void genGate(World w, int x, int y, int z, BlockFace direction){
        new Location(w,x,y,z+1).getBlock().setType(Material.FENCE);
        new Location(w,x,y,z+2).getBlock().setType(Material.FENCE);
        new Location(w,x,y+1,z+1).getBlock().setType(Material.FENCE);
        new Location(w,x,y+1,z+2).getBlock().setType(Material.FENCE);
        new Location(w,x,y,z-1).getBlock().setType(Material.FENCE);
        new Location(w,x,y,z-2).getBlock().setType(Material.FENCE);
        new Location(w,x,y+1,z-1).getBlock().setType(Material.FENCE);
        new Location(w,x,y+1,z-2).getBlock().setType(Material.FENCE);
        new Location(w,x,y+2,z+1).getBlock().setType(Material.FENCE);
        new Location(w,x,y+2,z).getBlock().setType(Material.FENCE);
        new Location(w,x,y+2,z-1).getBlock().setType(Material.FENCE);
        Block sign;
        org.bukkit.material.Sign s = new org.bukkit.material.Sign(Material.WALL_SIGN);
        if(direction == BlockFace.EAST){
            sign = new Location(w,x+1,y+2,z).getBlock();
        }
        else if(direction == BlockFace.WEST){
            sign = new Location(w,x-1,y+2,z).getBlock();
        }
        else{
            return;
        }
        s.setFacingDirection(direction);
        sign.setType(Material.WALL_SIGN);
        Sign plotSign = (Sign) sign.getState();
        plotSign.setData(s);
        plotSign.setLine(0, "");
        plotSign.setLine(1, ChatColor.RED + "Closed");
        plotSign.update();
    }
    public void close(){
        genGate(getCent().getWorld(), getCent().getBlockX()-3, getCent().getBlockY()+1, getCent().getBlockZ(), BlockFace.EAST);
        genGate(getCent().getWorld(), getCent().getBlockX()+3, getCent().getBlockY()+1, getCent().getBlockZ(), BlockFace.WEST);
        DBmanager.save();
    }
    public void genPlots(boolean first){
        Bukkit.getServer().broadcastMessage(Freebuild.prefix + "Generating new plots, Lag incoming");
        Plot p1;
        Plot p2;
        Plot p3;
        Plot p4;
        Block b;
        BlockState state;
        Stairs stairs;
        int sizex = DBmanager.currModel.getSizex()+1;
        int sizez = DBmanager.currModel.getSizez()+1;
        if(first){
//            Bukkit.getServer().broadcastMessage("1");
            p1 = new Plot(new Location(cent.getWorld(), cent.getBlockX()+3, cent.getBlockY()+1, cent.getBlockZ()+3), 1, sizex, sizez);
            p2 = new Plot(new Location(cent.getWorld(), cent.getBlockX()-3, cent.getBlockY()+1, cent.getBlockZ()+3), 2, sizex, sizez);
            p3 = new Plot(new Location(cent.getWorld(), cent.getBlockX()+3, cent.getBlockY()+1, cent.getBlockZ()-3), 4, sizex, sizez);
            p4 = new Plot(new Location(cent.getWorld(), cent.getBlockX()-3, cent.getBlockY()+1, cent.getBlockZ()-3), 3, sizex, sizez);
            this.x_left = cent.getBlockX()+3;
            this.x_right = cent.getBlockX()-3;
        }else{
            this.x_left += (sizex + 5);
            this.x_right -= (sizex + 5);
            p1 = new Plot(new Location(cent.getWorld(), this.x_left, cent.getBlockY()+1, cent.getBlockZ()+3), 1, sizex, sizez);
            p2 = new Plot(new Location(cent.getWorld(), this.x_right, cent.getBlockY()+1, cent.getBlockZ()+3), 2, sizex, sizez);
            p3 = new Plot(new Location(cent.getWorld(), this.x_left, cent.getBlockY()+1, cent.getBlockZ()-3), 4, sizex, sizez);
            p4 = new Plot(new Location(cent.getWorld(), this.x_right, cent.getBlockY()+1, cent.getBlockZ()-3), 3, sizex, sizez);
        }
        currplots.clear();
        currplots.add(p1);
        currplots.add(p2);
        currplots.add(p3);
        currplots.add(p4);
        plots.add(p1);
        plots.add(p2);
        plots.add(p3);
        plots.add(p4);
        for(int z = cent.getBlockZ()-1; z<cent.getBlockZ()+2; z++){
            for(int x = p1.getCorner().getBlockX()-1; x<p1.getCorner().getBlockX()+sizex+4; x++){
                new Location(cent.getWorld(), x, cent.getBlockY(), z).getBlock().setType(Material.SANDSTONE);
            }
            for(int x = p2.getCorner().getBlockX()+1; x>p2.getCorner().getBlockX()-sizex-4; x--){
                new Location(cent.getWorld(), x, cent.getBlockY(), z).getBlock().setType(Material.SANDSTONE);
            }
        }
        for(int x = p1.getCorner().getBlockX()-1; x<p1.getCorner().getBlockX()+sizex+4; x++){
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()-2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.NORTH);
            state.setData(stairs);
            state.update(true);
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()+2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.SOUTH);
            state.setData(stairs);
            state.update(true);
        }
        for(int x = p2.getCorner().getBlockX()+1; x>p2.getCorner().getBlockX()-sizex-4; x--){
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()-2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.NORTH);
            state.setData(stairs);
            state.update(true);
            b = new Location(cent.getWorld(), x, cent.getBlockY(), cent.getBlockZ()+2).getBlock();
            b.setType(Material.BRICK_STAIRS);
            state = b.getState();
            stairs = (Stairs) state.getData();
            stairs.setFacingDirection(BlockFace.SOUTH);
            state.setData(stairs);
            state.update(true);
        }
    }
}
