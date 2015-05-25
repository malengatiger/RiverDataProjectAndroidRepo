/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.rivermapper.dto;

import java.util.List;

/**
 *
 * @author aubreyM
 */
public class ResponseDTO {
 
    private int statusCode;
    private String message;
    
    private List<RiverDTO> riverList;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RiverDTO> getRiverList() {
        return riverList;
    }

    public void setRiverList(List<RiverDTO> riverList) {
        this.riverList = riverList;
    }
    
    
}
