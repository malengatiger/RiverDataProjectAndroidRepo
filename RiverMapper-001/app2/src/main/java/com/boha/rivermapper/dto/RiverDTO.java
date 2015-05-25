/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.rivermapper.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class RiverDTO implements Serializable, Comparable<RiverDTO> {
    private static final long serialVersionUID = 1L;
    private Integer riverID;
    private String riverName;
    private Date dateAdded;
    private Double nearestLatitude, nearestLongitude;
    private float distanceFromMe;
    private List<RiverPartDTO> riverPartList;

    public RiverDTO() {
    }

    public float getDistanceFromMe() {
        return distanceFromMe;
    }

    public void setDistanceFromMe(float distanceFromMe) {
        this.distanceFromMe = distanceFromMe;
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

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getRiverID() {
        return riverID;
    }

    public void setRiverID(Integer riverID) {
        this.riverID = riverID;
    }

    public String getRiverName() {
        return riverName;
    }

    public void setRiverName(String riverName) {
        this.riverName = riverName;
    }

    public List<RiverPartDTO> getRiverPartList() {
        return riverPartList;
    }

    public void setRiverPartList(List<RiverPartDTO> riverPartList) {
        this.riverPartList = riverPartList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (riverID != null ? riverID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RiverDTO)) {
            return false;
        }
        RiverDTO other = (RiverDTO) object;
        if ((this.riverID == null && other.riverID != null) || (this.riverID != null && !this.riverID.equals(other.riverID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.rivers.data.River[ riverID=" + riverID + " ]";
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
    public int compareTo(RiverDTO another) {
        if (this.distanceFromMe < another.distanceFromMe) {
            return -1;
        }
        if (this.distanceFromMe > another.distanceFromMe) {
            return 1;
        }
        return 0;
    }
}
