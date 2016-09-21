package main;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
	
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		String input = "";
		while(true){
			System.out.println("Valid commands:");
			System.out.println("update -- updates text file from current Bovada XML feed");
			System.out.println("exit   -- exits program");
			input = sc.nextLine();
			
			if(input.equals("update")){
				updateFile();
				System.out.println("done.");
				System.out.println();
				System.out.println();
			}else if(input.equals("exit")){
				System.exit(0);
				sc.close();
			}else{
				System.out.println("invalid command...");
			}
		}
		
		
	}
	
	
	public static void updateFile() throws IOException{
		ArrayList<ArrayList<String>> data;
		data = processXML();


		//writes to data file
		for(int n = 0; n < data.get(0).size(); n++){
			String write = "";
			for(int i = 0; i < data.size(); i++){
				write += data.get(i).get(n).toString();
				write += ", ";
				
			}
			writeDataFile(write);
		}
	}
	
	public static int numEvents(String data){
		int index = 0; // keeps track of index throughout search
		int num = 0; // tracks number of events throughout search
		Find event = new Find(data, "<Event ");
		
		while (index != -1){
			index = event.findInstanceof(index);
			num++;
		}
		
		return num-1;
	}
	
	// returns string of away team name
	public static String getAway(String string){
		String away = "";
		for(int i=0; i<string.length(); i++){
			if(string.charAt(i+1) == '@'){
				return away;
			}
			away += string.charAt(i);
		}
		return "error";
	}
	
	// returns string of home team name
	public static String getHome(String string){
		String home = "";
		for(int i=0; i<string.length(); i++){
			if (string.charAt(i) == '@'){
				for(int n = i+1; n<string.length(); n++){
					home += string.charAt(n);
				}
				return home;
			}
		}
			
			return "error";
	}
	
	
	// takes index and retrieves data within double quotes of XML page
	public static String getDataWithinDQuotes(String data, int index){
		int start = 0;
		String string = "";
		
		if (data.charAt(index) == '"'){
			start = index+1;
		}else if (data.charAt(index+1) == '"'){
			start = index+2;
		}else if (data.charAt(index+2) == '"'){
			start = index+3;
		}else if (data.charAt(index+3) == '"'){
			start = index+4;
		}
		
		while(data.charAt(start) != '"'){
			string += Character.toString(data.charAt(start));
			start++;
		}
		
		
		return string;
	}
	
	public static ArrayList<ArrayList<String>> processXML(){
		ArrayList<String> homeLines = new ArrayList<String>();
		ArrayList<String> awayLines = new ArrayList<String>();
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		String string = readXML();
		int numEvents = numEvents(string);
		int index = 0;
		String temp = "";
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		
		Find event = new Find(string, "<Event ");
		Find id = new Find(string, "ID");
		Find name = new Find(string, "NAME");
		Find line = new Find(string, "Line=");
		
		for(int i = 0; i < numEvents; i++){
		//find index of events and update index 
		index = event.findInstanceof(index);

		//get game id's
		index = id.findInstanceof(index);
		ids.add(getDataWithinDQuotes(string, index));
		
		//get strings of game identifiers
		index = name.findInstanceof(index);
		names.add(getDataWithinDQuotes(string, index));
		
		//get strings of home team and respective line
		index = line.findInstanceof(index);
		if(index == -1){ // to protect against uncompleted feed for an event
			names.remove(i);
			ids.remove(i);
			break;
		}
		temp += getDataWithinDQuotes(string, index);
		temp += ", ";
		temp += getHome(names.get(i));
		homeLines.add(temp);
		temp = "";
		
		//get strings of away team and respective line
		index = line.findInstanceof(index);
		if(index == -1){ // to protect against uncompleted feed for an event
			homeLines.remove(i);
			names.remove(i);
			ids.remove(i);
			break;
		}
		temp += getDataWithinDQuotes(string, index);
		temp += ", ";
		temp += getAway(names.get(i));
		awayLines.add(temp);
		temp = "";
		
		}

		data.add(ids);
		data.add(names);
		data.add(homeLines);
		data.add(awayLines);
		return data;
	}
	
	
	//updateFile() function, writes to data file.
	public static void writeDataFile(String string) throws IOException {
		BufferedWriter outStream= new BufferedWriter(new FileWriter("data.txt", true));
		outStream.write(string);
		outStream.newLine();
		outStream.close();
	}
	
	// receives up to XML file from Bovada and updates text file with new information
	public static String readXML(){
		URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line="";

	    try {
	        url = new URL("http://sportsfeeds.bovada.lv/basic/MLB.xml");
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));

	        while ((line = br.readLine()) != null) {
	            return line;
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            // nothing to see here
	        }
	    }
	    return line;
	}
}
