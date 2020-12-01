import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;

public class Main {
    public static Graph exampleGraph() {
            Graph g = new SingleGraph("example");
            try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader("sehirler.txt")));
            
            while(scanner.hasNextLine()){
                String satir= scanner.nextLine();
                String[] array= satir.split(",");
                String sehir=array[0];
                int x = Integer.parseInt(array[1]);
                int y = Integer.parseInt(array[2]);
                
                g.addNode(sehir).addAttribute("xy", x, y);
            }
            } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader("komsular.txt")));
            
            while(scanner.hasNextLine()){
                String satir_k= scanner.nextLine();
                String[] array_k= satir_k.split(",");
                
                String komsuluk=array_k[0];
                String sehir1=array_k[1];
                String sehir2=array_k[2];
                int mesafe = Integer.parseInt(array_k[3]);
                
                g.addEdge(komsuluk, sehir1, sehir2).addAttribute("length", mesafe);
            }
            } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }  
            
                
		for (Node n : g)
			n.addAttribute("label", n.getId());
		for (Edge e : g.getEachEdge())
			e.addAttribute("label", "" + (int) e.getNumber("length"));
		return g;
	}
            
    public static void main(String[] args) throws InterruptedException {
                Graph g = exampleGraph();
		g.display(false);
                Scanner sc = new Scanner(System.in);
                String cityName,target = "", root = "";
                List<String> cityList = new ArrayList<String>();
                List<String> route = new ArrayList<String>();
                List<String> cities = new ArrayList<String>();
                double minLength=0,temp=0,totalLength=0;
                boolean check;
                
		Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

		dijkstra.init(g);
		dijkstra.setSource(g.getNode("Kocaeli"));
		dijkstra.compute();
                route.add("Kocaeli");
                System.out.println("Uyari!: Sehrin listeye eklenebilmesi icin Turkce karakter kullanmayiniz.\n");
                for(int i=0; i<=10; i++){
                    check= false;
                    System.out.print("Gitmek istediginiz sehirleri giriniz(Çıkmak için q)(Kalan sehir hakki: " + (10-i) +") : ");
                    cityName = sc.nextLine();
                    
                    for(Node node : g){
                        if( node.getId().equals(cityName))
                            check = true;
                    }
                    
                    if(cityName.equals("q")){
                        
                        break;
                    }    
                    else if(check == false){
                        System.out.println("Hata!: Hatali giris yaptiniz tekrar deneyin.");
                        i--;
                        continue;
                    }
                    else if(cityName.equals("q") == false){
                        cityList.add(cityName);
                            if(i==9){
                                break;
                            }
                    }
                }
                
                System.out.println("\n=========== En Kisa Guzergah Olusturuluyor ===========\n");
                Thread.sleep(1000);
                long start = System.currentTimeMillis();
                root = "Kocaeli";
                
                while(cityList.size() != 0){
                    dijkstra.setSource(g.getNode(root));
                    dijkstra.compute();
                    
                    
                    for (int j = 0; j < cityList.size(); j++){
                        for (Node node : g) {
                            if(cityList.get(j).equals(node.getId())){
                                minLength = dijkstra.getPathLength(node);
                                break;
                            }
                        }
                    }
                    for (int j = 0; j < cityList.size(); j++){
                        for (Node node : g) {
                            if(cityList.get(j).equals(node.getId())){
                                temp = dijkstra.getPathLength(node);
                                if( temp <= minLength){
                                    minLength=temp;
                                    target= node.getId();
                                }else
                                    continue;
                            }
                        }
                    }
                    totalLength += minLength;
                    route.add(target);
                    cities.add(target);
                    
                    for (Node node : dijkstra.getPathNodes(g.getNode(target)))
                            node.addAttribute("ui.style", "fill-color: blue;");

                    for (Edge edge : dijkstra.getPathEdges(g.getNode(target)))
                            edge.addAttribute("ui.style", "fill-color: red;");
                    
                    cityList.remove(target);
                    root=target;
                }
                
                dijkstra.setSource(g.getNode(root));
                dijkstra.compute();
                
                for(String s: route)
                    System.out.print("|| " + s +" ");
                
                double returnLength = dijkstra.getPathLength(g.getNode("Kocaeli"));
                System.out.println("|| ==> || Kocaeli ||");
                System.out.println("Gidis Mesafesi : "+totalLength+" km,  Donus Mesafesi: "+ returnLength+" km, Toplam Mesafe: "+ (totalLength+returnLength)+ " km");
                for (Node node : dijkstra.getPathNodes(g.getNode("Kocaeli")))
                            node.addAttribute("ui.style", "fill-color: red;");

                for (Edge edge : dijkstra.getPathEdges(g.getNode("Kocaeli")))
                            edge.addAttribute("ui.style", "fill-color: green;");
                
                long end = System.currentTimeMillis();
                
                System.out.println("Rota olusturma suresi: "+ ((end-start)*0.001)+" saniye\n");
                System.out.println("-Detayli guzergah harita uzerindedir.\n -Kirmizi cizgiler ve Mavi noktalar gidis yolunu gostermektedir. \n -Yesil cizgiler ve Kirmizi noktalar donus yolunu gostermektedir.\n");
                System.out.println("======================================================");
                
    }
}
