package main;

public class Find {
	
	String data;
	String find;

	public Find (String data, String find){
		this.data = data;
		this.find = find;

	}
	public int findInstanceof(int z){
		int index = z;
		int temp;
		
		for(int n = index; n < data.length(); n++){
			if(data.charAt(n) == find.charAt(0)){
				temp = n;
				for(int i = 0; i < find.length(); i++){
					if (data.charAt(temp) == find.charAt(i)){
						temp++;
						if(i == find.length()-1){
							return n+find.length();
						}
					}
				}
			}
		}
		
		return -1;
	}
}
