/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.rivermapper.dto;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class RiverPartDTO implements Serializable, Comparable<RiverPartDTO> {
    private static final long serialVersionUID = 1L;
    private Integer riverPartID;
    private String riverName;
    private int fNode;
    private int tNode;
    private int iprivID;
    private double distanceToMouth;
    private float distanceFromMe;
    private Double nearestLatitude, nearestLongitude;
    private RiverDTO river;
    private List<RiverPointDTO> riverPointList;

    public RiverPartDTO() {
    }

    public Double getNearestLatitude() {
        return nearestLatitude;
    }

    public void setNearestLatitude(Double nearestLatitude) {
        this.nearestLatitude = nearestLatitude;
    }

    public Double getNearestLongitude() {
        return nearestLongitude;
    }

    public void setNearestLongitude(Double nearestLongitude) {
        this.nearestLongitude = nearestLongitude;
    }

    public RiverPartDTO(Integer riverPartID) {
        this.riverPartID = riverPartID;
    }

    public float getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(float distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
    }

    public RiverDTO getRiver() {
        return river;
    }

    public void setRiver(RiverDTO river) {
        this.river = river;
    }

    public Integer getRiverPartID() {
        return riverPartID;
    }

    public void setRiverPartID(Integer riverPartID) {
        this.riverPartID = riverPartID;
    }

    public String getRiverName() {
        return riverName;
    }

    public void setRiverName(String riverName) {
        this.riverName = riverName;
    }

    public int getFNode() {
        return fNode;
    }

    public void setFNode(int fNode) {
        this.fNode = fNode;
    }

    public int getTNode() {
        return tNode;
    }

    public void setTNode(int tNode) {
        this.tNode = tNode;
    }

    public int getIprivID() {
        return iprivID;
    }

    public void setIprivID(int iprivID) {
        this.iprivID = iprivID;
    }

    public double getDistanceToMouth() {
        return distanceToMouth;
    }

    public void setDistanceToMouth(double distanceToMouth) {
        this.distanceToMouth = distanceToMouth;
    }

   

    public List<RiverPointDTO> getRiverPointList() {
        return riverPointList;
    }

    public void setRiverPointList(List<RiverPointDTO> riverPointList) {
        this.riverPointList = riverPointList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riverPartID != null ? riverPartID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiverPartDTO)) {
            return false;
        }
        RiverPartDTO other = (RiverPartDTO) object;
        if ((this.riverPartID == null && other.riverPartID != null) || (this.riverPartID != null && !this.riverPartID.equals(other.riverPartID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.rivers.data.RiverPart[ riverPartID=" + riverPartID + " ]";
    }


    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(RiverPartDTO another) {
        if (this.distanceFromMe < another.distanceFromMe) {
            return -1;
        }
        if (this.distanceFromMe > another.distanceFromMe) {
            return 1;
        }
        return 0;
    }
}
