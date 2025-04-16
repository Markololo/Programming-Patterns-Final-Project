/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example;

import java.util.Date;

//model layer
public class Room 
{
 private int roomNo;

private String type ;

private double price;

private String availability ;

private Date addedDate; 



//Availability: status (“Yes”, “No”)
 public boolean isAvailable()
 {
  if(availability.equalsIgnoreCase("yes"))
  {
      return true;
  }
  
  else
  {
    availability.equalsIgnoreCase("no");
     return false;
  }
  }
}

