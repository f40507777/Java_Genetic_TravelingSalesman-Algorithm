
import java.io.*;
/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;*/
import java.util.*;
import java.util.Map.Entry;
public class GA_ShortestPath {
	static int[][] citylenght;
	static int parentCount=6;//Childcount=10;
	static ArrayList<Integer> commont = new ArrayList<Integer>();
	static int fewest,nagcount;
	static int Changepercent=5;
	static int jumpscore=2410;
	static int round=0;
	static int stopround=1000000;
	static int bestscore=100000;
	static ArrayList<Integer> bestpath = new ArrayList<Integer>();
	static HashMap<ArrayList<Integer>,Integer> childpath;
	public static void main(String[] args) throws IOException{
		ReadData();
		FirstParents();
		
		while(round<stopround){
			sort(childpath);
			round++;
		}
		System.out.println(round+"次");
		System.out.println("score="+bestscore);
		System.out.println("path="+bestpath);
		
	}
	public static void ReadData()throws IOException
	{ 
		//FileReader fr=new FileReader("./TSPdata2.txt");
		FileReader fr=new FileReader("./pathdata.txt");
		BufferedReader br=new BufferedReader(fr);
		String s;
		int count=0;
		while((s=br.readLine())!=null){
			//System.out.println(s);
			String []strarray=s.split(",");
			if(count==0){
				citylenght=new int[strarray.length][strarray.length];
			}
			citylenght[count]=arrayStringToIntArray(strarray);
			count++;
		}
	}
	static int[] arrayStringToIntArray(String []strarray) {
		int[] intarray=new int[strarray.length];
		for(int i=0;i<strarray.length;i++){
			intarray[i]=Integer.parseInt(strarray[i]);
		}
		return intarray;
	}
	public static int pathlenght(ArrayList<Integer> patharray)
	{
		int pathlenghts=0;
		for(int i=0;i<patharray.size();i++){
			if((i != (patharray.size()-1))){
				pathlenghts=pathlenghts+citylenght[patharray.get(i)][patharray.get(i+1)];
			}else{
				pathlenghts=pathlenghts+citylenght[patharray.get(i)][patharray.get(0)];
			}
		}
		return pathlenghts;
	}
	public static ArrayList<Integer> randompath()
	{
		ArrayList<Integer> patharray = new ArrayList<Integer>();
		int randomnum;
		HashSet randSet=new HashSet<Integer>(citylenght.length);
		for(int i=0;i<citylenght.length;i++){
			randomnum=(int)(Math.random()*citylenght.length);
			while(!randSet.add(randomnum)){
				randomnum=(int)(Math.random()*citylenght.length);
			}
			patharray.add(randomnum);
		}
		return patharray;
	}
	
