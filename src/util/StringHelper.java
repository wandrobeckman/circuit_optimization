/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author pedro
 */
public class StringHelper {
 
    public static String replaceOnIndex(String string, int index, String toReplace,String input){
        
        char stringArray[] = string.toCharArray();
        String output = "";
        for(int i=0; i< string.length();i++){
            
                
            if(i==index){
                output = output+toReplace;
                i = i+(input.length()-1);
            }else{
                output = output + stringArray[i];
            }
        }
        return output;
    }
    
    
}
