package project4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class virtualBitmap {
	
	int n;
	int m;
	int l;
	double p;
	Map<String,Integer> ip_element;
	
	int[] B; //this is the physical bitmap
	int[] R;
	
	Map<String,int[]> assist_map;
	
	int[] sourceip_code;
	Random rand ;
	int hash;
	virtualBitmap(int n, int m, int l, Map<String,Integer> ip_element,double p){
		this.rand = new Random();
		this.n = n;
		this.m = m;
		this.l = l;
		this.p=p;
		
		this.ip_element = new LinkedHashMap<>(ip_element);
		this.assist_map = new LinkedHashMap<>();
		this.sourceip_code = new int[n];//
		this.B = new int[m];
		this.R = new int[l];//500
		this.hash = (int)(Math.random() * (Integer.MAX_VALUE - 1) + 1);
		create_R_Array();
		
		create_assist_map();
	}
	
	private void create_R_Array() {
		rand = new Random();
		Set<Integer> set = new HashSet<>();
		while(set.size()!=l) {
			set.add(Math.abs(rand.nextInt(Integer.MAX_VALUE-1)+1));
		}
		int j =0;
		for(int i : set) {
			R[j] =i;
			j++;
		}
	}
	
	private void create_assist_map() {
		for(Map.Entry<String, Integer> entry :ip_element.entrySet()) {			
			int[] temp = new int[entry.getValue()];
			for(int i =0;i<entry.getValue();i++) {
				temp[i] = Math.abs(rand.nextInt(Integer.MAX_VALUE-1)+1);
			}
			assist_map.put(entry.getKey(), temp);
		}
	}
	
	private int getmax() {
		int i =0;
		for(Map.Entry<String, Integer> entry :ip_element.entrySet()) {
			sourceip_code[i] = entry.getKey().hashCode();
			i++;
		}
		return Arrays.stream(sourceip_code).max().getAsInt();
		
	}
	
	public void hashInto() {
		
		for(Map.Entry<String, int[]> entry : assist_map.entrySet()){
			String sourceIp = entry.getKey();
			int[] temp = entry.getValue();
			int threshold = (int)(Arrays.stream(temp).max().getAsInt()*(1/p));
			for(int i : temp) {
				if(i<threshold) {
					int index = ((Math.abs(sourceIp.hashCode())) ^ R[(i ^ hash) %l])%m;
					B[index] = 1;
				}
				
			}
		}
	}
	
	public void get_estimatevalue() throws IOException {
		File file = new File("VitualBitmap10.txt");
		File file1 = new File("output.txt");
		if(!file1.exists())
			file1.createNewFile();
	 	FileWriter fw1 = new FileWriter("output.txt");
		if(!file.exists())
			file.createNewFile();
	 	FileWriter fw = new FileWriter("VitualBitmap10.txt");
	 	int num_of_zero_in_B = 0;
	 	for(int i : B) {
	 		if(i==0)
	 			num_of_zero_in_B++;
	 	}
	 	for(Map.Entry<String, int[]> map : assist_map.entrySet()) {
	 		String sourceIp = map.getKey();
	 		double Vb = l*Math.log((double)num_of_zero_in_B /(double)m);
	 		//System.out.println("num_of_zero_in_B:" + num_of_zero_in_B/m);
	 		//System.out.println("vb:" + Vb);
	 		//double Vf = 
	 		
	 		double Vf = construct_virtualBitmap(sourceIp);
	 		
	 		double temp = (Vb-Vf)*(1/p) < 0? 0.0: (Vb-Vf)*(1/p);
	 		if(temp >600)
	 			System.out.println(temp);
	 		fw1.write(map.getValue().length+"\t"+temp+"\n");
	 		fw.write(map.getValue().length + "," + temp+"\n");
	 		System.out.println(map.getValue().length + "\t" + temp);
	 	}
	 	fw.close();
	 	fw1.close();
	}
	
	private double construct_virtualBitmap(String sourceIp) {
		double num_of_zeros_in_virtual = 0;
		for(int i=0;i<l;i++) {
			int index = ((Math.abs(sourceIp.hashCode())) ^ R[i]) % m;
			if(B[index]==0)
				num_of_zeros_in_virtual++;
		}
		return l*Math.log(num_of_zeros_in_virtual / (double)l);
	}
	public void print() {
		for(Map.Entry<String, Integer> map : ip_element.entrySet()) {
			System.out.println(map.getValue());
		}
	}
	

	public static void main(String[] args) throws IOException{
		double p =1.0;
		int m = 500000; //physical bitmap size
		int l =400; // virtual bitmap size for each
		int n = 0;
		Map<String,Integer> ip_element = new LinkedHashMap<>();
		File fl = new File("project4input.txt");

		if (fl.isFile() && fl.exists()) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(fl));
			BufferedReader br = new BufferedReader(read);
			String lineTxt = null;
			int lineNumber = 0;
			int i = 0;
			while ((lineTxt = br.readLine()) != null) {
				//
				if (lineNumber == 0) {
					String[] temp = lineTxt.split("\\s+");
					n = Integer.parseInt(temp[0]);
					lineNumber++;
				} 
				else {
					String[] temp = lineTxt.split("\\s+");
					//System.out.println(Integer.parseInt(temp[1]));
					ip_element.put(temp[0], Integer.parseInt(temp[1]));
					i++;
				}
			}
			br.close();
			read.close();
		} else {
			System.out.println("Cannot find file project4input !!!");
		}
		
		//File fl1 = new File("VitualBitmap.txt");
 		//if(!fl1.exists())
 		//	fl1.createNewFile();
 		//FileWriter fw = new FileWriter("VitualBitmap.txt");
 		// write code below.
		
		virtualBitmap vb = new virtualBitmap(n,m,l,ip_element,p);
		/*
		 * for(Map.Entry<String, Integer> map : ip_element.entrySet()) {
		 * System.out.println(map.getKey()); System.out.println(map.getValue()); }
		 */
 		vb.hashInto();
 		vb.get_estimatevalue();
 		//vb.print();
		//fw.close();
	}
}