	public static void FirstParents()//隨機產生不重複路徑 10取5
	{
		HashMap<ArrayList<Integer>,Integer> pathashmap = new HashMap<ArrayList<Integer>,Integer>();
		ArrayList<Integer> temp = new ArrayList<Integer>();
		
		int childsize=(parentCount*(parentCount-1))/2;
		for(int i=0;i<childsize;i++){
			temp=randompath();
			pathashmap.put(temp,pathlenght(temp));
		}
		sort(pathashmap);
	}
	public static void sort(HashMap<ArrayList<Integer>,Integer> pathashmap)//Sort
	{
		//System.out.println("已排序");
		List<Map.Entry<ArrayList<Integer>,Integer>> list_path =new ArrayList<Map.Entry<ArrayList<Integer>,Integer>>(pathashmap.entrySet());
		Collections.sort(list_path, new Comparator<Map.Entry<ArrayList<Integer>,Integer>>(){
            public int compare(Map.Entry<ArrayList<Integer>,Integer> entry1,
                               Map.Entry<ArrayList<Integer>,Integer> entry2){
                return (entry1.getValue().compareTo(entry2.getValue()));
            }
        });
		int sorttop[][]=new int[parentCount][list_path.get(0).getKey().size()];
		for(int i=0;i<parentCount;i++){
			for(int j=0;j<list_path.get(i).getKey().size();j++){
				sorttop[i][j]=list_path.get(i).getKey().get(j);
			}
			//System.out.println(list_path.get(i).getValue()+"="+list_path.get(i).getKey());
			if(list_path.get(i).getValue()<bestscore){
				bestscore=list_path.get(i).getValue();
				bestpath=list_path.get(i).getKey();
			}
			if(list_path.get(i).getValue()==jumpscore){
				System.out.println("/////////////Final///////////");
				System.out.println(list_path.get(i).getValue()+"="+list_path.get(i).getKey());
				System.out.println("疊代次數="+round);
				System.exit(0);
			}
		}
		CrossOver(sorttop);
	}
	public static void CrossOver(int Patharray[][]){//選擇交叉交配
		childpath = new HashMap<ArrayList<Integer>,Integer>();
		ArrayList<Integer> path = new ArrayList<Integer>();
		int Parent[][]=new int[2][];//tow parent
		for(int i=0;i<Patharray.length-1;i++){
			for(int j=i+1;j<Patharray.length;j++){
				
				Parent[0]=Patharray[i];
				Parent[1]=Patharray[j];
				path=Mating(EdgeTable(Parent),Parent[(int)(Math.random()*2)][0]);
				childpath.put(path,pathlenght(path));
			}
		}

		//System.out.println(childpath.values());
		//sort(childpath);
	}
	public static ArrayList<Integer> Mating(HashMap<Integer, ArrayList<Integer>> EdgeTable ,int City){//交配

		ArrayList<Integer> path = new ArrayList<Integer>();
		
		while(EdgeTable.size()!=0){
			int tmp=City;
			if(EdgeTable.size()==1){
				path.add(City);
				EdgeTable.remove(City);
				for (Iterator it = childpath.entrySet().iterator(); it.hasNext(); ) {
					Map.Entry entry = (Map.Entry) it.next();
					if(entry.getKey().equals(path) ||((int)(Math.random()*100))<Changepercent){	
						path=Change(path);
					}
				}
				return path;
			}
			fewest=100;
			path.add(City);
			nagcount=100;
			commont = new ArrayList<Integer>();
			int newnage=0;
			//System.out.println("目前="+EdgeTable.get(City));
			if(EdgeTable.get(City).size()!=0){
				for(int w=0;w<EdgeTable.get(City).size();w++){
					if(EdgeTable.get(City).get(w)<0){
						commont.add(Math.abs(EdgeTable.get(City).get(w)));
						newnage++;
					}
				}
				for(int i=0;i<EdgeTable.get(City).size();i++){//size移除問題
					int nagtive=0;

					int listremove=Math.abs(EdgeTable.get(City).get(i));
					EdgeTable.get(listremove).remove((Object)City);//移除相關的
					EdgeTable.get(listremove).remove((Object)((-1)*City));//包含負數
					//System.out.println(listremove+"="+EdgeTable.get(listremove));
					/*for(int q=0;q<EdgeTable.get(listremove).size();q++){
						if(EdgeTable.get(listremove).get(q)<0){
							nagtive++;
						}
					}*/
					//int condidate=FewestEdgelisy(EdgeTable.get(listremove),nagtive);
					//System.out.println("child="+listremove+"	size="+EdgeTable.get(listremove).size());
					if(newnage==0 && FewestEdgelisy(EdgeTable.get(listremove)) ){
						commont.add(listremove);
					}
				}
				EdgeTable.remove(City);				
			}else{
				EdgeTable.remove(City);
				for(int k=0;k<EdgeTable.size();k++){
					int hashnag=0;
					if(FewestEdgelisy(EdgeTable.get(EdgeTable.keySet().toArray()[k]))){
						commont.add((Integer) (EdgeTable.keySet().toArray())[k]);
					}
				}
				
			}
			//System.out.println("commont="+commont);
			//System.out.println(commont);
			//==================================================================================random mod
			City=Math.abs(commont.get((int)(Math.random()*commont.size())));
			//==================================================================================sortest mod
			/*int sortest=1000,sortcity = 0;
			for(int z=0;z<commont.size();z++){
				if(citylenght[tmp][commont.get(z)]<sortest){
					sortest=citylenght[tmp][commont.get(z)];
					sortcity=commont.get(z);
				}
			}
			City=sortcity;*/
			//System.out.println("選擇="+City);
		}
		return null;
	}
	public static boolean FewestEdgelisy(ArrayList<Integer> Edge){
		if((Edge.size()<fewest)){
			//若長度比較短，或者長度相等且負數較小者
			fewest=Edge.size();
			commont.clear();
			return true;
			
		}else if(Edge.size()==fewest){
			return true;
		}
		
		return false;
	}
	public static HashMap<Integer, ArrayList<Integer>> EdgeTable(int parent[][]){
		HashMap<Integer, ArrayList<Integer>> HashEdgeTable = new HashMap<Integer, ArrayList<Integer>>();
		int index;
		for(int i=0;i<parent.length;i++){
			for(int j=0;j<parent[i].length;j++){
				ArrayList<Integer> Edgelist = new ArrayList<Integer>();
				index=Arrayindex(parent[i],j);//從0到100找位置
				//int before,after;
				int [] locat=new int[2];//0=before 1=after
				if(index==0){
					locat[0]=parent[i].length-1;
					locat[1]=1;
				}else if(index==parent[i].length-1){
					locat[0]=parent[i].length-2;
					locat[1]=0;
				}else{
					locat[0]=index-1;
					locat[1]=index+1;
				}
				for(int k=0;k<locat.length;k++){
					if(HashEdgeTable.get(j)==null){
						HashEdgeTable.put(j, Edgelist);
					}
					if(!(HashEdgeTable.get(j).indexOf(parent[i][locat[k]])>=0)){
						HashEdgeTable.get(j).add(parent[i][locat[k]]);
					}else{
						HashEdgeTable.get(j).remove((Object)parent[i][locat[k]]);
						HashEdgeTable.get(j).add(parent[i][locat[k]]*(-1));
					}	
				}
			}
		}
		//System.out.println(HashEdgeTable);
		return HashEdgeTable;
	}


	
	public static int Arrayindex(int []array,int key)
	{
		for(int i=0;i<array.length;i++){
			if(array[i]==key){
				return i;
			}
		}
		return -1;
	}
	public static ArrayList<Integer> Change(ArrayList<Integer> path){
		int index_A=(int)(Math.random()*path.size()),index_B=(int)(Math.random()*path.size());
		int tmp=path.get(index_A);
		path.set(index_A, path.get(index_B));
		path.set(index_B, tmp);
		return path;
	}
}
